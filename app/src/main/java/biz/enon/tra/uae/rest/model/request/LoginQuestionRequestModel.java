package biz.enon.tra.uae.rest.model.request;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mobimaks on 10.12.2015.
 */
public class LoginQuestionRequestModel extends LoginModel {

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
        dest.writeValue(this.secretQuestionType);
        dest.writeString(this.secretQuestionAnswer);
    }

    public LoginQuestionRequestModel() {
    }

    protected LoginQuestionRequestModel(Parcel in) {
        super(in);
        this.secretQuestionType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.secretQuestionAnswer = in.readString();
    }

    public static final Creator<LoginQuestionRequestModel> CREATOR = new Creator<LoginQuestionRequestModel>() {
        public LoginQuestionRequestModel createFromParcel(Parcel source) {
            return new LoginQuestionRequestModel(source);
        }

        public LoginQuestionRequestModel[] newArray(int size) {
            return new LoginQuestionRequestModel[size];
        }
    };
}
