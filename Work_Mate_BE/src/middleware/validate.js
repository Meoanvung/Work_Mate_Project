import { validationResult } from 'express-validator';

/** Collects express-validator results; responds 400 with the list of errors when invalid. */
export function validate(req, res, next) {
  const errors = validationResult(req);
  if (errors.isEmpty()) return next();

  return res.status(400).json({
    success: false,
    message: 'Dữ liệu không hợp lệ',
    errors: errors.array().map((e) => ({ field: e.path, message: e.msg })),
  });
}
