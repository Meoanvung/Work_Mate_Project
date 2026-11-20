import { Router } from 'express';
import { body } from 'express-validator';
import { signup, verifyEmailOtp, resendOtp, login, me } from '../controllers/authController.js';
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

// Public routes
router.post('/signup', [emailRule, phoneRule, companyIdRule, passwordRule, validate], signup);
router.post('/verify-email-otp', [emailRule, otpRule, validate], verifyEmailOtp);
router.post('/resend-otp', [emailRule, validate], resendOtp);
router.post('/login', [identifierRule, loginPasswordRule, validate], login);

// Protected route (JWT)
router.get('/me', authenticate, me);

export default router;
