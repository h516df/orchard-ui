#!/usr/bin/env python3
"""Build the local framework-free reference as one offline HTML artifact."""
import argparse
import re
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--output', type=Path, default=ROOT / '.cloud/preview/orchard-reference.html')
args = parser.parse_args()
source = ROOT / 'examples/web-reference'
html = (source / 'index.html').read_text()
css = (ROOT / 'adapters/web/tokens.css').read_text() + (ROOT / 'adapters/web/orchard.css').read_text().replace("@import './tokens.css';", '')
css += (source / 'reference.css').read_text()
html = re.sub(r'<link\s+rel="stylesheet"\s+href="[^"]+"\s*/?>', '', html)
html = html.replace('</head>', '<style>' + css + '</style></head>')
html = html.replace('<script type="module" src="./reference.js"></script>', '<script type="module">' + (source / 'reference.js').read_text() + '</script>')
args.output.parent.mkdir(parents=True, exist_ok=True)
args.output.write_text(html)
print(args.output)
