package biz.enon.tra.uae.fragment.authorization;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.dialog.AlertDialogFragment;
import biz.enon.tra.uae.entities.CustomFilterPool;
import biz.enon.tra.uae.entities.Filter;
import biz.enon.tra.uae.fragment.base.BaseAuthorizationFragment;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.rest.model.request.RestorePasswordRequestModel;
import biz.enon.tra.uae.rest.robo_requests.RestorePasswordRequest;
import biz.enon.tra.uae.util.LayoutDirectionUtils;
import retrofit.client.Response;

/**
 * Created by ak-buffalo on 22.07.15.
 */
public class RestorePasswordFragment extends BaseAuthorizationFragment
        implements Button.OnClickListener, AlertDialogFragment.OnOkListener, Loader.Cancelled{

    private EditText etEmail;
    private Button btnDoRestorePass;
    private CustomFilterPool<RestorePasswordRequestModel> mFilterPool;
    private RestorePasswordRequestModel mRestorePasswordRequestModel;
    private RestorePasswordRequest mRestorePasswordRequest;
    private int onRestorePassMessageId;

    public static RestorePasswordFragment newInstance() {
        return new RestorePasswordFragment();
    }

    @Override
    protected void initData() {
        super.initData();
        mFilterPool = new CustomFilterPool<RestorePasswordRequestModel>(){
            {
                addFilter(new Filter<RestorePasswordRequestModel>() {
                    @Override
                    public boolean check(RestorePasswordRequestModel _data) {
                        if (TextUtils.isEmpty(_data.getEmail())) {
                            showMessage(R.string.str_error, R.string.error_fill_all_fields);
                            return false;
                        }
                        return true;
                    }
                });
                addFilter(new Filter<RestorePasswordRequestModel>() {
                    @Override
                    public boolean check(RestorePasswordRequestModel _data) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(_data.getEmail()).matches()) {
                            etEmail.setError(getString(R.string.authorization_invalid_email_format));
                            etEmail.requestFocus();
                            return false;
                        }
                        return true;
                    }
                });
            }
        };
    }

    @Override
    protected void initViews() {
        etEmail = findView(R.id.etEmail_FRP);
        LayoutDirectionUtils.setDrawableStart(getActivity(), etEmail, R.drawable.ic_spam_sms);
        btnDoRestorePass = findView(R.id.btnDoRestorePass_FRP);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnDoRestorePass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDoRestorePass_FRP:
                doRestorePassword();
                break;
        }
    }

    private void doRestorePassword() {
        mRestorePasswordRequestModel = new RestorePasswordRequestModel(etEmail.getText().toString());

        if (mFilterPool.check(mRestorePasswordRequestModel)) {
            loaderOverlayShow(getString(R.string.str_restoring), this, false);
            getSpiceManager()
                    .execute(
                            mRestorePasswordRequest = new RestorePasswordRequest(mRestorePasswordRequestModel),
                            new RestorePasswordRequestListener());
        }
    }

    @Override
    public void onLoadingCanceled() {
        if(getSpiceManager().isStarted() && mRestorePasswordRequest!=null){
            getSpiceManager().cancel(mRestorePasswordRequest);
        }
    }

    @Override
    public void onOkPressed(final int _mMessageId) {
        if (onRestorePassMessageId == _mMessageId){
            actionsListener.onRestorePassSuccess();
        }
    }

    private class RestorePasswordRequestListener implements PendingRequestListener<Response> {

        @Override
        public void onRequestNotFound() { /* Not implemented */ }

        @Override
        public void onRequestSuccess(Response result) {
            onRestorePassMessageId = (int) Math.random();
            if (isAdded()) {
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        getFragmentManager().popBackStack();
                        getFragmentManager().popBackStack();
                    }
                });
                showMessage(onRestorePassMessageId, R.string.str_success, R.string.str_restore_pass_mail);
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            onRestorePassMessageId = (int) Math.random();
            if (isAdded()) {
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        getFragmentManager().popBackStack();
                        getFragmentManager().popBackStack();
                    }
                });
            }
            processError(spiceException);
        }
    }

    @Override
    protected int getTitle() {
        return R.string.forgot_password_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_restore_pass;
    }
}
