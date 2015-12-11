package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.LoginQuestionRequestModel;
import retrofit.client.Response;

/**
 * Created by mobimaks on 15.08.2015.
 */
public class LoginQuestionRequest extends BaseRequest<Response, TRAServicesAPI> {

    private final LoginQuestionRequestModel mLoginModel;

    public LoginQuestionRequest(final LoginQuestionRequestModel _loginModel) {
        super(Response.class, TRAServicesAPI.class);
        mLoginModel = _loginModel;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().loginWithQuestion(mLoginModel);
    }
}
