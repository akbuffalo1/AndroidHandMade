package com.uae.tra_smart_services.fragment.authorization;

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
import com.uae.tra_smart_services.BuildConfig;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.fragment.base.BaseAuthorizationFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.rest.model.request.LoginModel;
import com.uae.tra_smart_services.rest.robo_requests.LoginRequest;
import com.uae.tra_smart_services.util.LayoutDirectionUtils;
import com.uae.tra_smart_services.util.PreferenceManager;

import retrofit.client.Response;

import static com.uae.tra_smart_services.global.C.MAX_PASSWORD_LENGTH;
import static com.uae.tra_smart_services.global.C.MAX_USERNAME_LENGTH;
import static com.uae.tra_smart_services.global.C.MIN_PASSWORD_LENGTH;
import static com.uae.tra_smart_services.global.C.MIN_USERNAME_LENGTH;

/**
 * Created by ak-buffalo on 22.07.15.
 */
public class LoginFragment extends BaseAuthorizationFragment
        implements OnClickListener, Loader.Cancelled {

    private static final String KEY_LOGIN_REQUEST = "LOGIN_REQUEST";

    private EditText etUserName, etPassword;
    private Button btnLogIn;
    private TextView tvRegisterNow, tvForgotPassword;

    private RequestResponseListener mRequestLoginListener;

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
                    etUserName.setText("foouser");
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
                actionsListener.onOpenRegisterScreen();
                break;
            case R.id.tvForgotPass_FLI:
                actionsListener.onOpenRestorePassScreen();
                break;
        }
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
                    if (true){//TODO: check if security question enabled
                        LoginModel loginModel = new LoginModel();
                        loginModel.login = etUserName.getText().toString();
                        loginModel.pass = etPassword.getText().toString();
                        actionsListener.onOpenSecurityLoginScreen(loginModel);
                    }else {
                        actionsListener.onLogInSuccess();
                    }

                }
            }
            getSpiceManager().removeDataFromCache(Response.class, KEY_LOGIN_REQUEST);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            processError(spiceException);
            getSpiceManager().removeDataFromCache(Response.class, KEY_LOGIN_REQUEST);
        }
    }
}
