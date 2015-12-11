package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.response.SearchDeviceResponseModel;

/**
 * Created by Mikazme on 13/08/2015.
 */
public class SearchByImeiRequest extends BaseRequest<SearchDeviceResponseModel.List, TRAServicesAPI> {

    private String mIMEI;

    public SearchByImeiRequest(final String _imei) {
        super(SearchDeviceResponseModel.List.class, TRAServicesAPI.class);
        mIMEI = _imei;
    }

    @Override
    public final SearchDeviceResponseModel.List loadDataFromNetwork() throws Exception {
        return getService().searchDeviceByImei(mIMEI);
    }
}
