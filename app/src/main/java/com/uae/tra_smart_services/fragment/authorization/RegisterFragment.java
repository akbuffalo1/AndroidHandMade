package com.uae.tra_smart_services.fragment.authorization;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.StateRegisterAdapter;
import com.uae.tra_smart_services.entities.CustomFilterPool;
import com.uae.tra_smart_services.entities.Filter;
import com.uae.tra_smart_services.fragment.base.BaseAuthorizationFragment;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.rest.model.request.RegisterModel;
import com.uae.tra_smart_services.rest.robo_requests.RegisterRequest;
import com.uae.tra_smart_services.util.LayoutDirectionUtils;
import com.uae.tra_smart_services.util.StringUtils;
import com.uae.tra_smart_services.util.TRAPatterns;

import retrofit.client.Response;

import static com.uae.tra_smart_services.global.C.MAX_PASSWORD_LENGTH;
import static com.uae.tra_smart_services.global.C.MAX_USERNAME_LENGTH;
import static com.uae.tra_smart_services.global.C.MIN_PASSWORD_LENGTH;
import static com.uae.tra_smart_services.global.C.MIN_USERNAME_LENGTH;

/**
 * Created by ak-buffalo on 22.07.15.
 */
public class RegisterFragment extends BaseAuthorizationFragment implements View.OnClickListener, Loader.Cancelled {

    private static final String KEY_REGISTER_REQUEST = "REGISTER_REQUEST";
    private static final int MIN_PHONE_LENGTH = 4;

    private EditText etUserName, etPhone, etPassword, etConfirmPassword, etFirstName,
            etLastName, etEmiratesId, etEmail;
    private Button tvRegister;
    private TextView btnLogInNow;
    private Spinner acsState, acsCountry;

    private RegisterRequest mRegisterRequest;

    private StateRegisterAdapter mStatesAdapter, mCountriesAdapter;
    private CustomFilterPool<RegisterModel> mFilterPool;

    private RequestListener mRequestListener;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    protected int getTitle() {
        return R.string.register_title;
    }

    @Override
    protected final void initViews() {
        // Input fields
        etUserName = findView(R.id.etUsername_FR);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etUserName, R.drawable.ic_username);
        etPhone = findView(R.id.etPhone_FR);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etPhone, R.drawable.ic_phone);
        etPassword = findView(R.id.etPassword_FR);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etPassword, R.drawable.ic_pass);
        etConfirmPassword = findView(R.id.etConfirmPassword_FR);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etConfirmPassword, R.drawable.ic_pass);
        etFirstName = findView(R.id.etFirstName_FR);
        setCapitalizeTextWatcher(etFirstName);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etFirstName, R.drawable.ic_username);
        etLastName = findView(R.id.etLastName_FR);
        setCapitalizeTextWatcher(etLastName);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etLastName, R.drawable.ic_username);
        etEmiratesId = findView(R.id.etEmiratesID_FR);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etEmiratesId, R.drawable.ic_id);

        etEmail = findView(R.id.etEmail_FRP);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etEmail, R.drawable.ic_mail);

        tvRegister = findView(R.id.tvRegister_FLI);
    }

    @Override
    protected final void initListeners() {
        mRequestListener = new RequestListener();
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFilters();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_register;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().addListenerIfPending(Response.class, KEY_REGISTER_REQUEST, mRequestListener);
    }

    @Override
    public void onClick(View _v) {
        hideKeyboard(getView());
        switch (_v.getId()) {
            case R.id.tvRegister_FLI:
                if (validateData()) {
                    doRegistration();
                }
                break;
        }
    }

    private boolean validateData() {
        //region Validate first and last name
        final String firstName = etFirstName.getText().toString().trim();
        final String lastName = etLastName.getText().toString().trim();
        final String emiratesID = etEmiratesId.getText().toString().trim();
        final String userName = etUserName.getText().toString().trim();
        final String phoneNumber = etPhone.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || emiratesID.isEmpty() ||
                userName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (firstName.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_firstname_short, Toast.LENGTH_SHORT).show();
            return false;
        } else if (firstName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_firstname_long, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_lastname_short, Toast.LENGTH_SHORT).show();
            return false;
        } else if (lastName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_lastname_long, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!StringUtils.isAllLetters(firstName)) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_first_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!StringUtils.isAllLetters(lastName)) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_last_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        //endregion
        //region Validate Emirates ID
        if (emiratesID.isEmpty() || !TRAPatterns.EMIRATES_ID.matcher(emiratesID).matches()) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_emirates_id, Toast.LENGTH_SHORT).show();
            return false;
        }
        //endregion
        //region Validate username
        if (userName.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_username_short, Toast.LENGTH_SHORT).show();
            return false;
        } else if (userName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_username_long, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Character.isLetter(userName.charAt(0))) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_username_start_char, Toast.LENGTH_SHORT).show();
            return false;
        }
        //endregion
        //region Validate phone number
        if (phoneNumber.length() < MIN_PHONE_LENGTH || !Patterns.PHONE.matcher(phoneNumber).matches()) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_phone_number, Toast.LENGTH_SHORT).show();
            return false;
        }
        //endregion
        //region Validate account credentials
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_email_format, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_password_short, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() > MAX_PASSWORD_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_password_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), R.string.error_password_confirm, Toast.LENGTH_SHORT).show();
            return false;
        }
        //endregion
        return true;
    }

    private void doRegistration() {
        final RegisterModel registerModel = new RegisterModel();
        registerModel.login = etUserName.getText().toString();
        registerModel.pass = etPassword.getText().toString();
        registerModel.mobile = etPhone.getText().toString();
        registerModel.first = etFirstName.getText().toString();
        registerModel.last = etLastName.getText().toString();
        registerModel.state = 3; // HARDCODED DUBAI
        registerModel.email = etEmail.getText().toString();
        registerModel.emiratesId = etEmiratesId.getText().toString();

        if (mFilterPool.check(registerModel)) {
            loaderDialogShow(getString(R.string.str_registering), this);
            getSpiceManager().execute(mRegisterRequest = new RegisterRequest(registerModel),
                    KEY_REGISTER_REQUEST, DurationInMillis.ALWAYS_EXPIRED, mRequestListener);
        }
    }

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted() && mRegisterRequest != null) {
            getSpiceManager().cancel(mRegisterRequest);
        }
    }

    private class RequestListener implements PendingRequestListener<Response> {

        @Override
        public void onRequestNotFound() {

        }

        @Override
        public void onRequestSuccess(Response result) {
            Log.d(getClass().getSimpleName(), "Success. isAdded: " + isAdded());
            if (isAdded()) {
                loaderDialogDismiss();
                if (result != null && actionsListener != null) {
                    actionsListener.onRegisterSuccess();
                }
            }
            getSpiceManager().removeDataFromCache(Response.class, KEY_REGISTER_REQUEST);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            processError(spiceException);
            getSpiceManager().removeDataFromCache(Response.class, KEY_REGISTER_REQUEST);
        }
    }

    private void initFilters() {
        mFilterPool = new CustomFilterPool<RegisterModel>() {
            {
                addFilter(new Filter<RegisterModel>() {
                    @Override
                    public boolean check(final RegisterModel _data) {
                        if (TextUtils.isEmpty(_data.email) || TextUtils.isEmpty(_data.login) ||
                                TextUtils.isEmpty(_data.pass) || TextUtils.isEmpty(_data.first) ||
                                TextUtils.isEmpty(_data.last) || TextUtils.isEmpty(_data.emiratesId) ||
                                TextUtils.isEmpty(_data.mobile)) {
                            showMessage(R.string.str_error, R.string.error_fill_all_fields);
                            return false;
                        }
                        return true;
                    }
                });

                addFilter(new Filter<RegisterModel>() {
                    @Override
                    public boolean check(final RegisterModel _data) {
                        if (_data.state == null || _data.state == 0) {
                            showMessage(R.string.str_error, R.string.error_select_state);
                            acsState.requestFocus();
                            return false;
                        }
                        return true;
                    }
                });

                addFilter(new Filter<RegisterModel>() {
                    @Override
                    public boolean check(final RegisterModel _data) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(_data.email).matches()) {
                            showMessage(0, R.string.str_error, R.string.error_valid_email);
                            etEmail.setError(getString(R.string.error_valid_email));
                            etEmail.requestFocus();
                            return false;
                        }
                        return true;
                    }
                });

                addFilter(new Filter<RegisterModel>() {
                    @Override
                    public boolean check(RegisterModel _data) {
                        if (!TRAPatterns.EMIRATES_ID.matcher(_data.emiratesId).matches()) {
                            showMessage(0, R.string.str_error, R.string.error_valid_emid);
                            etEmiratesId.setError(getString(R.string.error_valid_emid));
                            etEmiratesId.requestFocus();
                            return false;
                        }
                        return true;
                    }
                });

                addFilter(new Filter<RegisterModel>() {
                    @Override
                    public boolean check(final RegisterModel _data) {
                        if (!Patterns.PHONE.matcher(_data.mobile).matches()) {
                            showMessage(0, R.string.str_error, R.string.error_valid_phone);
                            etPhone.setError(getString(R.string.error_valid_phone));
                            etPhone.requestFocus();
                            return false;
                        }
                        return true;
                    }
                });

                addFilter(new Filter<RegisterModel>() {
                    @Override
                    public boolean check(RegisterModel _data) {
                        if (!_data.pass.equals(etConfirmPassword.getText().toString())) {
                            showMessage(0, R.string.str_error, R.string.error_password_confirm);
                            etPassword.setError(getString(R.string.error_password_confirm));
                            etConfirmPassword.setError(getString(R.string.error_password_confirm));
                            etConfirmPassword.requestFocus();
                            return false;
                        }
                        return true;
                    }
                });
            }
        };
    }
}
