package biz.enon.tra.uae.rest.model.request;

import com.google.gson.annotations.Expose;

/**
 * Created by ak-buffalo on 18.09.15.
 */

public class RestorePasswordRequestModel {

    @Expose
    public String email;

    public RestorePasswordRequestModel(String _email){
        email = _email;
    }

    public String getEmail(){
       return email;
    }
}
