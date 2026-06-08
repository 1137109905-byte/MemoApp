#!/bin/bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export PATH="$JAVA_HOME/bin:$PATH"
export ANDROID_HOME=~/Library/Android/sdk
cd /Users/fengjin/MemoApp

echo "=== Build started at $(date) ===" > /tmp/memo_build.log

~/.gradle/wrapper/dists/gradle-8.5-all/3zlzzgtsutfj0pbojr50n2l7z/gradle-8.5/bin/gradle assembleDebug --no-daemon >> /tmp/memo_build.log 2>&1

EXIT_CODE=$?
echo "=== Build finished at $(date) with exit code $EXIT_CODE ===" >> /tmp/memo_build.log
exit $EXIT_CODE
