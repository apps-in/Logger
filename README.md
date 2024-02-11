# Logger

Android logging library

## How to use

Add maven repository to the build.gradle (project level)

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://maven.pkg.github.com/apps-in/Logger' }
    }
}
```

Add logger dependency to the build.gradle (app level)

```gradle
implementation 'apps.in:logger:[vesrion number. ]'
```

Initialize logger before first use

```java
  Logger.initializeLogger(this)
                .setAppId("My app id")
                .setAppVersion("My app version")
                .writeToConsole("My tag")
                .writeToFile()
                .initialize();
```

Log your data with on of the **Logger.log()** method

## How to publish update

Add **publishing** section to build.gradle (app level)

```gradle
publishing {
    publications {
        bar(MavenPublication){
            groupId "apps.in"
            artifactId "logger"
            version "${versionName}"
            artifact ("$buildDir/outputs/aar/android_logger-release.aar")
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/apps-in/Logger")
            credentials {
                username = System.getenv("GITHUB_PKG_USER")
                password = System.getenv("GITHUB_PKG_TOKEN")
            }
        }
    }
}
```

Setup environment variables with your github user name and token with **package:write** permission
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
