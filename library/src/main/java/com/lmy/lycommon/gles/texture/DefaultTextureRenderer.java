package com.lmy.lycommon.gles.texture;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by 李明艺 on 2016/2/29.
 *
 * @author lrlmy@foxmail.com
 */
public class DefaultTextureRenderer extends TextureRenderer {

    private static final String fshDrawOrigin = "" +
            "precision mediump float;\n" +
            "varying vec2 texCoord;\n" +
            "uniform %s inputImageTexture;\n" +
            "void main()\n" +
            "{\n" +
            "   gl_FragColor = texture2D(inputImageTexture, texCoord);\n" +
            "}";

    //初始化默认的顶点序列等。
    public DefaultTextureRenderer() {
        defaultInitialize();
    }

    public DefaultTextureRenderer(boolean noDefaultInitialize) {
        if (!noDefaultInitialize)
            defaultInitialize();
    }

    public static DefaultTextureRenderer create(boolean isExternalOES) {
        DefaultTextureRenderer renderer = new DefaultTextureRenderer();
        if (!renderer.init(isExternalOES)) {
            renderer.release();
            Log.e(TAG, "纹理处理创建失败...");
            return null;
        }
        return renderer;
    }

    @Override
    public boolean init(boolean isExternalOES) {
        return setProgramDefualt(getVertexShaderString(), getFragmentShaderString(), isExternalOES);
    }

    @Override
    public void release() {
        GLES20.glDeleteBuffers(1, new int[]{mVertexBuffer}, 0);
        mVertexBuffer = 0;
        mProgram.release();
        mProgram = null;
    }

    @Override
    public void renderTexture(int texID, Viewport viewport) {
        if (viewport != null) {
            GLES20.glViewport(viewport.x, viewport.y, viewport.width, viewport.height);
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//指定哪一个纹理单元被置为活动状态。texture必须是GL_TEXTUREi之一，其中0 <= i < GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS，初始值为GL_TEXTURE0。
        GLES20.glBindTexture(TEXTURE_2D_BINDABLE, texID);// 图形绘制

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(0);// 允许使用顶点颜色数组,当glDrawArrays或者glDrawElements被调用时，顶点属性数组会被使用。
        /**
         * index: 指定要修改的顶点着色器中顶点变量id；
         * size:指定每个顶点属性的组件数量。必须为1、2、3或者4。如position是由3个（x,y,z）组成，而颜色是4个（r,g,b,a））；
         * type:指定数组中每个组件的数据类型。
         * normalized:指定当被访问时，固定点数据值是否应该被归一化（GL_TRUE）或者直接转换为固定点值（GL_FALSE）；
         * stride:指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0。如果normalized被设置为GL_TRUE，意味着整数型的值会被映射至区间[-1,1](有符号整数)，或者区间[0,1]（无符号整数），反之，这些值会被直接转换为浮点值而不进行归一化处理；
         * ptr:顶点的缓冲数据。
         */
        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 0, 0);

        mProgram.bind();
        GLES20.glDrawArrays(DRAW_FUNCTION, 0, 4);//从一个数据数组中提取数据渲染基本图元，与glDrawElements作用一样
    }

    @Override
    public void setTextureSize(int w, int h) {
        mTextureWidth = w;
        mTextureHeight = h;
    }

    @Override
    public String getVertexShaderString() {
        return vshDrawDefault;
    }

    @Override
    public String getFragmentShaderString() {
        return fshDrawOrigin;
    }
}
