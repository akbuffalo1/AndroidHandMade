package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.LoginModel;
import retrofit.client.Response;

/**
 * Created by mobimaks on 15.08.2015.
 */
public class LoginRequest extends BaseRequest<Response, TRAServicesAPI> {

    private final LoginModel mLoginModel;

    public LoginRequest(final LoginModel _loginModel) {
        super(Response.class, TRAServicesAPI.class);
        mLoginModel = _loginModel;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().login(mLoginModel);
    }
}
