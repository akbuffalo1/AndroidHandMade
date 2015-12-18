package biz.enon.tra.uae.rest;

import android.content.Context;

import com.squareup.okhttp.CipherSuite;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.TlsVersion;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import biz.enon.tra.uae.BuildConfig;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.ServerConstants;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.client.OkClient;

import static biz.enon.tra.uae.global.ServerConstants.TIMEOUT;

/**
 * Created by mobimaks on 07.10.2015.
 */
public final class RestClient {

    private static RestClient mRestClient;
    private TRAServicesAPI mTRAServicesAPI;
    private DynamicServicesApi mDynamicServicesApi;
    private static SSLSocketFactory SSL_SOCKET_FACTORY;

    public static SSLSocketFactory getSSLSocketFactory(Context _context){
        if(SSL_SOCKET_FACTORY == null){
            try {
                KeyStore ksTrust = KeyStore.getInstance("BKS");
                InputStream instream = _context.getResources().openRawResource(C.HTTPS_KEYSTORE_FILE_RES_ID);
                ksTrust.load(instream, C.HTTPS_KEYSTORE_FILE_PASS.toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ksTrust);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);
                SSL_SOCKET_FACTORY = sslContext.getSocketFactory();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        return SSL_SOCKET_FACTORY;
    }

    private RestClient(Context _context) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setSslSocketFactory(getSSLSocketFactory(_context));

        final RestAdapter adapter = new Builder()
                .setEndpoint(ServerConstants.BASE_URL)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        mTRAServicesAPI = adapter.create(TRAServicesAPI.class);
        mDynamicServicesApi = adapter.create(DynamicServicesApi.class);
    }

    public static RestClient getInstance(Context _context) {
        if (mRestClient == null) {
            mRestClient = new RestClient(_context);
        }
        return mRestClient;
    }

    public final TRAServicesAPI getTRAServicesAPI() {
        return mTRAServicesAPI;
    }

    public final DynamicServicesApi getDynamicServicesApi() {
        return mDynamicServicesApi;
    }
}
