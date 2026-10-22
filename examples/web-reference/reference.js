// Framework-free reference host. All state is local synthetic demo data.
const paths = {
  back: "M14 5 7 12l7 7",
  more: "M4 12h.01M12 12h.01M20 12h.01",
  search: "M16 10a6 6 0 1 1-12 0 6 6 0 0 1 12 0m-1 5 6 6",
  compose: "M13 4H4v17h17v-9M10 14l1-4 9-9 3 3-9 9z",
  folder: "M2 6h8l2 3h10v12H2zM2 9h20",
  wifi: "M2 7q10-9 20 0M5 11q7-7 14 0M8 15q4-4 8 0M12 19h.01",
  bluetooth: "M12 2l7 6L5 18M5 6l14 10-7 6V2",
  sun: "M16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0M12 1v3m0 16v3M1 12h3m16 0h3M4 4l2 2m12 12 2 2M4 20l2-2M18 6l2-2",
  bell: "m5 17 2-3V9a5 5 0 0 1 10 0v5l2 3H5m5 4h4",
  gear: "M20 12a8 8 0 1 1-16 0 8 8 0 0 1 16 0m-5 0a3 3 0 1 1-6 0 3 3 0 0 1 6 0",
  shield: "m12 3 9 4-2 9-7 6-7-6-2-9z",
  person: "M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0M3 22c0-11 18-11 18 0",
  radio: "M3 5q-8 7 0 14M6 8q-5 4 0 8M21 5q8 7 0 14M18 8q5 4 0 8M12 10v11",
  music:
    "M10 5 22 2v15c0 5-8 6-8 1 0-3 3-4 6-3V7l-8 2v10c0 5-8 6-8 1 0-3 3-4 6-3z",
  home: "m2 10 10-8 10 8-2 2v9h-6v-7h-4v7H4v-9z",
  library: "M5 2h14v2H5zM3 6h18v2H3zM2 10h20v13H2z",
  play: "m6 3 15 9-15 9z",
  pause: "M5 3h5v18H5zM14 3h5v18h-5z",
  forward: "m2 4 11 8-11 8zM12 4l11 8-11 8z",
  check: "m5 12 5 5L19 7",
  trash: "M3 6h18M8 6V2h8v4M5 6l1 16h12l1-16M10 10v8m4-8v8",
  chevron: "m9 6 6 6-6 6",
};
const filled = new Set([
  "music",
  "home",
  "library",
  "play",
  "pause",
  "forward",
]);
const icon = (name) =>
  `<svg viewBox="0 0 24 24" aria-hidden="true" fill="${filled.has(name) ? "currentColor" : "none"}" stroke="${filled.has(name) ? "none" : "currentColor"}" stroke-width="${name === "more" ? 3.5 : 1.8}" stroke-linecap="round" stroke-linejoin="round"><path d="${paths[name] || paths.gear}"/></svg>`;
const esc = (s) =>
  String(s).replace(
    /[&<>"']/g,
    (c) =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[
        c
      ],
  );
const group = (html) => `<div class="o-group o-group--inset">${html}</div>`;
const tile = (name, color) =>
  `<span class="o-icon-tile" style="--tile-color:${color}">${icon(name)}</span>`;
const row = (label, key, name, color, value = "") =>
  `<button class="o-row o-row--icon" data-detail="${key}">${tile(name, color)}<span class="o-row__content">${label}</span>${value ? `<span class="o-row__value">${value}</span>` : ""}${icon("chevron").replace("<svg ", '<svg class="o-row__chevron" ')}</button>`;
const toggle = (label, key, on, leading = "") =>
  `<label class="o-row ${leading ? "o-row--icon" : ""}">${leading}<span class="o-row__content">${label}</span><input class="o-switch" type="checkbox" role="switch" data-toggle="${key}" ${on ? "checked" : ""}></label>`;
const round = (name, label, action) =>
  `<button class="o-round-button" aria-label="${label}" data-action="${action}">${icon(name)}</button>`;
const state = {
  page: new URLSearchParams(location.search).get("page") || "settings",
  detail: "",
  query: "",
  notesQuery: "",
  airplane: false,
  wifi: true,
  bluetooth: true,
  tab: "library",
  selected: "",
  playing: false,
  hasTrack: false,
  track: 0,
  editor: false,
  index: -1,
  draftTitle: "",
  draftBody: "",
  notes: [
    [
      "周末的小计划",
      "买一束花，去河边散步。\n\n把想做的事写下来，留一点时间给自己。",
    ],
    ["灵感随记", "让日常的记录，回到简单。\n\n少一点打扰，多一点专注。"],
    ["想读的书", "关于自然、建筑和生活的几本书。"],
    ["旅行清单", "相机、充电器、一本随身的小书。"],
    ["慢慢积累", "一段喜欢的话，一个突然想到的点子。"],
    ["雨停之后", "窗外的树叶被洗得很亮。"],
  ],
};
const names = ["微风", "慢一点", "沿途", "日落以后"];
const art = [
  ["#96bbad", "#294d4a"],
  ["#f0c5a4", "#ae6d5f"],
  ["#b4bbda", "#444a76"],
  ["#e2bd77", "#624a38"],
];
const app = document.querySelector("#app");
const pages = document.querySelector("#pages");
function title() {
  return state.detail === "appearance"
    ? "显示与亮度"
    : state.page === "notes"
      ? "备忘录"
      : state.page === "music"
        ? {
            home: "主页",
            radio: "广播",
            search: "搜索",
            library: state.selected || "资料库",
          }[state.tab]
        : "设置";
}
function settings() {
  const match = (s) => !state.query || s.includes(state.query);
  const groups = [];
  if (match("h516df 个人资料 账户"))
    groups.push(
      group(
        `<button class="o-row" data-detail="account"><span class="account-avatar">H</span><span class="account-label o-row__content"><strong>h516df</strong><small>个人资料、同步与备份</small></span>${icon("chevron").replace("<svg ", '<svg class="o-row__chevron" ')}</button>`,
      ),
    );
  const wireless = [
    match("飞行模式")
      ? toggle(
          "飞行模式",
          "airplane",
          state.airplane,
          tile("forward", "#ff9500"),
        )
      : "",
    match("无线局域网")
      ? row(
          "无线局域网",
          "wifi",
          "wifi",
          "#007aff",
          state.wifi ? "Orchard" : "关闭",
        )
      : "",
    match("蓝牙")
      ? row(
          "蓝牙",
          "bluetooth",
          "bluetooth",
          "#007aff",
          state.bluetooth ? "打开" : "关闭",
        )
      : "",
  ].join("");
  if (wireless) groups.push(group(wireless));
  for (const rows of [
    [
      ["通知", "notifications", "bell", "#ff3b30"],
      ["声音与触感", "sounds", "radio", "#ff3b30"],
    ],
    [
      ["通用", "general", "gear", "#8e8e93"],
      ["显示与亮度", "appearance", "sun", "#007aff"],
      ["辅助功能", "accessibility", "person", "#007aff"],
      ["隐私与安全性", "privacy", "shield", "#30b0c7"],
    ],
  ]) {
    const html = rows
      .filter((r) => match(r[0]))
      .map((r) => row(...r))
      .join("");
    if (html) groups.push(group(html));
  }
  return groups.join("") || '<div class="o-empty">没有结果</div>';
}
function display() {
  return `<section class="o-section"><h2>外观</h2>${group(`<div class="display-options">${["浅色", "深色"].map((label, i) => `<label><span aria-hidden="true" class="display-device ${i ? "dark" : ""}">9:41<i></i><i></i><i></i></span>${label}<input type="radio" name="mode" value="${i ? "dark" : "light"}" ${document.documentElement.dataset.orchardMode === (i ? "dark" : "light") ? "checked" : ""}></label>`).join("")}</div>`)}</section><section class="o-section"><h2>亮度</h2>${group(`<input type="range" class="brightness" aria-label="亮度" value="60">${toggle("原彩显示", "trueTone", true)}`)}<p class="o-caption">亮度滑块用于组件演示，不改变设备屏幕。</p></section><section class="o-section"><h2>辅助功能</h2>${group(toggle("降低透明度", "opaque", document.querySelector("#opaque").checked))}</section>`;
}
function notes() {
  const indices = state.notes
    .map((n, i) => i)
    .filter(
      (i) =>
        !state.notesQuery ||
        state.notes[i].join(" ").includes(state.notesQuery),
    );
  if (!indices.length) return '<div class="o-empty">没有结果</div>';
  return [
    ["今天", indices.filter((i) => i < 2)],
    ["昨天", indices.filter((i) => i >= 2)],
  ]
    .filter(([, a]) => a.length)
    .map(
      ([label, ids]) =>
        `<section class="o-note-section"><h2 class="o-title">${label}</h2>${group(ids.map((i) => `<button class="o-note-row" data-note="${i}"><strong>${esc(state.notes[i][0])}</strong><span>${i === 0 ? "09:41" : i === 1 ? "08:26" : "昨天"}&nbsp; ${esc(state.notes[i][1].split("\n")[0])}</span><small>${icon("folder")}备忘录</small></button>`).join(""))}</section>`,
    )
    .join("");
}
function albums() {
  return `<section><h2 class="o-title">${state.tab === "radio" ? "电台精选" : state.tab === "home" ? "为你精选" : state.tab === "search" ? "浏览分类" : state.selected || "最近添加"}</h2><div class="o-album-grid">${names
    .map((name, i) => ({ name, i }))
    .filter(({ name }) => !state.query || name.includes(state.query))
    .map(
      ({ name, i }) =>
        `<button class="o-album" aria-label="播放${name}" data-track="${i}"><span class="o-album-art" style="--art-start:${art[i][0]};--art-end:${art[i][1]}"><small>ORCHARD</small><b>${name}</b></span><span>${name}</span><small>Orchard Sessions</small></button>`,
    )
    .join("")}</div></section>`;
}
function music() {
  return (
    (state.tab === "library" && !state.selected
      ? `<div class="o-music-list">${["播放列表", "艺人", "专辑", "歌曲", "已下载"].map((label, i) => `<button class="o-row" data-category="${label}">${icon(["radio", "person", "library", "music", "music"][i])}<span>${label}</span>${icon("chevron").replace("<svg ", '<svg class="o-row__chevron" ')}</button>`).join("")}</div>`
      : "") +
    (state.tab === "search"
      ? `<label class="o-search">${icon("search")}<input aria-label="音乐、艺人、专辑" placeholder="音乐、艺人、专辑" value="${esc(state.query)}" data-search="music"></label>`
      : "") +
    albums()
  );
}
function dock() {
  if (state.page === "music")
    return `<div class="o-media-dock"><div class="o-mini-player o-glass"><span class="o-mini-art">${icon("music")}</span><strong id="track-title">${state.hasTrack ? names[state.track] : "未在播放"}</strong><button aria-label="${state.playing ? "暂停" : "播放"}" data-action="play">${icon(state.playing ? "pause" : "play")}</button><button aria-label="下一首" data-action="next">${icon("forward")}</button></div><nav class="o-bottom-nav" aria-label="音乐导航">${[
      ["home", "主页"],
      ["radio", "广播"],
      ["library", "资料库"],
      ["search", "搜索"],
    ]
      .map(
        ([key, label]) =>
          `<a href="#${key}" data-tab="${key}" ${state.tab === key ? 'aria-current="page"' : ""}>${icon(key === "search" ? "search" : key)}${label}</a>`,
      )
      .join("")}</nav></div>`;
  return `<div class="o-search-dock"><label class="o-search o-glass">${icon("search")}<input type="search" aria-label="搜索" placeholder="搜索" value="${esc(state.page === "notes" ? state.notesQuery : state.query)}" data-search="${state.page}"></label>${state.page === "notes" ? round("compose", "新建备忘录", "compose") : ""}</div>`;
}
function render() {
  app.dataset.page = state.page;
  app.style.setProperty(
    "--o-primary",
    state.page === "music"
      ? document.documentElement.dataset.orchardMode === "dark"
        ? "#ff536b"
        : "#d7003b"
      : "",
  );
  if (state.editor) {
    app.innerHTML = `<header class="o-app-header" style="background:var(--editor-background)">${round("back", "返回", "close-editor")}<div style="display:flex;gap:8px">${round("trash", "删除备忘录", "delete")}${round("check", "完成", "save")}</div></header><main class="o-editor"><input aria-label="备忘录标题" placeholder="标题"><textarea aria-label="备忘录正文" placeholder="开始记录…"></textarea></main>`;
    app.querySelector("input").value = state.draftTitle;
    app.querySelector("textarea").value = state.draftBody;
    app.querySelector("input").oninput = (e) =>
      (state.draftTitle = e.target.value);
    app.querySelector("textarea").oninput = (e) =>
      (state.draftBody = e.target.value);
    return;
  }
  app.innerHTML = `<header class="o-app-header">${state.page === "notes" || state.detail || state.selected ? round("back", "返回", "back") : ""}${round("more", "更多", "menu")}<span class="o-header-middle" aria-hidden="true">${title()}</span></header><div class="o-app-title"><h1 class="o-large-title" tabindex="-1">${title()}</h1>${state.page === "notes" ? `<p>${state.notes.length} 则备忘录</p>` : ""}</div><main class="o-app-content">${state.detail === "appearance" ? display() : state.page === "settings" ? settings() : state.page === "notes" ? notes() : music()}</main>${state.detail ? "" : dock()}`;
}
function openNote(i) {
  state.index = i;
  state.editor = true;
  state.draftTitle = i >= 0 ? state.notes[i][0] : "";
  state.draftBody = i >= 0 ? state.notes[i][1] : "";
  render();
  window.scrollTo(0, 0);
}
function closeEditor() {
  const old = state.notes[state.index] || ["", ""];
  if (state.draftTitle !== old[0] || state.draftBody !== old[1])
    document.querySelector("#confirm").showModal();
  else {
    state.editor = false;
    render();
  }
}
function detail(key) {
  if (key === "appearance") {
    state.detail = key;
    render();
    window.scrollTo(0, 0);
    return;
  }
  const titles = {
    wifi: "无线局域网",
    bluetooth: "蓝牙",
    account: "个人资料",
    general: "通用",
    notifications: "通知",
    sounds: "声音与触感",
    privacy: "隐私与安全性",
    accessibility: "辅助功能",
  };
  document.querySelector("#detail-title").textContent = titles[key];
  document.querySelector("#detail-body").innerHTML =
    key === "wifi" || key === "bluetooth"
      ? toggle(titles[key], key, state[key])
      : key === "account"
        ? '<p>h516df</p><p class="o-sample-caption">合成的本机账户展示，不接入账户服务。</p>'
        : toggle(
            "降低透明度",
            "opaque",
            document.querySelector("#opaque").checked,
          ) + '<p class="o-sample-caption">这里的选项仅作用于展示页面。</p>';
  document.querySelector("#detail").showModal();
}
document.addEventListener("click", (e) => {
  const target = e.target.closest(
    "[data-action],button[data-page],[data-detail],[data-note],[data-track],[data-tab],[data-category]",
  );
  if (!target) return;
  if (target.dataset.page) {
    state.page = target.dataset.page;
    state.detail = "";
    state.query = "";
    pages.close();
    render();
    window.scrollTo(0, 0);
    return;
  }
  if (target.dataset.detail) {
    detail(target.dataset.detail);
    return;
  }
  if (target.dataset.note !== undefined) {
    openNote(Number(target.dataset.note));
    return;
  }
  if (target.dataset.track !== undefined) {
    state.track = Number(target.dataset.track);
    state.playing = true;
    state.hasTrack = true;
    render();
    return;
  }
  if (target.dataset.tab) {
    e.preventDefault();
    state.tab = target.dataset.tab;
    state.selected = "";
    state.query = "";
    render();
    window.scrollTo(0, 0);
    return;
  }
  if (target.dataset.category) {
    state.selected = target.dataset.category;
    render();
    return;
  }
  switch (target.dataset.action) {
    case "menu":
      pages.showModal();
      break;
    case "compose":
      openNote(-1);
      break;
    case "back":
      if (state.detail) state.detail = "";
      else if (state.selected) state.selected = "";
      else state.page = "settings";
      render();
      break;
    case "close-editor":
      closeEditor();
      break;
    case "save":
      if (state.draftTitle.trim() || state.draftBody.trim()) {
        const entry = [state.draftTitle || "新建备忘录", state.draftBody];
        if (state.index >= 0) state.notes[state.index] = entry;
        else state.notes.unshift(entry);
      }
      state.editor = false;
      render();
      break;
    case "delete":
      document.querySelector("#confirm-title").textContent = "删除备忘录？";
      document.querySelector("#discard").textContent = "删除";
      document.querySelector("#confirm").dataset.delete = "true";
      document.querySelector("#confirm").showModal();
      break;
    case "play":
      state.playing = !state.playing;
      state.hasTrack = true;
      render();
      break;
    case "next":
      state.track = (state.track + 1) % names.length;
      state.playing = true;
      state.hasTrack = true;
      render();
      break;
  }
});
document.addEventListener("input", (e) => {
  if (e.target.dataset.search) {
    if (e.target.dataset.search === "notes") state.notesQuery = e.target.value;
    else state.query = e.target.value;
    const main = app.querySelector("main");
    if (state.page === "music") {
      const albumSection = main.querySelector("section");
      albumSection.outerHTML = albums();
    } else main.innerHTML = state.page === "notes" ? notes() : settings();
  }
});
document.addEventListener("change", (e) => {
  const key = e.target.dataset.toggle;
  if (key === "opaque") {
    document.querySelector("#opaque").checked = e.target.checked;
    document.documentElement.dataset.orchardTransparency = e.target.checked
      ? "reduced"
      : "normal";
  } else if (key) {
    state[key] = e.target.checked;
  }
  if (e.target.name === "mode") {
    document.documentElement.dataset.orchardMode = e.target.value;
    document.querySelector("#dark").checked = e.target.value === "dark";
  }
});
document.querySelector("#dark").onchange = (e) => {
  document.documentElement.dataset.orchardMode = e.target.checked
    ? "dark"
    : "light";
  render();
};
document.querySelector("#opaque").onchange = (e) =>
  (document.documentElement.dataset.orchardTransparency = e.target.checked
    ? "reduced"
    : "normal");
const confirmation = document.querySelector("#confirm");
function resetConfirm() {
  confirmation.close();
  confirmation.dataset.delete = "";
  document.querySelector("#confirm-title").textContent = "放弃修改？";
  document.querySelector("#discard").textContent = "放弃修改";
}
document.querySelector("#keep").onclick = resetConfirm;
confirmation.addEventListener("cancel", () => {
  confirmation.dataset.delete = "";
  document.querySelector("#confirm-title").textContent = "放弃修改？";
  document.querySelector("#discard").textContent = "放弃修改";
});
document.querySelector("#discard").onclick = () => {
  if (confirmation.dataset.delete === "true" && state.index >= 0)
    state.notes.splice(state.index, 1);
  state.editor = false;
  resetConfirm();
  render();
};
render();

window.addEventListener(
  "scroll",
  () => {
    const header = app.querySelector(".o-app-header");
    if (header) header.dataset.scrolled = String(window.scrollY > 72);
  },
  { passive: true },
);
