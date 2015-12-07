package com.uae.tra_smart_services;

import android.app.Application;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.uae.tra_smart_services.global.C;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Mikazme on 24/08/2015.
 */
public class TRAApplication extends Application {

    private static boolean isLoggedIn = false;

    @Override
    public void onCreate() {
        Fabric.with(this, new Crashlytics());
        isLoggedIn = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean(C.IS_LOGGED_IN, false);
        super.onCreate();
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static void setIsLoggedIn(boolean _isLoggedIn) {
        isLoggedIn = _isLoggedIn;
    }
}
