package biz.enon.tra.uae.rest.robo_requests;

import android.support.annotation.NonNull;

import biz.enon.tra.uae.entities.dynamic_service.DynamicService;
import biz.enon.tra.uae.rest.TRAServicesAPI;

/**
 * Created by mobimaks on 20.10.2015.
 */
public final class DynamicServiceDetailsRequest extends BaseRequest<DynamicService, TRAServicesAPI> {

    private final String mServiceId;

    public DynamicServiceDetailsRequest(@NonNull final String _serviceId) {
        super(DynamicService.class, TRAServicesAPI.class);
        mServiceId = _serviceId;
    }

    @Override
    public final DynamicService loadDataFromNetwork() throws Exception {
        return getService().getDynamicServiceDetails(mServiceId);
    }

}
