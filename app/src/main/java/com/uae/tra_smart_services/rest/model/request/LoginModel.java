package com.uae.tra_smart_services.rest.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by mobimaks on 15.08.2015.
 */
public class LoginModel implements Parcelable {

    @Expose
    public String login;

    @Expose
    public String pass;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.login);
        dest.writeString(this.pass);
    }

    public LoginModel() {
    }

    protected LoginModel(Parcel in) {
        this.login = in.readString();
        this.pass = in.readString();
    }

    public static final Creator<LoginModel> CREATOR = new Creator<LoginModel>() {
        public LoginModel createFromParcel(Parcel source) {
            return new LoginModel(source);
        }

        public LoginModel[] newArray(int size) {
            return new LoginModel[size];
        }
    };

}
