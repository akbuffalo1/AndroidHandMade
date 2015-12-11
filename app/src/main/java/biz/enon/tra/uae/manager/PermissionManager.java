package biz.enon.tra.uae.manager;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.entities.Permission;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.interfaces.OnOpenPermissionExplanationDialogListener;
import biz.enon.tra.uae.util.PermissionUtils;

/**
 * Created by mobimaks on 13.10.2015.
 */
public final class PermissionManager {

    private static final String KEY_PERMISSIONS = PermissionManager.class.getSimpleName() + " PERMISSIONS";

    private final Context mContext;
    private final Map<String, Permission> mRequiredPermissions;
    private final OnOpenPermissionExplanationDialogListener mExplanationDialogListener;

    private ArrayList<Permission> mPermissionsToCheck;
    private OnPermissionRequestSuccessListener mRequestSuccessListener;

    public PermissionManager(@NonNull final Context _context,
                             @NonNull final List<Permission> _permissions,
                             @NonNull final OnOpenPermissionExplanationDialogListener _explanationDialogListener) {
        mContext = _context.getApplicationContext();
        mRequiredPermissions = new HashMap<>();
        initRequiredPermissions(_permissions);
        mExplanationDialogListener = _explanationDialogListener;
    }

    private void initRequiredPermissions(@NonNull final List<Permission> _permissions) {
        for (int i = 0; i < _permissions.size(); i++) {
            final Permission permission = _permissions.get(i);
            mRequiredPermissions.put(permission.getPermissionName(), permission);
        }
    }

    public void setRequestSuccessListener(@Nullable OnPermissionRequestSuccessListener _requestSuccessListener) {
        mRequestSuccessListener = _requestSuccessListener;
    }

    public boolean isAllPermissionsChecked() {
        return PermissionUtils.getUncheckedPermissions(mContext, mRequiredPermissions.values()).isEmpty();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestUncheckedPermissions(final Fragment _fragment, final int _requestCode) {
        final ArrayList<Permission> uncheckedPermissions =
                PermissionUtils.getUncheckedPermissions(mContext, mRequiredPermissions.values());

        final ArrayList<Permission> rationalesPermission = PermissionUtils.getPermissionRationales(_fragment, uncheckedPermissions);
        if (rationalesPermission.isEmpty()) {
            _fragment.requestPermissions(PermissionUtils.getPermissionNames(uncheckedPermissions), _requestCode);
        } else {
            openPermissionExplanationDialog(_fragment, rationalesPermission, _requestCode);
        }
    }

    private void openPermissionExplanationDialog(final Fragment _fragment,
                                                 final ArrayList<Permission> _rationalesPermission,
                                                 final int _requestCode) {

        final StringBuilder explanationTextBuilder = new StringBuilder();
        explanationTextBuilder.append(_fragment.getString(_rationalesPermission.get(0).getPermissionExplanationRes()));
        for (int i = 0; i < _rationalesPermission.size() - 1; i++) {
            explanationTextBuilder.append('\n');
            explanationTextBuilder.append(_fragment.getString(_rationalesPermission.get(i + 1).getPermissionExplanationRes()));
        }
        mPermissionsToCheck = _rationalesPermission;
        mExplanationDialogListener.onOpenPermissionExplanationDialog(_requestCode, explanationTextBuilder.toString());
    }

    @TargetApi(Build.VERSION_CODES.M)
    public final void onConfirmPermissionExplanationDialog(final Fragment _fragment, final int _requestCode) {
        _fragment.requestPermissions(PermissionUtils.getPermissionNames(mPermissionsToCheck), _requestCode);
        mPermissionsToCheck = null;
    }

    public final boolean onRequestPermissionsResult(@NonNull Fragment _fragment,
                                                    int _requestCode,
                                                    String[] _permissions,
                                                    @NonNull int[] _grantResults) {

        final ArrayList<Permission> requestedPermissions = getRequestedPermissions(_permissions);
        ArrayList<Permission> declinedPermissions = PermissionUtils.getDeclinedPermissions(requestedPermissions, _grantResults);
        if (declinedPermissions.isEmpty()) {
            if (mRequestSuccessListener != null) {
                mRequestSuccessListener.onPermissionRequestSuccess(_fragment, _requestCode);
            }
        } else {
            Toast.makeText(mContext, R.string.attachment_permission_denied, C.TOAST_LENGTH).show();
        }
        return true;
    }

    @NonNull
    private ArrayList<Permission> getRequestedPermissions(String[] _permissions) {
        final ArrayList<Permission> requestedPermissions = new ArrayList<>();
        for (final String permission : _permissions) {
            requestedPermissions.add(mRequiredPermissions.get(permission));
        }
        return requestedPermissions;
    }


    public final void onRestoreInstanceState(@NonNull final Bundle _savedInstanceState) {
        mPermissionsToCheck = _savedInstanceState.getParcelableArrayList(KEY_PERMISSIONS);
    }

    public final void removeSavedState(@NonNull final Bundle _savedInstanceState) {
        _savedInstanceState.remove(KEY_PERMISSIONS);
    }

    public final void onSaveInstanceState(@NonNull final Bundle _outState) {
        _outState.putParcelableArrayList(KEY_PERMISSIONS, mPermissionsToCheck);
    }

    public interface OnPermissionRequestSuccessListener {
        void onPermissionRequestSuccess(final Fragment _fragment, final int _requestCode);
    }
}
