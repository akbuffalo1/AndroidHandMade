package com.uae.tra_smart_services.fragment.authorization;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.dialog.AlertDialogFragment;
import com.uae.tra_smart_services.entities.CustomFilterPool;
import com.uae.tra_smart_services.entities.Filter;
import com.uae.tra_smart_services.fragment.base.BaseAuthorizationFragment;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.rest.model.request.RestorePasswordRequestModel;
import com.uae.tra_smart_services.rest.robo_requests.RestorePasswordRequest;
import com.uae.tra_smart_services.util.LayoutDirectionUtils;

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
                            etEmail.setError(getString(R.string.error_valid_email));
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
            loaderDialogShow(getString(R.string.str_restoring), this);
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
        public void onRequestNotFound() { /* Not implemented */}

        @Override
        public void onRequestSuccess(Response result) {
            onRestorePassMessageId = (int) Math.random();
            if (isAdded()) {
                loaderDialogDismiss();
                showMessage(onRestorePassMessageId, R.string.str_success, R.string.str_restore_pass_mail);
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            onRestorePassMessageId = (int) Math.random();
            if (isAdded()) {
                loaderDialogDismiss();
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
