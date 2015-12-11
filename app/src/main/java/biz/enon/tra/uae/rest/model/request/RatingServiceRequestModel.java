package biz.enon.tra.uae.rest.model.request;

import com.google.gson.annotations.Expose;

/**
 * Created by PC on 9/3/2015.
 */
public class RatingServiceRequestModel{
    @Expose
    public String serviceName;

    @Expose
    public Integer rate;

    @Expose
    public String feedback = "";

    public RatingServiceRequestModel(String _serviceName, Integer _rate) {
        this(_serviceName, _rate, null);
    }

    public RatingServiceRequestModel(String _serviceName, Integer _rate, String _feedback) {
        serviceName = _serviceName;
        rate = _rate;
        feedback = (_feedback == null) ? String.valueOf(_rate) : _feedback;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String _serviceName) {
        serviceName = _serviceName;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int _rate) {
        this.rate = _rate;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String _feedback) {
        feedback = _feedback;
    }

    @Override
    public String toString() {
        return "{"+
                    "serviceName:" + serviceName + ","+
                    "rate:" + rate + ", " +
                    "feedback:" + feedback +
                "}";
    }
}