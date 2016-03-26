package com.lmy.lycommon.http;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.lmy.lycommon.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lmy on 2016/3/26.
 */
public class HttpUtil implements IHttpUtil {
    public final static int EXECUTE_TYPE_GET = 0x00;
    public final static int EXECUTE_TYPE_POST = 0x01;
    public final static int TIME_OUT_DEFAULT = 10000;
    public final static String CHARSET_DEFAULT = "utf-8";
    public final static String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
    public final static String PREFIX = "--", LINE_END = "\r\n";
    public final static String CONTENT_TYPE = "multipart/form-data"; //内容类型
    private int timeOut;
    private String charset;

    public static HttpUtil create() {
        return new HttpUtil();
    }

    public static HttpUtil create(int timeOut, String charset) {
        return new HttpUtil(timeOut, charset);
    }

    private HttpUtil() {
        this(TIME_OUT_DEFAULT, CHARSET_DEFAULT);
    }

    private HttpUtil(int timeOut, String charset) {
        this.timeOut = timeOut;
        this.charset = charset;
    }

    @Override
    public void execute(@NonNull HttpTask task) {
        new AsyncHttpTask(task).execute();
    }

    private class AsyncHttpTask extends AsyncTask<HttpTask, Integer, String[]> {
        private HttpTask task;

        public AsyncHttpTask(HttpTask task) {
            this.task = task;
        }

        public void progress(int progress) {
            publishProgress(progress);
        }

        @Override
        protected String[] doInBackground(HttpTask... params) {
            checkType(task.getType());
            try {
                switch (task.getType()) {
                    case EXECUTE_TYPE_GET:
                        return doGet(this, task);
                    case EXECUTE_TYPE_POST:
                        return doPost(this, task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result == null) task.getHttpExecuteLinstener().onError(-1, "unknown error!");
            else if (result.length >= 2)
                task.getHttpExecuteLinstener().onError(Integer.parseInt(result[0]), result[1]);
            else task.getHttpExecuteLinstener().onSuccess(result[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            task.getHttpExecuteLinstener().onProgress(values[0]);
        }
    }

    private void checkType(int type) {
        if (type > EXECUTE_TYPE_POST || type < EXECUTE_TYPE_GET)
            throw new RuntimeException("This type(" + type + ") of request is not supported!");
    }

    private String[] doGet(AsyncHttpTask asyncTask, HttpTask task) throws IOException {
        asyncTask.progress(0);
        HttpURLConnection connection = initConnection(task.getURL(), EXECUTE_TYPE_POST);
        setCookies(connection);
        /**
         * 获取响应码 200=成功 当响应成功，获取响应的流
         */
        int code = connection.getResponseCode();
        String[] result = new String[]{""};
        if (code == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null) {
                result[0] += lines;
            }
        } else {
            result = new String[]{"", ""};
            result[0] = String.valueOf(code);
            result[1] = "Error!";
        }
        connection.disconnect();
        asyncTask.progress(100);
        return result;
    }

    private String[] doPost(AsyncHttpTask asyncTask, HttpTask task) throws IOException {
        asyncTask.progress(0);
        HttpURLConnection connection = initConnection(task.getURL(), EXECUTE_TYPE_POST);
        setCookies(connection);
        OutputStream os = connection.getOutputStream();//打开输出流
        StringBuffer sb = parseParams(task.getParams());
        byte[] paramsByteArray = sb.toString().getBytes();
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                .getBytes();
        os.write(paramsByteArray);
        os.write(end_data);
        os.write(LINE_END.getBytes());
        os.flush();
        os.close();
        /**
         * 获取响应码 200=成功 当响应成功，获取响应的流
         */
        int code = connection.getResponseCode();
        String[] result = new String[]{""};
        if (code == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines = "";
            while ((lines = reader.readLine()) != null) {
                result[0] += lines;
            }
        } else {
            result = new String[]{"", ""};
            result[0] = String.valueOf(code);
            result[1] = "Error!";
        }
        connection.disconnect();
        asyncTask.progress(100);
        return result;
    }

    private StringBuffer parseParams(Map<String, String> map) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            sb.append("Content-Disposition: form-data; name=\""
                    + entry.getKey() + "\"" + LINE_END);
            sb.append("Content-Type: text/plain; charset=" + charset + LINE_END);
            sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
            sb.append(LINE_END);
            sb.append(entry.getValue());
            sb.append(LINE_END);
        }
        return sb;
    }

    private void setCookies(HttpURLConnection connection) {

    }

    @Override
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public int setTimeOut() {
        return timeOut;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    private HttpURLConnection initConnection(String url, int type) throws IOException {
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setReadTimeout(timeOut);
        conn.setConnectTimeout(timeOut);
        conn.setDoInput(true); // 允许输入流
        conn.setDoOutput(true); // 允许输出流
        conn.setUseCaches(false); // 不允许使用缓存
        if (type == EXECUTE_TYPE_GET) // 请求方式
            conn.setRequestMethod("GET");
        else if (type == EXECUTE_TYPE_POST)
            conn.setRequestMethod("POST");
        conn.setRequestProperty("Charset", charset);// 设置编码
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
        return conn;
    }
}
