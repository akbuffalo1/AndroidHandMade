package biz.enon.tra.uae.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import biz.enon.tra.uae.entities.UserProfile;
import biz.enon.tra.uae.global.C;

/**
 * Created by mobimaks on 30.09.2015.
 */
public final class PreferenceManager {

    //region User Profile Keys
    private static final String KEY_USER_PROFILE_PREFS = "USER_PROFILE";

    private static final String KEY_FIRST_NAME = "FIRST_NAME";
    private static final String KEY_LAST_NAME = "LAST_NAME";
    private static final String KEY_STREET_ADDRESS = "STREET_ADDRESS";
    private static final String KEY_CONTACT_NUMBER = "CONTACT_NUMBER";
    //endregion

    private PreferenceManager() {
    }

    protected static Editor getEditor(final Context _context) {
        return getSharedPreferences(_context).edit();
    }

    protected static SharedPreferences getSharedPreferences(final Context _context) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(_context);
    }

    protected static Editor getEditor(final String _prefName, final Context _context) {
        return getSharedPreferences(_prefName, _context).edit();
    }

    protected static SharedPreferences getSharedPreferences(final String _prefName, final Context _context) {
        return _context.getSharedPreferences(_prefName, Context.MODE_PRIVATE);
    }

    public static boolean saveUserProfile(final Context _context, final UserProfile _profile) {
        final Editor editor = getEditor(KEY_USER_PROFILE_PREFS, _context);
        editor.putString(KEY_FIRST_NAME, _profile.firstName);
        editor.putString(KEY_LAST_NAME, _profile.lastName);
        editor.putString(KEY_STREET_ADDRESS, _profile.streetAddress);
        editor.putString(KEY_CONTACT_NUMBER, _profile.phoneNumber);
        return editor.commit();
    }

    public static UserProfile getSavedUserProfile(final Context _context) {
        final SharedPreferences prefs = getSharedPreferences(KEY_USER_PROFILE_PREFS, _context);
        final UserProfile profile = new UserProfile();
        profile.firstName = prefs.getString(KEY_FIRST_NAME, "");
        profile.lastName = prefs.getString(KEY_LAST_NAME, "");
        profile.streetAddress = prefs.getString(KEY_STREET_ADDRESS, "");
        profile.phoneNumber = prefs.getString(KEY_CONTACT_NUMBER, "");
        return profile;
    }

    public static boolean clearSavedUserProfile(final Context _context) {
        return getEditor(KEY_USER_PROFILE_PREFS, _context).clear().commit();
    }

    public static void setLoggedIn(final Context _context, final boolean _isLoggedIn) {
        getEditor(_context).putBoolean(C.IS_LOGGED_IN, _isLoggedIn).commit();
    }
}
