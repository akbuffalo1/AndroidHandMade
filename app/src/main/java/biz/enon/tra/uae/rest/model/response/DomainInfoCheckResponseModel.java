package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public class DomainInfoCheckResponseModel implements Parcelable {

    @Expose
    public String urlData;

    /*
    String domainName;
    String registrationId;
    String registrationName;
    String status;
    String registrantContactId;
    String registrantContactName;

    public String getDomainName() {
        return domainName;
    }

    public DomainInfoCheckResponseModel setDomainName(String domainName) {
        this.domainName = domainName;
        return this;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public DomainInfoCheckResponseModel setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
        return this;
    }

    public String getRegistrationName() {
        return registrationName;
    }

    public DomainInfoCheckResponseModel setRegistrationName(String registrationName) {
        this.registrationName = registrationName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public DomainInfoCheckResponseModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getRegistrantContactId() {
        return registrantContactId;
    }

    public DomainInfoCheckResponseModel setRegistrantContactId(String registrantContactId) {
        this.registrantContactId = registrantContactId;
        return this;
    }

    public String getRegistrantContactName() {
        return registrantContactName;
    }

    public DomainInfoCheckResponseModel setRegistrantContactName(String registrantContactName) {
        this.registrantContactName = registrantContactName;
        return this;
    }
    */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.urlData);
    }

    public DomainInfoCheckResponseModel() {
    }

    protected DomainInfoCheckResponseModel(Parcel in) {
        this.urlData = in.readString();
    }

    public static final Parcelable.Creator<DomainInfoCheckResponseModel> CREATOR = new Parcelable.Creator<DomainInfoCheckResponseModel>() {
        public DomainInfoCheckResponseModel createFromParcel(Parcel source) {
            return new DomainInfoCheckResponseModel(source);
        }

        public DomainInfoCheckResponseModel[] newArray(int size) {
            return new DomainInfoCheckResponseModel[size];
        }
    };

}


