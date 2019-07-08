[![](https://jitpack.io/v/13mile/androidcheat.svg)](https://jitpack.io/#13mile/androidcheat)

# AndroidCheat Tool


## Setup
Add the JitPack repository in your build.gradle (top level module):
```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

And add next dependencies in the build.gradle of the module:
```gradle
dependencies {
    implementation 'com.github.13mile:androidcheat:0.0.1'
}
```