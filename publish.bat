@echo off
call gradlew :android_logger:assembleRelease
call gradlew :android_logger:publishAndReleaseToMavenCentral
