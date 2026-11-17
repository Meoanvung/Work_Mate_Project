import nodemailer from 'nodemailer';
import { config } from '../config/index.js';

function buildOtpEmail(to, otp) {
  const minutes = config.otpTtlMinutes;
  const subject = `[WorkMate] Mã xác thực email của bạn: ${otp}`;
  const text = [
    `Xin chào,`,
    ``,
    `Mã OTP xác thực tài khoản WorkMate của bạn là: ${otp}`,
    `Mã có hiệu lực trong ${minutes} phút.`,
    ``,
    `Nếu bạn không yêu cầu đăng ký tài khoản, hãy bỏ qua email này.`,
  ].join('\n');
  const html = `
    <div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e5e7eb;border-radius:12px">
      <h2 style="color:#111827;margin-bottom:8px">Xác thực email WorkMate</h2>
      <p style="color:#374151">Nhập mã bên dưới để hoàn tất đăng ký tài khoản:</p>
      <div style="font-size:32px;letter-spacing:8px;font-weight:bold;text-align:center;padding:16px;background:#f3f4f6;border-radius:8px;margin:16px 0">${otp}</div>
      <p style="color:#6b7280;font-size:13px">Mã có hiệu lực trong ${minutes} phút. Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>
    </div>`;

  return { from: config.emailFrom, to, subject, text, html };
}

let cachedTransporter = null;

async function getTransporter() {
  if (cachedTransporter) return cachedTransporter;

  const hasSmtp = config.smtp.user && config.smtp.pass;
  if (config.emailProvider === 'smtp' || (config.emailProvider === 'auto' && hasSmtp)) {
    cachedTransporter = {
      sendMail: (mail) =>
        nodemailer
          .createTransport({
            host: config.smtp.host,
            port: config.smtp.port,
            secure: config.smtp.port === 465,
            auth: { user: config.smtp.user, pass: config.smtp.pass },
          })
          .sendMail(mail),
    };
    return cachedTransporter;
  }

  // Fallback for local development: auto-created Ethereal test inbox
  const testAccount = await nodemailer.createTestAccount();
  const transporter = nodemailer.createTransport({
    host: 'smtp.ethereal.email',
    port: 587,
    secure: false,
    auth: { user: testAccount.user, pass: testAccount.pass },
  });
  cachedTransporter = {
    sendMail: async (mail) => {
      const info = await transporter.sendMail(mail);
      console.log(`[email] Preview URL: ${nodemailer.getTestMessageUrl(info)}`);
      return info;
    },
  };
  return cachedTransporter;
}

/**
 * Send the OTP email. In `console` mode nothing is sent — the OTP is printed
 * to the server console and stashed on globalThis.__lastOtp for local tests.
 */
export async function sendOtpEmail(to, otp) {
  const mail = buildOtpEmail(to, otp);

  if (config.emailProvider === 'console') {
    console.log(`[email:console] To: ${to} | Subject: ${mail.subject}`);
    console.log(`[email:console] OTP for ${to} => ${otp}`);
    globalThis.__lastOtp = otp; // used by local/E2E tests only
    return { mode: 'console' };
  }

  const transporter = await getTransporter();
  return transporter.sendMail(mail);
}
