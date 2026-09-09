# Logger

Android logging library

## How to use

Add maven repository to the build.gradle (project level)

```gradle
    repositories {
        ...
        maven { url 'https://maven.pkg.github.com/apps-in/Logger' }
    }
```

Add logger dependency to the build.gradle (app level)

```gradle
implementation 'apps.in:logger:1.5.0'
```

Initialize logger before first use

```java
  InLogger.initializeLogger(this)
                .setAppId("My app id")
                .setAppVersion("My app version")
                .writeToConsole("My tag")
                .writeToFile()
                .initialize();
```

Log your data with one of the **InLogger.log()** methods

## How to publish update

Publishing is configured in `android_logger/build.gradle` via `maven-publish` and `components.release`.

Setup environment variables with your GitHub user name and token with **package:write** permission
```
GITHUB_PKG_USER
GITHUB_PKG_TOKEN
```

Run publish script:

for Linux or MacOs
```sh
publish.sh
```
for Windows
```sh
publish.bat
```

## License

MIT
