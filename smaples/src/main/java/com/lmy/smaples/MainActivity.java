package com.lmy.smaples;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmy.lycommon.http.BitmapHttpTask;
import com.lmy.lycommon.http.HttpTask;
import com.lmy.lycommon.http.HttpUtil;
import com.lmy.lycommon.http.StringHttpTask;

public class MainActivity extends AppCompatActivity {
    private String url = "http://pic.to8to.com/attch/day_160218/20160218_a9c8ab4599980f55577bp7at2oEwM7s9.png";
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.result);
        HttpTask task = BitmapHttpTask.create(HttpTask.Method.EXECUTE_TYPE_POST, url, new BitmapHttpTask.HttpExecuteLinstener<Bitmap>() {

            @Override
            public void onSuccess(Bitmap result) {
                Log.v("000", "result=" + result);
                imageView.setImageBitmap(result);
//                textView.setText("result=" + result);
            }

            @Override
            public void onError(int code, String msg) {
                textView.setText("code=" + code + ", msg=" + msg);
            }

            @Override
            public void onProgress(int progress) {
                Log.v("000", "onProgress, progress=" + progress);
            }
        });
//        task.addParam("studentId", "2012213738").addParam("passWord", "3131031");
        HttpUtil.create().execute(task);
    }
}
