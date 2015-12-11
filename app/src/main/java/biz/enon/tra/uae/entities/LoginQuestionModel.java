package biz.enon.tra.uae.entities;

import android.os.Parcel;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import biz.enon.tra.uae.rest.model.request.LoginModel;
import biz.enon.tra.uae.rest.model.response.SecurityQuestionResponse;

/**
 * Created by mobimaks on 10.12.2015.
 */
public class LoginQuestionModel extends LoginModel {

    @Expose
    public ArrayList<SecurityQuestionResponse> secretQuestions;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(secretQuestions);
    }

    public LoginQuestionModel() {
    }

    protected LoginQuestionModel(Parcel in) {
        super(in);
        this.secretQuestions = in.createTypedArrayList(SecurityQuestionResponse.CREATOR);
    }

    public static final Creator<LoginQuestionModel> CREATOR = new Creator<LoginQuestionModel>() {
        public LoginQuestionModel createFromParcel(Parcel source) {
            return new LoginQuestionModel(source);
        }

        public LoginQuestionModel[] newArray(int size) {
            return new LoginQuestionModel[size];
        }
    };

}
