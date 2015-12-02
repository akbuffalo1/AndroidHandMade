package com.uae.tra_smart_services.rest;

import com.uae.tra_smart_services.global.ServerConstants;

/**
 * Created by mobimaks on 19.10.2015.
 */
public final class DynamicRestService extends BaseRetrofitSpiceService {

    @Override
    protected final String getServerUrl() {
        return ServerConstants.BASE_URL4;
    }

}
