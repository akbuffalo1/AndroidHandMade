package biz.enon.tra.uae.rest;

import biz.enon.tra.uae.global.ServerConstants;

/**
 * Created by mobimaks on 19.10.2015.
 */
public final class DynamicRestService extends BaseRetrofitSpiceService {

    @Override
    protected final String getServerUrl() {
        return ServerConstants.BASE_URL1;
    }

}
