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
import biz.enon.tra.uae.rest.model.response.TransactionModel;
import biz.enon.tra.uae.rest.robo_requests.BaseRequest;
import biz.enon.tra.uae.rest.robo_requests.PutTransactionsRequest;
import biz.enon.tra.uae.rest.robo_requests.SmsReportRequest;
import biz.enon.tra.uae.util.SmsUtils;
import retrofit.client.Response;

/**
 * Created by mobimaks on 24.09.2015.
 */
public class SmsSpamReportFragment extends BaseServiceFragment implements OnClickListener, Cancelled, RequestListener<Response> {

    private static final String KEY_REPORT_SMS_SPAM_REQUEST = "REPORT_SMS_SPAM_REQUEST";
    private static final String KEY_PUT_TRANSACTION_REQUEST = "PUT_TRANSACTION_REQUEST";

    private Spinner sProviderSpinner;
    private EditText etNumberOfSpammer, etDescription;
    private Button btnSubmit;

    private SpamServiceProviderAdapter mProviderAdapter;
    private BaseRequest mRequest;

    public static SmsSpamReportFragment newInstance() {
        return new SmsSpamReportFragment();
    }

    public static SmsSpamReportFragment newInstance(Parcelable _inputData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_TARNS_MODEL, _inputData);
        SmsSpamReportFragment fragment = new SmsSpamReportFragment();
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
        mProviderAdapter = new SpamServiceProviderAdapter(getActivity());
        sProviderSpinner.setAdapter(mProviderAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void prepareFieldsIfModelExist(TransactionModel _transactionModel){
        int i = 0;
        for (ServiceProvider provider : mProviderAdapter.getProviderList()) {
            if (provider.toString().equals(_transactionModel.serviceProvider)) {
                sProviderSpinner.setSelection(i);
                break;
            }
            i++;
        }
        etNumberOfSpammer.setText(_transactionModel.phone);
        etDescription.setText(_transactionModel.description);
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
            loaderOverlayShow(getString(R.string.str_sending), this);
            loaderOverlayButtonBehavior(new Loader.BackButton() {
                @Override
                public void onBackButtonPressed(LoaderView.State _currentState) {
                    getFragmentManager().popBackStack();
                    if (_currentState == LoaderView.State.FAILURE || _currentState == LoaderView.State.SUCCESS) {
                        getFragmentManager().popBackStack();
                        reloadTransactionListIfNeed(_currentState);
                    }
                }
            });

            TransactionModel model = getTransactionModel();
            if(isIsInEditMode() && model != null) {
                model.phone = etNumberOfSpammer.getText().toString();
                model.description = etDescription.getText().toString();
                mRequest = new PutTransactionsRequest(model, getActivity(), null);
                getSpiceManager().execute(
                        mRequest, KEY_PUT_TRANSACTION_REQUEST,
                        DurationInMillis.ALWAYS_EXPIRED, this);
            } else {
                mRequest = new SmsReportRequest(
                        new SmsReportRequestModel(
                                etNumberOfSpammer.getText().toString(),
                                etDescription.getText().toString()
                        ));
                getSpiceManager().execute(
                        mRequest, KEY_REPORT_SMS_SPAM_REQUEST,
                        DurationInMillis.ALWAYS_EXPIRED, new SmsSpamReportResponseListener());
            }
        }
    }

    private boolean validateData() {
        if (etNumberOfSpammer.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.str_invalid_number, C.TOAST_LENGTH).show();
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().getFromCache(SmsSpamResponseModel.class, KEY_REPORT_SMS_SPAM_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, new SmsSpamReportResponseListener());
        getSpiceManager().getFromCache(Response.class, KEY_REPORT_SMS_SPAM_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, this);
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

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        processError(spiceException);
    }

    @Override
    public void onRequestSuccess(Response _response) {
        if (isAdded()) {
            getSpiceManager().removeDataFromCache(SmsSpamResponseModel.class, KEY_PUT_TRANSACTION_REQUEST);
            loaderOverlaySuccess(getString(R.string.str_reuqest_has_been_sent));
        }
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
