package com.lmy.lycommon.fingerprint.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.lmy.lycommon.utils.Log;

/**
 * Created by lmy on 16-2-1.
 */
public class FingerprintUtil {
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isSupportFingerprint(Context context) {
        try {
            FingerprintManager fpManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            return true;
        } catch (NoClassDefFoundError e) {
            Log.v(FingerprintUtil.class, "不支持指纹传感器");
            return false;
        }
    }
}
