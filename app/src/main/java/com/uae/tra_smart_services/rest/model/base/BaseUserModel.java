package com.uae.tra_smart_services.rest.model.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse;

import java.util.List;

/**
 * Created by mobimaks on 08.10.2015.
 */
public abstract class BaseUserModel implements Parcelable {

    @Expose
    @SerializedName("first")
    public String firstName;

    @Expose
    @SerializedName("last")
    public String lastName;

    @Expose
    @SerializedName("email")
    public String email;

    @Expose
    @SerializedName("mobile")
    public String mobile;

    @Expose
    @SerializedName("enhancedSecurity")
    public Boolean enhancedSecurity;

    @Expose
    @SerializedName("secretQuestionType")
    public Integer secretQuestionType;

    @Expose
    @SerializedName("secretQuestionAnswer")
    public String secretQuestionAnswer;

    @Expose
    @SerializedName("secretQuestions")
    public List<SecurityQuestionResponse> secretQuestions;

    public String getUsername() {
        return firstName + " " + lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.mobile);
        dest.writeByte(enhancedSecurity ? (byte) 1 : (byte) 0);
        dest.writeValue(this.secretQuestionType);
        dest.writeString(this.secretQuestionAnswer);
        dest.writeTypedList(secretQuestions);
    }

    public BaseUserModel() {
    }

    protected BaseUserModel(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.mobile = in.readString();
        this.enhancedSecurity = in.readByte() != 0;
        this.secretQuestionType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.secretQuestionAnswer = in.readString();
        this.secretQuestions = in.createTypedArrayList(SecurityQuestionResponse.CREATOR);
    }

}
