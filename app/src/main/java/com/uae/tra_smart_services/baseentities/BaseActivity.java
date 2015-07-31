package com.uae.tra_smart_services.baseentities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.interfaces.ProgressDialogManager;

import static com.uae.tra_smart_services.entities.H.getResIdFromString;

/**
 * Created by Vitaliy on 22/07/2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements ProgressDialogManager, BaseFragment.ErrorHandler, BaseFragment.ThemaDefiner {

    private ProgressDialog mProgressDialog;
    private String stringThemaValue;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void showProgressDialog() {
        showProgressDialog("");
    }

    @Override
    public final void showProgressDialog(final String _text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
        } else if (mProgressDialog.isShowing())
            return;
        mProgressDialog.setMessage(_text == null ? "" : _text);
        mProgressDialog.show();
    }

    @Override
    public final void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public final <T extends View> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }

    @Override
    public String getStringThemeValue() {
        return stringThemaValue;
    }

    public void setApplicationTheme(){
        stringThemaValue = PreferenceManager.getDefaultSharedPreferences(this).getString(BaseCustomSwitcher.Type.THEME.toString(), "AppThemeOrange");
        setTheme(getResIdFromString(stringThemaValue, R.style.class));
    }
}