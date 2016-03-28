package com.lmy.smaples;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lmy.lycommon.gles.widget.CameraGLSurfaceView;
import com.lmy.lycommon.gles.widget.ShotGLSurfaceView;

public class MainActivity extends AppCompatActivity {

    private CameraGLSurfaceView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view=(CameraGLSurfaceView)findViewById(R.id.shot_view);
    }
}
