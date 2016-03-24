package com.lmy.lycommon.gles.texture;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.util.Log;


/**
 * Created by 李明艺 on 2016/2/29.
 *
 * @author lrlmy@foxmail.com
 */
public class GLProgram {
    public static final String TAG = "GLProgram";
    public static final boolean DEBUG = true;

    private int mProgramID;
    private ShaderObject mVertexShader, mFragmentShader;

    //单独初始化之后可以进行一些 attribute location 的绑定操作
    //之后再进行init
    public GLProgram() {
        mProgramID = GLES20.glCreateProgram();// 创建一个空的OpenGL ES Program
    }

    public GLProgram(final String vsh, final String fsh) {
        init(vsh, fsh);
    }

    public int programID() {
        return mProgramID;
    }

    public final void release() {
        if (mProgramID != 0) {
            GLES20.glDeleteProgram(mProgramID);
            mProgramID = 0;
        }
    }

    public boolean init(final String vsh, final String fsh) {
        return init(vsh, fsh, 0);
    }

    @SuppressLint("Assert")
    public boolean init(final String vsh, final String fsh, int programID) {
        if (programID == 0)
            programID = GLES20.glCreateProgram();

        assert programID != 0 : "glCreateProgram failed!";

        if (mVertexShader != null)
            mVertexShader.release();
        if (mFragmentShader != null)
            mFragmentShader.release();

        mVertexShader = new ShaderObject(vsh, GLES20.GL_VERTEX_SHADER);
        mFragmentShader = new ShaderObject(fsh, GLES20.GL_FRAGMENT_SHADER);

        GLES20.glAttachShader(programID, mVertexShader.shaderID());//向程序中加入顶点着色器
        GLES20.glAttachShader(programID, mFragmentShader.shaderID());//向程序中加入片元着色器
        checkGLError("AttachShaders...");
        GLES20.glLinkProgram(programID);//链接程序

        int[] programStatus = {0};
        GLES20.glGetProgramiv(programID, GLES20.GL_LINK_STATUS, programStatus, 0);

        //link 完毕之后即可释放 shader object
        mVertexShader.release();
        mFragmentShader.release();
        mVertexShader = null;
        mFragmentShader = null;

        if (programStatus[0] != GLES20.GL_TRUE) {
            String msg = GLES20.glGetProgramInfoLog(programID);
            Log.e(TAG, msg);
            return false;
        }

        mProgramID = programID;
        return true;
    }

    public void bind() {
        GLES20.glUseProgram(mProgramID);//制定使用某套shader程序，传递数据之前必须调用
    }

    public int getUniformLoc(final String name) {
        int uniform = GLES20.glGetUniformLocation(mProgramID, name);//获取指向着色器中name的index
        if (DEBUG) {
            if (uniform < 0)
                Log.e(TAG, String.format("uniform name %s does not exist", name));
        }
        return uniform;
    }

    public void sendUniformf(final String name, float x) {
        GLES20.glUniform1f(getUniformLoc(name), x);
    }

    public void sendUniformf(final String name, float x, float y) {
        GLES20.glUniform2f(getUniformLoc(name), x, y);
    }

    public void sendUniformf(final String name, float x, float y, float z) {
        GLES20.glUniform3f(getUniformLoc(name), x, y, z);
    }

    public void sendUniformf(final String name, float x, float y, float z, float w) {
        GLES20.glUniform4f(getUniformLoc(name), x, y, z, w);
    }

    public void sendUniformi(final String name, int x) {
        GLES20.glUniform1i(getUniformLoc(name), x);
    }

    public void sendUniformi(final String name, int x, int y) {
        GLES20.glUniform2i(getUniformLoc(name), x, y);
    }

    public void sendUniformi(final String name, int x, int y, int z) {
        GLES20.glUniform3i(getUniformLoc(name), x, y, z);
    }

    public void sendUniformi(final String name, int x, int y, int z, int w) {
        GLES20.glUniform4i(getUniformLoc(name), x, y, z, w);
    }

    public void sendUniformMat2(final String name, int count, boolean transpose, float[] matrix) {
        GLES20.glUniformMatrix2fv(getUniformLoc(name), count, transpose, matrix, 0);
    }

    public void sendUniformMat3(final String name, int count, boolean transpose, float[] matrix) {
        GLES20.glUniformMatrix3fv(getUniformLoc(name), count, transpose, matrix, 0);
    }

    public void sendUniformMat4(final String name, int count, boolean transpose, float[] matrix) {
        GLES20.glUniformMatrix4fv(getUniformLoc(name), count, transpose, matrix, 0);
    }

    public int attributeLocation(final String name) {
        return GLES20.glGetAttribLocation(mProgramID, name);
    }

    public void bindAttribLocation(final String name, int index) {
        GLES20.glBindAttribLocation(mProgramID, index, name);//把program的顶点属性索引与顶点shader中的变量名进行绑定
    }

    /**
     * Created by wangyang on 15/7/18.
     */
    public static class ShaderObject {

        private int mShaderType;
        private int mShaderID;


        public int shaderID() {
            return mShaderID;
        }

        public ShaderObject() {
            mShaderType = 0;
            mShaderID = 0;
        }

        public ShaderObject(final String shaderCode, final int shaderType) {
            init(shaderCode, shaderType);
        }

        @SuppressLint("Assert")
        public boolean init(final String shaderCode, final int shaderType) {
            mShaderType = shaderType;
            mShaderID = loadShader(shaderType, shaderCode);

            //Debug Only
            assert mShaderID != 0 : "Shader Create Failed!";

            if (mShaderID == 0) {
                Log.e(TAG, "glCreateShader Failed!...");
                return false;
            }

            return true;
        }

        public final void release() {
            if (mShaderID == 0)
                return;
            GLES20.glDeleteShader(mShaderID);
            mShaderID = 0;
        }

        public static int loadShader(int type, final String code) {
            int shaderID = GLES20.glCreateShader(type);//创建一个容纳shader的容器，称为shader容器。方法参数：GLES20.GL_VERTEX_SHADER(顶点shader)、GLES20.GL_FRAGMENT_SHADER(片元shader)

            if (shaderID != 0) {
                GLES20.glShaderSource(shaderID, code);//在创建好的shader容器中添加shader的源代码。源代码应该以字符串数组的形式表示
                GLES20.glCompileShader(shaderID);//使用glCompileShader函数来对shader容器中的源代码进行编译。
                int[] compiled = {0};
                GLES20.glGetShaderiv(shaderID, GLES20.GL_COMPILE_STATUS, compiled, 0);//获取编译情况
                if (compiled[0] != GLES20.GL_TRUE) {
                    String errMsg = GLES20.glGetShaderInfoLog(shaderID);
                    Log.e(TAG, errMsg);
                    GLES20.glDeleteShader(shaderID);
                    return 0;
                }
            }
            return shaderID;
        }

    }

    public void checkGLError(final String tag) {
        int loopCnt = 0;
        for(int err = GLES20.glGetError(); loopCnt < 32 && err != GLES20.GL_FALSE; err = GLES20.glGetError(), ++loopCnt)
        {
            String msg;
            switch (err)
            {
                case GLES20.GL_INVALID_ENUM:
                    msg = "invalid enum"; break;
                case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
                    msg = "invalid framebuffer operation"; break;
                case GLES20.GL_INVALID_OPERATION:
                    msg = "invalid operation";break;
                case GLES20.GL_INVALID_VALUE:
                    msg = "invalid value";break;
                case GLES20.GL_OUT_OF_MEMORY:
                    msg = "out of memory"; break;
                default: msg = "unknown error";
            }
            Log.e(TAG, String.format("After tag \"%s\" glGetError %s(0x%x) ", tag, msg, err));
        }
    }
}
