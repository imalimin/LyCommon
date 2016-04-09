package com.lmy.lycommon.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by lmy on 2016/3/26.
 */
public interface HttpTask<T> {
    HttpTask setURL(String url);

    String getURL();

    HttpTask setType(int type);

    int getType();

    HttpTask addParam(String key, String value);

    String getParam(String key);

    Map<String, String> getParams();

    HttpTask addFile(String key, File file);

    File getFile(String key);

    Map<String, File> getFileMap();

    void setResponeData(T data);

    T getResponeData();

    T parseRespone(InputStream inputStream) throws IOException;

    HttpTask setHttpExecuteLinstener(HttpExecuteLinstener<T> linstener);

    HttpExecuteLinstener getHttpExecuteLinstener();

    public interface HttpExecuteLinstener<T> {
        void onSuccess(T result);

        void onError(int code, String msg);

        void onProgress(int progress);
    }

    public class Method {
        public final static int EXECUTE_TYPE_GET = 0x00;
        public final static int EXECUTE_TYPE_POST = 0x01;
    }
}
