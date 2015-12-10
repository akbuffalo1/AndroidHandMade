package com.uae.tra_smart_services.rest.robo_requests;

import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.request.LoginQuestionRequestModel;

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
