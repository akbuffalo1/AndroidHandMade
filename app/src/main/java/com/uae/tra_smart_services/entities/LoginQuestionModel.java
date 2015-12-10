package com.uae.tra_smart_services.entities;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.uae.tra_smart_services.rest.model.request.LoginModel;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse;

import java.util.ArrayList;

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
