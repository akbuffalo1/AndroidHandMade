package biz.enon.tra.uae.rest.model.response;

import com.google.gson.annotations.Expose;

/**
 * Created by mobimaks on 10.12.2015.
 */
public class LoginQuestionsErrorModel extends ErrorResponseModel {

    @Expose
    public SecurityQuestionResponse.List secretQuestions;

}
