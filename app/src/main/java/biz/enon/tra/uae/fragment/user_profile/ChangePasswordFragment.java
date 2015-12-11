package biz.enon.tra.uae.fragment.user_profile;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.HexagonView;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.customviews.ProfileController;
import biz.enon.tra.uae.customviews.ProfileController.ControllerButton;
import biz.enon.tra.uae.customviews.ProfileController.OnControllerButtonClickListener;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.interfaces.Loader.BackButton;
import biz.enon.tra.uae.interfaces.Loader.Cancelled;
import biz.enon.tra.uae.rest.robo_requests.ChangePasswordRequest;
import retrofit.client.Response;

import static biz.enon.tra.uae.global.C.MAX_PASSWORD_LENGTH;
import static biz.enon.tra.uae.global.C.MIN_PASSWORD_LENGTH;

/**
 * Created by mobimaks on 08.09.2015.
 */
public class ChangePasswordFragment extends BaseFragment implements OnCheckedChangeListener, OnControllerButtonClickListener {

    private static final String KEY_CHANGE_PASSWORD_REQUEST = "CHANGE_PASSWORD_REQUEST";

    private HexagonView hvUserAvatar;
    private EditText etOldPassword, etNewPassword, etNewPasswordRetype;
    private ToggleButton tbOldPassword, tgNewPassword, tbNewPasswordRetype;
    private ProfileController pcProfileController;

    private ChangePasswordRequest mChangePasswordRequest;
    private ChangePasswordRequestListener mChangePasswordRequestListener;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    protected void initViews() {
        super.initViews();
        hvUserAvatar = findView(R.id.hvUserAvatar_FCP);
        etOldPassword = findView(R.id.etOldPassword_FCP);
        etNewPassword = findView(R.id.etNewPassword_FCP);
        etNewPasswordRetype = findView(R.id.etNewPasswordRetype_FCP);
        tbOldPassword = findView(R.id.tbOldPassword_FCP);
        tgNewPassword = findView(R.id.tgNewPassword_FCP);
        tbNewPasswordRetype = findView(R.id.tbNewPasswordRetype_FCP);
        pcProfileController = findView(R.id.pcProfileController_FCP);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tbOldPassword.setOnCheckedChangeListener(this);
        tgNewPassword.setOnCheckedChangeListener(this);
        tbNewPasswordRetype.setOnCheckedChangeListener(this);
        pcProfileController.setOnButtonClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChangePasswordRequestListener = new ChangePasswordRequestListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().removeDataFromCache(Response.class, KEY_CHANGE_PASSWORD_REQUEST);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tbOldPassword_FCP:
                setPasswordVisibility(etOldPassword, isChecked);
                break;
            case R.id.tgNewPassword_FCP:
                setPasswordVisibility(etNewPassword, isChecked);
                break;
            case R.id.tbNewPasswordRetype_FCP:
                setPasswordVisibility(etNewPasswordRetype, isChecked);
                break;
        }
    }

    private void setPasswordVisibility(final EditText _passwordEditText, final boolean _isVisible) {
        _passwordEditText.setTransformationMethod(_isVisible ? null : PasswordTransformationMethod.getInstance());
    }

    @Override
    public void onControllerButtonClick(final View _view, final @ControllerButton int _buttonId) {
        switch (_buttonId) {
            case ProfileController.BUTTON_CANCEL:
                getFragmentManager().popBackStack();
                break;
            case ProfileController.BUTTON_CONFIRM:
                if (validateData()) {
                    changePassword();
                }
                break;
            case ProfileController.BUTTON_RESET:
                clearAllFields();
                break;
        }
    }

    private boolean validateData() {
        final String oldPass = etOldPassword.getText().toString().trim();
        final String newPass = etNewPassword.getText().toString().trim();
        final String newPassRetype = etNewPasswordRetype.getText().toString().trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || newPassRetype.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_fill_all_fields, C.TOAST_LENGTH).show();
            return false;
        }

        if (newPass.length() < MIN_PASSWORD_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_password_short, C.TOAST_LENGTH).show();
            return false;
        }

        if (newPass.length() > MAX_PASSWORD_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_password_long, C.TOAST_LENGTH).show();
            return false;
        }

        if (!newPass.equals(newPassRetype)) {
            Toast.makeText(getActivity(), R.string.error_password_confirm, C.TOAST_LENGTH).show();
            return false;
        }

        if (!newPass.equals(newPassRetype)) {
            Toast.makeText(getActivity(), R.string.error_password_confirm, C.TOAST_LENGTH).show();
            return false;
        }

        return true;
    }

    private void changePassword() {
        mChangePasswordRequest = new ChangePasswordRequest(etOldPassword.getText().toString(), etNewPassword.getText().toString());

        hideKeyboard(getView());
        loaderOverlayShow(getString(R.string.fragment_edit_user_profile_saving), mChangePasswordRequestListener, false);
        loaderOverlayButtonBehavior(mChangePasswordRequestListener);
        getSpiceManager().execute(mChangePasswordRequest, KEY_CHANGE_PASSWORD_REQUEST,
                DurationInMillis.ALWAYS_EXPIRED, mChangePasswordRequestListener);
    }

    private void clearAllFields() {
        etOldPassword.getText().clear();
        etNewPassword.getText().clear();
        etNewPasswordRetype.getText().clear();
    }

    private class ChangePasswordRequestListener implements RequestListener<Response>, BackButton, Cancelled {

        private boolean isRequestSuccess;

        @Override
        public void onRequestSuccess(Response result) {
            getSpiceManager().removeDataFromCache(Response.class, KEY_CHANGE_PASSWORD_REQUEST);
            if (isAdded() && result != null) {
                isRequestSuccess = true;
                loaderOverlaySuccess(getString(R.string.fragment_change_password_success));
            }
        }

        @Override
        public void onBackButtonPressed(LoaderView.State _currentState) {
            getFragmentManager().popBackStackImmediate();
            if (isRequestSuccess) {
                getFragmentManager().popBackStackImmediate();
            }
        }

        @Override
        public void onLoadingCanceled() {
            isRequestSuccess = false;
            if (getSpiceManager().isStarted()) {
                getSpiceManager().cancel(mChangePasswordRequest);
                getSpiceManager().removeDataFromCache(Response.class, KEY_CHANGE_PASSWORD_REQUEST);
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(Response.class, KEY_CHANGE_PASSWORD_REQUEST);
            processError(spiceException);
        }
    }

    @Override
    protected int getTitle() {
        return R.string.fragment_user_profile_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_change_password;
    }
}
