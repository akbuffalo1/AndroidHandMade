package biz.enon.tra.uae.fragment.authorization;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.util.ArrayList;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.SecurityQuestionAdapter;
import biz.enon.tra.uae.adapter.StateRegisterAdapter;
import biz.enon.tra.uae.entities.CustomFilterPool;
import biz.enon.tra.uae.entities.Filter;
import biz.enon.tra.uae.fragment.base.BaseAuthorizationFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.rest.model.request.RegisterModel;
import biz.enon.tra.uae.rest.model.response.SecurityQuestionResponse;
import biz.enon.tra.uae.rest.robo_requests.RegisterRequest;
import biz.enon.tra.uae.util.ImageUtils;
import biz.enon.tra.uae.util.LayoutDirectionUtils;
import biz.enon.tra.uae.util.StringUtils;
import biz.enon.tra.uae.util.TRAPatterns;
import retrofit.client.Response;

/**
 * Created by ak-buffalo on 22.07.15.
 */
public class RegisterFragment extends BaseAuthorizationFragment implements OnClickListener, Loader.Cancelled, OnCheckedChangeListener, TextWatcher {

    private static final String KEY_SECURITY_QUESTIONS = "SECURITY_QUESTIONS";
    private static final String KEY_REGISTER_REQUEST = "REGISTER_REQUEST";
    private static final int MIN_PHONE_LENGTH = 4;
    private static final int MAX_EMIRATES_ID_LENGTH = 4;

    private EditText etUserName, etPhone, etPassword, etConfirmPassword, etFirstName,
            etLastName, etEmiratesId, etEmail;
    private Button tvRegister;
    private TextView btnLogInNow;
    private Spinner acsState, acsCountry;

    private RelativeLayout rlEnhancedSecurity;
    private CheckBox cbEnhancedSecurity;
    private Spinner sSecurityQuestion;
    private EditText etSecurityAnswer;

    private RegisterRequest mRegisterRequest;

    private ArrayList<SecurityQuestionResponse> mQuestions;
    private SecurityQuestionAdapter mQuestionAdapter;

    private StateRegisterAdapter mStatesAdapter, mCountriesAdapter;
    private CustomFilterPool<RegisterModel> mFilterPool;

    private RequestListener mRequestListener;

    public static RegisterFragment newInstance(@NonNull ArrayList<SecurityQuestionResponse> _questions) {
        final RegisterFragment fragment = new RegisterFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_SECURITY_QUESTIONS, _questions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuestions = getArguments().getParcelableArrayList(KEY_SECURITY_QUESTIONS);
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

        rlEnhancedSecurity = findView(R.id.rlEnhancedSecurity_FR);
        sSecurityQuestion = findView(R.id.sSecurityQuestion_FR);
        cbEnhancedSecurity = findView(R.id.cbEnhancedSecurity_FR);
        etSecurityAnswer = findView(R.id.etSecurityAnswer_FR);
    }

    @Override
    protected final void initListeners() {
        mRequestListener = new RequestListener();
        tvRegister.setOnClickListener(this);
        cbEnhancedSecurity.setOnCheckedChangeListener(this);
        rlEnhancedSecurity.setOnClickListener(this);
        etEmiratesId.addTextChangedListener(this);
        etEmiratesId.setOnFocusChangeListener(this);
        etPassword.setOnFocusChangeListener(this);
        etConfirmPassword.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if(v.getId() == R.id.etEmiratesID_FR && hasFocus){
            etEmiratesId.setText(current = MASK);
        }
        if(v.getId() == R.id.etPassword_FR && ((TextView) v).getText().length() > 0 && !hasFocus){
            if(!TRAPatterns.PASSWORD_PATTERN.matcher(((TextView) v).getText().toString()).matches()){
                Toast.makeText(getActivity(), getString(R.string.error_password_rules), Toast.LENGTH_LONG).show();
                v.setTag(C.PASS_BAD);
                return;
            }
            v.setTag(C.PASS_GOOD);
        }
        if(v.getId() == R.id.etConfirmPassword_FR && ((TextView) v).getText().length() > 0 && !hasFocus && etPassword.getTag().equals(C.PASS_GOOD)){
            if (!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())) {
                Toast.makeText(getActivity(), getString(R.string.error_password_confirm), Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFilters();
        initQuestionsSpinner();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setSecurityQuestionEnabled(cbEnhancedSecurity.isChecked());
    }

    private void initQuestionsSpinner() {
        mQuestionAdapter = new SecurityQuestionAdapter(getActivity(), mQuestions);
        sSecurityQuestion.setAdapter(mQuestionAdapter);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cbEnhancedSecurity_FR) {
            setSecurityQuestionEnabled(isChecked);
        }
    }

    private void setSecurityQuestionEnabled(boolean _enabled) {
        sSecurityQuestion.setEnabled(_enabled);
        etSecurityAnswer.setEnabled(_enabled);
        if (mQuestionAdapter != null) {
            int textColor = _enabled ? ImageUtils.getThemeColor(getActivity()) : ContextCompat.getColor(getActivity(), R.color.hex_color_middle_gray);
            mQuestionAdapter.setItemTextColor(textColor);
        }
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
            case R.id.rlEnhancedSecurity_FR:
                cbEnhancedSecurity.toggle();
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

        if (firstName.length() < C.MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_firstname_short, C.TOAST_LENGTH).show();
            return false;
        } else if (firstName.length() > C.MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_firstname_long, C.TOAST_LENGTH).show();
            return false;
        }

        if (lastName.length() < C.MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_lastname_short, C.TOAST_LENGTH).show();
            return false;
        } else if (lastName.length() > C.MAX_USERNAME_LENGTH) {
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
        if (userName.length() < C.MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_username_short, C.TOAST_LENGTH).show();
            return false;
        } else if (userName.length() > C.MAX_USERNAME_LENGTH) {
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

        if (!TRAPatterns.PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(getActivity(), getString(R.string.error_password_rules), C.TOAST_LENGTH).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), R.string.error_password_confirm, C.TOAST_LENGTH).show();
            return false;
        }
        //endregion

        //region Security answer validation
        if (cbEnhancedSecurity.isChecked()) {
            if (etSecurityAnswer.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), R.string.error_fill_all_fields, C.TOAST_LENGTH).show();
                return false;
            }
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
        registerModel.enhancedSecurity = cbEnhancedSecurity.isChecked();
        if (registerModel.enhancedSecurity) {
            registerModel.secretQuestionType = ((SecurityQuestionResponse) sSecurityQuestion.getSelectedItem()).id;
            registerModel.secretQuestionAnswer = etSecurityAnswer.getText().toString();
        }

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

    private static final String MASK = "784-YYYY-NNNNNNN-C";
    private static final char[] SPLITTED_MASK = MASK.replaceAll("-","").toCharArray();
    private static final String STARTS_WITH = MASK.substring(0, 3);
    private String current = "";
    private boolean shouldReset;
    private int jumpTo;
    private int[] values = new int[] {7, 8, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*Not implemented*/}

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if(start <= 3 && !current.equals(charSequence.toString())){
            jumpTo = 4;
            return;
        }

        if(!charSequence.toString().startsWith(STARTS_WITH)){
            shouldReset = true;
            return;
        } else {
            shouldReset = false;
        }

        if(count > 0 && before == 0) {
            if(start >= 18 || !String.valueOf(charSequence.charAt(start)).matches("[0-9]+") || charSequence.length() <= 18){
                return;
            }
            if (start > 0 && start < 3) {
                values[start] = Character.getNumericValue(charSequence.charAt(start));
            } else if (start >= 4 && start <= 7) {
                values[start - 1] = Character.getNumericValue(charSequence.charAt(start));
            } else if (start >= 8 && start <= 15) {
                values[start - 2] = Character.getNumericValue(charSequence.charAt(start));
            } else if (start > 16) {
                values[start - 3] = Character.getNumericValue(charSequence.charAt(start));
            }

            jumpTo = start + 1;
            if(start == 3 || start == 7 || start == 15){
                jumpTo++;
            }
        } else {
            if (start > 0 && start < 3) {
                return;
            } else if (start >= 4 && start <= 7) {
                values[start - 1] = -1;
            } else if (start >= 8 && start <= 15) {
                values[start - 2] = -1;
            } else if (start > 16) {
                values[start - 3] = -1;
            }

            if(start == 9 || start == 17){
                jumpTo = start - 1;
            } else {
                jumpTo = start;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable charSequence) {
        if(shouldReset){
            resetToPatternIfNeed();
        }
        if(!current.equals(charSequence.toString())){
            etEmiratesId.removeTextChangedListener(this);
            etEmiratesId.setText(current = buildByValues(values).toUpperCase());
            etEmiratesId.setSelection(jumpTo);
            etEmiratesId.addTextChangedListener(this);
        }
    }

    private void resetToPatternIfNeed(){
        if(current.isEmpty() && etEmiratesId.getText().toString().startsWith(STARTS_WITH)){
            etEmiratesId.setText(current = MASK);
        }
    }

    private String buildByValues(int[] _values){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < _values.length; i++){
            builder.append((_values[i] == -1) ? SPLITTED_MASK[i] : String.valueOf(_values[i]));
            if(i == 2 || i == 6 || i == 13){
                builder.append("-");
            }
        }
        if(_values.length < 15){
            builder.append(MASK.substring(_values.length + 1));
        }
        return builder.toString();
    }

    private class RequestListener implements PendingRequestListener<Response> {

        @Override
        public void onRequestNotFound() { /* Not implemented */ }

        @Override
        public void onRequestSuccess(Response result) {
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
                        if (!TRAPatterns.PASSWORD_PATTERN.matcher(_data.pass).matches()) {
                            showMessage(0, R.string.str_error, R.string.error_password_rules);
                            etPassword.setError(getString(R.string.error_password_rules));
                            etPassword.requestFocus();
                            return false;
                        }
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
