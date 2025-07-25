#!/bin/bash

# Exit on error
set -e

# Set environment variables for test execution
export ANDROID_SERIAL=emulator-5554
export ADB_INSTALL_TIMEOUT=300  # 5 minutes
export GRADLE_OPTS="-Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8"

# Clean the project
echo "🔨 Cleaning project..."
./gradlew clean

# Run unit tests with coverage
echo "🚀 Running unit tests..."
./gradlew testDebugUnitTest \
  --parallel \
  --max-workers=4 \
  --info \
  --rerun-tasks \
  --tests "com.secondbrain.app.*" || { echo "❌ Unit tests failed"; exit 1; }

# Run instrumented tests
echo "📱 Running instrumented tests..."
./gradlew connectedDebugAndroidTest \
  --parallel \
  --max-workers=2 \
  --info \
  --no-scan \
  --full-stacktrace \
  --console=plain \
  --rerun-tasks \
  -Pandroid.testInstrumentationRunnerArguments.package=com.secondbrain.app || { 
    echo "❌ Instrumented tests failed"; 
    exit 1; 
  }

# Generate test reports
echo "📊 Generating test reports..."
./gradlew createDebugCoverageReport
./gradlew jacocoTestReport

# Open the test report in default browser
echo "📂 Opening test reports..."
if [[ "$OSTYPE" == "darwin"* ]]; then
  open app/build/reports/tests/debug/index.html
  open app/build/reports/coverage/debug/report-html/index.html
  open app/build/reports/jacoco/jacocoTestReport/html/index.html
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
  xdg-open app/build/reports/tests/debug/index.html
  xdg-open app/build/reports/coverage/debug/report-html/index.html
  xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html
fi

echo "✅ All tests completed successfully!"
