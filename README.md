# Logger

Android logging library (`apps.in:logger:1.5.0`).

Requires **minSdk 23**.

## How to use

Add the GitHub Packages repository. In `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://maven.pkg.github.com/apps-in/Logger' }
    }
}
```

GitHub Packages may require credentials with `read:packages` permission.

Add the dependency to the app-level `build.gradle`:

```gradle
implementation 'apps.in:logger:1.5.0'
```

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

Publishing is already configured in `android_logger/build.gradle` (`maven-publish`, `components.release`).

Set environment variables with a GitHub user name and a token that has **package:write** permission:

```
GITHUB_PKG_USER
GITHUB_PKG_TOKEN
```

Run the publish script:

Linux or macOS:

```sh
./publish.sh
```

Windows:

```bat
publish.bat
```

## License

MIT
