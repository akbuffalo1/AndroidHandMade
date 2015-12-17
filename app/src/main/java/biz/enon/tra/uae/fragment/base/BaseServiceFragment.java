package biz.enon.tra.uae.fragment.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.dialog.ServiceRatingDialog;
import biz.enon.tra.uae.fragment.LoaderFragment;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader.Cancelled;
import biz.enon.tra.uae.interfaces.OpenServiceInfo;
import biz.enon.tra.uae.rest.model.request.RatingServiceRequestModel;
import biz.enon.tra.uae.rest.model.response.TransactionModel;
import biz.enon.tra.uae.rest.model.response.RatingServiceResponseModel;
import biz.enon.tra.uae.rest.robo_requests.RatingServiceRequest;

/**
 * Created by ak-buffalo on 27.08.15.
 */
public abstract class BaseServiceFragment extends BaseFragment
        implements Cancelled, LoaderFragment.CallBacks, ServiceRatingDialog.CallBacks {

    protected static String KEY_MODE = "MODE";
    protected static String KEY_TARNS_MODEL = "TRANS_MODEL";
    private boolean mIsInEditMode = false;
    private TransactionModel mTransactionModel;

    private OpenServiceInfo mOpenServiceInfoListener;

    @CallSuper
    @Override
    public void onAttach(final Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OpenServiceInfo) {
            mOpenServiceInfoListener = (OpenServiceInfo) _activity;
        }
    }

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TransactionModel model = prepareTransactionModel(savedInstanceState);
        if(model != null){
            mIsInEditMode = true;
            prepareFieldsIfModelExist(model);
        }
    }

    protected boolean isIsInEditMode(){
        return mIsInEditMode;
    }

    protected TransactionModel getTransactionModel(){
        return mTransactionModel;
    }

    protected void prepareFieldsIfModelExist(@NonNull TransactionModel _transModel){
        //TODO Implement this method if you like to edit transaction
    }

    protected TransactionModel prepareTransactionModel(Bundle savedInstanceState) {
        mTransactionModel = null;
        if (getArguments() != null){
            mTransactionModel = getArguments().getParcelable(KEY_TARNS_MODEL);
        } else if (savedInstanceState != null && savedInstanceState.getBoolean(KEY_MODE)) {
            mTransactionModel = savedInstanceState.getParcelable(KEY_TARNS_MODEL);
        }
        return mTransactionModel;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {outState.putBoolean(KEY_MODE, getArguments() != null);
        outState.putBoolean(KEY_MODE, mTransactionModel != null);
        if(getArguments() != null){
            outState.putParcelable(KEY_TARNS_MODEL, mTransactionModel);
        }
        super.onSaveInstanceState(outState);
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
        if ((service = getServiceType()) != null && (serviceName = service.getServiceInfoName()) != null
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
