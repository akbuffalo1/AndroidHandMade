package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import biz.enon.tra.uae.global.ServerConstants;
import biz.enon.tra.uae.rest.model.base.BaseUserModel;

/**
 * Created by mobimaks on 03.10.2015.
 */
public class UserProfileResponseModel extends BaseUserModel {

    @Expose
    @SerializedName("image")
    public String imageUrl;

    public String getImageUrl() {
        if (imageUrl == null) {
            return "";
        }
        return ServerConstants.BASE_URL + imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.imageUrl);
    }

    public UserProfileResponseModel() {
    }

    protected UserProfileResponseModel(Parcel in) {
        super(in);
        this.imageUrl = in.readString();
    }

    public static final Creator<UserProfileResponseModel> CREATOR = new Creator<UserProfileResponseModel>() {
        public UserProfileResponseModel createFromParcel(Parcel source) {
            return new UserProfileResponseModel(source);
        }

        public UserProfileResponseModel[] newArray(int size) {
            return new UserProfileResponseModel[size];
        }
    };
}
