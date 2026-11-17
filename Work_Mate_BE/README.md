# WorkMate Backend (Work_Mate_BE)

Backend API cho ứng dụng WorkMate — hiện có module **Signup + xác thực email bằng OTP + JWT**.

**Stack:** Node.js 18+ · Express · MongoDB (Mongoose) · JWT · bcrypt (salt) · Nodemailer

## 1. Cài đặt

```bash
cd Work_Mate_BE
npm install
cp .env.example .env   # rồi chỉnh sửa .env
```

### 2. Cấu hình MongoDB

Trong `.env`, đặt `MONGODB_URI`:

- Local: `mongodb://127.0.0.1:27017/work_mate_be` (cần chạy sẵn MongoDB local), hoặc
- Atlas: `mongodb+srv://<user>:<password>@<cluster>.mongodb.net/work_mate_be`

> Không có MongoDB? Test tự động (`npm test`) dùng MongoDB ảo trong bộ nhớ nên **không cần cài MongoDB**.
>
> **Windows — service `MongoDB` không khởi động được?** Service có thể đã crash (kiểm tra `C:\Program Files\MongoDB\Server\8.2\log\mongod.log`) và cần quyền admin để `Start-Service MongoDB`. Chạy MongoDB local không cần admin bằng:
> ```powershell
> powershell -ExecutionPolicy Bypass -File scripts\start-mongodb.ps1
> ```
> Script này chạy `mongod` ở port 27017 với thư mục data riêng (`%USERPROFILE%\mongodb-data`).
> Lưu ý: nếu service chạy ở Program Files bị crash, dữ liệu dev của bạn (nếu có) nằm ở dbPath cũ; instance dev mới bắt đầu rỗng.

### 3. Cấu hình gửi email OTP

Trong `.env`, chọn một trong các chế độ qua `EMAIL_PROVIDER`:

| Giá trị   | Hành vi |
|-----------|---------|
| `console` | Không gửi email thật — in OTP ra console của server (mặc định, tiện cho dev/test) |
| `smtp`    | Gửi qua SMTP thật (điền `SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASS`, ví dụ Gmail + App Password) |
| `auto`    | Dùng SMTP nếu có cấu hình, ngược lại tự tạo hộp thư test Ethereal (in preview URL) |

### 4. Chạy server

```bash
npm run dev    # nodemon (auto-reload)
npm start      # node thường
```

Server chạy tại `http://localhost:5000`. Health check: `GET /api/health`.

### 5. Chạy test

```bash
npm test
```

Chạy E2E thật với MongoDB ảo: signup → OTP sai → OTP đúng → lấy JWT → gọi `/me` → login → chặn trùng email.

---

## API Reference — `/api/auth`

### `POST /api/auth/signup`
Tạo tài khoản (chưa kích hoạt) và gửi OTP qua email.

```json
{
  "email": "user@example.com",
  "phoneNumber": "+84901234567",
  "companyId": "COMP-001",
  "password": "Password123"
}
```

- Mật khẩu: ≥ 8 ký tự, có ít nhất 1 chữ cái + 1 chữ số → được hash bằng **bcrypt với salt (10 rounds)**.
- Nếu email đã đăng ký **và đã xác thực** → `409`. Nếu chưa xác thực → cập nhật lại thông tin và gửi OTP mới.
- Response: `200 { "success": true, "message": "..." }` (không bao giờ trả về OTP).

### `POST /api/auth/verify-email-otp`
Nhập OTP nhận được qua mail → kích hoạt tài khoản và **trả về JWT**.

```json
{ "email": "user@example.com", "otp": "123456" }
```

- OTP gồm 6 chữ số, có hiệu lực trong `OTP_TTL_MINUTES` (mặc định 10 phút).
- OTP được lưu dưới dạng **HMAC-SHA256 hash**, so khớp bằng constant-time compare; tối đa 5 lần thử sai.
- Response: `200 { "success": true, "token": "<JWT>", "user": { ... } }`.

### `POST /api/auth/resend-otp`
```json
{ "email": "user@example.com" }
```
Gửi lại OTP (chỉ khi tài khoản chưa xác thực).

### `POST /api/auth/login`
```json
{ "email": "user@example.com", "password": "Password123" }
```
Chỉ cho phép đăng nhập khi email **đã xác thực**. Response chứa JWT.

### `GET /api/auth/me` 🔒
Header: `Authorization: Bearer <JWT>` → trả về thông tin user.

---

## Cấu trúc thư mục

```
src/
  config/      # đọc .env + kết nối MongoDB (db.js)
  models/      # Mongoose models (User)
  controllers/ # Logic: signup / verify OTP / resend / login / me
  routes/      # Định tuyến + validation (express-validator)
  middleware/  # validate, authenticate (JWT), error handler
  utils/       # otp.js (tạo + hash OTP), emailService.js, token.js
tests/
  auth.e2e.mjs # E2E test với mongodb-memory-server
```

## Bảo mật

- Password: **bcrypt + salt** (không bao giờ lưu plaintext).
- OTP: HMAC-SHA256 trước khi lưu DB, giới hạn 5 lần thử, hết hạn theo `OTP_TTL_MINUTES`.
- `password`, `verificationOtpHash`, … dùng `select: false` nên không lẫn vào query kết quả.
- JWT ký bằng `JWT_SECRET`, thời hạn `JWT_EXPIRES_IN` (mặc định 7d).
- `.env` được git-ignored (xem `.gitignore`), chỉ commit `.env.example`.
