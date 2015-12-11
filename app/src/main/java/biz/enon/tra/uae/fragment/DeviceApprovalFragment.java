package biz.enon.tra.uae.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;

import com.enon.tra.uae.R;

import java.util.List;

import biz.enon.tra.uae.adapter.DeviceApprovalAdapter;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.rest.model.response.SearchDeviceResponseModel;

/**
 * Created by mobimaks on 11.08.2015.
 */
@SuppressWarnings("FieldCanBeLocal")
public class DeviceApprovalFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static final String SELECTED_DEVICE_KEY = "SELECTED_DEVICE_KEY";
    private static final String KEY_SELECTED_BRAND_LOGO = "SELECTED_BRAND_LOGO";

    private ImageView ivBrandLogo;
    private RecyclerView rvDevices;

    private DeviceApprovalAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private SearchView svSearchView;

    @DrawableRes
    private int mSelectedBrandLogoRes;
    private List<SearchDeviceResponseModel> mApprovedDevices;

    public static DeviceApprovalFragment newInstance(int _selectedBrandLogoRes, final SearchDeviceResponseModel.List _device) {
        DeviceApprovalFragment fragment = new DeviceApprovalFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(SELECTED_DEVICE_KEY, _device);
        args.putInt(KEY_SELECTED_BRAND_LOGO, _selectedBrandLogoRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        final Bundle args = getArguments();
        mApprovedDevices = args.getParcelableArrayList(SELECTED_DEVICE_KEY);
        mSelectedBrandLogoRes = args.getInt(KEY_SELECTED_BRAND_LOGO);
    }

    @Override
    protected void initViews() {
        super.initViews();
        ivBrandLogo = findView(R.id.ivBrandLogo_FDA);
        if (mSelectedBrandLogoRes != 0) {
            ivBrandLogo.setImageResource(mSelectedBrandLogoRes);
        }
        rvDevices = findView(R.id.rvDevices_FDA);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDevicesList();
    }

    private void initDevicesList() {
        mAdapter = new DeviceApprovalAdapter(getActivity(), mApprovedDevices);
        rvDevices.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getActivity());
        rvDevices.setLayoutManager(mLayoutManager);
    }

    @Override
     public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        svSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        svSearchView.setOnQueryTextListener(this);
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
    protected int getTitle() {
        return R.string.device_approval;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_device_approval;
    }
}
