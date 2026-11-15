/**
 * logger.ts — lightweight structured logger used across utils and scripts.
 * Writes to stdout with ISO timestamps so CI logs are grep-friendly.
 */

type Level = 'INFO' | 'WARN' | 'ERROR' | 'DEBUG';

function write(level: Level, message: string): void {
  const ts = new Date().toISOString();
  const line = `[${ts}] [${level}] ${message}`;
  if (level === 'ERROR') {
    console.error(line);
  } else {
    console.log(line);
  }
}

export const log = {
  info: (msg: string) => write('INFO', msg),
  warn: (msg: string) => write('WARN', msg),
  error: (msg: string) => write('ERROR', msg),
  debug: (msg: string) => {
    if (process.env.DEBUG) write('DEBUG', msg);
  },
};
