package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mobimaks on 05.10.2015.
 */
public final class ServiceInfoResponse implements Parcelable {

    @Expose
    @SerializedName("Name")
    public String name;

    @Expose
    @SerializedName("About the service")
    public String aboutService;

    @Expose
    @SerializedName("Required documents")
    public String requiredDocs;

    @Expose
    @SerializedName("Terms and conditions")
    public String termsAndConditions;

    @Expose
    @SerializedName("Service Package")
    public String servicePackage;

    @Expose
    @SerializedName("Expected time")
    public String expectedTime;

    @Expose
    @SerializedName("Service fee")
    public String serviceFee;

    @Expose
    @SerializedName("Officer in charge of this service")
    public String officer;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.aboutService);
        dest.writeString(this.requiredDocs);
        dest.writeString(this.termsAndConditions);
        dest.writeString(this.servicePackage);
        dest.writeString(this.expectedTime);
        dest.writeString(this.serviceFee);
        dest.writeString(this.officer);
    }

    public ServiceInfoResponse() {
    }

    protected ServiceInfoResponse(Parcel in) {
        this.name = in.readString();
        this.aboutService = in.readString();
        this.requiredDocs = in.readString();
        this.termsAndConditions = in.readString();
        this.servicePackage = in.readString();
        this.expectedTime = in.readString();
        this.serviceFee = in.readString();
        this.officer = in.readString();
    }

    public static final Parcelable.Creator<ServiceInfoResponse> CREATOR = new Parcelable.Creator<ServiceInfoResponse>() {
        public ServiceInfoResponse createFromParcel(Parcel source) {
            return new ServiceInfoResponse(source);
        }

        public ServiceInfoResponse[] newArray(int size) {
            return new ServiceInfoResponse[size];
        }
    };
}
