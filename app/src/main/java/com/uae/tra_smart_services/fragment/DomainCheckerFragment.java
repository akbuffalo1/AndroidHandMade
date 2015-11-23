package com.uae.tra_smart_services.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.dialog.AlertDialogFragment;
import com.uae.tra_smart_services.entities.CustomFilterPool;
import com.uae.tra_smart_services.entities.Filter;
import com.uae.tra_smart_services.fragment.base.BaseServiceFragment;
import com.uae.tra_smart_services.global.ServerConstants;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.rest.model.response.DomainAvailabilityCheckResponseModel;
import com.uae.tra_smart_services.rest.model.response.DomainInfoCheckResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.DomainAvailabilityCheckRequest;
import com.uae.tra_smart_services.rest.robo_requests.DomainInfoCheckRequest;

import java.util.regex.Pattern;

/**
 * Created by ak-buffalo on 10.08.15.
 */
public class DomainCheckerFragment extends BaseServiceFragment
        implements View.OnClickListener, AlertDialogFragment.OnOkListener/*, Loader.Cancelled*/ {

    private static final Pattern AE_DOMAIN_PATTERN = Pattern.compile("^.+(\\.ae)\\/?$");

    /**
     * Views
     */
    private Button btnAvail, btnWhoIS;
    private EditText etDomainAvail;
    /**
     * Listeners
     */
    private HexagonHomeFragment.OnServiceSelectListener mServiceSelectListener;
    /**
     * Requests
     */
    private DomainInfoCheckRequest mDomainInfoCheckRequest;
    private DomainAvailabilityCheckRequest mDomainAvailabilityCheckRequest;
    /**
     * Entities
     */
    private CustomFilterPool<String> filters;

    public static DomainCheckerFragment newInstance() {
        return new DomainCheckerFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        mServiceSelectListener = (HexagonHomeFragment.OnServiceSelectListener) _activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected final int getLayoutResource() {
        return R.layout.fragment_domain_checker;
    }

    @Override
    protected final int getTitle() {
        return R.string.str_domain_check;
    }

    @Override
    protected final void initViews() {
        super.initViews();
        btnAvail = findView(R.id.btnAvail_FDCH);
        btnWhoIS = findView(R.id.btnWhoIs_FDCH);
        etDomainAvail = findView(R.id.etDomainAvail_FDCH);
    }

    @Override
    protected final void initListeners() {
        super.initListeners();
        btnAvail.setOnClickListener(this);
        btnWhoIS.setOnClickListener(this);
    }

    @Override
    protected final void initData() {
        super.initData();
        filters = new CustomFilterPool<>();

        filters.addFilter(new Filter<String>() {
            @Override
            public boolean check(String _data) {
                return !_data.isEmpty();
            }
        });
        filters.addFilter(new Filter<String>() {
            @Override
            public boolean check(String _data) {
                return Patterns.WEB_URL.matcher(_data).matches();
            }
        });
    }

    @Override
    public final void onClick(View _view) {
        final String domain = etDomainAvail.getText().toString();
        if (filters.check(domain)) {
            hideKeyboard(_view);
            switch (_view.getId()) {
                case R.id.btnAvail_FDCH:
                    if (validateAeDomain()) {
                        loaderOverlayShow(getString(R.string.str_checking), this, false);
                        checkAvailability(domain);
                    }
                    break;
                case R.id.btnWhoIs_FDCH:
                    if (validateAeDomain()) {
                        loaderOverlayShow(getString(R.string.str_checking), this, false);
                        checkWhoIs(domain);
                    }
                    break;
            }
        } else {
            showMessage(R.string.str_error, R.string.str_invalid_url);
        }
    }

    private boolean validateAeDomain() {
        if (!AE_DOMAIN_PATTERN.matcher(etDomainAvail.getText()).matches()) {
            Toast.makeText(getActivity(), R.string.fragment_domain_checker_hint, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void checkAvailability(String _domain) {
        mDomainAvailabilityCheckRequest = new DomainAvailabilityCheckRequest(_domain);
        getSpiceManager()
                .execute(
                        mDomainAvailabilityCheckRequest,
                        new DomainAvailabilityCheckRequestListener(_domain)
                );
    }

    private void checkWhoIs(String _domain) {
        mDomainInfoCheckRequest = new DomainInfoCheckRequest(_domain);
        getSpiceManager()
                .execute(
                        mDomainInfoCheckRequest,
                        new DomainInfoCheckRequestListener(_domain)
                );
    }

    @Override
    public void onOkPressed(final int _mMessageId) {
        // Unimplemented method
        // Used exceptionally to specify OK button in dialog
    }

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted()) {
            if (mDomainAvailabilityCheckRequest != null) {
                getSpiceManager().cancel(mDomainAvailabilityCheckRequest);
            }
            if (mDomainInfoCheckRequest != null) {
                getSpiceManager().cancel(mDomainInfoCheckRequest);
            }
        }
    }

    private abstract class DomainCheckRequestListener<T> implements RequestListener<T> {
        protected String mDomain;

        DomainCheckRequestListener(String _domain) {
            mDomain = _domain;
        }

        public void onRequestFailure(SpiceException spiceException) {
            processError(spiceException);
        }
    }

    private final class DomainAvailabilityCheckRequestListener
            extends DomainCheckRequestListener<DomainAvailabilityCheckResponseModel> {
        private String mDomain;

        DomainAvailabilityCheckRequestListener(String _domain) {
            super(_domain);
            mDomain = _domain;
        }

        @Override
        public void onRequestSuccess(final DomainAvailabilityCheckResponseModel _responseModel) {
            if (_responseModel != null) {
                _responseModel.domainStrValue = mDomain;
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        getFragmentManager().popBackStack();
                        mServiceSelectListener.onServiceSelect(Service.DOMAIN_CHECK_AVAILABILITY, _responseModel);
                    }
                });
            } else {
                loaderOverlayCancelled(getString(R.string.str_url_not_avail));
            }
        }
    }

    private final class DomainInfoCheckRequestListener
            extends DomainCheckRequestListener<DomainInfoCheckResponseModel> {

        DomainInfoCheckRequestListener(String _domain) {
            super(_domain);
        }

        @Override
        public void onRequestSuccess(final DomainInfoCheckResponseModel _reponseModel) {
            if (!_reponseModel.urlData.equals(ServerConstants.NO_DATA_FOUND)) {
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        getFragmentManager().popBackStack();
                        mServiceSelectListener.onServiceSelect(Service.DOMAIN_CHECK_INFO, _reponseModel);
                    }
                });
            } else {
                loaderOverlayCancelled(String.format(getString(R.string.str_url_doesnot_exist), mDomain));
            }
        }
    }

    @Nullable
    @Override
    protected Service getServiceType() {
        return Service.DOMAIN_CHECK;
    }

    @Override
    protected String getServiceName() {
        return "Domain check";
    }
}

