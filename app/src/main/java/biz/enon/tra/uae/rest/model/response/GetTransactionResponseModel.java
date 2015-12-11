package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Mikazme on 05/10/2015.
 */
public class GetTransactionResponseModel implements Parcelable {
    @Expose
    public String title;

    @Expose
    public String type;

    @Expose
    public String traSubmitDatetime;

    @Expose
    public String modifiedDatetime;

    @Expose
    public String stateCode;

    @Expose
    public String statusCode;

    @Expose
    public String traStatus;

    @Expose
    public String serviceStage;

    @Expose
    public String description;

    public static class List extends ArrayList<GetTransactionResponseModel> {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.type);
        dest.writeString(this.traSubmitDatetime);
        dest.writeString(this.modifiedDatetime);
        dest.writeString(this.stateCode);
        dest.writeString(this.statusCode);
        dest.writeString(this.traStatus);
        dest.writeString(this.serviceStage);
        dest.writeString(this.description);
    }

    public GetTransactionResponseModel() {
    }

    protected GetTransactionResponseModel(Parcel in) {
        this.title = in.readString();
        this.type = in.readString();
        this.traSubmitDatetime = in.readString();
        this.modifiedDatetime = in.readString();
        this.stateCode = in.readString();
        this.statusCode = in.readString();
        this.traStatus = in.readString();
        this.serviceStage = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<GetTransactionResponseModel> CREATOR = new Parcelable.Creator<GetTransactionResponseModel>() {
        public GetTransactionResponseModel createFromParcel(Parcel source) {
            return new GetTransactionResponseModel(source);
        }

        public GetTransactionResponseModel[] newArray(int size) {
            return new GetTransactionResponseModel[size];
        }
    };
}
