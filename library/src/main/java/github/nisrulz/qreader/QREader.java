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

public class QREader {

    private static final String TAG = "QREader";
    private static BarcodeDetector barcodeDetector;
    private static CameraSource cameraSource;

    public static void start(final Context context, final SurfaceView surfaceView, final QRDataListener QRDataListener) {
        barcodeDetector =
                new BarcodeDetector.Builder(context)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(context, barcodeDetector)
                .setRequestedPreviewSize(800, 800)
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
                        startCameraView(context, cameraSource, surfaceView);
                    }
                });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    QRDataListener.onDetected(barcodes.valueAt(0).displayValue);
                }
            }
        });

    }

    private static void startCameraView(Context context, CameraSource cameraSource, SurfaceView
            surfaceView) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(surfaceView.getHolder());
            } else {
                Log.e(TAG, "Permission not granted!");
            }
        } catch (IOException ie) {
            Log.e(TAG, ie.getMessage());
            ie.printStackTrace();
        }

    }
}

