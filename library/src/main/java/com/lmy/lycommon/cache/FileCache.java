package com.lmy.lycommon.cache;

import android.content.Context;

import java.io.File;

/**
 * Created by 李明艺 on 2015/7/21.
 */
public class FileCache {
    private static final String DIR_NAME = "ZUtilCache/";//默认保存目录
    private String dirName;
    private File cacheDir;

    public FileCache(Context context) {
        this(context, DIR_NAME);
    }

    public FileCache(Context context, String dirName) {
        this.dirName = dirName;
        // Find the directory to save cached images
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    this.dirName);
        } else {
            cacheDir = context.getCacheDir();
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public File getFile(String url) {
        // Identify images by url's hash code
        String filename = url.hashCode() + ".zcache";
        File f = new File(cacheDir, filename);
        return f;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        } else {
            for (File f : files) {
                f.delete();
            }
        }
    }
}
