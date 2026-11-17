import mongoose from 'mongoose';
import { config } from './index.js';

export async function connectDB() {
  mongoose.set('strictQuery', true);

  const conn = await mongoose.connect(config.mongoUri, {
    serverSelectionTimeoutMS: 8000,
    autoIndex: config.nodeEnv !== 'production',
  });

  console.log(`[db] MongoDB connected -> ${conn.connection.host}/${conn.connection.name}`);
  return conn;
}

export async function disconnectDB() {
  await mongoose.disconnect();
}
