package com.lmy.lycommon.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by lmy on 2016/3/26.
 */
public class ByteHttpTask extends BaseHttpTask<byte[]> {

    public static ByteHttpTask create(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        return new ByteHttpTask(type, url, httpExecuteLinstener);
    }

    public static ByteHttpTask create(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
        return new ByteHttpTask(type, url, params, httpExecuteLinstener);
    }

    private ByteHttpTask(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
        super(type, url, params, httpExecuteLinstener);
    }

    private ByteHttpTask(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        super(type, url, httpExecuteLinstener);
    }

    @Override
    public byte[] parseRespone(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
