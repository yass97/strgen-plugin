# strgen-plugin
Android String Accessor Generator

## Features
Automatically generates `StrGen.kt` at build time

## Installation

### strgen-plugin
```bash
./gradlew publishToMavenLocal
```

### app
> settings.gradle.kts
```kotlin
pluginManagement {
    repositories {
        mavenLocal()
    }
}
```
> build.gradle.kts
```kotlin
plugins {
    id("io.github.yass97.strgen") version "1.0.0"
}
```

## Example

### strings.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">My App</string>
    <string name="sample_result_success">success</string>
    <string name="sample_result_failure">failure</string>
</resources>
```

### StrGen.kt
Output to `build/generated/source/strGen`.

```kotlin
object StrGen {
    class StringResource(private val resId: Int) {
        fun get(context: Context): String = context.getString(resId)
    }

    val appName = StringResource(R.string.app_name)
    val sampleResultSuccess = StringResource(R.string.sample_result_success)
    val sampleResultFailure = StringResource(R.string.sample_result_failure)
}
```

### Activity.kt
```kotlin
StrGen.appName.get(this)
StrGen.sampleResultSuccess.get(this)
StrGen.sampleResultFailure.get(this)
```
