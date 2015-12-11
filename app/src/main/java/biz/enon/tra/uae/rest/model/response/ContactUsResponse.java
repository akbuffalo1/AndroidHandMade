package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by mobimaks on 02.12.2015.
 */
public class ContactUsResponse implements Parcelable {

    @Expose
    @SerializedName("_id")
    public String id;

    @Expose
    public String phone;

    @Expose
    public String fax;

    @Expose
    public String email;

    @Expose
    public Location location;

    @Expose
    public String customerWorkingHours;

    @Expose
    public String workingHours;

    @Expose
    public String poBox;

    @Expose
    public String title;

    @Nullable
    public LatLng getLatLng() {
        if (location != null && location.latitude != null && location.longitude != null) {
            return new LatLng(location.latitude, location.longitude);
        }
        return null;
    }

    public static final class Location implements Parcelable {

        @Expose
        public Double latitude;

        @Expose
        public Double longitude;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.latitude);
            dest.writeValue(this.longitude);
        }

        public Location() {
        }

        protected Location(Parcel in) {
            this.latitude = (Double) in.readValue(Double.class.getClassLoader());
            this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        }

        public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
            public Location createFromParcel(Parcel source) {
                return new Location(source);
            }

            public Location[] newArray(int size) {
                return new Location[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.phone);
        dest.writeString(this.fax);
        dest.writeString(this.email);
        dest.writeParcelable(this.location, 0);
        dest.writeString(this.customerWorkingHours);
        dest.writeString(this.workingHours);
        dest.writeString(this.poBox);
        dest.writeString(this.title);
    }

    public ContactUsResponse() {
    }

    protected ContactUsResponse(Parcel in) {
        this.id = in.readString();
        this.phone = in.readString();
        this.fax = in.readString();
        this.email = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.customerWorkingHours = in.readString();
        this.workingHours = in.readString();
        this.poBox = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<ContactUsResponse> CREATOR = new Parcelable.Creator<ContactUsResponse>() {
        public ContactUsResponse createFromParcel(Parcel source) {
            return new ContactUsResponse(source);
        }

        public ContactUsResponse[] newArray(int size) {
            return new ContactUsResponse[size];
        }
    };

    public static final class List extends ArrayList<ContactUsResponse> {
    }
}
