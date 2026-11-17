import { assertConfig } from './config/index.js';
import { config } from './config/index.js';
import { connectDB, disconnectDB } from './config/db.js';
import app from './app.js';

async function start() {
  assertConfig();

  await connectDB();

  const server = app.listen(config.port, () => {
    console.log(`[server] WorkMate BE listening on http://localhost:${config.port}`);
  });

  const shutdown = async (signal) => {
    console.log(`\n[server] ${signal} received, shutting down...`);
    server.close(async () => {
      await disconnectDB();
      process.exit(0);
    });
  };

  process.on('SIGINT', () => shutdown('SIGINT'));
  process.on('SIGTERM', () => shutdown('SIGTERM'));
}

start().catch((err) => {
  console.error('[server] Failed to start:', err);
  process.exit(1);
});
