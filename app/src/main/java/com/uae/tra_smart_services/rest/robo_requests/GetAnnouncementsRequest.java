package com.uae.tra_smart_services.rest.robo_requests;

import com.uae.tra_smart_services.global.QueryAdapter;
import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.response.GetAnnouncementsResponseModel;

/**
 * Created by ak-buffalo on 23.10.15.
 */

public class GetAnnouncementsRequest extends BaseRequest<GetAnnouncementsResponseModel, TRAServicesAPI> {

    private int mOffset, mLimit;

    public GetAnnouncementsRequest(final QueryAdapter.OffsetQuery _query) {
        super(GetAnnouncementsResponseModel.class, TRAServicesAPI.class);
        mOffset = _query.offset;
        mLimit = _query.limit;
    }

    @Override
    public GetAnnouncementsResponseModel loadDataFromNetwork() throws Exception {
        return getService().getAnnouncements(mOffset, mLimit);
    }
}