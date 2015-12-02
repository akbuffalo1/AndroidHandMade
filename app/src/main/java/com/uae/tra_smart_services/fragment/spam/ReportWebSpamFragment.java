package com.uae.tra_smart_services.fragment.spam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.entities.CustomFilterPool;
import com.uae.tra_smart_services.entities.Filter;
import com.uae.tra_smart_services.fragment.base.BaseServiceFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.interfaces.Loader.Cancelled;
import com.uae.tra_smart_services.rest.model.request.HelpSalimModel;
import com.uae.tra_smart_services.rest.model.response.SmsSpamResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.HelpSalimRequest;

import retrofit.client.Response;

/**
 * Created by mobimaks on 24.09.2015.
 */
public class ReportWebSpamFragment extends BaseServiceFragment implements OnClickListener, Cancelled {

    private static final String KEY_REPORT_WEB_SPAM = "REPORT_WEB_SPAM";

    private EditText etUrl, etDescription;
    private Button btnClose, btnSubmit;

    private HelpSalimRequest mHelpSalimRequest;
    private CustomFilterPool<String> mFilters;

    public static ReportWebSpamFragment newInstance() {
        return new ReportWebSpamFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected final void initData() {
        super.initData();
        mFilters = new CustomFilterPool<String>() {
            {
                addFilter(new Filter<String>() {
                    @Override
                    public boolean check(String _data) {
                        return Patterns.WEB_URL.matcher(_data).matches();
                    }
                });
            }
        };
    }

    @Override
    protected void initViews() {
        super.initViews();
        etUrl = findView(R.id.etUrlOfSpammer_FRWS);
        etDescription = findView(R.id.etDescription_FRWS);
        setCapitalizeTextWatcher(etDescription);
//        btnClose = findView(R.id.btnClose_FRWS);
        btnSubmit = findView(R.id.btnSubmit_FRWS);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
//        btnClose.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etDescription.setOnFocusChangeListener(this);
    }

    @Override
    public final void onClick(final View _view) {
        hideKeyboard(_view);
        switch (_view.getId()) {
//            case R.id.btnClose_FRWS:
//                getFragmentManager().popBackStack();
//                break;
            case R.id.btnSubmit_FRWS:
                hideKeyboard(_view);
                collectAndSendToServer();
                break;
        }
    }

    private void collectAndSendToServer() {
        if (validateData()) {
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
            HelpSalimModel helpSalimModel = new HelpSalimModel(
                    etUrl.getText().toString(),
                    etDescription.getText().toString());
            mHelpSalimRequest = new HelpSalimRequest(helpSalimModel);
            getSpiceManager().execute(mHelpSalimRequest, KEY_REPORT_WEB_SPAM,
                    DurationInMillis.ALWAYS_EXPIRED, new HelpSalimRequestListener());
        }
    }

    private boolean validateData() {
        if (!mFilters.check(etUrl.getText().toString())) {
            Toast.makeText(getActivity(), R.string.str_invalid_url, C.TOAST_LENGTH).show();
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
        getSpiceManager().getFromCache(Response.class, KEY_REPORT_WEB_SPAM,
                DurationInMillis.ALWAYS_RETURNED, new HelpSalimRequestListener());
    }

    @Override
    public void onLoadingCanceled() {
        getSpiceManager().removeDataFromCache(SmsSpamResponseModel.class, KEY_REPORT_WEB_SPAM);
        if (getSpiceManager().isStarted() && mHelpSalimRequest != null) {
            getSpiceManager().cancel(mHelpSalimRequest);
        }
    }

    @Nullable
    @Override
    protected Service getServiceType() {
        return Service.BLOCK_WEBSITE;
    }

    @Override
    protected String getServiceName() {
        return "Web report";
    }

    private final class HelpSalimRequestListener implements RequestListener<Response> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(SmsSpamResponseModel.class, KEY_REPORT_WEB_SPAM);
            processError(spiceException);
        }

        @Override
        public void onRequestSuccess(Response smsSpamReportResponse) {
            getSpiceManager().removeDataFromCache(SmsSpamResponseModel.class, KEY_REPORT_WEB_SPAM);
            if (isAdded()) {
                if (smsSpamReportResponse != null) {
                    loaderOverlaySuccess(getString(R.string.str_reuqest_has_been_sent));
                }
            }
        }
    }

    @Override
    protected int getTitle() {
        return R.string.hexagon_button_block_website;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_report_web_spam;
    }
}
