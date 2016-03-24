package com.lmy.lycommon.gles.texture;

import android.annotation.SuppressLint;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 李明艺 on 2016/2/29.
 *
 * @author lrlmy@foxmail.com
 */
public abstract class TextureRenderer {
    public static final String TAG = "TextureRenderer";

    //初始化program 等
    public abstract boolean init(boolean isExternalOES);

    //为了保证GLContext 的对应， 不能等待finalize
    public abstract void release();

    public abstract void renderTexture(int texID, Viewport viewport);

    public abstract void setTextureSize(int width, int height);

    public abstract String getVertexShaderString();

    public abstract String getFragmentShaderString();

    protected static final String REQUIRE_STRING_EXTERNAL_OES = "#extension GL_OES_EGL_image_external : require\n";
    protected static final String SAMPLER2D_VAR_EXTERNAL_OES = "samplerExternalOES";
    protected static final String SAMPLER2D_VAR = "sampler2D";

    protected static final String vshDrawDefault = "" +
            "attribute vec2 vPosition;\n" +
            "varying vec2 texCoord;\n" +
            "uniform mat4 transform;\n" +
            "uniform mat2 rotation;\n" +
            "uniform vec2 flipScale;\n" +
            "void main()\n" +
            "{\n" +
            "   gl_Position = vec4(vPosition, 0.0, 1.0);\n" +
            "   vec2 coord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n" +
            "   texCoord = (transform * vec4(coord, 0.0, 1.0)).xy;\n" +
            "}";


    protected static final String POSITION_NAME = "vPosition";
    protected static final String ROTATION_NAME = "rotation";
    protected static final String FLIPSCALE_NAME = "flipScale";
    protected static final String TRANSFORM_NAME = "transform";

    public static final float[] vertices = {-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
    public static final int DRAW_FUNCTION = GLES20.GL_TRIANGLE_FAN;

    protected int TEXTURE_2D_BINDABLE;

    protected int mVertexBuffer;
    protected GLProgram mProgram;

    protected int mTextureWidth, mTextureHeight;

    protected int mRotationLoc, mFlipScaleLoc, mTransformLoc;

    public static int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    //设置界面旋转弧度 -- 录像时一般是 PI / 2 (也就是 90°) 的整数倍
    public void setRotation(float rad) {
        final float cosRad = (float) Math.cos(rad);
        final float sinRad = (float) Math.sin(rad);

        float rot[] = new float[]{
                cosRad, sinRad,
                -sinRad, cosRad
        };

        assert mProgram != null : "setRotation must not be called before init!";

        mProgram.bind();
        /**
         * 更改一个uniform变量或数组的值。要更改的uniform变量的位置由location指定，location的值应该由glGetUniformLocation函数返回。通过调用glUseProgram，glUniform操作的程序对象将成为当前状态的一部分
         * glUniform{1|2|3|4}{f|i}使用传进来的实参，修改通过location指定的uniform变量。
         * 所有在程序对象中定义的活动uniform变量，在程序对象链接成功后都会被初始化为0.直到下一次程序对象链接成功再一次被初始化为0前，它们将保留通过调用glUniform赋给它们的值。
         * glUniform{1|2|3|4}{f|i}v可以用来更改单个uniform变量的值，或者一个uniform变量数组。
         * glUniformMatrix{2|3|4}fv用来更改一个矩阵或一个矩阵数组。
         * location:指明要更改的uniform变量的位置
         * count:指明要更改的矩阵个数
         * transpose:指明是否要转置矩阵，并将它作为uniform变量的值。必须为GL_FALSE。
         * value:指明一个指向count个元素的指针，用来更新指定的uniform变量。
         */
        GLES20.glUniformMatrix2fv(mRotationLoc, 1, false, rot, 0);
    }

    public void setFlipscale(float x, float y) {
        mProgram.bind();
        GLES20.glUniform2f(mFlipScaleLoc, x, y);
    }

    public void setTransform(float[] matrix) {
        mProgram.bind();
        GLES20.glUniformMatrix4fv(mTransformLoc, 1, false, matrix, 0);
    }

    protected boolean setProgramDefualt(String vsh, String fsh, boolean isExternalOES) {
        TEXTURE_2D_BINDABLE = isExternalOES ? GLES11Ext.GL_TEXTURE_EXTERNAL_OES : GLES20.GL_TEXTURE_2D;
        mProgram = new GLProgram();
        mProgram.bindAttribLocation(POSITION_NAME, 0);
        String fshResult = (isExternalOES ? REQUIRE_STRING_EXTERNAL_OES : "") + String.format(fsh, isExternalOES ? SAMPLER2D_VAR_EXTERNAL_OES : SAMPLER2D_VAR);
        if (mProgram.init(vsh, fshResult)) {
            mRotationLoc = mProgram.getUniformLoc(ROTATION_NAME);
            mFlipScaleLoc = mProgram.getUniformLoc(FLIPSCALE_NAME);
            mTransformLoc = mProgram.getUniformLoc(TRANSFORM_NAME);
            setRotation(0.0f);
            setFlipscale(1.0f, 1.0f);
            setTransform(new float[]{
                    1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f
            });
            return true;
        }
        return false;
    }

    @SuppressLint("Assert")
    protected void defaultInitialize() {
        int[] vertexBuffer = new int[1];
        GLES20.glGenBuffers(1, vertexBuffer, 0);//在buffers数组中返回当前n个未使用的名称，表示缓冲区对象
        mVertexBuffer = vertexBuffer[0];

        assert mVertexBuffer != 0 : "Invalid VertexBuffer!";

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBuffer);//指定当前活动缓冲区的对象
        FloatBuffer buffer = FloatBuffer.allocate(vertices.length);
        buffer.put(vertices).position(0);
        /**
         * target:可以是GL_ARRAY_BUFFER()（顶点数据）或GL_ELEMENT_ARRAY_BUFFER(索引数据)
         * size:存储相关数据所需的内存容量
         * data:用于初始化缓冲区对象，可以是一个指向客户区内存的指针，也可以是NULL
         * usage:数据在分配之后如何进行读写,如GL_STREAM_READ，GL_STREAM_DRAW(数据只指定一次，但可以多次作为绘图...)，GL_STREAM_COPY
         */
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 32, buffer, GLES20.GL_STATIC_DRAW);
    }
}
