import { createHmac, randomInt, timingSafeEqual } from 'crypto';
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

export const MAX_OTP_ATTEMPTS = 5;
