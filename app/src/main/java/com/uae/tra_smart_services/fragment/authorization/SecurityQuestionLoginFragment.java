package com.uae.tra_smart_services.fragment.authorization;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.SecurityQuestionAdapter;
import com.uae.tra_smart_services.fragment.base.BaseAuthorizationFragment;
import com.uae.tra_smart_services.rest.model.request.LoginModel;
import com.uae.tra_smart_services.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by mobimaks on 09.12.2015.
 */
public class SecurityQuestionLoginFragment extends BaseAuthorizationFragment implements OnClickListener {

    private static final String KEY_LOGIN_DATA = "LOGIN_DATA";
    private static final String KEY_SECURITY_QUESTIONS = "SECURITY_QUESTIONS";

    private final String KEY_LOGIN_REQUEST = getClass().getSimpleName() + "_LOGIN_REQUEST";

    private LoginModel mLoginData;

    private Spinner sSecurityQuestion;
    private EditText etSecurityAnswer;
    private Button btnLogin;

    private SecurityQuestionAdapter mQuestionAdapter;

    public static SecurityQuestionLoginFragment newInstance(@NonNull LoginModel _loginData) {
        final SecurityQuestionLoginFragment fragment = new SecurityQuestionLoginFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_LOGIN_DATA, _loginData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        mLoginData = args.getParcelable(KEY_LOGIN_DATA);
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<String> data = new ArrayList<>();//TODO: remove stub data
        data.add("Test item #" + 1);
        data.add("Test item #" + 2);
        data.add("Test item #" + 3);
        data.add("Test item #" + 4);
        mQuestionAdapter = new SecurityQuestionAdapter(getActivity(), data);
        sSecurityQuestion.setAdapter(mQuestionAdapter);
        mQuestionAdapter.setItemTextColor(ImageUtils.getThemeColor(getActivity()));
    }

    @Override
    public void onClick(final View _view) {
        if (_view.getId() == R.id.btnLogin_FSL) {
            validateAndSendDataIfCan();
        }
    }

    private void validateAndSendDataIfCan() {
        if (validateData()) {
            //TODO: add send data logic
            actionsListener.onLogInSuccess();
        } else {
            Toast.makeText(getActivity(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateData() {
        return !etSecurityAnswer.getText().toString().isEmpty();
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
