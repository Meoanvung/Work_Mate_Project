import { Router } from 'express';
import { body } from 'express-validator';
import {
  signup,
  verifyEmailOtp,
  resendOtp,
  login,
  requestPasswordReset,
  resendPasswordReset,
  verifyPasswordResetOtp,
  completePasswordReset,
  requestPasswordChange,
  resendPasswordChange,
  verifyPasswordChange,
  me,
} from '../controllers/authController.js';
import { validate } from '../middleware/validate.js';
import { authenticate } from '../middleware/authenticate.js';

const router = Router();
const PHONE_PATTERN = /^\+?[0-9\s().-]{9,20}$/;

const emailRule = body('email')
  .trim()
  .normalizeEmail()
  .isEmail()
  .withMessage('Email không hợp lệ')
  .isLength({ max: 254 })
  .withMessage('Email quá dài');

const phoneRule = body('phoneNumber')
  .trim()
  .matches(PHONE_PATTERN)
  .withMessage('Số điện thoại không hợp lệ (ví dụ: +84901234567)');

const companyIdRule = body('companyId')
  .trim()
  .notEmpty()
  .withMessage('Company ID không được để trống')
  .isLength({ min: 2, max: 64 })
  .withMessage('Company ID phải từ 2 đến 64 ký tự');

const passwordRule = body('password')
  .isLength({ min: 8, max: 72 })
  .withMessage('Mật khẩu phải có ít nhất 8 ký tự')
  .matches(/[A-Za-z]/)
  .withMessage('Mật khẩu phải chứa ít nhất một chữ cái')
  .matches(/[0-9]/)
  .withMessage('Mật khẩu phải chứa ít nhất một chữ số');

const otpRule = body('otp')
  .trim()
  .matches(/^\d{6}$/)
  .withMessage('OTP phải gồm đúng 6 chữ số');

const identifierRule = body('identifier')
  .isString()
  .withMessage('Email hoặc số điện thoại không hợp lệ')
  .bail()
  .trim()
  .notEmpty()
  .withMessage('Email hoặc số điện thoại không được để trống')
  .bail()
  .custom((identifier) => {
    const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(identifier) && identifier.length <= 254;
    const isPhone = PHONE_PATTERN.test(identifier);

    if (!isEmail && !isPhone) {
      throw new Error('Email hoặc số điện thoại không hợp lệ');
    }

    return true;
  });

const loginPasswordRule = body('password')
  .isString()
  .withMessage('Mật khẩu không hợp lệ')
  .bail()
  .notEmpty()
  .withMessage('Mật khẩu không được để trống');

const newPasswordRule = body('newPassword')
  .isString()
  .withMessage('Mật khẩu mới không hợp lệ')
  .bail()
  .isLength({ min: 8, max: 72 })
  .withMessage('Mật khẩu mới phải có ít nhất 8 ký tự')
  .matches(/[A-Za-z]/)
  .withMessage('Mật khẩu mới phải chứa ít nhất một chữ cái')
  .matches(/[0-9]/)
  .withMessage('Mật khẩu mới phải chứa ít nhất một chữ số');

const confirmPasswordRule = body('confirmPassword')
  .isString()
  .withMessage('Xác nhận mật khẩu không hợp lệ')
  .bail()
  .custom((confirmPassword, { req }) => {
    if (confirmPassword !== req.body.newPassword) {
      throw new Error('Xác nhận mật khẩu không khớp');
    }
    return true;
  });

const currentPasswordRule = body('currentPassword')
  .isString()
  .withMessage('Mật khẩu hiện tại không hợp lệ')
  .bail()
  .notEmpty()
  .withMessage('Mật khẩu hiện tại không được để trống');

const resetTokenRule = body('resetToken')
  .isString()
  .withMessage('Yêu cầu đặt lại mật khẩu không hợp lệ')
  .bail()
  .trim()
  .isLength({ min: 32, max: 256 })
  .withMessage('Yêu cầu đặt lại mật khẩu không hợp lệ');

// Public routes
router.post('/signup', [emailRule, phoneRule, companyIdRule, passwordRule, validate], signup);
router.post('/verify-email-otp', [emailRule, otpRule, validate], verifyEmailOtp);
router.post('/resend-otp', [emailRule, validate], resendOtp);
router.post('/login', [identifierRule, loginPasswordRule, validate], login);
router.post('/password-reset/request', [emailRule, validate], requestPasswordReset);
router.post('/password-reset/resend', [emailRule, validate], resendPasswordReset);
router.post('/password-reset/verify', [emailRule, otpRule, validate], verifyPasswordResetOtp);
router.post('/password-reset/complete', [resetTokenRule, newPasswordRule, confirmPasswordRule, validate], completePasswordReset);

// Protected routes (JWT)
router.get('/me', authenticate, me);
router.post('/password-change/request', [authenticate, currentPasswordRule, newPasswordRule, confirmPasswordRule, validate], requestPasswordChange);
router.post('/password-change/resend', authenticate, resendPasswordChange);
router.post('/password-change/verify', [authenticate, otpRule, validate], verifyPasswordChange);

export default router;
