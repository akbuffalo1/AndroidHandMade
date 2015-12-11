package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.RegisterModel;
import retrofit.client.Response;

/**
 * Created by mobimaks on 15.08.2015.
*/
public class RegisterRequest extends BaseRequest<Response, TRAServicesAPI> {

    private final RegisterModel mRegisterModel;

    public RegisterRequest(final RegisterModel _registerModel) {
        super(Response.class, TRAServicesAPI.class);
        mRegisterModel = _registerModel;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().register(mRegisterModel);
    }
}
