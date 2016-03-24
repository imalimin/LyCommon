package com.lmy.lycommon.cache;

import java.io.File;

/**
 * Created by 李明艺 on 2015/10/20.
 */
public interface CacheLoadListener {
    void show(int status, File file);
}
