package com.uae.tra_smart_services;

import android.app.Application;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.rest.RestClient;

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
        initPicasso();
        super.onCreate();
    }

    private void initPicasso() {
        OkHttpClient client = new OkHttpClient();
        client.setSslSocketFactory(RestClient.getSSLSocketFactory(getApplicationContext()));
        final Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(client))
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static void setIsLoggedIn(boolean _isLoggedIn) {
        isLoggedIn = _isLoggedIn;
    }
}
