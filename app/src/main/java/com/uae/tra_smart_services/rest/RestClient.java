package com.uae.tra_smart_services.rest;

import android.content.Context;

import com.squareup.okhttp.CipherSuite;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.TlsVersion;
import com.uae.tra_smart_services.BuildConfig;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.ServerConstants;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.client.OkClient;

import static com.uae.tra_smart_services.global.ServerConstants.TIMEOUT;

/**
 * Created by mobimaks on 07.10.2015.
 */
public final class RestClient {

    private static RestClient mRestClient;
    private TRAServicesAPI mTRAServicesAPI;
    private DynamicServicesApi mDynamicServicesApi;
    private static SSLSocketFactory SSL_SOCKET_FACTORY;
    public static ConnectionSpec SPECS;
    static {
        SPECS = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
                .build();
    }

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
//        okHttpClient.setConnectionSpecs(Collections.singletonList(SPECS));
//        okHttpClient.setHostnameVerifier(new AllowAllHostnameVerifier());

        final Builder builder = new Builder()
                .setEndpoint(ServerConstants.BASE_URL)
                .setClient(new OkClient(okHttpClient));
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.HEADERS);
        }
        final RestAdapter adapter = builder.build();
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
