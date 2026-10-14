#!/usr/bin/env bash
# Generates a Play Store upload keystore for LurkBD Agent.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
KS_DIR="$ROOT/keystore"
KS_FILE="$KS_DIR/lurkbd-release.jks"
PROPS="$ROOT/keystore.properties"

mkdir -p "$KS_DIR"
STORE_PASS="${STORE_PASS:-LurkBDStore2026!}"
KEY_PASS="${KEY_PASS:-LurkBDKey2026!}"

if [[ -f "$KS_FILE" ]]; then
  echo "Keystore already exists: $KS_FILE"
else
  keytool -genkeypair -v \
    -storetype JKS \
    -keystore "$KS_FILE" \
    -alias lurkbd \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass "$STORE_PASS" \
    -keypass "$KEY_PASS" \
    -dname "CN=LurkBD Agent, OU=Mobile, O=LurkBD, L=Dhaka, ST=Dhaka, C=BD"
  echo "Created $KS_FILE"
fi

cat > "$PROPS" <<EOF
storeFile=keystore/lurkbd-release.jks
storePassword=$STORE_PASS
keyAlias=lurkbd
keyPassword=$KEY_PASS
EOF

echo "Wrote $PROPS"
echo "Build signed release: ./gradlew :app:assembleRelease :app:bundleRelease"
echo "Keep keystore + passwords secret — do not commit them."
