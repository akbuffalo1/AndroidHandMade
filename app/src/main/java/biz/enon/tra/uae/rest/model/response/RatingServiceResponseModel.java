package biz.enon.tra.uae.rest.model.response;

import com.google.gson.annotations.Expose;

import java.util.List;

import retrofit.client.Header;
import retrofit.mime.TypedInput;

/**
 * Created by PC on 9/3/2015.
 */
public class RatingServiceResponseModel extends CustomResponse {

    @Expose
    public String serviceName;

    @Expose
    public Integer rate;

    @Expose
    public String feedback = "";

    public RatingServiceResponseModel(String url, int status, String reason, List<Header> headers, TypedInput body) {
        super(url, status, reason, headers, body);
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "{" +
                "serviceName:" + serviceName + "," +
                "rate:" + rate + ", " +
                "feedback:" + feedback +
                "}";
    }
}