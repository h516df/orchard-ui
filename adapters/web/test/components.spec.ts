import {test, expect} from '@playwright/test';

test.beforeEach(async ({page}) => {await page.goto('http://127.0.0.1:4173/adapters/web/test/fixture.html');});
test('semantic controls work with keyboard and preserve text after failed save', async ({page}) => {
  await page.getByRole('switch').focus(); await page.keyboard.press('Space'); await expect(page.getByRole('switch')).toBeChecked();
  await page.getByRole('radio', {name: '已收藏'}).check(); await expect(page.getByRole('radio', {name: '已收藏'})).toBeChecked();
  await page.getByRole('button', {name: '保存', exact: true}).click(); await expect(page.getByRole('status')).toContainText('保存失败');
  await expect(page.getByRole('textbox', {name: '正文'})).toHaveValue('还没写完的草稿');
  await page.getByRole('button', {name: '保存', exact: true}).click(); await expect(page.getByRole('status')).toHaveText('已保存');
});
test('native dialog closes with Escape and restores trigger focus', async ({page}) => {
  const trigger = page.getByRole('button', {name: '更多选择'}); await trigger.click();
  await expect(page.getByRole('dialog')).toBeVisible(); await page.keyboard.press('Escape');
  await expect(page.getByRole('dialog')).not.toBeVisible(); await expect(trigger).toBeFocused();
});
test('all themes fit a narrow viewport at 200 percent base text', async ({page}) => {
  await page.setViewportSize({width: 320, height: 900});
  await page.addStyleTag({content: 'html { font-size: 200%; }'});
  for (const palette of ['blue', 'sage', 'peach', 'ink']) for (const mode of ['light', 'dark']) {
    await page.evaluate(({palette, mode}) => {document.documentElement.dataset.orchardPalette = palette; document.documentElement.dataset.orchardMode = mode;}, {palette, mode});
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true);
    const colors = await page.locator('body').evaluate(el => ({fg: getComputedStyle(el).color, bg: getComputedStyle(el).backgroundColor}));
    expect(colors.fg).not.toBe(colors.bg);
  }
});
test('reduced motion disables component transitions', async ({page}) => {
  await page.emulateMedia({reducedMotion: 'reduce'});
  await expect(page.getByRole('button', {name: '保存', exact: true})).toHaveCSS('transition-duration', '0s');
});
