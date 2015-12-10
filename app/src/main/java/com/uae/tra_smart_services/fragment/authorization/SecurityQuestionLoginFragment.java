package com.uae.tra_smart_services.fragment.authorization;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.SecurityQuestionAdapter;
import com.uae.tra_smart_services.entities.LoginQuestionModel;
import com.uae.tra_smart_services.fragment.base.BaseAuthorizationFragment;
import com.uae.tra_smart_services.interfaces.Loader.Cancelled;
import com.uae.tra_smart_services.rest.model.request.LoginQuestionRequestModel;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse;
import com.uae.tra_smart_services.rest.robo_requests.LoginQuestionRequest;
import com.uae.tra_smart_services.util.ImageUtils;

import retrofit.client.Response;

/**
 * Created by mobimaks on 09.12.2015.
 */
public class SecurityQuestionLoginFragment extends BaseAuthorizationFragment implements OnClickListener {

    private static final String KEY_LOGIN_DATA = "LOGIN_DATA";
    private static final String KEY_SECURITY_QUESTIONS = "SECURITY_QUESTIONS";

    private final String KEY_LOGIN_REQUEST = getClass().getSimpleName() + "_LOGIN_REQUEST";

    private LoginQuestionModel mLoginData;
    private LoginResponseListener mResponseListener;
    private LoginQuestionRequest mLoginRequest;

    private Spinner sSecurityQuestion;
    private EditText etSecurityAnswer;
    private Button btnLogin;

    private SecurityQuestionAdapter mQuestionAdapter;

    public static SecurityQuestionLoginFragment newInstance(@NonNull LoginQuestionModel _loginData) {
        final SecurityQuestionLoginFragment fragment = new SecurityQuestionLoginFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_LOGIN_DATA, _loginData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginData = getArguments().getParcelable(KEY_LOGIN_DATA);
    }

    @Override
    protected void initViews() {
        super.initViews();
        sSecurityQuestion = findView(R.id.sSecurityQuestion_FSL);
        etSecurityAnswer = findView(R.id.etSecurityAnswer_FSL);
        btnLogin = findView(R.id.btnLogin_FSL);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnLogin.setOnClickListener(this);
        mResponseListener = new LoginResponseListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mQuestionAdapter = new SecurityQuestionAdapter(getActivity(), mLoginData.secretQuestions);
        sSecurityQuestion.setAdapter(mQuestionAdapter);
        mQuestionAdapter.setItemTextColor(ImageUtils.getThemeColor(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().getFromCache(Response.class, KEY_LOGIN_REQUEST, DurationInMillis.ALWAYS_RETURNED, mResponseListener);
    }

    @Override
    public void onClick(final View _view) {
        if (_view.getId() == R.id.btnLogin_FSL) {
            validateAndSendDataIfCan();
        }
    }

    private void validateAndSendDataIfCan() {
        if (validateData()) {
            doLogin();
        } else {
            Toast.makeText(getActivity(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateData() {
        return !etSecurityAnswer.getText().toString().isEmpty();
    }

    private void doLogin() {
        loaderOverlayShow(getString(R.string.str_loading), mResponseListener, false);
        LoginQuestionRequestModel loginModel = new LoginQuestionRequestModel();
        loginModel.login = mLoginData.login;
        loginModel.pass = mLoginData.pass;
        loginModel.secretQuestionType = ((SecurityQuestionResponse) sSecurityQuestion.getSelectedItem()).id;
        loginModel.secretQuestionAnswer = etSecurityAnswer.getText().toString();
        mLoginRequest = new LoginQuestionRequest(loginModel);
        getSpiceManager().execute(mLoginRequest, KEY_LOGIN_REQUEST, DurationInMillis.ALWAYS_EXPIRED, mResponseListener);
    }

    private final class LoginResponseListener implements RequestListener<Response>, Cancelled {

        @Override
        public void onRequestSuccess(Response result) {
            getSpiceManager().removeDataFromCache(Response.class, KEY_LOGIN_REQUEST);
            if (isAdded()) {
                loaderOverlayDismissWithAction(SecurityQuestionLoginFragment.this);
                if (result != null && actionsListener != null) {
                    actionsListener.onLogInSuccess();
                }
            }
        }

        @Override
        public void onLoadingCanceled() {
            if (getSpiceManager().isStarted()) {
                getSpiceManager().removeDataFromCache(Response.class, KEY_LOGIN_REQUEST);
                getSpiceManager().cancel(mLoginRequest);
            }

        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(Response.class, KEY_LOGIN_REQUEST);
            if (isAdded()) {
                processError(spiceException);
            }
        }
    }

    @Override
    protected int getTitle() {
        return R.string.str_login;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_security_login;
    }
}
