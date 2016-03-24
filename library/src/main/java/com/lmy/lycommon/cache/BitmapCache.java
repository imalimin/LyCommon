package com.lmy.lycommon.cache;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/22.
 */
public class BitmapCache {
    private static BitmapCache instance;
    private Map<String, SoftReference<Bitmap>> cache = Collections
            .synchronizedMap(new LinkedHashMap<String, SoftReference<Bitmap>>(
                    10, 1.5f, true));

    public synchronized static BitmapCache instance() {
        if (instance == null)
            instance = new BitmapCache();
        return instance;
    }

    public Bitmap get(String id) {
        if (!cache.containsKey(id)) {
            return null;
        }
        SoftReference<Bitmap> ref = cache.get(id);

        return ref.get();
    }

    public void put(String id, Bitmap b) {
        cache.put(id, new SoftReference<Bitmap>(b));
    }

    public void clear() {
        cache.clear();
    }
}
