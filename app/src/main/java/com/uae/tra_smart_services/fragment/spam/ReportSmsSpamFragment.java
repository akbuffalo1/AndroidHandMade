package com.uae.tra_smart_services.fragment.spam;

import android.os.Bundle;
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
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.SpamServiceProviderAdapter;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.fragment.base.BaseServiceFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.interfaces.Loader.Cancelled;
import com.uae.tra_smart_services.interfaces.LoaderMarker;
import com.uae.tra_smart_services.rest.model.request.SmsReportRequestModel;
import com.uae.tra_smart_services.rest.model.response.SmsSpamResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.SmsReportRequest;
import com.uae.tra_smart_services.util.SmsUtils;

/**
 * Created by mobimaks on 24.09.2015.
 */
public class ReportSmsSpamFragment extends BaseServiceFragment implements OnClickListener, Cancelled {

    private static final String KEY_REPORT_SMS_SPAM_REQUEST = "REPORT_SMS_SPAM_REQUEST";

    private Spinner sProviderSpinner;
    private EditText etNumberOfSpammer, etDescription;
    private Button btnClose, btnSubmit;

    private SpamServiceProviderAdapter mProviderAdapter;
    private SmsReportRequest mSmsReportRequest;

    public static ReportSmsSpamFragment newInstance() {
        return new ReportSmsSpamFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        sProviderSpinner = findView(R.id.sProviderSpinner_FRSS);
        etNumberOfSpammer = findView(R.id.etNumberOfSpammer_FRSS);
        etDescription = findView(R.id.etDescription_FRSS);
        setCapitalizeTextWatcher(etDescription);
//        btnClose = findView(R.id.btnClose_FRSS);
        btnSubmit = findView(R.id.btnSubmit_FRSS);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
//        btnClose.setOnClickListener(this);
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
    public final void onClick(final View _view) {
        hideKeyboard(_view);
        switch (_view.getId()) {
//            case R.id.btnClose_FRSS:
//                getFragmentManager().popBackStack();
//                break;
            case R.id.btnSubmit_FRSS:
                validateAndSendData();
                break;
        }
    }

    private void validateAndSendData() {
        if (validateData()) {
            loaderOverlayShow(getString(R.string.str_sending),  (LoaderMarker) this);
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
            mSmsReportRequest = new SmsReportRequest(
                    new SmsReportRequestModel(
                            etNumberOfSpammer.getText().toString(),
                            etDescription.getText().toString()
                    ));
            getSpiceManager().execute(
                    mSmsReportRequest, KEY_REPORT_SMS_SPAM_REQUEST,
                    DurationInMillis.ALWAYS_EXPIRED, new SmsSpamReportResponseListener());
        }
    }

    private boolean validateData() {
        if (etNumberOfSpammer.getText().toString().isEmpty()) {
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
        if (getSpiceManager().isStarted() && mSmsReportRequest != null) {
            getSpiceManager().cancel(mSmsReportRequest);
        }
    }

    @Nullable
    @Override
    protected Service getServiceType() {
        return Service.REPORT_SPAM;
    }

    @Override
    protected String getServiceName() {
        return "SMS Spam Report";
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
