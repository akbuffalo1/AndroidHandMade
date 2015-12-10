package com.uae.tra_smart_services.rest.model.request;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mobimaks on 15.08.2015.
 */
public class RegisterModel extends LoginModel {

    @Expose
    public String first;

    @Expose
    public String last;

    @Expose
    public String emiratesId;

    @Expose
    public Integer state;

    @Expose
    public String mobile;

    @Expose
    public String email;

    @Expose
    @SerializedName("enhancedSecurity")
    public Boolean enhancedSecurity;

    @Expose
    @SerializedName("secretQuestionType")
    public Integer secretQuestionType;

    @Expose
    @SerializedName("secretQuestionAnswer")
    public String secretQuestionAnswer;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.first);
        dest.writeString(this.last);
        dest.writeString(this.emiratesId);
        dest.writeValue(this.state);
        dest.writeString(this.mobile);
        dest.writeString(this.email);
        dest.writeValue(this.enhancedSecurity);
        dest.writeValue(this.secretQuestionType);
        dest.writeString(this.secretQuestionAnswer);
    }

    public RegisterModel() {
    }

    protected RegisterModel(Parcel in) {
        super(in);
        this.first = in.readString();
        this.last = in.readString();
        this.emiratesId = in.readString();
        this.state = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mobile = in.readString();
        this.email = in.readString();
        this.enhancedSecurity = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.secretQuestionType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.secretQuestionAnswer = in.readString();
    }

    public static final Creator<RegisterModel> CREATOR = new Creator<RegisterModel>() {
        public RegisterModel createFromParcel(Parcel source) {
            return new RegisterModel(source);
        }

        public RegisterModel[] newArray(int size) {
            return new RegisterModel[size];
        }
    };
}
