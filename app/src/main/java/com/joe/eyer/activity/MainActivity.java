package com.joe.eyer.activity;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;

import com.joe.eyer.R;
import com.joe.eyer.compat.CameraCompatUtil;
import com.joe.eyer.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    private CameraCompatUtil cameraCompatUtil;
    private TextureView textureView;
    private View shadeView;
    private int surfaceWidth, surfaceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraCompatUtil = new CameraCompatUtil();
        shadeView = findViewById(R.id.open_camera);
        shadeView.setOnClickListener(clickListener);
        textureView = (TextureView) findViewById(R.id.surface_camera);
        textureView.setSurfaceTextureListener(textureListener);
        findViewById(R.id.btn_set).setOnClickListener(clickListener);
    }

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            surfaceWidth = width;
            surfaceHeight = height;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            surfaceWidth = width;
            surfaceHeight = height;
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.open_camera:
                    cameraCompatUtil.openCamera(MainActivity.this, openCallBack);
                    break;
                case R.id.btn_set:
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private CameraCompatUtil.OnCameraOpenCallBack openCallBack = new CameraCompatUtil.OnCameraOpenCallBack() {
        @Override
        public void onOpened() {
            shadeView.post(new Runnable() {
                @Override
                public void run() {
                    shadeView.setVisibility(View.GONE);
                    textureView.setVisibility(View.VISIBLE);
                    ToastUtils.show(MainActivity.this, "相机打开成功");
                }
            });
            cameraCompatUtil.setPreview(textureView, surfaceWidth, surfaceHeight);
        }

        @Override
        public void onClosed() {

        }

        @Override
        public void onFailed() {
            shadeView.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show(MainActivity.this, "相机打开失败");
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraCompatUtil.destroy();
    }
}
