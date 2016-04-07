package com.lmy.lycommon.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by lifeix on 2016/4/7.
 */
public class BitmapHttpTask extends BaseHttpTask<Bitmap> {

    public static BitmapHttpTask create(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        return new BitmapHttpTask(type, url, httpExecuteLinstener);
    }

    public static BitmapHttpTask create(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
        return new BitmapHttpTask(type, url, params, httpExecuteLinstener);
    }

    private BitmapHttpTask(int type, String url, Map<String, String> params, HttpExecuteLinstener httpExecuteLinstener) {
        super(type, url, params, httpExecuteLinstener);
    }

    private BitmapHttpTask(int type, String url, HttpExecuteLinstener httpExecuteLinstener) {
        super(type, url, httpExecuteLinstener);
    }

    @Override
    public Bitmap parseRespone(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        byte[] data = output.toByteArray();
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
