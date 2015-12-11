package biz.enon.tra.uae.fragment.spam;

import android.os.Bundle;
import android.os.Parcelable;
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

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.entities.CustomFilterPool;
import biz.enon.tra.uae.entities.Filter;
import biz.enon.tra.uae.fragment.base.BaseServiceFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.interfaces.Loader.Cancelled;
import biz.enon.tra.uae.rest.model.request.HelpSalimModel;
import biz.enon.tra.uae.rest.model.response.SmsSpamResponseModel;
import biz.enon.tra.uae.rest.robo_requests.HelpSalimRequest;
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

    public static ReportWebSpamFragment newInstance(Parcelable _inputData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DATA, _inputData);
        ReportWebSpamFragment fragment = new ReportWebSpamFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null && (mModel = getArguments().getParcelable(KEY_DATA)) != null){
            etUrl.setText(mModel.title);
            etDescription.setText(mModel.description);
        }
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
        return C.RATE_NAME_WEB_REPORT;//"Web report";
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
