package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.LogoutRequestModel;
import retrofit.client.Response;

/**
 * Created by mobimaks on 15.08.2015.
 */
public class LogoutRequest extends BaseRequest<Response, TRAServicesAPI> {

    private LogoutRequestModel mModel;

    public LogoutRequest(final LogoutRequestModel _model) {
        super(Response.class, TRAServicesAPI.class);
        mModel = _model;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().logout(mModel);
    }
}
