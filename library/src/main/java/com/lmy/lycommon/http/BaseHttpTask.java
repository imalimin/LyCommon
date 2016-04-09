package com.lmy.lycommon.http;

import com.lmy.lycommon.utils.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lifeix on 2016/4/7.
 */
public abstract class BaseHttpTask<T> implements HttpTask<T> {
    private String url;
    private int type;
    private Map<String, String> params;
    private Map<String, File> mFileMap;
    private HttpExecuteLinstener httpExecuteLinstener;
    private T responeData;

    public BaseHttpTask(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        this.type = type;
        this.url = url;
        this.httpExecuteLinstener = httpExecuteLinstener;
    }

    public BaseHttpTask(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
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
    public HttpTask addFile(String key, File file) {
        if (mFileMap == null) this.mFileMap = new HashMap<>();
        if (!file.exists()) {
            Log.w(BaseHttpTask.class, "File(" + file.getAbsolutePath() + ") not exists!");
            return this;
        }
        mFileMap.put(key, file);
        return this;
    }

    @Override
    public File getFile(String key) {
        if (mFileMap == null)
            return null;
        return mFileMap.get(key);
    }

    @Override
    public Map<String, File> getFileMap() {
        return mFileMap;
    }

    @Override
    public void setResponeData(T data) {
        this.responeData = data;
    }

    @Override
    public T getResponeData() {
        if (this.responeData == null)
            throw new RuntimeException("You must set responeData!");
        return this.responeData;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public HttpTask setHttpExecuteLinstener(HttpExecuteLinstener<T> linstener) {
        this.httpExecuteLinstener = linstener;
        return this;
    }

    @Override
    public HttpExecuteLinstener getHttpExecuteLinstener() {
        return httpExecuteLinstener;
    }
}
