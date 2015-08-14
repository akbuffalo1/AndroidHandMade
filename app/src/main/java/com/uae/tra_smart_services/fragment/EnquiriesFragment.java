package com.uae.tra_smart_services.fragment;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.uae.tra_smart_services.rest.model.new_request.ComplainTRAServiceModel;
import com.uae.tra_smart_services.rest.new_request.ComplainEnquiriesServiceRequest;

/**
 * Created by mobimaks on 14.08.2015.
 */
public class EnquiriesFragment extends ComplainAboutTraFragment {

    public static EnquiriesFragment newInstance() {
        return new EnquiriesFragment();
    }

    @Override
    protected void sendComplain() {
        ComplainTRAServiceModel traServiceModel = new ComplainTRAServiceModel();
        traServiceModel.title = getTitleText();
        traServiceModel.description = getDescriptionText();
        ComplainEnquiriesServiceRequest request = new ComplainEnquiriesServiceRequest(traServiceModel, getActivity(), getImageUri());
        showProgressDialog(getFragmentManager());
        getSpiceManager().execute(request, KEY_COMPLAIN_REQUEST, DurationInMillis.ALWAYS_EXPIRED, getRequestListener());
    }
}
