export function notFound(req, res) {
  res.status(404).json({ success: false, message: `Route không tồn tại: ${req.method} ${req.originalUrl}` });
}

// eslint-disable-next-line no-unused-vars
export function errorHandler(err, req, res, next) {
  console.error('[error]', err);

  // Mongo duplicate key (e.g. email unique index)
  if (err?.code === 11000) {
    return res.status(409).json({ success: false, message: 'Dữ liệu đã tồn tại (email trùng lặp).' });
  }

  const status = err?.statusCode || 500;
  res.status(status).json({
    success: false,
    message: status === 500 ? 'Lỗi máy chủ nội bộ.' : err.message,
  });
}
