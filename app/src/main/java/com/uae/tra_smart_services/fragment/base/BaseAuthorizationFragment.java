package com.uae.tra_smart_services.fragment.base;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.entities.LoginQuestionModel;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse;

import java.util.ArrayList;

/**
 * Created by ak-buffalo on 22.07.15.
 */
public abstract class BaseAuthorizationFragment extends BaseFragment{
    protected AuthorizationActionsListener actionsListener;

    @Override
    public void onAttach(final Activity _activity) {
        super.onAttach(_activity);

        try {
            actionsListener = (AuthorizationActionsListener) _activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(_activity.toString()
                    + " must implement AuthorizationActionsListener");
        }
    }

    public interface AuthorizationActionsListener {

        /** Handlers methods for log in screen*/
        void onOpenLoginScreen();

        void onOpenSecurityLoginScreen(@NonNull LoginQuestionModel _loginModel);

        void onLogInSuccess();

        /** Handlers methods for register screen*/
        void onOpenRegisterScreen(@NonNull ArrayList<SecurityQuestionResponse> _questions);

        void onRegisterSuccess();

        /** Handlers methods for restore password screen*/
        void onOpenRestorePassScreen();

        void onRestorePassSuccess();

        void onBackPressed();

        void onHomeScreenOpenWithoutAuth();
    }

    @Override
    protected int getGlobalContainerResource() {
        return R.id.rlGlobalContainer_AA;
    }
}
