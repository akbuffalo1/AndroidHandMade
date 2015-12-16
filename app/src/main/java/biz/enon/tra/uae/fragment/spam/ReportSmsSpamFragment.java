package biz.enon.tra.uae.fragment.spam;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.SpamServiceProviderAdapter;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.fragment.base.BaseServiceFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.global.ServiceProvider;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.interfaces.Loader.Cancelled;
import biz.enon.tra.uae.rest.model.request.SmsReportRequestModel;
import biz.enon.tra.uae.rest.model.response.SmsSpamResponseModel;
import biz.enon.tra.uae.rest.robo_requests.BaseRequest;
import biz.enon.tra.uae.rest.robo_requests.PutTransactionsRequest;
import biz.enon.tra.uae.rest.robo_requests.SmsReportRequest;
import biz.enon.tra.uae.util.SmsUtils;

/**
 * Created by mobimaks on 24.09.2015.
 */
public class ReportSmsSpamFragment extends BaseServiceFragment implements OnClickListener, Cancelled {

    private static final String KEY_REPORT_SMS_SPAM_REQUEST = "REPORT_SMS_SPAM_REQUEST";

    private Spinner sProviderSpinner;
    private EditText etNumberOfSpammer, etDescription;
    private Button btnSubmit;

    private SpamServiceProviderAdapter mProviderAdapter;
    private BaseRequest mRequest;

    public static ReportSmsSpamFragment newInstance() {
        return new ReportSmsSpamFragment();
    }

    public static ReportSmsSpamFragment newInstance(Parcelable _inputData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DATA, _inputData);
        ReportSmsSpamFragment fragment = new ReportSmsSpamFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        sProviderSpinner = findView(R.id.sProviderSpinner_FRSS);
        etNumberOfSpammer = findView(R.id.etNumberOfSpammer_FRSS);
        etDescription = findView(R.id.etDescription_FRSS);
        setCapitalizeTextWatcher(etDescription);
        btnSubmit = findView(R.id.btnSubmit_FRSS);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnSubmit.setOnClickListener(this);
        etNumberOfSpammer.setOnFocusChangeListener(this);
        etDescription.setOnFocusChangeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProviderAdapter = new SpamServiceProviderAdapter(getActivity());
        sProviderSpinner.setAdapter(mProviderAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareFieldsIfNeed();
    }

    private void prepareFieldsIfNeed(){
        if(getArguments() != null && (mTransactionModel = getArguments().getParcelable(KEY_DATA)) != null) {
            mIsInEditMode = true;
            int i = 0;
            for (ServiceProvider provider : mProviderAdapter.getProviderList()) {
                if (provider.toString().equals(mTransactionModel.serviceProvider)) {
                    sProviderSpinner.setSelection(i);
                    return;
                }
                i++;
            }
            etNumberOfSpammer.setText(mTransactionModel.phone);
            etDescription.setText(mTransactionModel.description);
        }
    }

    @Override
    public final void onClick(final View _view) {
        hideKeyboard(_view);
        if (_view.getId() == R.id.btnSubmit_FRSS) {
            validateAndSendData();
        }
    }

    private void validateAndSendData() {
        if (validateData()) {
            if(mIsInEditMode && mTransactionModel != null) {
                mTransactionModel.phone = etNumberOfSpammer.getText().toString();
                mTransactionModel.description = etDescription.getText().toString();
                mRequest = new PutTransactionsRequest(mTransactionModel, getActivity(), null);
            } else {
                mRequest = new SmsReportRequest(
                        new SmsReportRequestModel(
                                etNumberOfSpammer.getText().toString(),
                                etDescription.getText().toString()
                        ));
            }
            loaderOverlayShow(getString(R.string.str_sending), this);
            loaderOverlayButtonBehavior(new Loader.BackButton() {
                @Override
                public void onBackButtonPressed(LoaderView.State _currentState) {
                    getFragmentManager().popBackStack();
                    if (_currentState == LoaderView.State.FAILURE || _currentState == LoaderView.State.SUCCESS) {
                        getFragmentManager().popBackStack();
                        getFragmentManager().popBackStack();
                    }
                }
            });
            getSpiceManager().execute(
                    mRequest, KEY_REPORT_SMS_SPAM_REQUEST,
                    DurationInMillis.ALWAYS_EXPIRED, new SmsSpamReportResponseListener());
        }
    }

    private boolean validateData() {
        if (etNumberOfSpammer.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.str_invalid_number, C.TOAST_LENGTH).show();
            return false;
        }
//        if (etDescription.getText().toString().isEmpty()) {
//            Toast.makeText(getActivity(), R.string.fragment_complain_no_description, C.TOAST_LENGTH).show();
//            return false;
//        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().getFromCache(SmsSpamResponseModel.class, KEY_REPORT_SMS_SPAM_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, new SmsSpamReportResponseListener());
    }

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted() && mRequest != null) {
            getSpiceManager().cancel(mRequest);
        }
    }

    @Nullable
    @Override
    protected Service getServiceType() {
        return Service.REPORT_SPAM;
    }

    @Override
    protected String getServiceName() {
        return C.RATE_NAME_SPAM_REPORT;//"SMS Spam Report";
    }

    private final class SmsSpamReportResponseListener implements RequestListener<SmsSpamResponseModel> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            processError(spiceException);
            getSpiceManager().removeDataFromCache(SmsSpamResponseModel.class, KEY_REPORT_SMS_SPAM_REQUEST);
        }

        @Override
        public void onRequestSuccess(SmsSpamResponseModel smsSpamReportResponse) {
            if (isAdded()) {
                if (smsSpamReportResponse != null) {
                    SmsUtils.sendBlockSms(getActivity(), etNumberOfSpammer.getText().toString());
                    getSpiceManager().removeDataFromCache(SmsSpamResponseModel.class, KEY_REPORT_SMS_SPAM_REQUEST);
                    loaderOverlaySuccess(getString(R.string.str_reuqest_has_been_sent));
                }
            }
        }
    }

    @Override
    protected int getTitle() {
        return R.string.fragment_report_spam_sms_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_report_sms_spam;
    }
}
