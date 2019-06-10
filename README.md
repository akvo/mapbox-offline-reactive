# mapbox-offline-reactive [![Build Status](https://travis-ci.org/akvo/mapbox-offline-reactive.svg?branch=master)](https://travis-ci.org/akvo/mapbox-offline-reactive) [![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15) [![](https://jitpack.io/v/akvo/mapbox-offline-reactive.svg)](https://jitpack.io/#akvo/mapbox-offline-reactive)

Wrapper aroung [Mapbox](https://github.com/mapbox/mapbox-gl-native/tree/master/platform/android) offline areas callbacks with RxJava

## Download
Step1. Add this code to your project's root `build.gradle` at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Step 2. Add the dependency in your apps `build.gradle`
```
dependencies {
  implementation 'com.github.akvo:mapbox-offline-reactive:v1.0'
}
```

## Usage
You will need to setup a mapbox account and get an API Key, please see [their documentation](https://docs.mapbox.com/android/maps/overview/).
See [MainActivity](https://github.com/akvo/mapbox-offline-reactive/blob/master/app/src/main/java/org/akvo/flow/mapbox/offline/reactive/example/MainActivity.kt) for an example of how to use it.

✅ Currently it's possible to:
  * create new offline areas 
  * list existing areas.

❌ Coming soon: 
  * delete area
  * rename area
