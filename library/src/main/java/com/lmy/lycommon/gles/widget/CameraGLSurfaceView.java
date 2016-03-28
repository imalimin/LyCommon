package com.lmy.lycommon.gles.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.lmy.lycommon.camera.CameraInstance;
import com.lmy.lycommon.gles.texture.TextureRenderer;
import com.lmy.lycommon.gles.texture.DefaultTextureRenderer;
import com.lmy.lycommon.gles.texture.Viewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 李明艺 on 2016/2/29.
 *
 * @author lrlmy@foxmail.com
 */
public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private final static String TAG = "CameraGLSurfaceView";
    private int mTextureID = -1;
    private SurfaceTexture mSurfaceTexture;
    private Viewport drawViewport;
    private TextureRenderer mRenderer;//纹理渲染及绘制

    public int viewWidth;
    public int viewHeight;
    //时间戳相关
    private long mTimeCount = 0;//总时间
    private long mFramesCount = 0;//当前帧率
    private long mLastTimestamp = 0;//上一个时间戳

    public CameraGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void reSize() {
        float camHeight = (float) cameraInstance().previewWidth();
        float camWidth = (float) cameraInstance().previewHeight();

        float scale = Math.min(viewWidth / camWidth, viewHeight / camHeight);
        int width = (int) (camWidth * scale);
        int height = (int) (camHeight * scale);
        reSize(width, height);
    }

    public void reSize(int width, int height) {
        getLayoutParams().width = width;
        getLayoutParams().height = height;
        requestLayout();
    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    private void measureMonthView(int widthMeasureSpec, int heightMeasureSpec) {
//        Display display = getDisplay();
//        int width = display.getWidth();
//        int height = (int) (width * 4 / (float) 3);
//        Log.v(TAG, String.format("width=%d, height=%d", width, height));
//        setMeasuredDimension(width, height);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void init() {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 8, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        setZOrderOnTop(true);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    private void initRenderer() {
        mRenderer = DefaultTextureRenderer.create(true);
        mRenderer.setRotation(-(float) (Math.PI / 2.0));
        mRenderer.setFlipscale(1.0f, 1.0f);
    }

    private void calcViewport() {
        float camHeight = (float) cameraInstance().previewWidth();
        float camWidth = (float) cameraInstance().previewHeight();

        drawViewport = new Viewport();
        float scale = camWidth / camHeight;

//        float scale = Math.min(viewWidth / camWidth, viewHeight / camHeight);
//        drawViewport.width = (int) (camWidth * scale);
//        drawViewport.height = (int) (camHeight * scale);
        drawViewport.width = viewWidth;
        drawViewport.height = (int) (viewWidth / scale);
        drawViewport.x = (viewWidth - drawViewport.width) / 2;
        drawViewport.y = (viewHeight - drawViewport.height) / 2;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        Log.i(TAG, "onFrameAvailable...");
        if (mLastTimestamp == 0)
            mLastTimestamp = mSurfaceTexture.getTimestamp();

        long currentTimestamp = mSurfaceTexture.getTimestamp();

        ++mFramesCount;
        mTimeCount += currentTimestamp - mLastTimestamp;
        mLastTimestamp = currentTimestamp;
        if (mTimeCount >= 1e9) {
//            Log.i(TAG, String.format("LastTimestamp: %d TimeCount: %d, Fps: %d", mLastTimestamp, mTimeCount, mFramesCount));
            mTimeCount -= 1e9;
            mFramesCount = 0;
        }
        requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.v(TAG, "onSurfaceCreated...");
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_STENCIL_TEST);

        mTextureID = TextureRenderer.createTextureID();
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
//        mTextureDrawer = new TextureDrawer(mTextureID);
        initRenderer();
        cameraInstance().tryOpenCamera(new CameraInstance.CameraOpenCallback() {
            @Override
            public void cameraReady() {
                Log.i(TAG, "tryOpenCamera OK...");
            }
        });
        requestRender();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        viewWidth = width;
        viewHeight = height;
        Log.v(TAG, String.format("width=%d, height=%d", viewWidth, viewHeight));

        if (!cameraInstance().isPreviewing()) {
            cameraInstance().startPreview(mSurfaceTexture);
        }
        calcViewport();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        if (mRenderer != null) {
            mRenderer.release();
            mRenderer = null;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);
        //画面变换
        mRenderer.setTransform(mtx);
        //纹理绘制
        mRenderer.renderTexture(mTextureID, drawViewport);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume...");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.i(TAG, "onPause...");
        cameraInstance().stopCamera();
    }

    public CameraInstance cameraInstance() {
        return CameraInstance.getInstance(1920, 1080);
    }

    public Viewport getDrawViewport() {
        return drawViewport;
    }
}
