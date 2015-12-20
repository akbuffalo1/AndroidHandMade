package biz.enon.tra.uae.util;

import android.util.Log;

import biz.enon.tra.uae.BuildConfig;

/**
 * Created by mobimaks on 09.11.2015.
 */
public final class Logger {

    private static final boolean LOGGING_ENABLED = BuildConfig.LOG_DEBUG;

    public static int d(String tag, String msg) {
        return LOGGING_ENABLED ? Log.d(tag, msg) : 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        return LOGGING_ENABLED ? Log.d(tag, msg, tr) : 0;
    }

    public static int i(String tag, String msg) {
        return LOGGING_ENABLED ? Log.i(tag, msg) : 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        return LOGGING_ENABLED ? Log.i(tag, msg, tr) : 0;
    }

    public static int e(String tag, String msg) {
        return LOGGING_ENABLED ? Log.e(tag, msg) : 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        return LOGGING_ENABLED ? Log.e(tag, msg, tr) : 0;
    }

    public static int v(String tag, String msg) {
        return LOGGING_ENABLED ? Log.v(tag, msg) : 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        return LOGGING_ENABLED ? Log.v(tag, msg, tr) : 0;
    }

}
