package com.oliver.sdk.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   :
 */

public class LogUtils {

    private static final String SERIAL_TAG = "WifiSDK";

    public static void d(String message) {
        d(SERIAL_TAG, message);
    }

    public static void d(String tag, Object message) {
        d(tag, message.toString());
    }

    public static void d(String tag, String message) {
        if (TextUtils.isEmpty(tag)) {
            tag = SERIAL_TAG;
        }
        if (TextUtils.isEmpty(message)) {
            e(tag, message);
            return;
        }
        Log.d(tag, message);
    }

    public static void e(String message) {
        e(SERIAL_TAG, message);
    }

    public static void e(String tag, String message) {
        if (TextUtils.isEmpty(tag)) {
            tag = SERIAL_TAG;
        }
        if (TextUtils.isEmpty(message)) {
            message = "请传递有效的打印信息";
        }
        Log.e(tag, message);
    }
}
