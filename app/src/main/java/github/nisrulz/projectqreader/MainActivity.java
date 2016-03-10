package github.nisrulz.projectqreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class MainActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView textView_qrcode_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
        textView_qrcode_info = (TextView) findViewById(R.id.code_info);

        QREader.start(this, surfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QREader", "Value : " + data);
                textView_qrcode_info.post(new Runnable() {
                    @Override
                    public void run() {
                        textView_qrcode_info.setText(data);
                    }
                });
            }
        });
    }
}
