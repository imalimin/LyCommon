package com.lmy.lycommon.http;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.lmy.lycommon.utils.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lmy on 2016/3/26.
 */
public class HttpUtil implements IHttpUtil {
    public final static int TIME_OUT_DEFAULT = 60000;
    public final static String CHARSET_DEFAULT = "utf-8";
    public final static String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
    public final static String PREFIX = "--", LINE_END = "\r\n";
    public final static String CONTENT_TYPE = "multipart/form-data"; //内容类型
    private CookieManager mCookieManager;
    private int timeOut;
    private String charset;

    public static HttpUtil create() {
        return new HttpUtil();
    }

    public static HttpUtil create(int timeOut, String charset) {
        return new HttpUtil(timeOut, charset);
    }

    protected HttpUtil() {
        this(TIME_OUT_DEFAULT, CHARSET_DEFAULT);
    }

    private HttpUtil(int timeOut, String charset) {
        this.timeOut = timeOut;
        this.charset = charset;
        initCookieManager();
    }

    private void initCookieManager() {
        this.mCookieManager = new CookieManager();
        // 将规则改掉，接受所有的 Cookie
        this.mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        // 保存这个定制的 CookieManager
        CookieHandler.setDefault(mCookieManager);
//        mCookieManager.getCookieStore().removeAll();
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

        private String[] respone(HttpURLConnection connection) throws IOException {
            int code = connection.getResponseCode();
            String[] result = new String[0];
            if (code == 200) {
                task.setResponeData(task.parseRespone(connection.getInputStream()));//缓存响应数据
            } else {
                result = new String[]{"", ""};
                result[0] = String.valueOf(code);
                result[1] = "Error!";
            }
            connection.disconnect();
            return result;
        }

        @Override
        protected String[] doInBackground(HttpTask... params) {
            checkType(task.getType());
            try {
                HttpURLConnection connection = null;
                switch (task.getType()) {
                    case HttpTask.Method.EXECUTE_TYPE_GET:
                        connection = doGet(this, task);
                        break;
                    case HttpTask.Method.EXECUTE_TYPE_POST:
                        connection = doPost(this, task);
                        break;
                }
                return respone(connection);
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return new String[]{"408", "Timeout!"};
            } catch (IOException e) {
                e.printStackTrace();
                return new String[]{"-2", "InputStream Error!"};
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return new String[]{"-1", "unknown error!"};
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result == null) task.getHttpExecuteLinstener().onError(-1, "unknown error!");
            else if (result.length == 2)
                task.getHttpExecuteLinstener().onError(Integer.parseInt(result[0]), result[1]);
            else task.getHttpExecuteLinstener().onSuccess(task.getResponeData());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            task.getHttpExecuteLinstener().onProgress(values[0]);
        }
    }

    private void checkType(int type) {
        if (type > HttpTask.Method.EXECUTE_TYPE_POST || type < HttpTask.Method.EXECUTE_TYPE_GET)
            throw new RuntimeException("This type(" + type + ") of request is not supported!");
    }

    private HttpURLConnection doGet(AsyncHttpTask asyncTask, HttpTask task) throws IOException, URISyntaxException {
        asyncTask.progress(0);
        HttpURLConnection connection = initConnection(task.getURL(), task.getType());
        connection.connect();
        return connection;
    }

    private HttpURLConnection doPost(AsyncHttpTask asyncTask, HttpTask task) throws IOException, URISyntaxException {
        asyncTask.progress(0);
        HttpURLConnection connection = initConnection(task.getURL(), task.getType());
        connection.connect();
//        setCookies(connection);
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());//打开输出流
        Map<String, File> map = task.getFileMap();
        if (map != null) {
            for (Map.Entry<String, File> entry : map.entrySet()) {
                StringBuffer fileSb = new StringBuffer();
                fileSb.append(PREFIX);
                fileSb.append(BOUNDARY);
                fileSb.append(LINE_END);
                fileSb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\""
                        + entry.getValue().getName() + "\"" + LINE_END);
                fileSb.append("Content-Type: application/octet-stream; charset="
                        + charset + LINE_END);
                fileSb.append(LINE_END);
                byte[] fileByteArray = fileSb.toString().getBytes();
                dos.write(fileByteArray);

                InputStream is = new FileInputStream(entry.getValue());
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
            }
        }
        if (task.getParams() != null) {//如果有参数则打开输出流提交
            StringBuffer sb = parseParams(task.getParams());
            byte[] paramsByteArray = sb.toString().getBytes();
            dos.write(paramsByteArray);
        }
        //结束数据传输
        dos.write(end_data);
        dos.write(LINE_END.getBytes());
        dos.flush();
        dos.close();
        return connection;
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

    @Override
    public void clearCookies() {
        mCookieManager.getCookieStore().removeAll();
    }

    @Override
    public void setCookies(List<HttpCookie> cookies) {
        if (cookies == null) return;
        mCookiesCache = cookies;
    }

    private List<HttpCookie> mCookiesCache;

    private void loadCookies(URI uri) {
        if (mCookiesCache == null || mCookiesCache.size() == 0) return;
        Log.v(HttpUtil.class, "loadCookies: " + mCookiesCache.toString());
        for (HttpCookie c : mCookiesCache)
            if (!c.hasExpired())
                mCookieManager.getCookieStore().add(uri, c);
        mCookiesCache.clear();
    }

    @Override
    public List<HttpCookie> getCookies() {

        return mCookieManager.getCookieStore().getCookies();
    }

    private HttpURLConnection initConnection(String url, int type) throws IOException, URISyntaxException {
        URL u = new URL(url);
        loadCookies(u.toURI());
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setReadTimeout(timeOut);
        conn.setConnectTimeout(timeOut);
        conn.setRequestProperty("Charset", charset);// 设置编码
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Language", "zh-cn");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
        if (type == HttpTask.Method.EXECUTE_TYPE_GET) { // 请求方式
            conn.setDoInput(true); // 允许输入流
            conn.setRequestMethod("GET");
        } else if (type == HttpTask.Method.EXECUTE_TYPE_POST) {
            conn.setDoOutput(true); // 允许输出流
            conn.setDoInput(true); // 允许输入流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST");
        }
        return conn;
    }
}
