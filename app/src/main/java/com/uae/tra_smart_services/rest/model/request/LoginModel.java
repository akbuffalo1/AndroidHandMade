package com.uae.tra_smart_services.rest.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse;

import java.util.ArrayList;

/**
 * Created by mobimaks on 15.08.2015.
 */
public class LoginModel implements Parcelable {

    @Expose
    public String login;

    @Expose
    public String pass;

    @Expose
    public ArrayList<SecurityQuestionResponse> secretQuestions;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.login);
        dest.writeString(this.pass);
        dest.writeTypedList(secretQuestions);
    }

    public LoginModel() {
    }

    protected LoginModel(Parcel in) {
        this.login = in.readString();
        this.pass = in.readString();
        this.secretQuestions = in.createTypedArrayList(SecurityQuestionResponse.CREATOR);
    }

    public static final Parcelable.Creator<LoginModel> CREATOR = new Parcelable.Creator<LoginModel>() {
        public LoginModel createFromParcel(Parcel source) {
            return new LoginModel(source);
        }

        public LoginModel[] newArray(int size) {
            return new LoginModel[size];
        }
    };
}
