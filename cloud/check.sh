#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
if [[ -f .cloud/env.sh ]]; then source .cloud/env.sh; fi
target="${1:-quick}"
quick() {
  python3 tools/generate_tokens.py --check
  python3 tools/check_docs.py
  python3 -m unittest discover -s tools/tests -v
}
android() {
  command -v javac >/dev/null || { echo 'A full JDK 21 is required (javac missing).' >&2; return 1; }
  ./gradlew :orchard:assembleRelease :catalog:assembleDebug :catalog:testDebugUnitTest :orchard:lintDebug :catalog:lintDebug --max-workers=2
}
flutter_check() {
  command -v flutter >/dev/null || { echo 'Install Flutter 3.41.5 and add it to PATH.' >&2; return 1; }
  (cd adapters/flutter && flutter pub get && flutter analyze && flutter test)
}
react_native() {
  (cd adapters/react-native && if [[ ! -d node_modules ]]; then npm ci; fi && npm run typecheck && npm test)
}
web() {
  if [[ ! -d node_modules ]]; then npm ci; fi
  npm run test:web
}
case "$target" in
  quick) quick ;;
  android) android ;;
  flutter) flutter_check ;;
  react-native) react_native ;;
  web) web ;;
  all) quick; android; flutter_check; react_native; web ;;
  *) echo 'Usage: bash cloud/check.sh quick|android|flutter|react-native|web|all' >&2; exit 2 ;;
esac
