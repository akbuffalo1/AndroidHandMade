package com.uae.tra_smart_services.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.FavoritesAdapter;
import com.uae.tra_smart_services.adapter.FavoritesAdapter.OnFavoriteClickListener;
import com.uae.tra_smart_services.customviews.DragFrameLayout;
import com.uae.tra_smart_services.customviews.DragFrameLayout.OnItemDeleteListener;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.interfaces.OpenServiceInfo;
import com.uae.tra_smart_services.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobimaks on 18.08.2015.
 */
public final class FavoritesFragment extends BaseFragment
        implements OnFavoriteClickListener, OnItemDeleteListener, OnClickListener, OnQueryTextListener {

    private DragFrameLayout dflContainer;
    private RecyclerView rvFavoritesList;
    private RelativeLayout rlEmptyContainer;
    private HexagonView hvAddService;
    private SearchView svSearchFavorites;

    private FavoritesAdapter mFavoritesAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private OnFavoritesEventListener mFavoritesEventListener;

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnFavoritesEventListener) {
            mFavoritesEventListener = (OnFavoritesEventListener) _activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void initViews() {
        super.initViews();
        rlEmptyContainer = findView(R.id.rlEmptyContainer_FF);
        hvAddService = findView(R.id.hvPlusBtn);
        hvAddService.setHexagonSrcDrawable(ImageUtils.getFilteredDrawableByTheme(getActivity(), R.drawable.ic_plus, R.attr.themeMainColor));
        dflContainer = findView(R.id.dflContainer_FF);
        rvFavoritesList = findView(R.id.rvFavoritesList_FF);
        setEmptyPlaceholderVisibility(true);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        dflContainer.setItemDeleteListener(this);
        hvAddService.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFavoritesList();
    }

    private void initFavoritesList() {
        final List<Service> favoriteServices = getFavoriteServices();
        mFavoritesAdapter = new FavoritesAdapter(getActivity(), favoriteServices);
        mFavoritesAdapter.setFavoriteClickListener(this);
        rvFavoritesList.setAdapter(mFavoritesAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        rvFavoritesList.setLayoutManager(mLinearLayoutManager);
        invalidateAddServiceBtnVisibility();
        setEmptyPlaceholderVisibility(mFavoritesAdapter.isEmpty());
    }

    private List<Service> getFavoriteServices() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String favoriteServicesStr = prefs.getString(C.KEY_FAVORITE_SERVICES, "");
        final List<Service> services = new ArrayList<>();

        if (!favoriteServicesStr.isEmpty()) {
            final String[] servicesStrArray = favoriteServicesStr.split(C.FAVORITE_SERVICES_DELIMITER);
            for (String serviceStr : servicesStrArray) {
                try {
                    final Service service = Service.valueOf(serviceStr);
                    services.add(service);
                } catch (IllegalArgumentException e) {
                    //continue cycle
                }
            }
        }
        return services;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_favorites, menu);
        svSearchFavorites = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        svSearchFavorites.setOnQueryTextListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final boolean isItemsVisible = !mFavoritesAdapter.isEmpty();
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(isItemsVisible);

//        final boolean isAllServicesAlreadyFavorite = mFavoritesAdapter.getAllData().size() == Service.getUniqueServicesCount();
//        final MenuItem addItem = menu.findItem(R.id.action_add);
//        addItem.setVisible(isItemsVisible && !isAllServicesAlreadyFavorite);
    }

    public final void addServicesToFavorites(final List<Service> _items) {
        Log.d("Favorites", "Items before new added: " + (mFavoritesAdapter.getItemCount() - mFavoritesAdapter.getHeaderCount()));
        mFavoritesAdapter.addData(_items);
        Log.d("Favorites", "Items after new added: " + (mFavoritesAdapter.getItemCount() - mFavoritesAdapter.getHeaderCount()));
        invalidateAddServiceBtnVisibility();
        getActivity().invalidateOptionsMenu();
        saveFavoriteServices();
        setEmptyPlaceholderVisibility(mFavoritesAdapter.isEmpty());
    }

    private void invalidateAddServiceBtnVisibility() {
        final boolean isAllServicesAlreadyFavorite = mFavoritesAdapter.getAllData().size() == Service.getUniqueServicesCount();
        mFavoritesAdapter.setAddButtonVisibility(!isAllServicesAlreadyFavorite);
    }

    private void saveFavoriteServices() {
        final List<Service> favoriteServices = mFavoritesAdapter.getAllData();
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        if (favoriteServices.isEmpty()) {
            editor.remove(C.KEY_FAVORITE_SERVICES).commit();
        } else {
            final int favoriteServicesCount = favoriteServices.size();
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < favoriteServicesCount; i++) {
                builder.append(favoriteServices.get(i).name()).append(C.FAVORITE_SERVICES_DELIMITER);
            }
            final String servicesStr = builder.toString();
            editor.putString(C.KEY_FAVORITE_SERVICES, servicesStr).commit();
        }
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add && mFavoritesEventListener != null) {
            mFavoritesEventListener.onAddFavoritesClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mFavoritesAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mFavoritesAdapter.getFilter().filter(query);
        hideKeyboard(getView());
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.hvPlusBtn) {
            onAddServiceClick();
        }
    }

    @Override
    public void onAddServiceClick() {
        if (mFavoritesEventListener != null) {
            mFavoritesEventListener.onAddFavoritesClick();
        }
    }

    @Override
    public void onItemClick(int _position) {
        if (mFavoritesEventListener != null) {
            hideKeyboard(getView());
            mFavoritesEventListener.onOpenServiceClick(mFavoritesAdapter.getItem(_position));
        }
    }

    @Override
    public void onServiceInfoClick(int _position) {
        if (mFavoritesEventListener != null) {
            hideKeyboard(getView());
            final Service service = mFavoritesAdapter.getItem(_position);
            openServiceInfoIfCan(service);
        }
        Log.d(getClass().getSimpleName(), "Service info click: " + _position);
    }

    private void openServiceInfoIfCan(final Service service) {
        final String serviceName = service.getServiceName();
        if (serviceName != null && mFavoritesEventListener != null) {
            mFavoritesEventListener.onOpenServiceInfo(serviceName);
        }
    }

    @Override
    public void onRemoveLongClick(View _view, int _position) {
        hideKeyboard(_view);
        ClipData data = ClipData.newPlainText("Item remove", "" + _position);
        View itemView = mLinearLayoutManager.findViewByPosition(_position);
        itemView.setTag(_view);
        dflContainer.starDragChild(itemView, data);
    }

    @Override
    public void onDeleteItem(int _position) {
        mFavoritesAdapter.removeItem(_position);
        Log.d("Favorites", "Items after delete operation: " + (mFavoritesAdapter.getItemCount()));
        invalidateAddServiceBtnVisibility();
        getActivity().invalidateOptionsMenu();
        saveFavoriteServices();
        if (mFavoritesAdapter.isEmpty()) {
            setEmptyPlaceholderVisibility(true);
        }
    }

    @Override
    public final void onDetach() {
        mFavoritesEventListener = null;
        super.onDetach();
    }

    private void setEmptyPlaceholderVisibility(final boolean _isEmpty) {
        if (_isEmpty) {
            rlEmptyContainer.setVisibility(View.VISIBLE);
            rvFavoritesList.setVisibility(View.INVISIBLE);
        } else {
            rlEmptyContainer.setVisibility(View.INVISIBLE);
            rvFavoritesList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getTitle() {
        return R.string.str_favorites;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_favorites;
    }

    public interface OnFavoritesEventListener extends OpenServiceInfo {
        void onAddFavoritesClick();

        void onOpenServiceClick(final Service _service);
    }
}
