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

  // 4. Correct OTP (the latest one)
  const verified = await post('/api/auth/verify-email-otp', { email: 'test@example.com', otp: freshOtp });
  check('correct OTP verifies (200)', verified.status === 200 && verified.body.success);
  check('verify returns JWT', typeof verified.body.token === 'string' && verified.body.token.split('.').length === 3);

  const token = verified.body.token;

  // 5. Password is stored hashed — check via /me and login
  const me = await fetch(`${base}/api/auth/me`, { headers: { Authorization: `Bearer ${token}` } }).then(async (r) => ({
    status: r.status,
    body: await r.json(),
  }));
  check('/me with JWT returns the user', me.status === 200 && me.body.user?.email === 'test@example.com');

  const login = await post('/api/auth/login', { email: 'test@example.com', password: 'Password123' });
  check('login with correct password (200)', login.status === 200 && typeof login.body.token === 'string');

  const badLogin = await post('/api/auth/login', { email: 'test@example.com', password: 'wrongPass1' });
  check('login with wrong password rejected (401)', badLogin.status === 401);

  // 6. Now verified -> duplicate signup blocked
  const dup = await post('/api/auth/signup', {
    email: 'test@example.com',
    phoneNumber: '+84901234567',
    companyId: 'COMP-001',
    password: 'Password123',
  });
  check('signup with already-verified email rejected (409)', dup.status === 409);

  // 7. Validation still works
  const invalid = await post('/api/auth/signup', { email: 'not-an-email', phoneNumber: 'abc', companyId: '', password: 'short' });
  check('invalid payload rejected (400)', invalid.status === 400 && Array.isArray(invalid.body.errors));
} finally {
  server.close();
  await disconnectDB();
  await mongod.stop();
}

console.log(failures === 0 ? '\nAll checks passed.' : `\n${failures} check(s) failed.`);
process.exit(failures === 0 ? 0 : 1);
