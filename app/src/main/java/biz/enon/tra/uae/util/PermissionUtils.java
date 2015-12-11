package biz.enon.tra.uae.util;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

import biz.enon.tra.uae.entities.Permission;

/**
 * Created by mobimaks on 12.10.2015.
 */
public final class PermissionUtils {

    private PermissionUtils() {
    }

    @NonNull
    @CheckResult
    public static ArrayList<Permission> getUncheckedPermissions(@NonNull final Context _context,
                                                                @NonNull final Collection<Permission> _permissions) {
        if (_permissions.size() < 1) {
            return new ArrayList<>();
        }

        final ArrayList<Permission> uncheckedPermissions = new ArrayList<>();
        for (final Permission permission : _permissions) {
            if (!permission.isGranted(_context)) {
                uncheckedPermissions.add(permission);
            }
        }
        return uncheckedPermissions;
    }

    public static ArrayList<Permission> getPermissionRationales(@NonNull final Fragment _fragment,
                                                                @NonNull final Collection<Permission> _permissions) {
        if (_permissions.size() < 1) {
            return new ArrayList<>();
        }

        final ArrayList<Permission> permissions = new ArrayList<>();
        for (final Permission permission : _permissions) {
            if (permission.shouldShowRequestPermissionRationale(_fragment)) {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    public static String[] getPermissionNames(@NonNull final ArrayList<Permission> _permissions) {
        final String[] permissionNames = new String[_permissions.size()];
        for (int i = 0; i < _permissions.size(); i++) {
            final Permission permission = _permissions.get(i);
            permissionNames[i] = permission.getPermissionName();
        }
        return permissionNames;
    }

    @NonNull
    @CheckResult
    public static ArrayList<Permission> getDeclinedPermissions(@NonNull ArrayList<Permission> _permissions,
                                                               @NonNull int[] grantResults) {

        final ArrayList<Permission> declinedPermissions = new ArrayList<>();
        for (int i = 0; i < _permissions.size(); i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                declinedPermissions.add(_permissions.get(i));
            }
        }
        return declinedPermissions;
    }


}
