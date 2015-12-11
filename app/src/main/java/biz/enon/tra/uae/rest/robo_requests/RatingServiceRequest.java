package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.RatingServiceRequestModel;
import biz.enon.tra.uae.rest.model.response.RatingServiceResponseModel;

/**
 * Created by PC on 9/3/2015.
 */
public class RatingServiceRequest extends BaseRequest<RatingServiceResponseModel, TRAServicesAPI> {

    private final RatingServiceRequestModel mRegisterModel;

    public RatingServiceRequest(final RatingServiceRequestModel _registerModel) {
        super(RatingServiceResponseModel.class, TRAServicesAPI.class);
        mRegisterModel = _registerModel;
    }

    @Override
    public RatingServiceResponseModel loadDataFromNetwork() throws Exception {
        return getService().ratingService(mRegisterModel);
    }
}
