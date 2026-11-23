import { createHmac, randomBytes, randomInt, timingSafeEqual } from 'crypto';
import { config } from '../config/index.js';

/** Generate a cryptographically-random 6-digit OTP. */
export function generateOtp() {
  return String(randomInt(0, 1_000_000)).padStart(6, '0');
}

/** HMAC-hash the OTP before storing it — the plain OTP never touches the database. */
export function hashOtp(otp) {
  return createHmac('sha256', config.jwtSecret).update(String(otp)).digest('hex');
}

/** Constant-time comparison of a plain OTP against its stored hash. */
export function verifyOtpHash(otp, storedHash) {
  if (!storedHash) return false;
  const candidate = Buffer.from(hashOtp(otp), 'hex');
  const stored = Buffer.from(String(storedHash), 'hex');
  if (candidate.length !== stored.length) return false;
  return timingSafeEqual(candidate, stored);
}

export function otpExpiryDate() {
  return new Date(Date.now() + config.otpTtlMinutes * 60 * 1000);
}

/** Generate a high-entropy, URL-safe one-use capability for completing a password reset. */
export function generatePasswordResetToken() {
  return randomBytes(32).toString('base64url');
}

/** HMAC-hash a reset capability before persisting it. */
export function hashPasswordResetToken(token) {
  return createHmac('sha256', config.jwtSecret).update(`password-reset:${String(token)}`).digest('hex');
}

/** Expiry for the one-use reset capability issued after OTP verification. */
export function passwordResetTokenExpiryDate() {
  return new Date(Date.now() + config.passwordResetTokenTtlMinutes * 60 * 1000);
}

export const MAX_OTP_ATTEMPTS = 5;
