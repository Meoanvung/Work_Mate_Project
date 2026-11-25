import mongoose from 'mongoose';

const userSchema = new mongoose.Schema(
  {
    email: {
      type: String,
      required: [true, 'Email is required'],
      unique: true,
      lowercase: true,
      trim: true,
      index: true,
    },
    phoneNumber: {
      type: String,
      required: [true, 'Phone number is required'],
      trim: true,
      unique: true,
      index: true,
    },
    companyId: {
      type: String,
      required: [true, 'Company ID is required'],
      trim: true,
    },
    // Stored as a salted bcrypt hash, never returned by default queries
    password: {
      type: String,
      required: [true, 'Password is required'],
      select: false,
    },
    isVerified: {
      type: Boolean,
      default: false,
    },
    // Personal Data is intentionally separate from account identifiers and authentication state.
    firstName: {
      type: String,
      trim: true,
      default: null,
    },
    lastName: {
      type: String,
      trim: true,
      default: null,
    },
    // A calendar-only date is saved at UTC midnight and serialized as YYYY-MM-DD.
    dateOfBirth: {
      type: Date,
      default: null,
    },
    position: {
      type: String,
      trim: true,
      default: null,
    },
    address: {
      country: { type: String, trim: true, default: null },
      state: { type: String, trim: true, default: null },
      city: { type: String, trim: true, default: null },
      fullAddress: { type: String, trim: true, default: null },
    },
    // This endpoint accepts only an HTTPS delivery URL; binary uploads use a separate future flow.
    avatarUrl: {
      type: String,
      trim: true,
      default: null,
    },
    // Email OTP verification data (HMAC-hashed OTP + expiry + brute-force guard)
    verificationOtpHash: {
      type: String,
      select: false,
      default: null,
    },
    verificationOtpExpires: {
      type: Date,
      select: false,
      default: null,
    },
    verificationAttempts: {
      type: Number,
      select: false,
      default: 0,
    },
    // Password reset state is intentionally separate from signup verification OTP state.
    passwordResetOtpHash: {
      type: String,
      select: false,
      default: null,
    },
    passwordResetOtpExpires: {
      type: Date,
      select: false,
      default: null,
    },
    passwordResetAttempts: {
      type: Number,
      select: false,
      default: 0,
    },
    passwordResetTokenHash: {
      type: String,
      select: false,
      default: null,
    },
    passwordResetTokenExpires: {
      type: Date,
      select: false,
      default: null,
    },
    // Authenticated password-change state is independent from signup and password reset flows.
    passwordChangeOtpHash: {
      type: String,
      select: false,
      default: null,
    },
    passwordChangeOtpExpires: {
      type: Date,
      select: false,
      default: null,
    },
    passwordChangeAttempts: {
      type: Number,
      select: false,
      default: 0,
    },
    passwordChangePendingPasswordHash: {
      type: String,
      select: false,
      default: null,
    },
  },
  { timestamps: true }
);

function formatCalendarDate(value) {
  if (!value) return null;

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date.toISOString().slice(0, 10);
}

userSchema.methods.toPublicJSON = function toPublicJSON() {
  return {
    id: this._id,
    email: this.email,
    phoneNumber: this.phoneNumber,
    companyId: this.companyId,
    isVerified: this.isVerified,
    firstName: this.firstName,
    lastName: this.lastName,
    dateOfBirth: formatCalendarDate(this.dateOfBirth),
    position: this.position,
    address: {
      country: this.address?.country ?? null,
      state: this.address?.state ?? null,
      city: this.address?.city ?? null,
      fullAddress: this.address?.fullAddress ?? null,
    },
    avatarUrl: this.avatarUrl,
    createdAt: this.createdAt,
  };
};

export const User = mongoose.model('User', userSchema);
