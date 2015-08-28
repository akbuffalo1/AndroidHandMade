package com.uae.tra_smart_services.fragment;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.fragment.base.BaseAuthorizationFragment;

/**
 * Created by Andrey Korneychuk on 22.07.15.
 */
public class RestorePassFragment extends BaseAuthorizationFragment {


    public static RestorePassFragment newInstance() {
        return new RestorePassFragment();
    }

    @Override
    protected int getTitle() {
        return R.string.forgot_password_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_restorepass;
    }

    @Override
    public void onDialogCancel() {
        // TODO Implement method when Restore password logic will be done.
    }
}
