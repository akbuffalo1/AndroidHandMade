package com.uae.tra_smart_services.rest.robo_requests;

import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse.List;

/**
 * Created by mobimaks on 10.12.2015.
 */
public class SecurityQuestionsRequest extends BaseRequest<List, TRAServicesAPI> {

    public SecurityQuestionsRequest() {
        super(List.class, TRAServicesAPI.class);
    }

    @Override
    public List loadDataFromNetwork() throws Exception {
        return getService().getSecurityQuestions();
    }
}
