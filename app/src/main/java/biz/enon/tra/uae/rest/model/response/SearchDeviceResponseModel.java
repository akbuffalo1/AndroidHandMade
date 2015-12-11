package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Mikazme on 13/08/2015.
 */
public class SearchDeviceResponseModel implements Parcelable {

    @Expose
    public String tac;

    @Expose
    public String marketingName;

    @Expose
    public String designationType;

    @Expose
    public String manufacturer;

    @Expose
    public String bands;

    @Expose
    public String allocationDate;

    @Expose
    public String countryCode;

    @Expose
    public String fixedCode;

    @Expose
    public String radioInterface;

    @Expose
    public String manufacturerCode;

    @Expose
    public Long startIndex;

    @Expose
    public Long endIndex;

    @Expose
    public Long count;

    @Expose
    public Long totalNumberofRecords;

    @SuppressWarnings("serial")
    public static class List extends ArrayList<SearchDeviceResponseModel> {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tac);
        dest.writeString(this.marketingName);
        dest.writeString(this.designationType);
        dest.writeString(this.manufacturer);
        dest.writeString(this.bands);
        dest.writeString(this.allocationDate);
        dest.writeString(this.countryCode);
        dest.writeString(this.fixedCode);
        dest.writeString(this.radioInterface);
        dest.writeString(this.manufacturerCode);
        dest.writeValue(this.startIndex);
        dest.writeValue(this.endIndex);
        dest.writeValue(this.count);
        dest.writeValue(this.totalNumberofRecords);
    }

    public SearchDeviceResponseModel() {
    }

    protected SearchDeviceResponseModel(Parcel in) {
        this.tac = in.readString();
        this.marketingName = in.readString();
        this.designationType = in.readString();
        this.manufacturer = in.readString();
        this.bands = in.readString();
        this.allocationDate = in.readString();
        this.countryCode = in.readString();
        this.fixedCode = in.readString();
        this.radioInterface = in.readString();
        this.manufacturerCode = in.readString();
        this.startIndex = (Long) in.readValue(Long.class.getClassLoader());
        this.endIndex = (Long) in.readValue(Long.class.getClassLoader());
        this.count = (Long) in.readValue(Long.class.getClassLoader());
        this.totalNumberofRecords = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<SearchDeviceResponseModel> CREATOR = new Parcelable.Creator<SearchDeviceResponseModel>() {
        public SearchDeviceResponseModel createFromParcel(Parcel source) {
            return new SearchDeviceResponseModel(source);
        }

        public SearchDeviceResponseModel[] newArray(int size) {
            return new SearchDeviceResponseModel[size];
        }
    };
}
