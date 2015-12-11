package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.RestorePasswordRequestModel;
import retrofit.client.Response;

/**
 * Created by ak-buffalo on 18.09.15.
 */
public class RestorePasswordRequest extends BaseRequest<Response, TRAServicesAPI> {

    private final RestorePasswordRequestModel mRegisterModel;

    public RestorePasswordRequest(final RestorePasswordRequestModel _registerModel) {
        super(Response.class, TRAServicesAPI.class);
        mRegisterModel = _registerModel;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().restorePassword(mRegisterModel);
    }
}