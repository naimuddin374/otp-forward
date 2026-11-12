#!/bin/sh
set -eu
cd "$(dirname "$0")"

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "${JAVA_HOME}/bin/java" ]; then
  for candidate in \
    "/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
    "$HOME/Applications/Android Studio.app/Contents/jbr/Contents/Home"
  do
    if [ -x "$candidate/bin/java" ]; then
      JAVA_HOME=$candidate
      break
    fi
  done
fi

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "${JAVA_HOME}/bin/java" ]; then
  echo "No Java runtime found. Install Android Studio, or set JAVA_HOME to a JDK." >&2
  exit 1
fi

export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

./gradlew assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk sms-forward.apk
echo "Built $(pwd)/sms-forward.apk"
