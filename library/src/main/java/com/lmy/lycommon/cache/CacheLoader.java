package com.lmy.lycommon.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 明艺 on 2015/7/21.
 */
public class CacheLoader {
    private Context context;
    //Network time out
    private static final int TIME_OUT = 30000;
    //Default picture resource
//    private final static int DEFAULT_BG = R.mipmap.ic_launcher;
    //Thread pool number
    private static final int THREAD_NUM = 5;
    //Memory image cache
    MemoryCache memoryCache = new MemoryCache();
    //File image cache
    FileCache fileCache;
    //Judge image view if it is reuse
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    //Thread pool
    private ExecutorService executorService;
    private int defaultBg;

    public CacheLoader(Context context) {
        this.fileCache = new FileCache(context);
        this.executorService = Executors.newFixedThreadPool(THREAD_NUM);
        this.context = context;
//        this.defaultBg = DEFAULT_BG;
    }

    public CacheLoader(Context context, int defaultBg) {
        this.fileCache = new FileCache(context);
        this.executorService = Executors.newFixedThreadPool(THREAD_NUM);
        this.context = context;
        this.defaultBg = defaultBg;
    }

    public CacheLoader(Context context, String cacheDir, int defaultBg) {
        this.fileCache = new FileCache(context, cacheDir + "/");
        this.executorService = Executors.newFixedThreadPool(THREAD_NUM);
        this.context = context;
        this.defaultBg = defaultBg;
    }

    public void disPlayImage(ImageView view, String url) {
        imageViews.put(view, url);
        Bitmap b = memoryCache.get(url);
        if (b == null) b = getBitmap(url);
        if (b != null) {
            // Display image from Memory cache
            Log.v("000", "Display image from Memory cache or File");
            view.setImageBitmap(b);
        } else {
            // Display image from File cache or Network
            Log.v("000", "Display image from  Network");
            view.setImageResource(defaultBg);
            queuePhoto(url, view);
        }
    }

    public void load(String url, CacheLoadListener cacheLoadListener) {
        File f = getFile(url);
        if (f != null && f.exists()) {
            // Display image from Memory cache
            Log.v("000", "Return file from Memory cache");
            cacheLoadListener.show(1, f);
        } else {
            // Display image from File cache or Network
            Log.v("000", "Return file from File cache or Network");
            queuePhoto(url, cacheLoadListener);
        }
    }

    private void queuePhoto(String url, ImageView view) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, view);
        executorService.submit(new PhotosLoader(photoToLoad));
    }

    private void queuePhoto(String url, CacheLoadListener cacheLoadListener) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, cacheLoadListener);
        executorService.submit(new PhotosLoader(photoToLoad));
    }

    private Bitmap getBitmap(final String url) {
        final File f = fileCache.getFile(url);
        if (f == null || !f.exists()) return null;
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        memoryCache.put(url, bitmap);
        return bitmap;
    }

    private File getFile(String url) {
        return fileCache.getFile(url);
    }

    private class PhotosLoader implements Runnable {
        private PhotoToLoad photoToLoad;

        public PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        private Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    File f = (File) message.obj;
                    if (f.exists() && photoToLoad.view != null) {
                        Bitmap b = decodeFile(f);
                        photoToLoad.view.setImageBitmap(b);
                        memoryCache.put(photoToLoad.url, b);
                    }
                    if (f.exists() && photoToLoad.cacheLoadListener != null)
                        photoToLoad.cacheLoadListener.show(1, f);
                }
            }
        };

        @Override
        public void run() {
            File f = fileCache.getFile(photoToLoad.url);
            try {
                URL url = new URL(photoToLoad.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                // 将得到的数据转化成InputStream
                InputStream is = connection.getInputStream();
                byte[] data = inputStreamToByte(is);
                writeFile(data, f);
                Message message = handler.obtainMessage(1, f);
                handler.sendMessage(message);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(byte[] data, File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 把数据流转换成字节流
    private byte[] inputStreamToByte(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
//            inStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outStream.toByteArray();
    }

    private Bitmap decodeFile(File f) {
        try {
            // TODO:Compress image size
            FileInputStream fileInputStream = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            return bitmap;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public void clearCache() {
        fileCache.clear();
    }

    private class PhotoToLoad {
        public String url;
        public ImageView view;
        public CacheLoadListener cacheLoadListener;

        public PhotoToLoad(String url, ImageView view) {
            this.url = url;
            this.view = view;
        }

        public PhotoToLoad(String url, CacheLoadListener cacheLoadListener) {
            this.url = url;
            this.cacheLoadListener = cacheLoadListener;
        }
    }

    public void changeCacheDir(String dirName) {
        fileCache = new FileCache(this.context, dirName + "/");
    }
}
