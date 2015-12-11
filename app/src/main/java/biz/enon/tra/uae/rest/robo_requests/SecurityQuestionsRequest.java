package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.response.SecurityQuestionResponse;

/**
 * Created by mobimaks on 10.12.2015.
 */
public class SecurityQuestionsRequest extends BaseRequest<SecurityQuestionResponse.List, TRAServicesAPI> {

    public SecurityQuestionsRequest() {
        super(SecurityQuestionResponse.List.class, TRAServicesAPI.class);
    }

    @Override
    public SecurityQuestionResponse.List loadDataFromNetwork() throws Exception {
        return getService().getSecurityQuestions();
    }
}
