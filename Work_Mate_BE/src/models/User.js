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
  },
  { timestamps: true }
);

userSchema.methods.toPublicJSON = function toPublicJSON() {
  return {
    id: this._id,
    email: this.email,
    phoneNumber: this.phoneNumber,
    companyId: this.companyId,
    isVerified: this.isVerified,
    createdAt: this.createdAt,
  };
};

export const User = mongoose.model('User', userSchema);
