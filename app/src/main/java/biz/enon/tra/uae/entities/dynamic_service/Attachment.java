package biz.enon.tra.uae.entities.dynamic_service;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import biz.enon.tra.uae.entities.dynamic_service.input_item.AttachmentInputItem;

/**
 * Created by mobimaks on 28.10.2015.
 */
public final class Attachment implements Parcelable {

    private final Uri mUri;
    private String mArgumentName;
    private String mServerId;

    public Attachment(@NonNull AttachmentInputItem _attachmentItem) {
        this(_attachmentItem.getAttachmentUri(), _attachmentItem.getQueryName());
    }

    public Attachment(@NonNull Uri _uri, @NonNull String _argumentName) {
        mUri = _uri;
        mArgumentName = _argumentName;
    }

    public Attachment(@NonNull Attachment _attachment) {
        this(_attachment, null);
    }

    public Attachment(@NonNull Attachment _attachment, @Nullable String _serverId) {
        mUri = _attachment.mUri.buildUpon().build();
        mArgumentName = _attachment.mArgumentName;
        mServerId = _serverId;
    }

    @NonNull
    public Uri getUri() {
        return mUri;
    }

    @NonNull
    public String getArgumentName() {
        return mArgumentName;
    }

    public void setArgumentName(String _argumentName) {
        mArgumentName = _argumentName;
    }

    @Nullable
    public String getServerId() {
        return mServerId;
    }

    public void setServerId(@NonNull String _serverId) {
        mServerId = _serverId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mUri, 0);
        dest.writeString(this.mArgumentName);
        dest.writeString(this.mServerId);
    }

    protected Attachment(Parcel in) {
        this.mUri = in.readParcelable(Uri.class.getClassLoader());
        this.mArgumentName = in.readString();
        this.mServerId = in.readString();
    }

    public static final Parcelable.Creator<Attachment> CREATOR = new Parcelable.Creator<Attachment>() {
        public Attachment createFromParcel(Parcel source) {
            return new Attachment(source);
        }

        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };
}
