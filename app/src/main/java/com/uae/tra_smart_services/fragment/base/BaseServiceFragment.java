package com.uae.tra_smart_services.fragment.base;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.dialog.ServiceRatingDialog;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.rest.model.request.RatingServiceRequestModel;
import com.uae.tra_smart_services.rest.model.response.RatingServiceResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.RatingServiceRequest;

/**
 * Created by ak-buffalo on 27.08.15.
 */
public abstract class BaseServiceFragment extends BaseFragment implements Loader.Cancelled, ServiceRatingDialog.CallBacks {

//    @CallSuper
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_rate, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @CallSuper
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_rate) {
//            hideKeyboard(getView());
//            showRatingDialog();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onRate(int _rate){
        final String[] rateNames = getResources().getStringArray(R.array.rate_names);

        sendRating(new RatingServiceRequestModel(getServiceName(), _rate, rateNames[_rate - 1]));
    }

    private void sendRating(RatingServiceRequestModel _model){
        loaderOverlayShow(getString(R.string.str_give_us_moment), BaseServiceFragment.this);
        loaderOverlayButtonBehavior(new Loader.BackButton() {
            @Override
            public void onBackButtonPressed(LoaderView.State _currentState) {
                getFragmentManager().popBackStack();
            }
        });

        getSpiceManager().execute(
                new RatingServiceRequest(_model),
                new RequestListener<RatingServiceResponseModel>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        processError(spiceException);
                    }

                    @Override
                    public void onRequestSuccess(RatingServiceResponseModel response) {
//                        switch (response.getStatus()) {
//                            case 201:
                        loaderOverlaySuccess(getString(R.string.str_rating_has_sent));
//                                break;
//                            case 400:
//                                loaderOverlayFailed(getString(R.string.str_something_went_wrong));
//                                break;
//                        }
                    }
                }
        );
    }

    protected void showRatingDialog(){
        hideKeyboard(getView());
        ServiceRatingDialog.newInstance(this)
                .show(getFragmentManager());
    }

    protected abstract String getServiceName();

    @Override
    public void onCancelPressed(){
        // Unimplemented method
        // Used exceptionally to specify Cancel button in dialog
    }

}
