/** Framework-free appearance and dock behavior. CSS is a blur/tint fallback. */
export function setOrchardGlassTint(value, root = document.documentElement) {
  const finite = Number.isFinite(value) ? value : 0.45;
  root.style.setProperty(
    "--o-glass-tint",
    String(Math.round(Math.max(0, Math.min(1, finite)) * 100)),
  );
}

/** Own one controller per scrolling host; destroy before replacing its DOM. */
export class OrchardDockController {
  constructor(
    dock,
    { scrollHost = window, collapseDistance = 48, expandDistance = 20 } = {},
  ) {
    this.dock = dock;
    this.host = scrollHost;
    this.collapseDistance = collapseDistance;
    this.expandDistance = expandDistance;
    this.last = this.y;
    this.travel = 0;
    this.onScroll = () => {
      const y = this.y,
        delta = y - this.last;
      this.travel = delta > 0 === this.travel > 0 ? this.travel + delta : delta;
      if (y < this.collapseDistance || this.travel < -this.expandDistance)
        this.setCollapsed(false);
      else if (this.travel > this.collapseDistance) this.setCollapsed(true);
      this.last = y;
    };
    this.onFocus = () => this.setCollapsed(false);
    scrollHost.addEventListener("scroll", this.onScroll, { passive: true });
    dock.addEventListener("focusin", this.onFocus);
  }
  get y() {
    return this.host === window ? window.scrollY : this.host.scrollTop;
  }
  setCollapsed(value) {
    this.dock.dataset.collapsed = String(value);
  }
  destroy() {
    this.host.removeEventListener("scroll", this.onScroll);
    this.dock.removeEventListener("focusin", this.onFocus);
  }
}

/** Uses native Popover dismissal/focus and clamps an anchored menu to the viewport. */
export function showOrchardMenu(menu, anchor) {
  menu.showPopover({ source: anchor });
  const rect = anchor.getBoundingClientRect();
  const box = menu.getBoundingClientRect();
  menu.style.left = `${Math.max(8, Math.min(rect.right - box.width, innerWidth - box.width - 8))}px`;
  menu.style.top = `${Math.max(8, Math.min(rect.bottom + 8, innerHeight - box.height - 8))}px`;
  const items = () => [...menu.querySelectorAll("button:not([hidden])")];
  items()[0]?.focus();
  menu.onkeydown = (e) => {
    const list = items(),
      index = list.indexOf(document.activeElement);
    if (e.key === "ArrowDown" || e.key === "ArrowUp") {
      e.preventDefault();
      list[
        (index + (e.key === "ArrowDown" ? 1 : -1) + list.length) % list.length
      ]?.focus();
    } else if (e.key === "Home" || e.key === "End") {
      e.preventDefault();
      list[e.key === "Home" ? 0 : list.length - 1]?.focus();
    }
  };
  menu.ontoggle = (e) => {
    if (e.newState === "closed" && anchor.isConnected) anchor.focus();
  };
}
