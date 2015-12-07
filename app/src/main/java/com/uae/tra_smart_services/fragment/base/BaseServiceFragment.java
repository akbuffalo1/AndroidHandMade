package com.uae.tra_smart_services.fragment.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.customviews.ServiceRatingView;
import com.uae.tra_smart_services.dialog.ServiceRatingDialog;
import com.uae.tra_smart_services.fragment.LoaderFragment;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.interfaces.Loader.Cancelled;
import com.uae.tra_smart_services.interfaces.OpenServiceInfo;
import com.uae.tra_smart_services.rest.model.request.RatingServiceRequestModel;
import com.uae.tra_smart_services.rest.model.response.GetTransactionResponseModel;
import com.uae.tra_smart_services.rest.model.response.RatingServiceResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.RatingServiceRequest;

/**
 * Created by ak-buffalo on 27.08.15.
 */
public abstract class BaseServiceFragment extends BaseFragment
        implements Cancelled, LoaderFragment.CallBacks, ServiceRatingDialog.CallBacks {

    protected static final String KEY_DATA = "data";

    private OpenServiceInfo mOpenServiceInfoListener;
    protected GetTransactionResponseModel mModel;

    @CallSuper
    @Override
    public void onAttach(final Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OpenServiceInfo) {
            mOpenServiceInfoListener = (OpenServiceInfo) _activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @CallSuper
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getServiceType() != null) {
            inflater.inflate(R.menu.menu_info, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard(getView());
        switch (item.getItemId()) {
            case R.id.action_show_info:
                openServiceInfoIfCan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openServiceInfoIfCan() {
        final Service service;
        final String serviceName;
        if ((service = getServiceType()) != null && (serviceName = service.getServiceName()) != null
                && mOpenServiceInfoListener != null) {
            mOpenServiceInfoListener.onOpenServiceInfo(serviceName);
        }
    }

    @Override
    public void onRate(int _rate, String _description, LoaderView.State _state){
        if(_state == LoaderView.State.SUCCESS || _state == LoaderView.State.FAILURE){
            getFragmentManager().popBackStackImmediate();
        }
        onRate(_rate, _description);
    }

    @Override
    public void onRate(int _rate, String _description){
        final String[] rateNames = getResources().getStringArray(R.array.rate_names);
        getFragmentManager().popBackStackImmediate();
        sendRating(new RatingServiceRequestModel(getServiceName(), _rate, !_description.isEmpty() ? _description : rateNames[_rate - 1]));
    }

    private void sendRating(RatingServiceRequestModel _model) {
        getSpiceManager().execute(
                new RatingServiceRequest(_model),
                new RequestListener<RatingServiceResponseModel>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        // Unimplemented method
                    }

                    @Override
                    public void onRequestSuccess(RatingServiceResponseModel response) {
                        // Unimplemented method
                    }
                }
        );
    }

    @CallSuper
    @Override
    public void onDetach() {
        mOpenServiceInfoListener = null;
        super.onDetach();
    }

    @Nullable
    protected abstract Service getServiceType();

    protected abstract String getServiceName();
}
