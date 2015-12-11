package biz.enon.tra.uae.rest;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import biz.enon.tra.uae.BuildConfig;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

import static biz.enon.tra.uae.global.ServerConstants.TIMEOUT;

/**
 * Created by mobimaks on 19.10.2015.
 */
abstract class BaseRetrofitSpiceService extends RetrofitGsonSpiceService {

    @Override
    protected final Builder createRestAdapterBuilder() {
        final Builder builder = super.createRestAdapterBuilder();
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setSslSocketFactory(RestClient.getSSLSocketFactory(getApplicationContext()));
//        okHttpClient.setConnectionSpecs(Collections.singletonList(RestClient.SPECS));
//        okHttpClient.setHostnameVerifier(new AllowAllHostnameVerifier());
        builder.setClient(new OkClient(okHttpClient));

        return builder;
    }

    @Override
    protected final Converter createConverter() {
        final Gson gson = getGsonBuilder().create();
        return new GsonConverter(gson);
    }

    @NonNull
    @CallSuper
    protected GsonBuilder getGsonBuilder() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation();
    }
}
