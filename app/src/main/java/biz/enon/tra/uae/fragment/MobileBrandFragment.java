package biz.enon.tra.uae.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.enon.tra.uae.R;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.adapter.BrandsListAdapter;
import biz.enon.tra.uae.adapter.BrandsListAdapter.OnBrandSelectListener;
import biz.enon.tra.uae.fragment.base.BaseServiceFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.rest.model.response.SearchDeviceResponseModel;
import biz.enon.tra.uae.rest.robo_requests.SearchByBrandRequest;

/**
 * Created by mobimaks on 11.08.2015.
 */
public final class MobileBrandFragment extends BaseServiceFragment implements OnQueryTextListener, OnBrandSelectListener {

    private static final String KEY_SEARCH_DEVICE_BY_BRAND_REQUEST = "SEARCH_DEVICE_BY_BRAND_REQUEST";
    private static final String KEY_SELECTED_BRAND_LOGO = "SELECTED_BRAND_LOGO";

    private RecyclerView rvBrands;
    private SearchView svSearchView;

    private BrandsListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private OnDeviceSelectListener mSelectListener;
    private RequestResponseListener mRequestListener;
    private SearchByBrandRequest mSearchByBrandRequest;

    @DrawableRes
    private int mSelectedBrandLogoRes;

    public static MobileBrandFragment newInstance() {
        return new MobileBrandFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnDeviceSelectListener) {
            mSelectListener = (OnDeviceSelectListener) _activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            mSelectedBrandLogoRes = savedInstanceState.getInt(KEY_SELECTED_BRAND_LOGO);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        rvBrands = findView(R.id.rvBrands_FAD);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mRequestListener = new RequestResponseListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDevicesList();
    }

    private void initDevicesList() {
        //region Init stub data list
        final String[] stubBrandsData = {"HTC", "Samsung", "Nokia", "Huawei", "Apple"};
        final int[] stubBrandsLogos = {R.drawable.ic_htc, R.drawable.ic_samsung, R.drawable.ic_nokia, R.drawable.logo_huaw, R.drawable.ic_apple};

        List<BrandItem> stubData = new ArrayList<>();
        for (int i = 0; i < stubBrandsData.length; i++) {
            BrandItem item = new BrandItem();
            item.mName = stubBrandsData[i];
            item.mBrandImageRes = R.drawable.nexus_5x;
            item.mBrandLogoRes = stubBrandsLogos[i];
            stubData.add(item);
        }
        //endregion

        mAdapter = new BrandsListAdapter(getActivity(), stubData);
        rvBrands.setAdapter(mAdapter);
        mAdapter.setOnBrandSelectListener(this);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        rvBrands.setLayoutManager(mLinearLayoutManager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        svSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        svSearchView.setOnQueryTextListener(this);
    }

    @Override
    protected String getServiceName() {
        return C.RATE_NAME_MOBILE_BRAND;//"Search Device By BrandName";
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("DeviceBrand", "onStart");
        getSpiceManager().getFromCache(SearchDeviceResponseModel.List.class, KEY_SEARCH_DEVICE_BY_BRAND_REQUEST, DurationInMillis.ALWAYS_RETURNED, mRequestListener);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.getFilter().filter(query);
        hideKeyboard(getView());
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public void onStop() {
        Log.d("DeviceBrand", "onStop");
        super.onStop();
    }

    @Override
    public void onBrandSelect(final BrandItem _item) {
        if (mSelectListener != null) {
            hideKeyboard(getView());
            mSelectedBrandLogoRes = _item.mBrandLogoRes;
            mSearchByBrandRequest = new SearchByBrandRequest(_item.mName, 0, 100);
            loaderOverlayShow(getString(R.string.str_loading), this);
            getSpiceManager().execute(mSearchByBrandRequest, KEY_SEARCH_DEVICE_BY_BRAND_REQUEST, DurationInMillis.ALWAYS_EXPIRED, mRequestListener);
        }
    }

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted()) {
            getSpiceManager().cancel(mSearchByBrandRequest);
            getSpiceManager().removeDataFromCache(SearchDeviceResponseModel.List.class, KEY_SEARCH_DEVICE_BY_BRAND_REQUEST);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle _outState) {
        _outState.putInt(KEY_SELECTED_BRAND_LOGO, mSelectedBrandLogoRes);
        super.onSaveInstanceState(_outState);
    }

    private class RequestResponseListener implements RequestListener<SearchDeviceResponseModel.List> {

        @Override
        public void onRequestSuccess(final SearchDeviceResponseModel.List result) {
            getSpiceManager().removeDataFromCache(SearchDeviceResponseModel.List.class, KEY_SEARCH_DEVICE_BY_BRAND_REQUEST);

            if (isAdded()) {
                if (result != null) {
                    if (result.isEmpty()) {
                        loaderOverlayCancelled(getString(R.string.fragment_approved_devices_no_results));
                    } else if (mSelectListener != null) {
                        loaderOverlayDismissWithAction(new Loader.Dismiss() {
                            @Override
                            public void onLoadingDismissed() {
                                getFragmentManager().popBackStack();
                                mSelectListener.onDeviceSelect(mSelectedBrandLogoRes, result);
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d("DeviceBrand", "onRequestFailure");
            getSpiceManager().removeDataFromCache(SearchDeviceResponseModel.List.class, KEY_SEARCH_DEVICE_BY_BRAND_REQUEST);
            processError(spiceException);
        }
    }

    @Override
    public void onDetach() {
        mSelectListener = null;
        super.onDetach();
    }

    public final class BrandItem {

        @DrawableRes
        public int mBrandImageRes, mBrandLogoRes;

        public String mName;

    }

    @Override
    protected Service getServiceType() {
        return Service.MOBILE_BRAND;
    }

    @Override
    protected int getTitle() {
        return R.string.approved_devices;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_approved_devices;
    }

    public interface OnDeviceSelectListener {
        void onDeviceSelect(final @DrawableRes int _selectedBrandLogoRes, final SearchDeviceResponseModel.List _device);
    }
}
