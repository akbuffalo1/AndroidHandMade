package biz.enon.tra.uae.fragment.base;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.entities.LoginQuestionModel;
import biz.enon.tra.uae.rest.model.response.SecurityQuestionResponse;

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
