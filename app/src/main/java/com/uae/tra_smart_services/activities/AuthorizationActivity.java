package com.uae.tra_smart_services.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.baseentities.BaseAuthorizationFragment;
import com.uae.tra_smart_services.baseentities.BaseFragmentActivity;
import com.uae.tra_smart_services.fragments.LoginFragment;
import com.uae.tra_smart_services.fragments.RegisterFragment;
import com.uae.tra_smart_services.fragments.RestorePassFragment;
import com.uae.tra_smart_services.interfaces.OnReloadData;
import com.uae.tra_smart_services.interfaces.ToolbarTitleManager;

import retrofit.RetrofitError;


public class AuthorizationActivity extends BaseFragmentActivity
            implements BaseAuthorizationFragment.AuthorizationActionsListener, ToolbarTitleManager {

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setApplicationTheme();

        setContentView(R.layout.activity_authorization);

        final Toolbar toolbar = findView(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        if (getFragmentManager().findFragmentById(getContainerId()) == null) {
            addFragment(LoginFragment.newInstance());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOpenLoginScreen() {
        super.popBackStack();
    }

    @Override
    public void onLogInSuccess() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onOpenRegisterScreen() {
        super.replaceFragmentWithBackStack(RegisterFragment.newInstance());
    }

    @Override
    public void onRegisterSuccess() {
        super.popBackStack();
    }

    @Override
    public void onOpenRestorePassScreen() {
        super.replaceFragmentWithBackStack(RestorePassFragment.newInstance());

    }

    @Override
    public void onRestorePassSuccess() {
        super.replaceFragmentWithBackStack(LoginFragment.newInstance());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int getContainerId() {
        return R.id.flContainer_AA;
    }

    @Override
    public void handleError(RetrofitError _error) {}

    @Override
    public void handleError(RetrofitError _error, OnReloadData _listener) {}
}
