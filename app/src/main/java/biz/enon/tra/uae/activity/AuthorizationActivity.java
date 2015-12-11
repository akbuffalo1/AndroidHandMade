package biz.enon.tra.uae.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.ArrayList;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.TRAApplication;
import biz.enon.tra.uae.activity.base.BaseFragmentActivity;
import biz.enon.tra.uae.entities.LoginQuestionModel;
import biz.enon.tra.uae.fragment.authorization.LoginFragment;
import biz.enon.tra.uae.fragment.authorization.RegisterFragment;
import biz.enon.tra.uae.fragment.authorization.RestorePasswordFragment;
import biz.enon.tra.uae.fragment.authorization.SecurityQuestionLoginFragment;
import biz.enon.tra.uae.fragment.base.BaseAuthorizationFragment.AuthorizationActionsListener;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.interfaces.ToolbarTitleManager;
import biz.enon.tra.uae.rest.model.response.SecurityQuestionResponse;
import biz.enon.tra.uae.util.ImageUtils;

/**
 * Created by ak-buffalo on 22.07.15.
 */
public class AuthorizationActivity extends BaseFragmentActivity
        implements AuthorizationActionsListener, ToolbarTitleManager {

    private String mAction;
    private ImageView ivLogo, ivBackground;

    public static Intent getStartForResultIntent(final Context _context, final Enum _fragmentIdentifier){
        final Intent intent = new Intent(_context, AuthorizationActivity.class);
        intent.setAction(C.ACTION_LOGIN);
        intent.putExtra(C.FRAGMENT_FOR_REPLACING, _fragmentIdentifier);
        return intent;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_authorization);

        ivLogo = findView(R.id.ivLogo_FSL);
        ivLogo.setImageDrawable(ImageUtils.getFilteredDrawable(this, R.drawable.authorization_logo));

        ivBackground = findView(R.id.ivBackground_AA);
        ivBackground.setImageDrawable(ImageUtils.getFilteredDrawable(this, R.drawable.bg_authorization));

        mAction = getIntent().getAction();

        final Toolbar toolbar = findView(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getFragmentManager().findFragmentById(getContainerId()) == null) {
            addFragmentWithOutBackStack(LoginFragment.newInstance());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOpenLoginScreen() {
        super.popBackStack();
    }

    @Override
    public void onOpenSecurityLoginScreen(@NonNull LoginQuestionModel _loginModel) {
        replaceFragmentWithBackStack(SecurityQuestionLoginFragment.newInstance(_loginModel));
    }

    @Override
    public void onLogInSuccess() {
        TRAApplication.setIsLoggedIn(true);

        if (mAction != null && mAction.equals(C.ACTION_LOGIN)) {
            final Intent data = new Intent();
            data.putExtra(C.FRAGMENT_FOR_REPLACING, getIntent().getSerializableExtra(C.FRAGMENT_FOR_REPLACING));
            data.putExtra(C.USE_BACK_STACK, getIntent().getBooleanExtra(C.USE_BACK_STACK, false));
            data.putExtra(C.UNCHECK_TAB_IF_NOT_LOGGED_IN, getIntent().getBooleanExtra(C.UNCHECK_TAB_IF_NOT_LOGGED_IN, false));
            setResult(C.LOGIN_SUCCESS, data);
            finish();
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    public void onOpenRegisterScreen(@NonNull ArrayList<SecurityQuestionResponse> _questions) {
        super.replaceFragmentWithBackStack(RegisterFragment.newInstance(_questions));
    }

    @Override
    public void onRegisterSuccess() {
        super.popBackStack();
    }

    @Override
    public void onOpenRestorePassScreen() {
        super.replaceFragmentWithBackStack(RestorePasswordFragment.newInstance());

    }

    @Override
    public void onRestorePassSuccess() {
        super.replaceFragmentWithBackStack(LoginFragment.newInstance());
    }

    @Override
    public void onBackPressed() {
        if (mAction != null && mAction.equals(C.ACTION_LOGIN)) {
            final Intent data = new Intent();
            data.putExtra(C.FRAGMENT_FOR_REPLACING, getIntent().getSerializableExtra(C.FRAGMENT_FOR_REPLACING));
            data.putExtra(C.USE_BACK_STACK, getIntent().getBooleanExtra(C.USE_BACK_STACK, false));
            data.putExtra(C.UNCHECK_TAB_IF_NOT_LOGGED_IN, getIntent().getBooleanExtra(C.UNCHECK_TAB_IF_NOT_LOGGED_IN, false));
            setResult(RESULT_CANCELED, data);
        }
        super.onBackPressed();
    }

    @Override
    public void onHomeScreenOpenWithoutAuth() {
        if (mAction != null && mAction.equals(C.ACTION_LOGIN)) {
            setResult(C.LOGIN_SKIP);
            finish();
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected int getContainerId() {
        return R.id.flContainer_AA;
    }

    @Override
    protected int getGlobalContainerId() {
        return getContainerId();
    }

    @Override
    public void setToolbarVisibility(boolean _isVisible) {
        if (_isVisible)
            getSupportActionBar().show();
        else
            getSupportActionBar().hide();
    }
}