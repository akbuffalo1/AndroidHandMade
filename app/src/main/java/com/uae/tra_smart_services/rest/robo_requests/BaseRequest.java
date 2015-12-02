package com.uae.tra_smart_services.rest.robo_requests;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.octo.android.robospice.retry.RetryPolicy;

/**
 * Created by mobimaks on 14.08.2015.
 */
public abstract class BaseRequest<Response, RestInterface> extends RetrofitSpiceRequest<Response, RestInterface> {

    public BaseRequest(Class<Response> clazz, Class<RestInterface> retrofitInterfaceClass) {
        super(clazz, retrofitInterfaceClass);
        setRetryPolicy(new DefaultRetryPolicy());
    }

    private static class DefaultRetryPolicy implements RetryPolicy {

        @Override
        public int getRetryCount() {
            return 0;
        }

        @Override
        public void retry(SpiceException e) {

        }

        @Override
        public long getDelayBeforeRetry() {
            return 0;
        }
    }

}
