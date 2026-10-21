# Web / React / Vue / Svelte / 混合容器

这个适配层是语义 HTML + CSS，不绑定 JS 框架。导入 `orchard.css`；打包工具需支持 CSS `@import`，直接复制时同时保留 `tokens.css`。旧浏览器须自行评估 `:has()`、`overflow: clip` 和动态视口单位的支持。

```html
<html lang="zh-CN" data-orchard-palette="sage" data-orchard-mode="light">
<body class="orchard">
  <main class="o-page o-stack">
    <h1 class="o-large-title">偏好设置</h1>
    <section class="o-section" aria-labelledby="display-title">
      <h2 id="display-title">显示</h2>
      <div class="o-group">
        <label class="o-row" for="feedback">
          <span>触感反馈</span>
          <input id="feedback" type="checkbox" role="switch" class="o-switch">
        </label>
      </div>
    </section>
    <div class="o-field">
      <label for="name">名称</label>
      <input id="name" class="o-input" aria-describedby="name-hint">
      <span id="name-hint" class="o-caption">可以稍后修改</span>
    </div>
    <button class="o-button" type="button">保存</button>
  </main>
</body>
</html>
```

主题入口为 `data-orchard-palette="blue|sage|peach|ink"` 与 `data-orchard-mode="light|dark"`。系统模式由宿主监听 `matchMedia('(prefers-color-scheme: dark)')`；显式选择优先，保存至宿主偏好。没有自动读取 localStorage 的脚本。

可用类：page / stack / grid / large-title / title / caption / card / group / section / row / button（soft/plain/destructive）/ actions / field / input / error / switch / badge / banner（error）/ empty / divider / reading / segments / dialog / bottom-nav / skeleton / sr-only，统一加 `o-` 前缀。

交互由宿主负责：按钮加载时同时设置 `disabled`、`aria-busy` 与可见状态文字；输入错误同时设置 `aria-invalid` 和关联说明；反馈使用适当的 `role="status"`。对话框使用原生 `<dialog>.showModal()`，标注 `aria-labelledby`，确认按钮执行成功后再关闭，关闭后把焦点交还触发点。不要只用一个 div 模拟弹窗。删除确认、编辑草稿、导航保护与系统返回按 [UX 模式](../../docs/UX_PATTERNS.md) 实现；样式本身不会提供存储、路由或原生权限能力。

在 WebView / Capacitor 中，安全区、软键盘、Android 系统返回和文件选择仍需对应容器验证。没有声称 CSS 能处理这些系统能力。

## 0.2 Apple外形

按钮为胶囊；输入可用`.o-field--inline`把标签与值放在同一行；分段是连续轨道，不是独立标签；开关为绿色轨道与白色滑块；`.o-bottom-nav`是浮动材质导航。列表前导图标使用`.o-icon-tile`，内容使用`.o-row__content`，次级值使用`.o-row__value`。

系统减少动态效果保持有效；降低透明度可设`data-orchard-transparency="reduced"`，或由受支持浏览器读取prefers-reduced-transparency。背景模糊依赖浏览器backdrop-filter支持；不支持时宿主应设置不透明回退。细节见[Apple视觉基线](../../docs/APPLE_VISUALS.md)。
