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

+ Next create an object of `QREader` using the `Builder` in your `onCreate()`
    ```java
    QREader qrEader;
    .
    ..
    ...
     @Override protected void onCreate(Bundle savedInstanceState) {
        ..
    
        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
    
        qrEader = new QREader.Builder(this, surfaceView, new QRDataListener() {
          @Override public void onDetected(final String data) {
            // Do something with the string data
            
            Log.d("QREader", "Value : " + data);
          }
        }).build();
      }    
    ```
    
    *where* 
    + `Builder` takes in arguments as `Builder(context, surfaceview, qrdatalistner)`
    +  To modify further you can call below functions on the `Builder` before calling the `build()`
      + `enableAutofocus(boolean autofocus_enabled)`  // Default is `true`
      + `width(int width)` // Default is `800`
      + `height(int height)` // Default is `800`
      + `facing(int facing)` // Default is `QREader.BACK_CAM`
        + where argument can be one of  `QREader.BACK_CAM` , `QREader.FRONT_CAM`

+ Next Call ` qrEader.init()`, right after you `build()` your object using the `Builder`.
    ```java
     @Override protected void onCreate(Bundle savedInstanceState) {
        ..
      
        qrEader = new QREader.Builder(this, surfaceView, new QRDataListener() {
          @Override public void onDetected(final String data) {
            ..
          }
        }).build();
        
        // Call Init
        qrEader.init();
     }    
    ```

+ To start QR code detection, call `start()` on the `qreader` object 
```java
qrEader.start();
```
+ To stop QR code detection, call `stop()` on the `qreader` object 
```java
qrEader.stop();
```
+ To release and cleanup , call `releaseAndCleanup()` on the `qreader` object 
```java
qrEader.releaseAndCleanup();
```

A typical use case would be , which works well with locking your device and when the app goes into background and then comes back in foreground
```java
   private SurfaceView surfaceView;
   private TextView textView_qrcode_info;
   QREader qrEader;
 
   @Override protected void onCreate(Bundle savedInstanceState) {
     ...
     ...
     
     surfaceView = (SurfaceView) findViewById(R.id.camera_view);
     textView_qrcode_info = (TextView) findViewById(R.id.code_info);
 
     qrEader = new QREader.Builder(this, surfaceView, new QRDataListener() {
       @Override public void onDetected(final String data) {
         Log.d("QREader", "Value : " + data);
         textView_qrcode_info.post(new Runnable() {
           @Override public void run() {
             textView_qrcode_info.setText(data);
           }
         });
       }
     }).build();
 
     qrEader.init();
   }
 
   @Override protected void onStart() {
     super.onStart();
     ...
 
     // Call in onStart
     qrEader.start();
   }
 
   @Override protected void onDestroy() {
     super.onDestroy();
     ..
 
     // Call in onDestroy
     qrEader.stop();
     qrEader.releaseAndCleanup();
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
