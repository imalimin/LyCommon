package com.lmy.lycommon.http;

/**
 * Created by lmy on 2016/3/26.
 */
public interface IHttpUtil {

    void execute(HttpTask task);

    void setTimeOut(int timeOut);

    int setTimeOut();

    void setCharset(String charset);

    String getCharset();
}
