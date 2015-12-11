package biz.enon.tra.uae.entities;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker.PermissionResult;

/**
 * Created by mobimaks on 12.10.2015.
 */
public final class Permission implements Parcelable {

    private final String mPermission;

    @StringRes
    private final int mPermissionExplanation;

    public Permission(final String _permission, @StringRes final int _permissionExplanation) {
        mPermission = _permission;
        mPermissionExplanation = _permissionExplanation;
    }

    @StringRes
    public final int getPermissionExplanationRes() {
        return mPermissionExplanation;
    }

    public final String getPermissionName() {
        return mPermission;
    }

    public final boolean isGranted(final Context _context) {
        return checkPermission(_context) == PackageManager.PERMISSION_GRANTED;
    }

    @PermissionResult
    public final int checkPermission(final Context _context) {
        return ContextCompat.checkSelfPermission(_context, mPermission);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public final boolean shouldShowRequestPermissionRationale(final Fragment _fragment){
        return _fragment.shouldShowRequestPermissionRationale(mPermission);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPermission);
        dest.writeInt(this.mPermissionExplanation);
    }

    protected Permission(Parcel in) {
        this.mPermission = in.readString();
        this.mPermissionExplanation = in.readInt();
    }

    public static final Parcelable.Creator<Permission> CREATOR = new Parcelable.Creator<Permission>() {
        public Permission createFromParcel(Parcel source) {
            return new Permission(source);
        }

        public Permission[] newArray(int size) {
            return new Permission[size];
        }
    };
}
