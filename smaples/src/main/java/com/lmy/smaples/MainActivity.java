package com.lmy.smaples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.lmy.lycommon.http.DefaultHttpTask;
import com.lmy.lycommon.http.HttpTask;
import com.lmy.lycommon.http.HttpUtil;

public class MainActivity extends AppCompatActivity {
    private String url = "http://192.168.99.160:8080/dinner/User_login";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.result);
        DefaultHttpTask task = DefaultHttpTask.create(HttpTask.EXECUTE_TYPE_POST, url, new DefaultHttpTask.HttpExecuteLinstener() {

            @Override
            public void onSuccess(String result) {
                Log.v("000", "result=" + result);
                textView.setText("result=" + result);
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
        task.addParam("studentId", "2012213738").addParam("passWord", "3131031");
        HttpUtil.create().execute(task);
    }
}
