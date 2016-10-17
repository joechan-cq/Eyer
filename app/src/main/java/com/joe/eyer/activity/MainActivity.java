package com.joe.eyer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.joe.eyer.R;
import com.joe.eyer.compat.CameraCompatUtil;
import com.joe.eyer.config.AppConfig;
import com.joe.eyer.dialog.BaseDialog;
import com.joe.eyer.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    private CameraCompatUtil cameraCompatUtil;
    private TextureView textureView;
    private AppCompatImageView captureImg, tempImg;
    private AppCompatTextView resultTv;
    private View shadeView;
    private int surfaceWidth, surfaceHeight;
    private BaseDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraCompatUtil = new CameraCompatUtil();
        shadeView = findViewById(R.id.open_camera);
        shadeView.setOnClickListener(clickListener);
        textureView = (TextureView) findViewById(R.id.surface_camera);
        textureView.setSurfaceTextureListener(textureListener);
        captureImg = (AppCompatImageView) findViewById(R.id.img_take_capture);
        captureImg.setOnClickListener(clickListener);
        findViewById(R.id.btn_set).setOnClickListener(clickListener);

        dialog = new BaseDialog(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_capture, null, false);
        dialog.setDialogContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        tempImg = (AppCompatImageView) view.findViewById(R.id.img_capture);
        resultTv = (AppCompatTextView) view.findViewById(R.id.tv_result);
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
                case R.id.img_take_capture:
                    cameraCompatUtil.takeCapture(new CameraCompatUtil.OnTakePictureCallBack() {
                        @Override
                        public void onCapture(final Bitmap bm) {
                            captureImg.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss(MainActivity.this);
                                    }
                                    tempImg.setImageBitmap(bm);
                                    resultTv.setText("正在解析");
                                    dialog.show(MainActivity.this);
                                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            bm.recycle();
                                        }
                                    });
                                }
                            });
                            String chosen = AppConfig.getInstance().getChosenMode();
                            if (TextUtils.isEmpty(chosen)) {
                                captureImg.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        resultTv.setText("未选择解析模式");
                                    }
                                });
                                return;
                            }
                            TessBaseAPI api = new TessBaseAPI();
                            boolean success = api.init(AppConfig.getInstance().root.getAbsolutePath(), AppConfig.getInstance().getChosenMode());
                            if (!success) {
                                ToastUtils.show(MainActivity.this, "解析数据初始化失败");
                            }
                            api.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
                            api.setVariable(TessBaseAPI.VAR_SAVE_BLOB_CHOICES, TessBaseAPI.VAR_TRUE);
                            if (!bm.isRecycled()) {
                                api.setImage(bm);
                            }
                            final String result = api.getUTF8Text();
                            captureImg.post(new Runnable() {
                                @Override
                                public void run() {
                                    resultTv.setText(result);
                                }
                            });
                            api.end();
                        }
                    });
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
                    captureImg.setVisibility(View.VISIBLE);
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
    protected void onStop() {
        super.onStop();
        shadeView.setVisibility(View.VISIBLE);
        captureImg.setVisibility(View.GONE);
        cameraCompatUtil.destroy();
    }
}
