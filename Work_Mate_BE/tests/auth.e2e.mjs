/**
 * E2E smoke test for the signup flow (in-memory MongoDB, no real email sent):
 *   signup -> wrong OTP -> correct OTP -> /me with JWT -> login -> duplicate signup
 * Run with: npm test
 */
import { MongoMemoryServer } from 'mongodb-memory-server';

process.env.NODE_ENV = 'test';
process.env.JWT_SECRET = 'e2e_test_secret_0123456789abcdef0123456789';
process.env.JWT_EXPIRES_IN = '1h';
process.env.OTP_TTL_MINUTES = '10';
process.env.EMAIL_PROVIDER = 'console'; // OTP is stashed on globalThis.__lastOtp

const mongod = await MongoMemoryServer.create();
process.env.MONGODB_URI = mongod.getUri('work_mate_be_test');

const { default: app } = await import('../src/app.js');
const { connectDB, disconnectDB } = await import('../src/config/db.js');

await connectDB();

const server = app.listen(0);
const { port } = server.address();
const base = `http://127.0.0.1:${port}`;

let failures = 0;
const check = (name, cond) => {
  const ok = Boolean(cond);
  console.log(`${ok ? 'PASS' : 'FAIL'}  ${name}`);
  if (!ok) failures += 1;
};

const post = (path, body) =>
  fetch(base + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  }).then(async (r) => ({ status: r.status, body: await r.json() }));

const postAuthorized = (path, token, body = {}) =>
  fetch(base + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
    body: JSON.stringify(body),
  }).then(async (r) => ({ status: r.status, body: await r.json() }));

const patchAuthorized = (path, token, body) =>
  fetch(base + path, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
    body: JSON.stringify(body),
  }).then(async (r) => ({ status: r.status, body: await r.json() }));

try {
  // 1. Signup
  const signup = await post('/api/auth/signup', {
    email: 'Test@Example.com',
    phoneNumber: '+84 901 234 567',
    companyId: 'COMP-001',
    password: 'Password123',
  });
  check('signup returns 200', signup.status === 200 && signup.body.success);
  check('signup does NOT leak the OTP in the response', !JSON.stringify(signup.body).match(/\b\d{6}\b/));

  const otp = globalThis.__lastOtp;
  check('OTP was "sent" (console mode)', typeof otp === 'string' && otp.length === 6);

  // 2. Duplicate verified account is blocked (not yet verified -> should succeed again instead)
  const reSignup = await post('/api/auth/signup', {
    email: 'test@example.com',
    phoneNumber: '+84901234567',
    companyId: 'COMP-001',
    password: 'Password123',
  });
  check('re-signup before verification refreshes OTP (200)', reSignup.status === 200 && reSignup.body.success);
  const freshOtp = globalThis.__lastOtp;

  // 3. Wrong OTP
  const wrong = await post('/api/auth/verify-email-otp', { email: 'test@example.com', otp: freshOtp === '000000' ? '000001' : '000000' });
  check('wrong OTP rejected (400)', wrong.status === 400 && wrong.body.success === false);

  // 4. Unverified users cannot log in, including by phone number.
  const unverifiedPhoneLogin = await post('/api/auth/login', { identifier: '+84 901 234 567', password: 'Password123' });
  check('unverified phone login rejected (403)', unverifiedPhoneLogin.status === 403);

  // 5. Correct OTP (the latest one)
  const verified = await post('/api/auth/verify-email-otp', { email: 'test@example.com', otp: freshOtp });
  check('correct OTP verifies (200)', verified.status === 200 && verified.body.success);
  check('verify returns JWT', typeof verified.body.token === 'string' && verified.body.token.split('.').length === 3);

  const token = verified.body.token;

  // 6. Password is stored hashed — check via /me and both identifier types.
  const me = await fetch(`${base}/api/auth/me`, { headers: { Authorization: `Bearer ${token}` } }).then(async (r) => ({
    status: r.status,
    body: await r.json(),
  }));
  check(
    '/me with JWT returns user and empty profile fields',
    me.status === 200 && me.body.user?.email === 'test@example.com' && me.body.user?.firstName === null && me.body.user?.address?.country === null
  );

  const noAuthProfileUpdate = await fetch(`${base}/api/auth/me`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({}),
  }).then(async (r) => ({ status: r.status, body: await r.json() }));
  check('profile update requires Bearer JWT (401)', noAuthProfileUpdate.status === 401);

  const profilePayload = {
    firstName: '  Tonald  ',
    lastName: ' Drump ',
    dateOfBirth: '1997-12-10',
    position: ' Senior   Full Stack Developer ',
    address: {
      country: ' Indonesia ',
      state: ' DKI Jakarta ',
      city: ' Jakarta Selatan ',
      fullAddress: ' Jl Mampang Prapatan XIV No 7A, Jakarta Selatan 12790 ',
    },
    avatarUrl: 'https://cdn.example.com/avatars/test.jpg',
  };
  const profileUpdate = await patchAuthorized('/api/auth/me', token, profilePayload);
  check(
    'profile update persists all Personal Data fields',
    profileUpdate.status === 200 &&
      profileUpdate.body.user?.firstName === 'Tonald' &&
      profileUpdate.body.user?.position === 'Senior Full Stack Developer' &&
      profileUpdate.body.user?.dateOfBirth === '1997-12-10' &&
      profileUpdate.body.user?.address?.fullAddress === 'Jl Mampang Prapatan XIV No 7A, Jakarta Selatan 12790' &&
      profileUpdate.body.user?.avatarUrl === profilePayload.avatarUrl
  );

  const persistedProfile = await fetch(`${base}/api/auth/me`, { headers: { Authorization: `Bearer ${token}` } }).then(async (r) => ({
    status: r.status,
    body: await r.json(),
  }));
  check(
    'profile data is returned by later GET /me',
    persistedProfile.status === 200 && persistedProfile.body.user?.dateOfBirth === '1997-12-10' && persistedProfile.body.user?.address?.city === 'Jakarta Selatan'
  );

  const profileUpdateWithoutAvatar = { ...profilePayload };
  delete profileUpdateWithoutAvatar.avatarUrl;
  profileUpdateWithoutAvatar.position = 'Backend Engineer';
  const profileWithoutAvatar = await patchAuthorized('/api/auth/me', token, profileUpdateWithoutAvatar);
  check(
    'profile update preserves avatar when avatarUrl is omitted',
    profileWithoutAvatar.status === 200 &&
      profileWithoutAvatar.body.user?.position === 'Backend Engineer' &&
      profileWithoutAvatar.body.user?.avatarUrl === profilePayload.avatarUrl
  );

  const invalidProfileDate = await patchAuthorized('/api/auth/me', token, { ...profilePayload, dateOfBirth: '2030-01-01' });
  check('future date of birth is rejected (400)', invalidProfileDate.status === 400 && Array.isArray(invalidProfileDate.body.errors));

  const invalidProfileAvatar = await patchAuthorized('/api/auth/me', token, { ...profilePayload, avatarUrl: 'content://device/avatar.jpg' });
  check('device-local avatar URL is rejected (400)', invalidProfileAvatar.status === 400 && Array.isArray(invalidProfileAvatar.body.errors));

  const protectedProfileField = await patchAuthorized('/api/auth/me', token, { ...profilePayload, email: 'attacker@example.com' });
  check('profile mass-assignment fields are rejected (400)', protectedProfileField.status === 400 && Array.isArray(protectedProfileField.body.errors));

  const afterProtectedFieldAttempt = await fetch(`${base}/api/auth/me`, { headers: { Authorization: `Bearer ${token}` } }).then(async (r) => ({
    status: r.status,
    body: await r.json(),
  }));
  check('protected account identity remains unchanged', afterProtectedFieldAttempt.body.user?.email === 'test@example.com' && afterProtectedFieldAttempt.body.user?.companyId === 'COMP-001');

  const emailLogin = await post('/api/auth/login', { identifier: 'TEST@EXAMPLE.COM', password: 'Password123' });
  check('login by email identifier (200)', emailLogin.status === 200 && typeof emailLogin.body.token === 'string');

  const phoneLogin = await post('/api/auth/login', { identifier: '+84 (901) 234-567', password: 'Password123' });
  check('login by normalized phone identifier (200)', phoneLogin.status === 200 && typeof phoneLogin.body.token === 'string');

  const badLogin = await post('/api/auth/login', { identifier: 'test@example.com', password: 'wrongPass1' });
  check('login with wrong password rejected (401)', badLogin.status === 401 && badLogin.body.message === 'Thông tin đăng nhập hoặc mật khẩu không đúng.');

  const unknownLogin = await post('/api/auth/login', { identifier: 'unknown@example.com', password: 'Password123' });
  check('unknown identifier returns generic 401', unknownLogin.status === 401 && unknownLogin.body.message === badLogin.body.message);

  const invalidLogin = await post('/api/auth/login', { identifier: 'not-an-identifier', password: 'Password123' });
  check('invalid identifier rejected (400)', invalidLogin.status === 400 && Array.isArray(invalidLogin.body.errors));

  const legacyLogin = await post('/api/auth/login', { email: 'test@example.com', password: 'Password123' });
  check('legacy email login payload rejected (400)', legacyLogin.status === 400 && Array.isArray(legacyLogin.body.errors));

  // 7. Password reset: request, resend, verify OTP, set a new password, then sign in again.
  const resetRequest = await post('/api/auth/password-reset/request', { email: 'test@example.com' });
  check(
    'password reset request is generic and does not leak OTP',
    resetRequest.status === 200 && resetRequest.body.message === 'Nếu email tồn tại, mã đặt lại mật khẩu đã được gửi.' && !JSON.stringify(resetRequest.body).match(/\b\d{6}\b/)
  );
  const firstResetOtp = globalThis.__lastPasswordResetOtp;
  check('password reset OTP was sent in console mode', typeof firstResetOtp === 'string' && firstResetOtp.length === 6);

  const unknownResetRequest = await post('/api/auth/password-reset/request', { email: 'unknown@example.com' });
  check(
    'unknown reset email receives the same generic response',
    unknownResetRequest.status === 200 && unknownResetRequest.body.message === resetRequest.body.message
  );

  const resetResend = await post('/api/auth/password-reset/resend', { email: 'test@example.com' });
  const latestResetOtp = globalThis.__lastPasswordResetOtp;
  check('password reset resend succeeds', resetResend.status === 200 && resetResend.body.message === resetRequest.body.message);

  const oldResetOtp = await post('/api/auth/password-reset/verify', { email: 'test@example.com', otp: firstResetOtp });
  check('resend invalidates prior reset OTP', oldResetOtp.status === 400);

  const resetVerify = await post('/api/auth/password-reset/verify', { email: 'test@example.com', otp: latestResetOtp });
  check(
    'reset OTP verification returns one-use reset token, not auth JWT',
    resetVerify.status === 200 && typeof resetVerify.body.resetToken === 'string' && !resetVerify.body.token && !resetVerify.body.user
  );

  const resetMismatch = await post('/api/auth/password-reset/complete', {
    resetToken: resetVerify.body.resetToken,
    newPassword: 'NewPassword123',
    confirmPassword: 'DifferentPassword123',
  });
  check('mismatched new passwords rejected (400)', resetMismatch.status === 400 && Array.isArray(resetMismatch.body.errors));

  const resetComplete = await post('/api/auth/password-reset/complete', {
    resetToken: resetVerify.body.resetToken,
    newPassword: 'NewPassword123',
    confirmPassword: 'NewPassword123',
  });
  check('password reset completes without auth JWT', resetComplete.status === 200 && !resetComplete.body.token && !resetComplete.body.user);

  const reusedResetToken = await post('/api/auth/password-reset/complete', {
    resetToken: resetVerify.body.resetToken,
    newPassword: 'AnotherPassword123',
    confirmPassword: 'AnotherPassword123',
  });
  check('used reset token is rejected (400)', reusedResetToken.status === 400);

  const oldPasswordLogin = await post('/api/auth/login', { identifier: 'test@example.com', password: 'Password123' });
  check('old password fails after reset (401)', oldPasswordLogin.status === 401);

  const newPasswordLogin = await post('/api/auth/login', { identifier: 'test@example.com', password: 'NewPassword123' });
  check('new password can sign in after reset (200)', newPasswordLogin.status === 200 && typeof newPasswordLogin.body.token === 'string');

  // 8. Authenticated password change: form -> OTP/resend -> successful update.
  const noAuthChangeRequest = await post('/api/auth/password-change/request', {
    currentPassword: 'NewPassword123',
    newPassword: 'ChangedPassword123',
    confirmPassword: 'ChangedPassword123',
  });
  check('password change requires Bearer JWT (401)', noAuthChangeRequest.status === 401);

  const changeToken = newPasswordLogin.body.token;
  const wrongCurrentPassword = await postAuthorized('/api/auth/password-change/request', changeToken, {
    currentPassword: 'WrongPassword123',
    newPassword: 'ChangedPassword123',
    confirmPassword: 'ChangedPassword123',
  });
  check('wrong current password rejected (401)', wrongCurrentPassword.status === 401);

  const mismatchedChangePassword = await postAuthorized('/api/auth/password-change/request', changeToken, {
    currentPassword: 'NewPassword123',
    newPassword: 'ChangedPassword123',
    confirmPassword: 'DifferentPassword123',
  });
  check('mismatched change passwords rejected (400)', mismatchedChangePassword.status === 400 && Array.isArray(mismatchedChangePassword.body.errors));

  const sameChangePassword = await postAuthorized('/api/auth/password-change/request', changeToken, {
    currentPassword: 'NewPassword123',
    newPassword: 'NewPassword123',
    confirmPassword: 'NewPassword123',
  });
  check('new password matching current password rejected (400)', sameChangePassword.status === 400);

  const changeRequest = await postAuthorized('/api/auth/password-change/request', changeToken, {
    currentPassword: 'NewPassword123',
    newPassword: 'ChangedPassword123',
    confirmPassword: 'ChangedPassword123',
  });
  check(
    'password change request returns recipient and does not leak OTP',
    changeRequest.status === 200 && changeRequest.body.recipientEmail === 'test@example.com' && !JSON.stringify(changeRequest.body).match(/\b\d{6}\b/)
  );
  const firstChangeOtp = globalThis.__lastPasswordChangeOtp;
  check('password change OTP was sent in console mode', typeof firstChangeOtp === 'string' && firstChangeOtp.length === 6);

  const changeResend = await postAuthorized('/api/auth/password-change/resend', changeToken);
  const latestChangeOtp = globalThis.__lastPasswordChangeOtp;
  check('password change resend succeeds', changeResend.status === 200 && changeResend.body.recipientEmail === 'test@example.com');

  const oldChangeOtp = await postAuthorized('/api/auth/password-change/verify', changeToken, { otp: firstChangeOtp });
  check('password change resend invalidates prior OTP', oldChangeOtp.status === 400);

  const invalidChangeOtp = latestChangeOtp === '000000' ? '000001' : '000000';
  for (let attempt = 1; attempt <= 4; attempt += 1) {
    const invalidChangeAttempt = await postAuthorized('/api/auth/password-change/verify', changeToken, { otp: invalidChangeOtp });
    check(`invalid password change OTP attempt ${attempt + 1} is rejected (400)`, invalidChangeAttempt.status === 400);
  }
  const changeOtpAttemptCap = await postAuthorized('/api/auth/password-change/verify', changeToken, { otp: invalidChangeOtp });
  check('password change OTP attempt cap is enforced (429)', changeOtpAttemptCap.status === 429);

  const changeResendAfterAttemptCap = await postAuthorized('/api/auth/password-change/resend', changeToken);
  const finalChangeOtp = globalThis.__lastPasswordChangeOtp;
  check('password change resend resets the OTP attempt cap', changeResendAfterAttemptCap.status === 200 && typeof finalChangeOtp === 'string' && finalChangeOtp.length === 6);

  const changeVerify = await postAuthorized('/api/auth/password-change/verify', changeToken, { otp: finalChangeOtp });
  check('password change verifies OTP without issuing JWT', changeVerify.status === 200 && !changeVerify.body.token && !changeVerify.body.user);

  const reusedChangeOtp = await postAuthorized('/api/auth/password-change/verify', changeToken, { otp: finalChangeOtp });
  check('used password change OTP is rejected (400)', reusedChangeOtp.status === 400);

  const afterChangeOldPasswordLogin = await post('/api/auth/login', { identifier: 'test@example.com', password: 'NewPassword123' });
  check('pre-change password fails after password change (401)', afterChangeOldPasswordLogin.status === 401);

  const afterChangeNewPasswordLogin = await post('/api/auth/login', { identifier: 'test@example.com', password: 'ChangedPassword123' });
  check('changed password can sign in (200)', afterChangeNewPasswordLogin.status === 200 && typeof afterChangeNewPasswordLogin.body.token === 'string');

  // 9. Now verified -> duplicate signup blocked
  const dup = await post('/api/auth/signup', {
    email: 'test@example.com',
    phoneNumber: '+84901234567',
    companyId: 'COMP-001',
    password: 'Password123',
  });
  check('signup with already-verified email rejected (409)', dup.status === 409);

  const duplicatePhone = await post('/api/auth/signup', {
    email: 'another@example.com',
    phoneNumber: '+84 901 234 567',
    companyId: 'COMP-002',
    password: 'Password123',
  });
  check('signup with normalized duplicate phone rejected (409)', duplicatePhone.status === 409);

  // 10. Validation still works
  const invalid = await post('/api/auth/signup', { email: 'not-an-email', phoneNumber: 'abc', companyId: '', password: 'short' });
  check('invalid payload rejected (400)', invalid.status === 400 && Array.isArray(invalid.body.errors));
} finally {
  server.close();
  await disconnectDB();
  await mongod.stop();
}

console.log(failures === 0 ? '\nAll checks passed.' : `\n${failures} check(s) failed.`);
process.exit(failures === 0 ? 0 : 1);
