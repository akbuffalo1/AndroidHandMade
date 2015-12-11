package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.response.UserProfileResponseModel;

/**
 * Created by mobimaks on 03.10.2015.
 */
public class UserProfileRequest extends BaseRequest<UserProfileResponseModel, TRAServicesAPI> {

    public UserProfileRequest() {
        super(UserProfileResponseModel.class, TRAServicesAPI.class);
    }

    @Override
    public UserProfileResponseModel loadDataFromNetwork() throws Exception {
        return getService().getUserProfile();
    }
}
