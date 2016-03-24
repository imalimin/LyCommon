package com.lmy.lycommon.cache;

import android.graphics.Bitmap;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by 明艺 on 2015/7/21.
 */
public class MemoryCache {
    private Map<String, SoftReference<Bitmap>> cache = Collections
            .synchronizedMap(new LinkedHashMap<String, SoftReference<Bitmap>>(
                    10, 1.5f, true));

    public Bitmap get(String id) {
        if (!cache.containsKey(id)) {
            return null;
        }

        SoftReference<Bitmap> ref = cache.get(id);

        return ref.get();
    }

    public void put(String id, Bitmap bitmap) {
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    public void clear() {
//        Iterator<Map.Entry<String, SoftReference<Bitmap>>> iterator = cache.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, SoftReference<Bitmap>> entry = iterator.next();
//            Bitmap b = entry.getValue().get();
//            if (!b.isRecycled())
//                b.recycle();
//            entry.getValue().clear();
//        }
        cache.clear();
    }
}
