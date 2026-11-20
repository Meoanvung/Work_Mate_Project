import bcryptjs from 'bcryptjs';
import { User } from '../models/User.js';
import { generateOtp, hashOtp, verifyOtpHash, otpExpiryDate, MAX_OTP_ATTEMPTS } from '../utils/otp.js';
import { sendOtpEmail } from '../utils/emailService.js';
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
