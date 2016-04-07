package com.lmy.lycommon.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by lifeix on 2016/4/7.
 */
public class StringHttpTask extends BaseHttpTask<String> {

    public static StringHttpTask create(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        return new StringHttpTask(type, url, httpExecuteLinstener);
    }

    public static StringHttpTask create(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
        return new StringHttpTask(type, url, params, httpExecuteLinstener);
    }

    private StringHttpTask(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
        super(type, url, params, httpExecuteLinstener);
    }

    private StringHttpTask(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        super(type, url, httpExecuteLinstener);
    }

    @Override
    public String parseRespone(InputStream inputStream) throws IOException {
        String result = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String lines;
        while ((lines = reader.readLine()) != null) {
            result += lines;
        }
        return result;
    }
}
