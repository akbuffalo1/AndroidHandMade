package biz.enon.tra.uae.fragment.authorization;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

import biz.enon.tra.uae.BuildConfig;
import biz.enon.tra.uae.R;
import biz.enon.tra.uae.entities.LoginQuestionModel;
import biz.enon.tra.uae.fragment.base.BaseAuthorizationFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.rest.model.request.LoginModel;
import biz.enon.tra.uae.rest.model.response.LoginQuestionsErrorModel;
import biz.enon.tra.uae.rest.model.response.SecurityQuestionResponse;
import biz.enon.tra.uae.rest.robo_requests.LoginRequest;
import biz.enon.tra.uae.rest.robo_requests.SecurityQuestionsRequest;
import biz.enon.tra.uae.util.LayoutDirectionUtils;
import biz.enon.tra.uae.util.Logger;
import biz.enon.tra.uae.util.PreferenceManager;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static biz.enon.tra.uae.global.C.MAX_PASSWORD_LENGTH;
import static biz.enon.tra.uae.global.C.MAX_USERNAME_LENGTH;
import static biz.enon.tra.uae.global.C.MIN_PASSWORD_LENGTH;
import static biz.enon.tra.uae.global.C.MIN_USERNAME_LENGTH;

/**
 * Created by ak-buffalo on 22.07.15.
 */
public class LoginFragment extends BaseAuthorizationFragment
        implements OnClickListener, Loader.Cancelled {

    private static final String KEY_SECRET_QUESTIONS_REQUEST = "SECRET_QUESTIONS_REQUEST";
    private static final String KEY_LOGIN_REQUEST = "LOGIN_REQUEST";

    private EditText etUserName, etPassword;
    private Button btnLogIn;
    private TextView tvRegisterNow, tvForgotPassword;

    private RequestResponseListener mRequestLoginListener;
    private QuestionsResponseListener mQuestionsListener;

    private SecurityQuestionsRequest mQuestionsRequest;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    protected int getTitle() {
        return R.string.str_login;
    }

    @Override
    protected final void initViews() {
        // Input fields
        etUserName = findView(R.id.etEmail_FRP);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etUserName, R.drawable.ic_username);
        etPassword = findView(R.id.etPassword_FR);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etPassword, R.drawable.ic_pass);

        // Actions
        btnLogIn = findView(R.id.btnLogin_FRP);
        tvRegisterNow = findView(R.id.tvRegisterNow_FLI);
        tvForgotPassword = findView(R.id.tvForgotPass_FLI);

        if (BuildConfig.DEBUG) {
            btnLogIn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    etUserName.setText("tuap");
                    etPassword.setText("qwerty123");
                    doLogIn();
                    return true;
                }
            });
        }
    }

    @Override
    protected final void initListeners() {
        mRequestLoginListener = new RequestResponseListener();
        mQuestionsListener = new QuestionsResponseListener();
//        tvRestorePassword.setOnClickListener(this);
        btnLogIn.setOnClickListener(this);
        tvRegisterNow.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
    }

    @Override
    protected final int getLayoutResource() {
        return R.layout.fragment_login;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().addListenerIfPending(Response.class, KEY_LOGIN_REQUEST, mRequestLoginListener);
        getSpiceManager().getFromCache(SecurityQuestionResponse.List.class, KEY_SECRET_QUESTIONS_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, mQuestionsListener);
    }

    @Override
    public final void onClick(final View _v) {
        hideKeyboard(getView());
        switch (_v.getId()) {
            case R.id.btnLogin_FRP:
                if (validateData()) {
                    doLogIn();
                }
                break;
            case R.id.tvRegisterNow_FLI:
                loadQuestionsAndOpenRegister();
                break;
            case R.id.tvForgotPass_FLI:
                actionsListener.onOpenRestorePassScreen();
                break;
        }
    }

    private void loadQuestionsAndOpenRegister() {
        loaderOverlayShow(getString(R.string.str_loading), mQuestionsListener, false);
        mQuestionsRequest = new SecurityQuestionsRequest();
        getSpiceManager().execute(mQuestionsRequest, KEY_SECRET_QUESTIONS_REQUEST,
                DurationInMillis.ALWAYS_EXPIRED, mQuestionsListener);
    }

    private boolean validateData() {
        final String userName = etUserName.getText().toString().trim();
        if (userName.length() < MIN_USERNAME_LENGTH || userName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_login_or_pass, C.TOAST_LENGTH).show();
            return false;
        } else if (!Character.isLetter(userName.charAt(0))) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_login_or_pass, C.TOAST_LENGTH).show();
            return false;
        }

        final String password = etPassword.getText().toString().trim();
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_login_or_pass, C.TOAST_LENGTH).show();
            return false;
        }
        return true;
    }

    private LoginRequest mRequest;

    private void doLogIn() {
        LoginModel model = new LoginModel();
        model.login = etUserName.getText().toString();
        model.pass = etPassword.getText().toString();

        loaderOverlayShow(getString(R.string.str_authenticating), this, false);

        getSpiceManager().execute(mRequest = new LoginRequest(model), KEY_LOGIN_REQUEST, DurationInMillis.ALWAYS_EXPIRED, mRequestLoginListener);
    }

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted() && mRequest != null) {
            getSpiceManager().cancel(mRequest);
        }
    }

    private class RequestResponseListener implements PendingRequestListener<Response> {

        @Override
        public void onRequestNotFound() { /* Not implemented */ }

        @Override
        public void onRequestSuccess(Response result) {
            Log.d(getClass().getSimpleName(), "Success. isAdded: " + isAdded());
            PreferenceManager.setLoggedIn(getActivity(), true);

            if (isAdded()) {
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        getFragmentManager().popBackStack();
                        getFragmentManager().popBackStack();
                    }
                });
                if (result != null && actionsListener != null) {
                    actionsListener.onLogInSuccess();
                }
            }
            getSpiceManager().removeDataFromCache(Response.class, KEY_LOGIN_REQUEST);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(Response.class, KEY_LOGIN_REQUEST);
            if (isAdded()) {
                LoginQuestionsErrorModel loginQuestionsErrorModel = parseLoginErrorIfCan(spiceException);
                if (loginQuestionsErrorModel == null || !C.NO_QUESTION_ERROR.equalsIgnoreCase(loginQuestionsErrorModel.error)) {
                    processError(spiceException);
                } else {
                    processLoginError(loginQuestionsErrorModel);
                }
            }
        }

        @Nullable
        private LoginQuestionsErrorModel parseLoginErrorIfCan(SpiceException spiceException) {
            if (spiceException.getCause() instanceof RetrofitError) {
                RetrofitError error = (RetrofitError) spiceException.getCause();
                if (error.getKind() == RetrofitError.Kind.HTTP) {
                    try {
                        return (LoginQuestionsErrorModel) error.getBodyAs(LoginQuestionsErrorModel.class);
                    } catch (RuntimeException e) {
                        Logger.d(getClass().getSimpleName(), "Parse error", error);
                    }
                }
            }
            return null;
        }

        private void processLoginError(LoginQuestionsErrorModel _loginError) {
            loaderOverlayDismissWithAction(new Loader.Dismiss() {
                @Override
                public void onLoadingDismissed() {
                    getFragmentManager().popBackStack();
                }
            });
            if (actionsListener != null) {
                LoginQuestionModel loginModel = new LoginQuestionModel();
                loginModel.login = etUserName.getText().toString();
                loginModel.pass = etPassword.getText().toString();
                loginModel.secretQuestions = _loginError.secretQuestions;
                actionsListener.onOpenSecurityLoginScreen(loginModel);
            }
        }
    }

    private class QuestionsResponseListener implements RequestListener<SecurityQuestionResponse.List>, Loader.Cancelled {

        @Override
        public void onRequestSuccess(SecurityQuestionResponse.List result) {
            getSpiceManager().removeDataFromCache(SecurityQuestionResponse.List.class, KEY_SECRET_QUESTIONS_REQUEST);
            if (isAdded()) {
                loaderOverlayDismissWithAction(LoginFragment.this);
                if (result != null && actionsListener != null) {
                    actionsListener.onOpenRegisterScreen(result);
                }
            }
        }

        @Override
        public void onLoadingCanceled() {
            if (getSpiceManager().isStarted()) {
                getSpiceManager().removeDataFromCache(SecurityQuestionResponse.List.class, KEY_SECRET_QUESTIONS_REQUEST);
                getSpiceManager().cancel(mQuestionsRequest);
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(SecurityQuestionResponse.List.class, KEY_SECRET_QUESTIONS_REQUEST);
            if (isAdded()) {
                loaderOverlayFailed(getString(R.string.str_request_failed), false);
            }

        }

    }
}
