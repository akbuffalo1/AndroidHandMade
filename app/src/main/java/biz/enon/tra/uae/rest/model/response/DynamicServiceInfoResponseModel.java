package biz.enon.tra.uae.rest.model.response;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Locale;

import biz.enon.tra.uae.BuildConfig;
import biz.enon.tra.uae.global.C;

/**
 * Created by mobimaks on 20.10.2015.
 */
public final class DynamicServiceInfoResponseModel implements Parcelable {

    @Expose
    @SerializedName("_id")
    public String id;

    @Expose
    public String icon;

    @Expose
    public boolean needAuth;

    @Expose
    private ServiceName serviceName;

    private String iconUrl;

    public DynamicServiceInfoResponseModel() {
    }

    public String getServiceName() {
        if (serviceName == null) {
            return "";
        } else {
            return Locale.getDefault().toString().equalsIgnoreCase(C.ARABIC) ? serviceName.arabicName : serviceName.englishName;
        }
    }

    public final String getIconUrl(final Context _context) {
        if (iconUrl == null) {
            String url = BuildConfig.SERVER_URL;
            if (TextUtils.isEmpty(icon)) {
                iconUrl = url;
            } else {
                iconUrl = url + icon + "/" + getDensityClass(_context);
            }
        }
        return iconUrl;
    }

    private String getDensityClass(final Context _context) {
        final int densityDpi = _context.getResources().getDisplayMetrics().densityDpi;
        if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
            return "mdpi";
        } else if (densityDpi <= DisplayMetrics.DENSITY_HIGH) {
            return "hdpi";
        } else if (densityDpi <= DisplayMetrics.DENSITY_XHIGH) {
            return "xhdpi";
        } else if (densityDpi <= DisplayMetrics.DENSITY_XXHIGH) {
            return "xxhdpi";
        } else {
            return "xxxhdpi";
        }
    }

    public static class List extends ArrayList<DynamicServiceInfoResponseModel> {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.icon);
        dest.writeParcelable(this.serviceName, 0);
    }

    protected DynamicServiceInfoResponseModel(Parcel in) {
        this.id = in.readString();
        this.icon = in.readString();
        this.serviceName = in.readParcelable(ServiceName.class.getClassLoader());
    }

    public static final Parcelable.Creator<DynamicServiceInfoResponseModel> CREATOR = new Parcelable.Creator<DynamicServiceInfoResponseModel>() {
        public DynamicServiceInfoResponseModel createFromParcel(Parcel source) {
            return new DynamicServiceInfoResponseModel(source);
        }

        public DynamicServiceInfoResponseModel[] newArray(int size) {
            return new DynamicServiceInfoResponseModel[size];
        }
    };

    private final static class ServiceName implements Parcelable {

        @Expose
        @SerializedName("AR")
        String arabicName;

        @Expose
        @SerializedName("EN")
        String englishName;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.arabicName);
            dest.writeString(this.englishName);
        }

        public ServiceName() {
        }

        protected ServiceName(Parcel in) {
            this.arabicName = in.readString();
            this.englishName = in.readString();
        }

        public static final Creator<ServiceName> CREATOR = new Creator<ServiceName>() {
            public ServiceName createFromParcel(Parcel source) {
                return new ServiceName(source);
            }

            public ServiceName[] newArray(int size) {
                return new ServiceName[size];
            }
        };
    }

}
