package com.lmy.lycommon.utils;

/**
 * Created by 明艺 on 2015/7/10.
 */
public class Log {
    private final static boolean ISLOG = true;
    private final static int NOLOG = 0;

    public static int v(Class cls, String msg) {
        return v(getTag(cls), msg);
    }

    public static int v(String tag, String msg) {
        if (ISLOG)
            return android.util.Log.v(tag, msg);
        return NOLOG;
    }

    public static int d(Class cls, String msg) {
        return d(getTag(cls), msg);
    }

    public static int d(String tag, String msg) {
        if (ISLOG)
            return android.util.Log.d(tag, msg);
        return NOLOG;
    }

    public static int i(Class cls, String msg) {
        return i(getTag(cls), msg);
    }

    public static int i(String tag, String msg) {
        if (ISLOG)
            return android.util.Log.i(tag, msg);
        return NOLOG;
    }

    public static int w(Class cls, String msg) {
        return w(getTag(cls), msg);
    }

    public static int w(String tag, String msg) {
        if (ISLOG)
            return android.util.Log.w(tag, msg);
        return NOLOG;
    }

    public static int e(Class cls, String msg) {
        return e(getTag(cls), msg);
    }

    public static int e(String tag, String msg) {
        if (ISLOG)
            return android.util.Log.e(tag, msg);
        return NOLOG;
    }

    private static String getTag(Class cls) {
        String name = cls.getCanonicalName();
        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }
}
