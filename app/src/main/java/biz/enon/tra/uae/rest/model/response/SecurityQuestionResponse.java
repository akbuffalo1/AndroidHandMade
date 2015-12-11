package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by mobimaks on 10.12.2015.
 */
public class SecurityQuestionResponse implements Parcelable {

    @Expose
    @SerializedName("value")
    public Integer id;

    @Expose
    @SerializedName("label")
    public String text;

    public SecurityQuestionResponse(Integer _id, String _text) {
        id = _id;
        text = _text;
    }

    public SecurityQuestionResponse() {
    }

    protected SecurityQuestionResponse(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.text = in.readString();
    }

    public static final Parcelable.Creator<SecurityQuestionResponse> CREATOR = new Parcelable.Creator<SecurityQuestionResponse>() {
        public SecurityQuestionResponse createFromParcel(Parcel source) {
            return new SecurityQuestionResponse(source);
        }

        public SecurityQuestionResponse[] newArray(int size) {
            return new SecurityQuestionResponse[size];
        }
    };

    public static final class List extends ArrayList<SecurityQuestionResponse> {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.text);
    }

}