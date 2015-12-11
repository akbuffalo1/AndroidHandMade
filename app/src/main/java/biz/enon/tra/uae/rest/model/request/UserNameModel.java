package biz.enon.tra.uae.rest.model.request;

import android.net.Uri;
import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import biz.enon.tra.uae.rest.model.base.BaseUserModel;

/**
 * Created by mobimaks on 03.10.2015.
 */
public class UserNameModel extends BaseUserModel {

    public Uri imageUri;

    @Expose
    @SerializedName("image")
    public String imageBase64;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.imageBase64);
    }

    public UserNameModel() {
    }

    protected UserNameModel(Parcel in) {
        super(in);
        this.imageBase64 = in.readString();
    }

    public static final Creator<UserNameModel> CREATOR = new Creator<UserNameModel>() {
        public UserNameModel createFromParcel(Parcel source) {
            return new UserNameModel(source);
        }

        public UserNameModel[] newArray(int size) {
            return new UserNameModel[size];
        }
    };
}
