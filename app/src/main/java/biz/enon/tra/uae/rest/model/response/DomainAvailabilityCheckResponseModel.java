package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public class DomainAvailabilityCheckResponseModel implements Parcelable {

    @Expose
    public String availableStatus;

    @Expose
    public String domainStrValue;

    @Override
    public String toString() {
        return "{\"availableStatus\":" + availableStatus + ", \"domainStrValue\":" + domainStrValue + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.availableStatus);
        dest.writeString(this.domainStrValue);
    }

    public DomainAvailabilityCheckResponseModel() {
    }

    protected DomainAvailabilityCheckResponseModel(Parcel in) {
        this.availableStatus = in.readString();
        this.domainStrValue = in.readString();
    }

    public static final Parcelable.Creator<DomainAvailabilityCheckResponseModel> CREATOR = new Parcelable.Creator<DomainAvailabilityCheckResponseModel>() {
        public DomainAvailabilityCheckResponseModel createFromParcel(Parcel source) {
            return new DomainAvailabilityCheckResponseModel(source);
        }

        public DomainAvailabilityCheckResponseModel[] newArray(int size) {
            return new DomainAvailabilityCheckResponseModel[size];
        }
    };
}
