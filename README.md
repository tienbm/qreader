#QREader    [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.nisrulz/qreader/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.nisrulz/qreader) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-QREader-green.svg?style=true)](https://android-arsenal.com/details/1/3478)

Android library using google's mobile vision api to read QR Code 

#Integration
- QREader is available in the MavenCentral, so getting it as simple as adding it as a dependency
```gradle
compile 'com.github.nisrulz:qreader:1.0.5'
```

#Usage
+ First add a `SurfaceView` to your layout and give it an id
```java
<SurfaceView
        android:id="@+id/camera_view"
        android:layout_width="320dp"
        android:layout_height="320dp"/>
```

+ Then in your Activity code reference the `SurfaceView`
```java
SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camera_view);
```

+ Next setup the config with `QRDataListener` as the last argument in any of the `setConfig()` calls
    + The default config uses autofocus, back camera and preview size set at 800x800 and is referenced as below
    ```java
    QREader.getInstance().setUpConfig(new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QREader", "Value : " + data);
                
                // Post data on UI Thread
                textView_qrcode_info.post(new Runnable() {
                    @Override
                    public void run() {
                        textView_qrcode_info.setText(data);
                    }
                });
            }
        });
    ```
    
    + There are other config methods to give you more granular configurations
    ```java
    // Disable/Enable autofocus
    // Choose between Front facing or Back facing camera | Possible arguments : CameraSource.CAMERA_FACING_BACK /  CameraSource.CAMERA_FACING_FRONT
     public void setUpConfig(boolean autofocus_enabled, int facing, QRDataListener qrDataListener) {
    // Change all the config values
     public void setUpConfig(boolean autofocus_enabled, int width, int height, int facing, QRDataListener qrDataListener) {
    ```   

+ Call `QREader.getInstance().init()` with required arguments in your Activity code, to start reading QR code.
```java
QREader.getInstance().init(this, surfaceView);
```

*where*

|argument|type|
|---|---|
|this|`Context`|
|surfaceView|`SurfaceView`|


+ To start QR code detection
```java
QREader.getInstance().start();
```
+ To stop QR code detection
```java
QREader.getInstance().stop();
```
+ To `releaseAndCleanup` by QREader
```java
QREader.getInstance().releaseAndCleanup();
```

A typical use case would be , which works well with locking your device and when the app goes into background and then comes back in foreground
```java
  @Override protected void onStart() {
    super.onStart();

    // Call in onStart
    QREader.getInstance().start();
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    // Call in onDestroy
    QREader.getInstance().stop();
    QREader.getInstance().releaseAndCleanup();
  }
```


> NOTE : 

> 1. The library uses `android.permission.CAMERA` permission implicitly. For Android 
platforms Marshmallow and above you need to make sure the permission is requested during  
runtime and granted for QREader to function.

> 2. The `onDetected(final String data)` function call returns the data string on a different
 thread, so in order for you to show the result on the main thread you need to use a handler. 

#####  Checkout the sample app for implementation


P.S : You can use this nice [QR Code generator](https://www.the-qrcode-generator.com/) to test.

License
=======

    Copyright 2016 Nishant Srivastava

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
