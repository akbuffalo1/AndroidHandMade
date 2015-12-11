package biz.enon.tra.uae.rest.model.response;

import com.google.gson.annotations.Expose;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public class SmsSpamResponseModel {

    @Expose
    public String status;

    @Override
    public String toString() {
        return "{\"status\":" + status + "}";
    }

}
