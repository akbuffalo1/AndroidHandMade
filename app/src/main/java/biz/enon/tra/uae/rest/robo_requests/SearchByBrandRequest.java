package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.response.SearchDeviceResponseModel;

/**
 * Created by Mikazme on 13/08/2015.
 */
public class SearchByBrandRequest extends BaseRequest<SearchDeviceResponseModel.List, TRAServicesAPI> {

    private String mBrand;
    private Integer mStart;
    private Integer mEnd;

    public SearchByBrandRequest(final String _brand, final Integer _start, final Integer _end) {
        super(SearchDeviceResponseModel.List.class, TRAServicesAPI.class);
        mBrand = _brand;
        mStart = _start;
        mEnd = _end;
    }

    @Override
    public final SearchDeviceResponseModel.List loadDataFromNetwork() throws Exception {
        return getService().searchDeviceByBrandName(mBrand, mStart, mEnd);
    }
}
