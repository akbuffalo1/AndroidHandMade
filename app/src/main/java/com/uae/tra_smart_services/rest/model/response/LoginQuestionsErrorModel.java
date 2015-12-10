package com.uae.tra_smart_services.rest.model.response;

import com.google.gson.annotations.Expose;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse.List;

/**
 * Created by mobimaks on 10.12.2015.
 */
public class LoginQuestionsErrorModel extends ErrorResponseModel {

    @Expose
    public List secretQuestions;

}
