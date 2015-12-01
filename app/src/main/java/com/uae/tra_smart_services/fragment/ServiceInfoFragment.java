package com.uae.tra_smart_services.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.interfaces.Loader.Cancelled;
import com.uae.tra_smart_services.rest.model.response.ServiceInfoResponse;
import com.uae.tra_smart_services.rest.model.response.UserProfileResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.ServiceInfoRequest;

/**
 * Created by Mikazme on 21/08/2015.
 */
public class ServiceInfoFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener {

    private static final String SERVICE = "service_type";
    private static final String KEY_SERVICE_NAME = "SERVICE_NAME";
    private static final String KEY_SERVICE_INFO_MODEL = "SERVICE_INFO_MODEL";
    private static final String KEY_SERVICE_INFO_REQUEST = "SERVICE_INFO_REQUEST";
    private static final String KEY_IS_INFO_LOADED = "IS_INFO_LOADED";

    private ImageView ivCloseInfo;
    private HexagonView hvServiceInfo, hvRequiredDocuments, hvTermsAndConditions,
            hvServicePackage, hvProcessingTime, hvServiceFees, hvContactInCharge;
    private RelativeLayout rlFragmentContainer;

    private String mServiceName;
    private ServiceInfoRequestListener mInfoRequestListener;
    private ServiceInfoRequest mServiceInfoRequest;
    private ServiceInfoResponse mServiceInfo;
    private boolean mIsInfoLoaded;

    private OnOpenServiceInfoDetailsListener mOpenDetailsListener;

    public static ServiceInfoFragment newInstance(final String _serviceName) {
        final ServiceInfoFragment fragment = new ServiceInfoFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SERVICE_NAME, _serviceName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnOpenServiceInfoDetailsListener) {
            mOpenDetailsListener = (OnOpenServiceInfoDetailsListener) _activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServiceName = getArguments().getString(KEY_SERVICE_NAME);
        if (savedInstanceState == null) {
            mServiceInfo = new ServiceInfoResponse();
        } else {
            mServiceInfo = savedInstanceState.getParcelable(KEY_SERVICE_INFO_MODEL);
            mIsInfoLoaded = savedInstanceState.getBoolean(KEY_IS_INFO_LOADED);
        }
    }

    @Override
    protected final void initViews() {
        ivCloseInfo = findView(R.id.ivCloseInfo_FSI);
        rlFragmentContainer = findView(R.id.rlFragmentContainer_FSI);
        hvServiceInfo = findView(R.id.hvAboutService_FSI);
        hvRequiredDocuments = findView(R.id.hvRequiredDocuments_FSI);
        hvTermsAndConditions = findView(R.id.hvTermsAndConditions_FSI);
        hvServicePackage = findView(R.id.hvServicePackage_FSI);
        hvProcessingTime = findView(R.id.hvProcessingTime_FSI);
        hvServiceFees = findView(R.id.hvServiceFees_FSI);
        hvContactInCharge = findView(R.id.hvContactInCharge_FSI);
    }

    @Override
    protected final void initListeners() {
        ivCloseInfo.setOnClickListener(this);
        rlFragmentContainer.setOnTouchListener(this);
        hvServiceInfo.setOnClickListener(this);
        hvRequiredDocuments.setOnClickListener(this);
        hvTermsAndConditions.setOnClickListener(this);
        hvServicePackage.setOnClickListener(this);
        hvProcessingTime.setOnClickListener(this);
        hvServiceFees.setOnClickListener(this);
        hvContactInCharge.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mIsInfoLoaded) {
            loadServiceInfo();
        }
    }

    private void loadServiceInfo() {
        mInfoRequestListener = new ServiceInfoRequestListener();
        mServiceInfoRequest = new ServiceInfoRequest(mServiceName, getResources().getConfiguration().locale.toString());
        loaderOverlayShow(getString(R.string.str_loading), mInfoRequestListener, false);
        loaderOverlayButtonBehavior(new Loader.BackButton() {
            @Override
            public void onBackButtonPressed(LoaderView.State _currentState) {
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
            }
        });
        getSpiceManager().execute(mServiceInfoRequest, KEY_SERVICE_INFO_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, mInfoRequestListener);
    }

    @Override
    public final void onClick(final View _view) {
        @DrawableRes int hexagonSrc;
        if (mOpenDetailsListener != null) {
            switch (_view.getId()) {
                case R.id.ivCloseInfo_FSI:
                    getFragmentManager().popBackStack();
                    break;
                case R.id.hvAboutService_FSI:
                    hexagonSrc = ((HexagonView) _view).getHexagonSrc();
                    mOpenDetailsListener.onOpenServiceInfoDetails(hexagonSrc, mServiceInfo.aboutService);
                    break;
                case R.id.hvRequiredDocuments_FSI:
                    hexagonSrc = ((HexagonView) _view).getHexagonSrc();
                    mOpenDetailsListener.onOpenServiceInfoDetails(hexagonSrc, mServiceInfo.requiredDocs);
                    break;
                case R.id.hvTermsAndConditions_FSI:
                    hexagonSrc = ((HexagonView) _view).getHexagonSrc();
                    mOpenDetailsListener.onOpenServiceInfoDetails(hexagonSrc, mServiceInfo.termsAndConditions);
                    break;
                case R.id.hvServicePackage_FSI:
                    hexagonSrc = ((HexagonView) _view).getHexagonSrc();
                    mOpenDetailsListener.onOpenServiceInfoDetails(hexagonSrc, mServiceInfo.servicePackage);
                    break;
                case R.id.hvProcessingTime_FSI:
                    hexagonSrc = ((HexagonView) _view).getHexagonSrc();
                    mOpenDetailsListener.onOpenServiceInfoDetails(hexagonSrc, mServiceInfo.expectedTime);
                    break;
                case R.id.hvServiceFees_FSI:
                    hexagonSrc = ((HexagonView) _view).getHexagonSrc();
                    mOpenDetailsListener.onOpenServiceInfoDetails(hexagonSrc, mServiceInfo.serviceFee);
                    break;
                case R.id.hvContactInCharge_FSI:
                    hexagonSrc = ((HexagonView) _view).getHexagonSrc();
                    mOpenDetailsListener.onOpenServiceInfoDetails(hexagonSrc, mServiceInfo.officer);
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mIsInfoLoaded) {
            getSpiceManager().getFromCache(ServiceInfoResponse.class, KEY_SERVICE_INFO_REQUEST,
                    DurationInMillis.ALWAYS_EXPIRED, mInfoRequestListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_INFO_LOADED, mIsInfoLoaded);
        outState.putParcelable(KEY_SERVICE_INFO_MODEL, mServiceInfo);
        super.onSaveInstanceState(outState);
    }

    private class ServiceInfoRequestListener implements RequestListener<ServiceInfoResponse>, Cancelled {

        @Override
        public void onRequestSuccess(ServiceInfoResponse result) {
            getSpiceManager().removeDataFromCache(ServiceInfoResponse.class, KEY_SERVICE_INFO_REQUEST);
            if (isAdded() && result != null) {
                mServiceInfo = result;
                mIsInfoLoaded = true;
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        getFragmentManager().popBackStack();
                    }
                });
            }
        }

        @Override
        public void onLoadingCanceled() {
            if (getSpiceManager().isStarted()) {
                getSpiceManager().removeDataFromCache(ServiceInfoResponse.class, KEY_SERVICE_INFO_REQUEST);
                getSpiceManager().cancel(mServiceInfoRequest);
            }

        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(UserProfileResponseModel.class, KEY_SERVICE_INFO_REQUEST);
            processError(spiceException);
        }
    }

    @Override
    public void onDetach() {
        mOpenDetailsListener = null;
        super.onDetach();
    }

    @Override
    public final boolean onTouch(final View _view, final MotionEvent _event) {
        return true;
    }

    public interface OnOpenServiceInfoDetailsListener {
        void onOpenServiceInfoDetails(final @DrawableRes int _iconSrc, final String _serviceInfo);
    }

    @Override
    protected final int getTitle() {
        return 0;
    }

    @Override
    protected final int getLayoutResource() {
        return R.layout.fragment_service_info;
    }
}
