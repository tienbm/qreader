/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.qreader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * QrEader Singleton
 */
public class QREader {

    private static final String TAG = "QREader";
    private CameraSource cameraSource = null;
    private BarcodeDetector barcodeDetector = null;

    private boolean autofocus_enabled;
    private int width;
    private int height;
    private int facing;
    private boolean cameraRunning = false;
    private QRDataListener qrDataListener;

    private static QREader INSTANCE;

    private QREader() {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static QREader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QREader();
        }
        return INSTANCE;
    }

    /**
     * Sets up config.
     *
     * @param qrDataListener the qr data listener
     */
    public void setUpConfig(final QRDataListener qrDataListener) {
        setUpConfig(true, 800, 800, CameraSource.CAMERA_FACING_BACK, qrDataListener);
    }

    /**
     * Sets up config.
     *
     * @param autofocus_enabled the autofocus enabled
     * @param facing            the facing
     * @param qrDataListener    the qr data listener
     */
    public void setUpConfig(boolean autofocus_enabled, int facing, final QRDataListener qrDataListener) {
        setUpConfig(autofocus_enabled, 800, 800, facing, qrDataListener);
    }

    /**
     * Sets up config.
     *
     * @param autofocus_enabled the autofocus enabled
     * @param width             the width
     * @param height            the height
     * @param facing            the facing
     * @param qrDataListener    the qr data listener
     */
    public void setUpConfig(boolean autofocus_enabled, int width, int height, int facing, final QRDataListener qrDataListener) {
        this.autofocus_enabled = autofocus_enabled;
        this.width = width;
        this.height = height;
        this.facing = facing;
        this.qrDataListener = qrDataListener;
    }


    /**
     * Start.
     *
     * @param context     the context
     * @param surfaceView the surface view
     */
    public void start(final Context context, final SurfaceView surfaceView) {
        if (barcodeDetector == null)
            barcodeDetector = new BarcodeDetector.Builder(context)
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();

        if (cameraSource == null)
            cameraSource = new CameraSource
                    .Builder(context, barcodeDetector)
                    .setAutoFocusEnabled(autofocus_enabled)
                    .setFacing(facing)
                    .setRequestedPreviewSize(width, height)
                    .build();

        surfaceView.getHolder()
                .addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        startCameraView(context, cameraSource, surfaceView);
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        stopQREaderAndCleanup();
                    }
                });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                if (barcodeDetector != null) {
                    barcodeDetector.release();
                    barcodeDetector = null;
                }

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0 && qrDataListener != null) {
                    qrDataListener.onDetected(barcodes.valueAt(0).displayValue);
                }
            }
        });

    }

    private void startCameraView(Context context, CameraSource cameraSource, SurfaceView surfaceView) {
        try {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission not granted!");
                return;
            } else {
                if (!cameraRunning && cameraSource != null) {
                    cameraSource.start(surfaceView.getHolder());
                    cameraRunning = true;
                }
            }

        } catch (IOException ie) {
            Log.e(TAG, ie.getMessage());
            ie.printStackTrace();
        }
    }

    /**
     * Stop QREader and cleanup.
     */
    public void stopQREaderAndCleanup() {
        try {
            if (cameraRunning && cameraSource != null) {
                cameraSource.stop();
                cameraSource.release();
                cameraRunning = false;
                cameraSource = null;
            }

            if (barcodeDetector != null) {
                barcodeDetector.release();
                barcodeDetector = null;
            }
        } catch (Exception ie) {
            Log.e(TAG, ie.getMessage());
            ie.printStackTrace();
        }
    }
}

