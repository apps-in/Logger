# Logger

Android logging library (`io.github.ihor-nepomniashchyi:logger:1.5.0`).

Requires **minSdk 23**.

## How to use

Add the dependency to the app-level `build.gradle`:

```gradle
implementation 'io.github.ihor-nepomniashchyi:logger:1.5.0'
```

The artifact is published to Maven Central, so no extra repository configuration is required when `mavenCentral()` is already enabled.

Initialize the logger before the first log call, typically in `Application.onCreate()`:

```java
import apps.in.android_logger.InLogger;

InLogger.initializeLogger(this)
        .setAppId("My app id")
        .setAppVersion("My app version")
        .writeToConsole("My tag")
        .writeToFile()
        .initialize();
```

Optional initializer methods:

- `writeToFile(int maxDays, int minCount)` — keep at least `minCount` log files and drop older files after `maxDays` (defaults are 1 day and 2 files).
- `setExternalLogger(ExternalLogger)` — extra sink called for every log line (Crashlytics, network, and so on). This is a callback, not Android external storage.

```java
InLogger.initializeLogger(this)
        .writeToConsole("My tag")
        .writeToFile()
        .setExternalLogger((tag, message) -> {
            // forward the line to another logger
        })
        .initialize();
```

Log with `InLogger.log()` / `InLogger.logWithTag()`:

```java
InLogger.log("plain message");
InLogger.log(this, "message with component name");
InLogger.log(this, "request failed", throwable);
InLogger.logWithTag("NETWORK", this, "timeout");
```

Overloads accept a context object or string, plus `Bundle`, `Intent`, or `Throwable`.

Share or zip files written by `writeToFile()`:

```java
InLogger.shareLog(activity, "Send logs");
InLogger.shareLog(activity, "Send logs", true); // zip
String zipPath = InLogger.getLogZip();
```

Uncaught exceptions are recorded. After a crash, `InLogger.hasUncheckedCrashes()` returns `true` once.

To log Android lifecycle events, extend `LogActivity`, `LogFragment`, `LogDialogFragment`, `LogService`, `LogPreferenceActivity`, or `LogPreferenceFragment`.

## How to publish update

Publishing to Maven Central is configured in `android_logger/build.gradle` via the [Gradle Maven Publish Plugin](https://github.com/vanniktech/gradle-maven-publish-plugin).

### Prerequisites

1. Namespace `io.github.ihor-nepomniashchyi` registered and verified on [Central Portal](https://central.sonatype.com/).
2. User token generated in Central Portal (Account → Generate User Token).
3. GPG key created and the public key uploaded to a keyserver (required for release builds).

### Credentials

Add the following to `~/.gradle/gradle.properties` (see `gradle/publishing.properties.example`):

```properties
mavenCentralUsername=YOUR_CENTRAL_PORTAL_USERNAME
mavenCentralPassword=YOUR_CENTRAL_PORTAL_PASSWORD

signing.useGpgCmd=true
signing.keyId=21912616
signing.password=YOUR_GPG_KEY_PASSWORD
```

On Windows with modern GnuPG, do **not** use `signing.secretKeyRingFile` (`~` is not expanded, and keyboxd keys are incompatible with the legacy key ring format).

If `useGpgCmd` still fails, use the in-memory key script:

```powershell
.\publish.ps1 -GpgPassword 'YOUR_GPG_KEY_PASSWORD'
```

For CI, use `ORG_GRADLE_PROJECT_`-prefixed environment variables instead (see the example file).

### Publish

Linux or macOS:

```sh
./publish.sh
```

Windows:

```bat
publish.bat
```

This runs `publishAndReleaseToMavenCentral`, which uploads the signed artifacts, validates the deployment, and releases it to Maven Central automatically.

## License

MIT
