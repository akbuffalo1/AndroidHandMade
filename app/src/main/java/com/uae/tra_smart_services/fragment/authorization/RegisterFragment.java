package com.uae.tra_smart_services.fragment.authorization;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.rest.model.request.RegisterModel;
import com.uae.tra_smart_services.rest.robo_requests.RegisterRequest;
import com.uae.tra_smart_services.util.LayoutDirectionUtils;
import com.uae.tra_smart_services.util.StringUtils;
import com.uae.tra_smart_services.util.TRAPatterns;

import java.util.regex.Pattern;

import retrofit.client.Response;

import static com.uae.tra_smart_services.global.C.MAX_PASSWORD_LENGTH;
import static com.uae.tra_smart_services.global.C.MAX_USERNAME_LENGTH;
import static com.uae.tra_smart_services.global.C.MIN_PASSWORD_LENGTH;
import static com.uae.tra_smart_services.global.C.MIN_USERNAME_LENGTH;

/**
 * Created by ak-buffalo on 22.07.15.
 */
public class RegisterFragment extends BaseAuthorizationFragment implements View.OnClickListener, Loader.Cancelled, TextWatcher {

    private static final String KEY_REGISTER_REQUEST = "REGISTER_REQUEST";
    private static final int MIN_PHONE_LENGTH = 4;
    private static final int MAX_EMIRATES_ID_LENGTH = 4;

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
        etEmiratesId.addTextChangedListener(this);
        etEmiratesId.setOnFocusChangeListener(this);
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
            Toast.makeText(getActivity(), R.string.error_fill_all_fields, C.TOAST_LENGTH).show();
            return false;
        }

        if (firstName.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_firstname_short, C.TOAST_LENGTH).show();
            return false;
        } else if (firstName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_firstname_long, C.TOAST_LENGTH).show();
            return false;
        }

        if (lastName.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_lastname_short, C.TOAST_LENGTH).show();
            return false;
        } else if (lastName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_lastname_long, C.TOAST_LENGTH).show();
            return false;
        }

        if (!StringUtils.isAllLettersOrWhiteSpace(firstName)) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_first_name, C.TOAST_LENGTH).show();
            return false;
        }
        if (!StringUtils.isAllLettersOrWhiteSpace(lastName)) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_last_name, C.TOAST_LENGTH).show();
            return false;
        }
        //endregion
        //region Validate Emirates ID
        if (emiratesID.isEmpty() || !TRAPatterns.EMIRATES_ID.matcher(emiratesID).matches()) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_emirates_id, C.TOAST_LENGTH).show();
            return false;
        }
        //endregion
        //region Validate username
        if (userName.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_username_short, C.TOAST_LENGTH).show();
            return false;
        } else if (userName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_username_long, C.TOAST_LENGTH).show();
            return false;
        } else if (!Character.isLetter(userName.charAt(0))) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_username_start_char, C.TOAST_LENGTH).show();
            return false;
        }
        //endregion
        //region Validate phone number
        if (phoneNumber.length() < MIN_PHONE_LENGTH || !Patterns.PHONE.matcher(phoneNumber).matches()) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_phone_number, C.TOAST_LENGTH).show();
            return false;
        }
        //endregion
        //region Validate account credentials
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_email_format, C.TOAST_LENGTH).show();
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_password_short, C.TOAST_LENGTH).show();
            return false;
        } else if (password.length() > MAX_PASSWORD_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_password_long, C.TOAST_LENGTH).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), R.string.error_password_confirm, C.TOAST_LENGTH).show();
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
            loaderOverlayShow(getString(R.string.str_registering), this, false);
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


    @Override
    public void onFocusChange(View _view, boolean hasFocus) {
        resetToPattern();
    }

    private void resetToPattern(){
        if(etEmiratesId.length() != 4 && current != (STARTS_WITH + "YYYY-NNNNNNN-C")){
            etEmiratesId.setText(current = (STARTS_WITH + "YYYY-NNNNNNN-C"));
        }

    }

    int len = 0;
    private String current = "";
    private String mMask = "784YYYYNNNNNNNC";
    private int sector = 1;
//    private String mMask = "DDMMYYYY";
    @Override
    public void afterTextChanged(Editable s) {
        /*String str = etEmiratesId.getText().toString();
        if((str.length() == 3 || str.length() == 8 || str.length() == 16) && len < str.length()) {
            etEmiratesId.append("-");
        }*/

        if (!s.toString().equals(current)) {
            String clean = s.toString().replaceAll("[^\\d.]", "");
            String cleanC = current.replaceAll("[^\\d.]", "");

            int cl = clean.length();
            int sel = cl;

            if(cl == 14) {
                if(clean.length() > cleanC.length()){
                    sel = cl++;
                    sel += ++ sector;
                } else {
                    sel = --cl;
                    sel += sector --;
                }
            } else if(cl == 7) {
                if(clean.length() > cleanC.length()){
                    sel = cl++;
                    sel += ++ sector;
                } else {
                    sel = --cl;
                    sel += sector --;
                }
            } /*else if(cl == 3) {
                if(clean.length() > cleanC.length()){
                    sel = cl++;
                    sel += ++ sector;
                } else {
                    sel = --cl;
                    sel += sector --;
                }
            }*/ else {
                sel += sector;
            }

            if (clean.equals(cleanC)) sel--;

            if (clean.length() < 15){
                clean = clean + mMask.substring(clean.length());
            }else{
                int num1  = Integer.parseInt(clean.substring(0,3));
                int num2  = Integer.parseInt(clean.substring(3,7));
                int num3 = Integer.parseInt(clean.substring(7,14));
                int num4 = Integer.parseInt(clean.substring(14,15));

                clean = String.format("%02d%02d%02d%02d", num1, num2, num3, num4);
            }

            clean = String.format("%s-%s-%s-%s",
                    clean.substring(0, 3),
                    clean.substring(3, 7),
                    clean.substring(7, 14),
                    clean.substring(14, 15)
            );

            sel = sel < 0 ? 0 : sel;

            if(sel <= 0) {sector = 0;}
            current = clean;
            etEmiratesId.setText(current);
            etEmiratesId.setSelection(sel < current.length() ? sel : current.length());

        }
        /*if (!s.toString().equals(current)) {
            String clean = s.toString().replaceAll("[^\\d.]", "");
            String cleanC = current.replaceAll("[^\\d.]", "");

            int cl = clean.length();
            int sel = cl;
            for (int i = 2; i <= cl && i < 6; i += 2) {
                sel++;
            }
            //Fix for pressing delete next to a forward slash
            if (clean.equals(cleanC)) sel--;

            if (clean.length() < 8) {
                clean = clean + mMask.substring(clean.length());
            } else {
                //This part makes sure that when we finish entering numbers
                //the date is correct, fixing it otherwise
                int day = Integer.parseInt(clean.substring(0, 2));
                int mon = Integer.parseInt(clean.substring(2, 4));
                int year = Integer.parseInt(clean.substring(4, 8));

                clean = String.format("%02d%02d%02d", day, mon, year);
            }

            clean = String.format("%s/%s/%s", clean.substring(0, 2),
                    clean.substring(2, 4),
                    clean.substring(4, 8));

            sel = sel < 0 ? 0 : sel;
            current = clean;
            etEmiratesId.setText(current);
            etEmiratesId.setSelection(sel < current.length() ? sel : current.length());
        }*/
        int i = 0;
    }

    private static final String STARTS_WITH = "784-";

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        /*len = etEmiratesId.getText().toString().length();*/
//        if(etEmiratesId.length() != 0 && arg0.toString().equals("XXX-YYYY-NNNNNNN-C")){
//            etEmiratesId.setText("");
//        }
//        resetToPattern();
        etEmiratesId.setText("");
        etEmiratesId.setSelection(4);
    }

    Pattern pattern = Pattern.compile("\\d{3}-\\d{4}-\\d{7}-\\d{1}");
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        /*Matcher matcher = pattern.matcher(charSequence);
        if (matcher.region(0, count).matches()) {
            etEmiratesId.setText(charSequence);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                etEmiratesId.cancelPendingInputEvents();
            }
        }*/
        int i = 0;
    }

    private class RequestListener implements PendingRequestListener<Response> {

        @Override
        public void onRequestNotFound() { /* Not implemented */ }

        @Override
        public void onRequestSuccess(Response result) {
            Log.d(getClass().getSimpleName(), "Success. isAdded: " + isAdded());
            if (isAdded()) {
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        getFragmentManager().popBackStack();
                        getFragmentManager().popBackStack();
                    }
                });
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
