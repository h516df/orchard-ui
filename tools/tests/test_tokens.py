import json
import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "tools"))
from generate_tokens import outputs


def luminance(hex_color):
    channels = [int(hex_color[i:i + 2], 16) / 255 for i in (1, 3, 5)]
    values = [v / 12.92 if v <= .04045 else ((v + .055) / 1.055) ** 2.4 for v in channels]
    return sum(a * b for a, b in zip(values, (.2126, .7152, .0722)))


def contrast(a, b):
    high, low = sorted((luminance(a), luminance(b)), reverse=True)
    return (high + .05) / (low + .05)


class TokenTests(unittest.TestCase):
    def test_all_eight_schemes_have_readable_text_and_controls(self):
        data = json.loads((ROOT / "tokens/orchard.json").read_text())
        pairs = [(fg, bg, 4.5) for fg in ("text", "textSecondary") for bg in ("background", "surface", "surfaceMuted")]
        pairs += [("primary", "background", 4.5), ("onPrimary", "primary", 4.5),
                  ("buttonText", "buttonFill", 4.5), ("onPrimaryContainer", "primaryContainer", 4.5), ("onDanger", "danger", 4.5),
                  ("onDangerContainer", "dangerContainer", 4.5), ("danger", "surface", 4.5),
                  ("outline", "surface", 3), ("outline", "background", 3)]
        for name, palette in data["palettes"].items():
            for mode in ("light", "dark"):
                colors = {**data["neutral"][mode], **palette[mode]}
                for fg, bg, minimum in pairs:
                    with self.subTest(palette=name, mode=mode, foreground=fg, background=bg):
                        ratio = contrast(colors[fg], colors[bg])
                        self.assertGreaterEqual(ratio, minimum, f"Contrast {ratio:.2f} < {minimum}")

    def test_native_touch_target_and_legible_body_defaults(self):
        data = json.loads((ROOT / "tokens/orchard.json").read_text())
        self.assertGreaterEqual(data["size"]["touch"], 48)
        self.assertGreaterEqual(data["type"]["body"], 16)

    def test_checked_in_adapters_match_source(self):
        for path, expected in outputs().items():
            with self.subTest(path=path):
                self.assertEqual((ROOT / path).read_text(), expected)


if __name__ == "__main__":
    unittest.main()
