package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by ak-buffalo on 21.10.15.
 */

public class GetAnnouncementsResponseModel {

    public static class Announcement implements Parcelable {
        @Expose public String _id;
        @Expose public String title;
        @Expose public String description;
        @Expose public String link;
        @Expose public String createdAt;
        @Expose public String pubDate;
        @Expose public String image;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this._id);
            dest.writeString(this.title);
            dest.writeString(this.description);
            dest.writeString(this.link);
            dest.writeString(this.createdAt);
            dest.writeString(this.pubDate);
            dest.writeString(this.image);
        }

        public Announcement() { }

        protected Announcement(Parcel in) {
            this._id = in.readString();
            this.title = in.readString();
            this.description = in.readString();
            this.link = in.readString();
            this.createdAt = in.readString();
            this.pubDate = in.readString();
            this.image = in.readString();
        }

        public static final Parcelable.Creator<Announcement> CREATOR = new Parcelable.Creator<Announcement>() {
            public Announcement createFromParcel(Parcel source) {
                return new Announcement(source);
            }

            public Announcement[] newArray(int size) {
                return new Announcement[size];
            }
        };
    }
    @Expose public List<Announcement> announcements;
}

