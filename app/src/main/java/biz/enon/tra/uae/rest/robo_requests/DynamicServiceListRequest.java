package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.response.DynamicServiceInfoResponseModel;

/**
 * Created by mobimaks on 20.10.2015.
 */
public class DynamicServiceListRequest extends BaseRequest<DynamicServiceInfoResponseModel.List, TRAServicesAPI> {

    public DynamicServiceListRequest() {
        super(DynamicServiceInfoResponseModel.List.class, TRAServicesAPI.class);
    }

    @Override
    public DynamicServiceInfoResponseModel.List loadDataFromNetwork() throws Exception {
        return getService().getDynamicServiceList();
    }
}
