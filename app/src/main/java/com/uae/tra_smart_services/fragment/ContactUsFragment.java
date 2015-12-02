package com.uae.tra_smart_services.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.rest.model.response.ContactUsResponse;
import com.uae.tra_smart_services.rest.model.response.ContactUsResponse.List;
import com.uae.tra_smart_services.rest.robo_requests.ContactUsRequest;

import java.util.ArrayList;

/**
 * Created by mobimaks on 02.12.2015.
 */
public class ContactUsFragment extends BaseFragment {

    private static final String KEY_CONTACT_US = "CONTACT_US";

    private RequestListener<List> mRequestListener;

    private ArrayList<ContactUsResponse> mContactUsResponse;

    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRequestListener = new ContactUsInfoResponseListener();

        if (savedInstanceState != null) {
            mContactUsResponse = savedInstanceState.getParcelableArrayList(KEY_CONTACT_US);
        }

//        if (mContactUsResponse == null) {
//            performContactUsInfoRequest();
//        } else {
//            showContactUsData(mContactUsResponse);
//        }
    }

    private void performContactUsInfoRequest() {
        ContactUsRequest contactUsRequest = new ContactUsRequest();
        getDynamicSpiceManager().execute(contactUsRequest, KEY_CONTACT_US, DurationInMillis.ALWAYS_EXPIRED, mRequestListener);
    }

    private void showContactUsData(final ArrayList<ContactUsResponse> _contactUsData) {

    }

    @Override
    public void onStart() {
        super.onStart();
        getDynamicSpiceManager().getFromCache(List.class, KEY_CONTACT_US,
                DurationInMillis.ALWAYS_RETURNED, mRequestListener);
    }

    private final class ContactUsInfoResponseListener implements RequestListener<List> {

        @Override
        public void onRequestSuccess(List result) {

        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_CONTACT_US, mContactUsResponse);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_contact_us;
    }

    @Override
    protected int getTitle() {
        return 0;
    }

}
