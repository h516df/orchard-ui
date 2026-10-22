import { test, expect } from "@playwright/test";
const url = "http://127.0.0.1:4173/examples/web-reference/";
test.use({ viewport: { width: 393, height: 852 } });
test("settings search, detail navigation and appearance are functional", async ({
  page,
}) => {
  await page.goto(url);
  await page.getByRole("switch", { name: "飞行模式" }).check();
  await expect(page.getByRole("switch", { name: "飞行模式" })).toBeChecked();
  await page.getByRole("searchbox", { name: "搜索", exact: true }).fill("显示");
  await expect(page.getByRole("switch", { name: "飞行模式" })).toHaveCount(0);
  await page.getByRole("button", { name: "显示与亮度" }).click();
  await page.getByRole("radio", { name: "深色", exact: true }).check();
  await expect(page.locator("html")).toHaveAttribute(
    "data-orchard-mode",
    "dark",
  );
  await page.getByRole("button", { name: "返回", exact: true }).click();
  await expect(
    page.getByRole("heading", { name: "设置", exact: true }),
  ).toBeVisible();
});
test("notes retain edits through cancel, save and search; deletion is confirmed", async ({
  page,
}) => {
  await page.goto(url + "?page=notes");
  await page.getByRole("button", { name: /周末的小计划/ }).click();
  await page.getByRole("textbox", { name: "备忘录正文" }).fill("保留这段输入");
  await page.getByRole("button", { name: "返回", exact: true }).click();
  await page.getByRole("button", { name: "继续编辑" }).click();
  await expect(page.getByRole("textbox", { name: "备忘录正文" })).toHaveValue(
    "保留这段输入",
  );
  await page.getByRole("button", { name: "完成", exact: true }).click();
  await page
    .getByRole("searchbox", { name: "搜索", exact: true })
    .fill("保留这段输入");
  await expect(
    page.getByRole("button", { name: /周末的小计划/ }),
  ).toBeVisible();
  await expect(page.getByRole("button", { name: /灵感随记/ })).toHaveCount(0);
  await page.getByRole("button", { name: /周末的小计划/ }).click();
  await page.getByRole("button", { name: "删除备忘录" }).click();
  await page.getByRole("button", { name: "删除", exact: true }).click();
  await expect(page.getByRole("button", { name: /周末的小计划/ })).toHaveCount(
    0,
  );
  await page.getByRole("button", { name: "新建备忘录" }).click();
  await page.getByRole("textbox", { name: "备忘录标题" }).fill("新增的记录");
  await page.getByRole("button", { name: "完成", exact: true }).click();
  await page.getByRole("searchbox", { name: "搜索", exact: true }).fill("");
  await expect(page.getByRole("button", { name: /新增的记录/ })).toBeVisible();
});
test("music navigation preserves playback and search filters albums", async ({
  page,
}) => {
  await page.goto(url + "?page=music");
  await page.getByRole("button", { name: "播放微风", exact: true }).click();
  await expect(
    page.getByRole("button", { name: "暂停", exact: true }),
  ).toBeVisible();
  await page.getByRole("button", { name: "下一首" }).click();
  await expect(page.locator("#track-title")).toHaveText("慢一点");
  await page.getByRole("link", { name: "广播", exact: true }).click();
  await expect(page.getByRole("heading", { name: "电台精选" })).toBeVisible();
  await expect(page.locator("#track-title")).toHaveText("慢一点");
  await page.getByRole("link", { name: "搜索", exact: true }).click();
  await page.getByRole("textbox", { name: "音乐、艺人、专辑" }).fill("沿途");
  await expect(page.getByRole("button", { name: "播放微风" })).toHaveCount(0);
  await expect(page.getByRole("button", { name: "播放沿途" })).toBeVisible();
});
test("three pages render light and dark; narrow large type keeps docks within viewport", async ({
  page,
}) => {
  for (const route of ["settings", "notes", "music"]) {
    await page.goto(url + "?page=" + route);
    for (const mode of ["light", "dark"]) {
      await page.evaluate(
        (mode) => (document.documentElement.dataset.orchardMode = mode),
        mode,
      );
      await page.screenshot({
        path: `test-results/reference-${route}-${mode}.png`,
      });
    }
    await page.setViewportSize({ width: 320, height: 1000 });
    await page.addStyleTag({ content: "html{font-size:200%}" });
    expect(
      await page.evaluate(
        () => document.documentElement.scrollWidth <= innerWidth,
      ),
    ).toBe(true);
    const dock = page.locator(
      route === "music" ? ".o-media-dock" : ".o-search-dock",
    );
    const box = await dock.boundingBox();
    expect(box).not.toBeNull();
    expect(box!.x).toBeGreaterThanOrEqual(0);
    expect(box!.x + box!.width).toBeLessThanOrEqual(320);
    await page.getByRole("button", { name: "更多", exact: true }).click();
    await page.getByRole("switch", { name: "降低透明度" }).check();
    await page.keyboard.press("Escape");
    await expect(page.locator(".o-glass").first()).toHaveCSS(
      "backdrop-filter",
      "none",
    );
    await page.setViewportSize({ width: 393, height: 852 });
  }
});

test("scrolling replaces large title with compact navigation and keeps actions usable", async ({
  page,
}) => {
  await page.setViewportSize({ width: 393, height: 600 });
  await page.goto(url + "?page=notes");
  await page.evaluate(() => window.scrollTo(0, 300));
  await expect(page.locator(".o-header-middle")).toHaveCSS("opacity", "1");
  await page.getByRole("button", { name: "更多", exact: true }).click();
  await expect(page.getByRole("dialog", { name: "参考页面" })).toBeVisible();
  await page.keyboard.press("Escape");
  await page.emulateMedia({ reducedMotion: "reduce" });
  await expect(page.locator(".o-header-middle")).toHaveCSS(
    "transition-duration",
    "0s",
  );
});

test("continuous appearance, scroll collapse, nested menu queue and sheet detents work", async ({
  page,
}) => {
  await page.goto(url + "?page=music");
  await page.getByRole("button", { name: "更多", exact: true }).click();
  await page.getByRole("slider", { name: "玻璃着色" }).fill("85");
  await expect(page.locator("html")).toHaveCSS("--o-glass-tint", "85");
  await page.keyboard.press("Escape");
  await page.evaluate(() => window.scrollTo(0, 340));
  await expect(page.locator(".o-media-dock")).toHaveAttribute(
    "data-collapsed",
    "true",
  );
  await page.evaluate(() => window.scrollTo(0, 100));
  await expect(page.locator(".o-media-dock")).toHaveAttribute(
    "data-collapsed",
    "false",
  );
  await page.getByRole("button", { name: "微风的操作", exact: true }).click();
  await expect(page.getByRole("menu", { name: "专辑操作" })).toBeVisible();
  await page.getByRole("menuitem", { name: "添加到待播" }).click();
  await page.getByRole("menuitem", { name: "插播下一首" }).click();
  await expect(page.getByRole("status")).toHaveText("微风 · 待播 1 首");
  await page.getByRole("button", { name: "播放慢一点", exact: true }).click();
  await page.getByRole("button", { name: "下一首", exact: true }).click();
  await expect(page.locator("#track-title")).toHaveText("微风");
  await page.getByRole("button", { name: "打开正在播放" }).click();
  const player = page.getByRole("dialog", { name: "正在播放" });
  await expect(player).toBeVisible();
  await expect(player).toHaveAttribute("data-detent", "medium");
  await player.getByRole("button", { name: "切换弹层高度" }).click();
  await expect(player).toHaveAttribute("data-detent", "large");
  await page.keyboard.press("Escape");
  await expect(
    page.getByRole("button", { name: "打开正在播放" }),
  ).toBeFocused();
});
