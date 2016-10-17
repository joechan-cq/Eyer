package com.joe.eyer.compat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Description
 * Created by chenqiao on 2016/10/13.
 */

public class CameraCompatUtil {

    private boolean isNew = false;

    private Surface mSurface;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder builder;
    private CameraCaptureSession mSession;
    private ImageReader imageReader;
    private Handler mHandler;
    private Camera camera;
    private HandlerThread thread;
    private Size[] supportSizes;
    private List<Camera.Size> oldSupportSizes;

    public CameraCompatUtil() {
        thread = new HandlerThread("EyerCamera");
        thread.start();
        mHandler = new Handler(thread.getLooper());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isNew = true;
        }
    }

    /**
     * 打开相机
     *
     * @param cameraOpenCallBack 结果回调
     */
    @SuppressLint("NewApi")
    public void openCamera(Context context, final OnCameraOpenCallBack cameraOpenCallBack) {
        if (isNew) {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                String[] cameraIds = cameraManager.getCameraIdList();
                if (cameraIds.length == 0) {
                    if (cameraOpenCallBack != null) {
                        cameraOpenCallBack.onFailed();
                    }
                    return;
                }
                supportSizes = cameraManager.getCameraCharacteristics(cameraIds[0]).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(SurfaceTexture.class);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (cameraOpenCallBack != null) {
                        cameraOpenCallBack.onFailed();
                    }
                    return;
                }
                cameraManager.openCamera(cameraIds[0], new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(CameraDevice camera) {
                        cameraDevice = camera;
                        try {
                            builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                        if (cameraOpenCallBack != null) {
                            cameraOpenCallBack.onOpened();
                        }
                    }

                    @Override
                    public void onDisconnected(CameraDevice camera) {
                        if (cameraOpenCallBack != null) {
                            cameraOpenCallBack.onClosed();
                        }
                    }

                    @Override
                    public void onError(CameraDevice camera, int error) {
                        if (cameraOpenCallBack != null) {
                            cameraOpenCallBack.onFailed();
                        }
                    }
                }, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Camera tempCamera = null;
                    try {
                        tempCamera = Camera.open();
                    } catch (RuntimeException e) {
                        camera = null;
                    }
                    if (tempCamera == null) {
                        if (cameraOpenCallBack != null) {
                            cameraOpenCallBack.onFailed();
                        }
                    } else {
                        camera = tempCamera;
                        Camera.Parameters parameters = camera.getParameters();
                        oldSupportSizes = parameters.getSupportedPreviewSizes();
                        if (cameraOpenCallBack != null) {
                            cameraOpenCallBack.onOpened();
                        }
                    }
                }
            });
        }
    }

    @SuppressLint("NewApi")
    public void setPreview(TextureView textureView, int width, int height) {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        Rect size = getSuitableSize(width, height);
        mSurface = new Surface(texture);
        if (isNew) {
            texture.setDefaultBufferSize(size.width(), size.height());
            imageReader = ImageReader.newInstance(size.width(), size.height(), ImageFormat.JPEG, 1);
            Log.d("CameraCompatUtil", "setPreview: " + size.toString());
            builder.addTarget(mSurface);
            try {
                cameraDevice.createCaptureSession(Arrays.asList(mSurface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(CameraCaptureSession session) {
                        mSession = session;
                        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        try {
                            mSession.setRepeatingRequest(builder.build(), null, mHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {
                        mSurface.release();
                    }
                }, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            texture.setDefaultBufferSize(size.width(), size.height());
            Log.d("CameraCompatUtil", "setPreview: " + size.toString());
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                camera.setParameters(parameters);
                camera.setPreviewTexture(texture);
                camera.setDisplayOrientation(90);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    public void takeCapture(final OnTakePictureCallBack onTakePictureCallBack) {
        if (isNew) {
            try {
                imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image.close();
                        if (onTakePictureCallBack != null) {
                            onTakePictureCallBack.onCapture(bm);
                        }
                    }
                }, mHandler);
                CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                builder.addTarget(imageReader.getSurface());
                builder.set(CaptureRequest.JPEG_ORIENTATION, 90);
                mSession.capture(builder.build(), null, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90);
                    Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    if (onTakePictureCallBack != null) {
                        onTakePictureCallBack.onCapture(bitmap2);
                    }
                    camera.startPreview();
                }
            });
        }
    }

    @SuppressLint("NewApi")
    public void destroy() {
        if (isNew) {
            imageReader.close();
            cameraDevice.close();
        } else {
            camera.stopPreview();
            camera.release();
        }
        mSurface.release();
        thread.interrupt();
    }

    @SuppressLint("NewApi")
    private Rect getSuitableSize(int surfaceWidth, int surfaceHeight) {
        if (isNew) {
            if (supportSizes.length == 0) {
                return new Rect(0, 0, 480, 320);
            }
            for (Size supportSize : supportSizes) {
                if (surfaceWidth >= supportSize.getWidth() && surfaceHeight >= supportSize.getHeight()) {
                    return new Rect(0, 0, supportSize.getWidth(), supportSize.getHeight());
                }
            }
            Size size = supportSizes[supportSizes.length - 1];
            return new Rect(0, 0, size.getWidth(), size.getHeight());
        } else {
            if (oldSupportSizes.size() == 0) {
                return new Rect(0, 0, 480, 320);
            }
            for (Camera.Size oldSupportSize : oldSupportSizes) {
                if (surfaceWidth >= oldSupportSize.width && surfaceHeight >= oldSupportSize.height) {
                    return new Rect(0, 0, oldSupportSize.width, oldSupportSize.height);
                }
            }
            int last = oldSupportSizes.size() - 1;
            return new Rect(0, 0, oldSupportSizes.get(last).width, oldSupportSizes.get(last).height);
        }
    }

    public interface OnTakePictureCallBack {
        void onCapture(Bitmap bm);
    }

    public interface OnCameraOpenCallBack {
        void onOpened();

        void onClosed();

        void onFailed();
    }
}