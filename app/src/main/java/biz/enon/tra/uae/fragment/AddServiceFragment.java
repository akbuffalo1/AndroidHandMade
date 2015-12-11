package biz.enon.tra.uae.fragment;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.AddFavoritesAdapter;
import biz.enon.tra.uae.adapter.AddFavoritesAdapter.OnItemClickListener;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.Service;

/**
 * Created by mobimaks on 22.08.2015.
 */
public class AddServiceFragment extends BaseFragment implements OnItemClickListener, OnQueryTextListener {

    public static AddServiceFragment newInstance() {
        return new AddServiceFragment();
    }

    private RecyclerView rvFavoritesList;
//    private ImageView ivBackground;
    private SearchView svSearchServices;

    private AddFavoritesAdapter mFavoritesAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<Service> mSelectedItems;
    private OnFavoriteServicesSelectedListener mServicesSelectedListener;

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnFavoriteServicesSelectedListener)
            mServicesSelectedListener = (OnFavoriteServicesSelectedListener) _activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSelectedItems = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_service, menu);
        svSearchServices = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        svSearchServices.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done && mServicesSelectedListener != null) {
            Log.d("Favorites", "Size: " + mFavoritesAdapter.getItemCount() + ", selected: " + mSelectedItems.size());
            mServicesSelectedListener.onFavoriteServicesSelected(mSelectedItems);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {
        super.initViews();
        rvFavoritesList = findView(R.id.rvFavoritesList_FAS);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFavoritesList();
    }

    private void initFavoritesList() {
        mFavoritesAdapter = new AddFavoritesAdapter(getActivity(), getNotFavoriteServices());
        mFavoritesAdapter.setItemClickListener(this);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        rvFavoritesList.setHasFixedSize(true);
        rvFavoritesList.setAdapter(mFavoritesAdapter);
        rvFavoritesList.setLayoutManager(mLinearLayoutManager);
    }

    private List<Service> getNotFavoriteServices() {
        final List<Service> services = Service.getUniqueServices();
        final List<Service> favoriteServices = getFavoriteServices();
        services.removeAll(favoriteServices);
        return services;
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
    public boolean onQueryTextChange(final String _newText) {
        mFavoritesAdapter.getFilter().filter(_newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(final String _query) {
        mFavoritesAdapter.getFilter().filter(_query);
        return true;
    }

    @Override
    public void onItemClick(Service _item, boolean isSelected) {
        if (isSelected)
            mSelectedItems.add(_item);
        else
            mSelectedItems.remove(_item);
    }

    @Override
    public void onDetach() {
        mServicesSelectedListener = null;
        super.onDetach();
    }

    @Override
    protected int getTitle() {
        return 0;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_add_service;
    }

    public interface OnFavoriteServicesSelectedListener {
        void onFavoriteServicesSelected(List<Service> _items);
    }
}
