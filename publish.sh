#!/usr/bin/env sh
set -e
./gradlew :android_logger:assembleRelease
./gradlew :android_logger:publishAndReleaseToMavenCentral
