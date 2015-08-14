package com.uae.tra_smart_services.rest.new_request;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.new_response.SearchDeviceResponseModel;

/**
 * Created by Vitaliy on 13/08/2015.
 */
public class SearchByImeiRequest extends RetrofitSpiceRequest<SearchDeviceResponseModel.List, TRAServicesAPI> {

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
