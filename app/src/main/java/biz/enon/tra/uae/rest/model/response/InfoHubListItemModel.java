package biz.enon.tra.uae.rest.model.response;

import android.os.Parcel;

import com.google.gson.annotations.Expose;

/**
 * Created by ak-buffalo on 20.08.15.
 */
public class InfoHubListItemModel<T> {

    @Expose
    private String mIconUrl;

    @Expose
    private String mHeaderImageUrl;

    @Expose
    private String mTitle = "";

    @Expose
    private String mDescription;

    @Expose
    private String mFullDescription;

    @Expose
    private String mDate;

    protected InfoHubListItemModel() {
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public void setIconUrl(String mIconUrl) {
        this.mIconUrl = mIconUrl;
    }

    public String getHeaderImageUrl() {
        return mHeaderImageUrl;
    }

    public void setHeaderImageUrl(String _headerImageUrl) {
        this.mHeaderImageUrl = _headerImageUrl;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setFullDescription(String _fullDescription) {
        this.mFullDescription = _fullDescription;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getFullDescription() {
        return mFullDescription;
    }

    public String getDate() {
        return mDate;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    public int describeContents() {
        return 0;
    }

    public int mData;

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    public InfoHubListItemModel(Parcel in) {
        mData = in.readInt();
    }
}
