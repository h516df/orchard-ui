#!/usr/bin/env python3
"""Check local Markdown links without sending private repository data externally."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
errors = []
for path in ROOT.rglob("*.md"):
    if any(part in {"node_modules", "build", ".git", ".dart_tool", ".gradle"} for part in path.relative_to(ROOT).parts):
        continue
    text = path.read_text()
    # Ignore examples in fenced code blocks; inspect authored relative links.
    text = re.sub(r"```.*?```", "", text, flags=re.S)
    for link in re.findall(r"\]\(([^)]+)\)", text):
        target = link.split("#")[0].split()[0] if link.split("#")[0] else ""
        if not target or ":" in target or target.startswith("/"):
            continue
        if not (path.parent / target).exists():
            errors.append(f"{path.relative_to(ROOT)} -> {target}")
if errors:
    raise SystemExit("Broken local links:\n" + "\n".join(errors))
print("Local Markdown links resolve.")
