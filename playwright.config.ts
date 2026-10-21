import {defineConfig} from '@playwright/test';

export default defineConfig({
  testDir: './adapters/web/test',
  workers: 1,
  use: {headless: true, launchOptions: {executablePath: process.env.CHROMIUM_PATH}},
  webServer: {command: 'python3 -m http.server 4173 --bind 127.0.0.1', port: 4173, reuseExistingServer: false},
});
