package com.lmy.lycommon.http;

import java.util.Map;

/**
 * Created by lmy on 2016/3/26.
 */
public interface HttpTask {
    public final static int EXECUTE_TYPE_GET = 0x00;
    public final static int EXECUTE_TYPE_POST = 0x01;
    HttpTask setURL(String url);

    String getURL();

    HttpTask setType(int type);

    int getType();

    HttpTask addParam(String key, String value);

    String getParam(String key);

    Map<String, String> getParams();

    HttpTask setHttpExecuteLinstener(HttpExecuteLinstener linstener);

    HttpExecuteLinstener getHttpExecuteLinstener();

    public interface HttpExecuteLinstener {
        void onSuccess(String result);

        void onError(int code, String msg);

        void onProgress(int progress);
    }
}
