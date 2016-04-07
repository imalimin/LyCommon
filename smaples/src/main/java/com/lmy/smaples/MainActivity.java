package com.lmy.smaples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.lmy.lycommon.http.ByteHttpTask;
import com.lmy.lycommon.http.HttpTask;
import com.lmy.lycommon.http.HttpUtil;
import com.lmy.lycommon.http.StringHttpTask;

public class MainActivity extends AppCompatActivity {
    private String url = "http://www.baidu.com/";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.result);
        StringHttpTask task = StringHttpTask.create(HttpTask.Method.EXECUTE_TYPE_GET, url, new StringHttpTask.HttpExecuteLinstener<String>() {

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
//        task.addParam("studentId", "2012213738").addParam("passWord", "3131031");
        HttpUtil.create().execute(task);
    }
}
