package com.uae.tra_smart_services.fragment;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.ServerConstants;
import com.uae.tra_smart_services.rest.model.response.DomainAvailabilityCheckResponseModel;
import com.uae.tra_smart_services.util.ImageUtils;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public final class DomainIsAvailableFragment extends BaseFragment{

    private static final String KEY_DOMAIN_AVAILABILITY_MODEL = "DOMAIN_AVAILABILITY_MODEL";

    private DomainAvailabilityCheckResponseModel mAvailabilityCheckResponse;

    public static DomainIsAvailableFragment newInstance(DomainAvailabilityCheckResponseModel _model) {
        final DomainIsAvailableFragment fragment = new DomainIsAvailableFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_DOMAIN_AVAILABILITY_MODEL, _model);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setHasOptionsMenu(true);
        mAvailabilityCheckResponse = getArguments().getParcelable(KEY_DOMAIN_AVAILABILITY_MODEL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rate, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected void initViews() {
        super.initViews();
        ((TextView) findView(R.id.tvDomainStrValue_FDCH)).setText(mAvailabilityCheckResponse.domainStrValue);
        String status = mAvailabilityCheckResponse.availableStatus;
        
        @StringRes int availabilityTextRes;
        @ColorRes int availabilityColorRes;
        switch (status) {
            case ServerConstants.AVAILABLE:
                availabilityTextRes = R.string.str_domain_available;
                availabilityColorRes = ImageUtils.isBlackAndWhiteMode(getActivity()) ?
                        R.color.hex_black_color : R.color.hex_primary_green;
                break;
            case ServerConstants.NOT_AVAILABLE:
                availabilityTextRes = R.string.str_domain_not_available;
                availabilityColorRes = ImageUtils.isBlackAndWhiteMode(getActivity()) ?
                        R.color.hex_black_color : R.color.hex_primary_red;
                break;
            default:
                availabilityTextRes = R.string.str_domain_no_information;
                availabilityColorRes = R.color.hex_black_color;
                break;
        }
        final TextView statusTextView = findView(R.id.tvDomainAvail_FDCH);
        statusTextView.setText(getString(availabilityTextRes));
        statusTextView.setTextColor(getResources().getColor(availabilityColorRes));
    }

    @Override
    protected int getTitle() {
        return R.string.fragment_domain_availability_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_domain_is_available;
    }
}