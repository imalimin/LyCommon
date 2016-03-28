package com.lmy.lycommon.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmy on 2016/3/26.
 */
public class DefaultHttpTask implements HttpTask {
    private String url;
    private int type;
    private Map<String, String> params;
    private HttpExecuteLinstener httpExecuteLinstener;

    public static DefaultHttpTask create(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        return new DefaultHttpTask(type, url, httpExecuteLinstener);
    }

    public static DefaultHttpTask create(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
        return new DefaultHttpTask(type, url, params, httpExecuteLinstener);
    }

    private DefaultHttpTask(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        this.type = type;
        this.url = url;
        this.httpExecuteLinstener = httpExecuteLinstener;
    }

    private DefaultHttpTask(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
        this.type = type;
        this.url = url;
        this.params = params;
        this.httpExecuteLinstener = httpExecuteLinstener;
    }

    @Override
    public HttpTask setURL(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public HttpTask setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public HttpTask addParam(String key, String value) {
        if (params == null) params = new HashMap<>();
        params.put(key, value);
        return this;
    }

    @Override
    public String getParam(String key) {
        if (params == null)
            return null;
        return params.get(key);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public HttpTask setHttpExecuteLinstener(HttpExecuteLinstener linstener) {
        this.httpExecuteLinstener = linstener;
        return this;
    }

    @Override
    public HttpExecuteLinstener getHttpExecuteLinstener() {
        return httpExecuteLinstener;
    }
}
