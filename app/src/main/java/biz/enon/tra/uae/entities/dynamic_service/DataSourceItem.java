package biz.enon.tra.uae.entities.dynamic_service;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import static biz.enon.tra.uae.global.C.ARABIC;

/**
 * Created by mobimaks on 03.11.2015.
 */
public final class DataSourceItem implements Parcelable {

    @Expose
    public String value;

    @Expose
    @SerializedName("AR")
    private String arabicName;

    @Expose
    @SerializedName("EN")
    private String englishName;

    public String getName() {
        return Locale.getDefault().toString().equalsIgnoreCase(ARABIC) ? arabicName : englishName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.arabicName);
        dest.writeString(this.englishName);
    }

    public DataSourceItem() {
    }

    protected DataSourceItem(Parcel in) {
        this.value = in.readString();
        this.arabicName = in.readString();
        this.englishName = in.readString();
    }

    public static final Parcelable.Creator<DataSourceItem> CREATOR = new Parcelable.Creator<DataSourceItem>() {
        public DataSourceItem createFromParcel(Parcel source) {
            return new DataSourceItem(source);
        }

        public DataSourceItem[] newArray(int size) {
            return new DataSourceItem[size];
        }
    };
}
