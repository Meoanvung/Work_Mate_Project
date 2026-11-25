import bcryptjs from 'bcryptjs';
import { User } from '../models/User.js';
import {
  generateOtp,
  hashOtp,
  verifyOtpHash,
  otpExpiryDate,
  generatePasswordResetToken,
  hashPasswordResetToken,
  passwordResetTokenExpiryDate,
  MAX_OTP_ATTEMPTS,
} from '../utils/otp.js';
import { sendOtpEmail, sendPasswordResetOtpEmail, sendPasswordChangeOtpEmail } from '../utils/emailService.js';
import { signAuthToken } from '../utils/token.js';

const SALT_ROUNDS = 10;

const normalizeEmail = (email) => String(email).trim().toLowerCase();
const normalizePhone = (phone) => String(phone).replace(/[\s().-]/g, '');

/** Attach a fresh OTP to the user document (without saving). */
function attachOtp(user, otp) {
  user.verificationOtpHash = hashOtp(otp);
  user.verificationOtpExpires = otpExpiryDate();
  user.verificationAttempts = 0;
}

function clearPasswordResetState(user) {
  user.passwordResetOtpHash = null;
  user.passwordResetOtpExpires = null;
  user.passwordResetAttempts = 0;
  user.passwordResetTokenHash = null;
  user.passwordResetTokenExpires = null;
}

function attachPasswordResetOtp(user, otp) {
  clearPasswordResetState(user);
  user.passwordResetOtpHash = hashOtp(otp);
  user.passwordResetOtpExpires = otpExpiryDate();
}

const passwordResetFields =
  '+passwordResetOtpHash +passwordResetOtpExpires +passwordResetAttempts ' +
  '+passwordResetTokenHash +passwordResetTokenExpires';

const GENERIC_RESET_REQUEST_MESSAGE = 'Nếu email tồn tại, mã đặt lại mật khẩu đã được gửi.';
const INVALID_RESET_OTP_MESSAGE = 'Mã đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.';
const INVALID_RESET_TOKEN_MESSAGE = 'Yêu cầu đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.';

function clearPasswordChangeState(user) {
  user.passwordChangeOtpHash = null;
  user.passwordChangeOtpExpires = null;
  user.passwordChangeAttempts = 0;
  user.passwordChangePendingPasswordHash = null;
}

function attachPasswordChangeOtp(user, otp) {
  user.passwordChangeOtpHash = hashOtp(otp);
  user.passwordChangeOtpExpires = otpExpiryDate();
  user.passwordChangeAttempts = 0;
}

const passwordChangeFields =
  '+passwordChangeOtpHash +passwordChangeOtpExpires +passwordChangeAttempts ' +
  '+passwordChangePendingPasswordHash';
const INVALID_PASSWORD_CHANGE_OTP_MESSAGE = 'Mã xác minh đổi mật khẩu không hợp lệ hoặc đã hết hạn.';

/**
 * POST /api/auth/signup
 * Body: { email, phoneNumber, companyId, password }
 * Creates (or refreshes) an unverified account and emails a 6-digit OTP.
 */
export async function signup(req, res, next) {
  try {
    const email = normalizeEmail(req.body.email);
    const phoneNumber = normalizePhone(req.body.phoneNumber);
    const companyId = String(req.body.companyId).trim();
    const { password } = req.body;

    const existing = await User.findOne({ email });
    const phoneOwner = await User.findOne({ phoneNumber });

    if (existing?.isVerified) {
      return res.status(409).json({ success: false, message: 'Email đã được đăng ký và xác thực.' });
    }

    if (phoneOwner && (!existing || !phoneOwner._id.equals(existing._id))) {
      return res.status(409).json({ success: false, message: 'Số điện thoại đã được đăng ký.' });
    }

    // Hash password with an explicit salt after conflict checks.
    const salt = await bcryptjs.genSalt(SALT_ROUNDS);
    const passwordHash = await bcryptjs.hash(password, salt);
    const otp = generateOtp();

    if (existing) {
      // Account was created before but never verified -> update it and re-send OTP
      existing.phoneNumber = phoneNumber;
      existing.companyId = companyId;
      existing.password = passwordHash;
      attachOtp(existing, otp);
      await existing.save();
    } else {
      const user = new User({ email, phoneNumber, companyId, password: passwordHash });
      attachOtp(user, otp);
      await user.save();
    }

    await sendOtpEmail(email, otp);

    return res.status(200).json({
      success: true,
      message: 'Mã OTP đã được gửi đến email của bạn. Vui lòng xác thực để hoàn tất đăng ký.',
    });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/verify-email-otp
 * Body: { email, otp }
 * Verifies the OTP -> marks the account verified -> returns a JWT.
 */
export async function verifyEmailOtp(req, res, next) {
  try {
    const email = normalizeEmail(req.body.email);
    const otp = String(req.body.otp).trim();

    const user = await User.findOne({ email }).select(
      '+verificationOtpHash +verificationOtpExpires +verificationAttempts'
    );

    if (!user) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy tài khoản với email này.' });
    }

    if (user.isVerified) {
      const token = signAuthToken(user);
      return res.status(200).json({
        success: true,
        message: 'Email đã được xác thực trước đó.',
        token,
        user: user.toPublicJSON(),
      });
    }

    if (!user.verificationOtpHash || !user.verificationOtpExpires) {
      return res.status(400).json({ success: false, message: 'Chưa có mã OTP nào được yêu cầu. Vui lòng gửi lại OTP.' });
    }

    if (user.verificationOtpExpires.getTime() < Date.now()) {
      return res.status(400).json({ success: false, message: 'Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.' });
    }

    if (user.verificationAttempts >= MAX_OTP_ATTEMPTS) {
      return res.status(429).json({ success: false, message: 'Bạn đã nhập sai quá nhiều lần. Vui lòng yêu cầu mã OTP mới.' });
    }

    if (!verifyOtpHash(otp, user.verificationOtpHash)) {
      user.verificationAttempts += 1;
      await user.save();
      const remaining = MAX_OTP_ATTEMPTS - user.verificationAttempts;
      return res.status(400).json({
        success: false,
        message: `Mã OTP không chính xác. Số lần còn lại: ${remaining}.`,
      });
    }

    // OTP valid -> activate the account
    user.isVerified = true;
    user.verificationOtpHash = null;
    user.verificationOtpExpires = null;
    user.verificationAttempts = 0;
    await user.save();

    const token = signAuthToken(user);

    return res.status(200).json({
      success: true,
      message: 'Xác thực email thành công.',
      token,
      user: user.toPublicJSON(),
    });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/resend-otp
 * Body: { email }
 */
export async function resendOtp(req, res, next) {
  try {
    const email = normalizeEmail(req.body.email);
    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy tài khoản với email này.' });
    }
    if (user.isVerified) {
      return res.status(400).json({ success: false, message: 'Tài khoản đã được xác thực.' });
    }

    const otp = generateOtp();
    attachOtp(user, otp);
    await user.save();

    await sendOtpEmail(email, otp);

    return res.status(200).json({ success: true, message: 'Mã OTP mới đã được gửi đến email của bạn.' });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/login
 * Body: { identifier, password }
 * Identifier can be an email or phone number. Requires a verified email and returns a JWT.
 */
export async function login(req, res, next) {
  try {
    const identifier = String(req.body.identifier).trim();
    const { password } = req.body;
    const isEmailIdentifier = identifier.includes('@');
    const lookup = isEmailIdentifier
      ? { email: normalizeEmail(identifier) }
      : { phoneNumber: normalizePhone(identifier) };

    const user = await User.findOne(lookup).select('+password');
    if (!user) {
      return res.status(401).json({ success: false, message: 'Thông tin đăng nhập hoặc mật khẩu không đúng.' });
    }

    const ok = await bcryptjs.compare(password, user.password);
    if (!ok) {
      return res.status(401).json({ success: false, message: 'Thông tin đăng nhập hoặc mật khẩu không đúng.' });
    }

    if (!user.isVerified) {
      return res.status(403).json({ success: false, message: 'Tài khoản chưa được xác thực email. Vui lòng nhập OTP trước.' });
    }

    const token = signAuthToken(user);
    return res.status(200).json({ success: true, message: 'Đăng nhập thành công.', token, user: user.toPublicJSON() });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/password-reset/request
 * Body: { email }
 * Always returns the same response to avoid revealing whether an account exists.
 */
export async function requestPasswordReset(req, res, next) {
  try {
    const email = normalizeEmail(req.body.email);
    const user = await User.findOne({ email });

    if (user?.isVerified) {
      const otp = generateOtp();
      attachPasswordResetOtp(user, otp);
      await user.save();
      await sendPasswordResetOtpEmail(email, otp);
    }

    return res.status(200).json({ success: true, message: GENERIC_RESET_REQUEST_MESSAGE });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/password-reset/resend
 * Body: { email }
 * Immediately rotates the reset code to match the Android recovery UI.
 */
export async function resendPasswordReset(req, res, next) {
  return requestPasswordReset(req, res, next);
}

/**
 * POST /api/auth/password-reset/verify
 * Body: { email, otp }
 * Exchanges a valid six-digit reset OTP for a short-lived, one-use reset token.
 */
export async function verifyPasswordResetOtp(req, res, next) {
  try {
    const email = normalizeEmail(req.body.email);
    const otp = String(req.body.otp).trim();
    const user = await User.findOne({ email }).select(passwordResetFields);

    if (!user?.isVerified || !user.passwordResetOtpHash || !user.passwordResetOtpExpires) {
      return res.status(400).json({ success: false, message: INVALID_RESET_OTP_MESSAGE });
    }

    if (user.passwordResetOtpExpires.getTime() < Date.now()) {
      return res.status(400).json({ success: false, message: INVALID_RESET_OTP_MESSAGE });
    }

    if (user.passwordResetAttempts >= MAX_OTP_ATTEMPTS) {
      return res.status(429).json({
        success: false,
        message: 'Bạn đã nhập sai quá nhiều lần. Vui lòng yêu cầu mã OTP mới.',
      });
    }

    if (!verifyOtpHash(otp, user.passwordResetOtpHash)) {
      user.passwordResetAttempts += 1;
      await user.save();
      return res.status(400).json({ success: false, message: INVALID_RESET_OTP_MESSAGE });
    }

    const resetToken = generatePasswordResetToken();
    user.passwordResetOtpHash = null;
    user.passwordResetOtpExpires = null;
    user.passwordResetAttempts = 0;
    user.passwordResetTokenHash = hashPasswordResetToken(resetToken);
    user.passwordResetTokenExpires = passwordResetTokenExpiryDate();
    await user.save();

    return res.status(200).json({
      success: true,
      message: 'Mã xác thực hợp lệ. Vui lòng đặt mật khẩu mới.',
      resetToken,
      expiresAt: user.passwordResetTokenExpires,
    });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/password-reset/complete
 * Body: { resetToken, newPassword, confirmPassword }
 * Sets a new password but intentionally does not issue an authenticated session.
 */
export async function completePasswordReset(req, res, next) {
  try {
    const { resetToken, newPassword } = req.body;
    const passwordResetTokenHash = hashPasswordResetToken(resetToken);
    const user = await User.findOne({ passwordResetTokenHash }).select(passwordResetFields);

    if (!user?.passwordResetTokenExpires || user.passwordResetTokenExpires.getTime() < Date.now()) {
      return res.status(400).json({ success: false, message: INVALID_RESET_TOKEN_MESSAGE });
    }

    const salt = await bcryptjs.genSalt(SALT_ROUNDS);
    user.password = await bcryptjs.hash(newPassword, salt);
    clearPasswordResetState(user);
    await user.save();

    return res.status(200).json({
      success: true,
      message: 'Đặt lại mật khẩu thành công. Vui lòng đăng nhập bằng mật khẩu mới.',
    });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/password-change/request
 * Body: { currentPassword, newPassword, confirmPassword }
 * Requires a Bearer JWT and creates an email OTP challenge before committing the new password.
 */
export async function requestPasswordChange(req, res, next) {
  try {
    const { currentPassword, newPassword } = req.body;
    const user = await User.findById(req.userId).select(`+password ${passwordChangeFields}`);

    if (!user) {
      return res.status(401).json({ success: false, message: 'Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.' });
    }

    const isCurrentPasswordValid = await bcryptjs.compare(currentPassword, user.password);
    if (!isCurrentPasswordValid) {
      return res.status(401).json({ success: false, message: 'Mật khẩu hiện tại không chính xác.' });
    }

    const isSamePassword = await bcryptjs.compare(newPassword, user.password);
    if (isSamePassword) {
      return res.status(400).json({ success: false, message: 'Mật khẩu mới phải khác mật khẩu hiện tại.' });
    }

    const salt = await bcryptjs.genSalt(SALT_ROUNDS);
    const otp = generateOtp();
    clearPasswordChangeState(user);
    user.passwordChangePendingPasswordHash = await bcryptjs.hash(newPassword, salt);
    attachPasswordChangeOtp(user, otp);
    await user.save();
    await sendPasswordChangeOtpEmail(user.email, otp);

    return res.status(200).json({
      success: true,
      message: 'Mã xác minh đổi mật khẩu đã được gửi đến email của bạn.',
      recipientEmail: user.email,
      expiresAt: user.passwordChangeOtpExpires,
    });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/password-change/resend
 * Rotates the OTP for the authenticated user's existing password-change request.
 */
export async function resendPasswordChange(req, res, next) {
  try {
    const user = await User.findById(req.userId).select(passwordChangeFields);

    if (!user?.passwordChangePendingPasswordHash) {
      return res.status(400).json({
        success: false,
        message: 'Không có yêu cầu đổi mật khẩu đang chờ xác minh. Vui lòng nhập lại thông tin đổi mật khẩu.',
      });
    }

    const otp = generateOtp();
    attachPasswordChangeOtp(user, otp);
    await user.save();
    await sendPasswordChangeOtpEmail(user.email, otp);

    return res.status(200).json({
      success: true,
      message: 'Mã xác minh đổi mật khẩu mới đã được gửi đến email của bạn.',
      recipientEmail: user.email,
      expiresAt: user.passwordChangeOtpExpires,
    });
  } catch (err) {
    return next(err);
  }
}

/**
 * POST /api/auth/password-change/verify
 * Body: { otp }
 * Applies the staged password only after a valid OTP for the authenticated account.
 */
export async function verifyPasswordChange(req, res, next) {
  try {
    const otp = String(req.body.otp).trim();
    const user = await User.findById(req.userId).select(passwordChangeFields);

    if (!user?.passwordChangePendingPasswordHash || !user.passwordChangeOtpHash || !user.passwordChangeOtpExpires) {
      return res.status(400).json({ success: false, message: INVALID_PASSWORD_CHANGE_OTP_MESSAGE });
    }

    if (user.passwordChangeOtpExpires.getTime() < Date.now()) {
      return res.status(400).json({ success: false, message: INVALID_PASSWORD_CHANGE_OTP_MESSAGE });
    }

    if (user.passwordChangeAttempts >= MAX_OTP_ATTEMPTS) {
      return res.status(429).json({
        success: false,
        message: 'Bạn đã nhập sai quá nhiều lần. Vui lòng yêu cầu mã OTP mới.',
      });
    }

    if (!verifyOtpHash(otp, user.passwordChangeOtpHash)) {
      user.passwordChangeAttempts += 1;
      await user.save();
      return res.status(400).json({ success: false, message: INVALID_PASSWORD_CHANGE_OTP_MESSAGE });
    }

    const consumed = await User.findOneAndUpdate(
      {
        _id: req.userId,
        passwordChangeOtpHash: user.passwordChangeOtpHash,
        passwordChangePendingPasswordHash: user.passwordChangePendingPasswordHash,
        passwordChangeOtpExpires: { $gte: new Date() },
      },
      {
        $set: {
          password: user.passwordChangePendingPasswordHash,
          passwordChangeOtpHash: null,
          passwordChangeOtpExpires: null,
          passwordChangeAttempts: 0,
          passwordChangePendingPasswordHash: null,
        },
      },
      { new: true }
    );

    if (!consumed) {
      return res.status(400).json({ success: false, message: INVALID_PASSWORD_CHANGE_OTP_MESSAGE });
    }

    return res.status(200).json({ success: true, message: 'Đổi mật khẩu thành công.' });
  } catch (err) {
    return next(err);
  }
}

const normalizeProfileText = (value) => String(value).normalize('NFC').trim().replace(/\s+/g, ' ');

/** PATCH /api/auth/me — updates the authenticated user's Personal Data fields only. */
export async function updateMyProfile(req, res, next) {
  try {
    const user = await User.findById(req.userId);
    if (!user) return res.status(404).json({ success: false, message: 'Không tìm thấy người dùng.' });

    const { firstName, lastName, dateOfBirth, position, address, avatarUrl } = req.body;
    user.firstName = normalizeProfileText(firstName);
    user.lastName = normalizeProfileText(lastName);
    // Date-only values are stored at UTC midnight to avoid a locale/timezone shift in MongoDB.
    user.dateOfBirth = new Date(`${dateOfBirth}T00:00:00.000Z`);
    user.position = normalizeProfileText(position);
    user.address = {
      country: normalizeProfileText(address.country),
      state: normalizeProfileText(address.state),
      city: normalizeProfileText(address.city),
      fullAddress: normalizeProfileText(address.fullAddress),
    };
    if (Object.hasOwn(req.body, 'avatarUrl')) {
      user.avatarUrl = avatarUrl === null ? null : normalizeProfileText(avatarUrl);
    }
    await user.save();

    return res.status(200).json({
      success: true,
      message: 'Cập nhật thông tin cá nhân thành công.',
      user: user.toPublicJSON(),
    });
  } catch (err) {
    return next(err);
  }
}

/** GET /api/auth/me — requires Bearer token */
export async function me(req, res, next) {
  try {
    const user = await User.findById(req.userId);
    if (!user) return res.status(404).json({ success: false, message: 'Không tìm thấy người dùng.' });
    return res.status(200).json({ success: true, user: user.toPublicJSON() });
  } catch (err) {
    return next(err);
  }
}
