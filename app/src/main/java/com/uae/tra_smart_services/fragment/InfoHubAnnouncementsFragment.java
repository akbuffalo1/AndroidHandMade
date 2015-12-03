package com.uae.tra_smart_services.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.AnnouncementsAdapter;
import com.uae.tra_smart_services.customviews.HexagonSwipeRefreshLayout;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.QueryAdapter;
import com.uae.tra_smart_services.interfaces.OnInfoHubItemClickListener;
import com.uae.tra_smart_services.interfaces.OperationStateManager;
import com.uae.tra_smart_services.rest.model.response.GetAnnouncementsResponseModel;
import com.uae.tra_smart_services.rest.request_listeners.AnnouncementsResponseListener;
import com.uae.tra_smart_services.rest.robo_requests.GetAnnouncementsRequest;
import com.uae.tra_smart_services.util.EndlessScrollListener;

import java.util.Locale;

/**
 * Created by ak-buffalo on 18.08.15.
 */
public class InfoHubAnnouncementsFragment extends BaseFragment
        implements OnInfoHubItemClickListener<GetAnnouncementsResponseModel.Announcement>,
        EndlessScrollListener.OnLoadMoreListener, SearchView.OnQueryTextListener, OperationStateManager, MenuItemCompat.OnActionExpandListener, View.OnClickListener, HexagonSwipeRefreshLayout.Listener, ViewTreeObserver.OnGlobalLayoutListener {

    private RecyclerView mList;
    private LinearLayoutManager mLayoutManager;
    private AnnouncementsAdapter mListAdapter;
    private ProgressBar pbLoading;
    private TextView tvNoResult;
    private EndlessScrollListener mEndlessScrollListener;
    private final BooleanHolder mIsAnnouncementsInLoading = new BooleanHolder();
    private static final int DEFAULT_OFFSET_ANNOUNCEMENTS = 10;
    private int mAnnouncementsPageNum;
    private AnnouncementsResponseListener mAnnouncementsResponseListener;
    private boolean mIsSearching;
    private boolean mIsAllAnnouncementsDownloaded;
    private SearchView svSearchTransaction;
    private HexagonSwipeRefreshLayout mHexagonSwipeRefreshLayout;
    private boolean mShouldHandle = true;
    private String mSearchPhrase = "";

    public static InfoHubAnnouncementsFragment newInstance() {
        return new InfoHubAnnouncementsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        initSearchView(menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        startFirstLoad();
        getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvNoResult = findView(R.id.tvNoResult_FIHA);
        mList = findView(R.id.rvList_FIHA);
        mHexagonSwipeRefreshLayout = findView(R.id.hsrlRefresher_FIHA);
        initAnnouncementsList();
    }

    private final void initAnnouncementsList(){
        mLayoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(mLayoutManager);
        mListAdapter = new AnnouncementsAdapter(getActivity(), this, false);
        mList.setAdapter(mListAdapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mAnnouncementsResponseListener = new AnnouncementsResponseListener(this, this, mListAdapter, mIsAnnouncementsInLoading, mIsAllAnnouncementsDownloaded, 1, null);
        mListAdapter.setOnItemClickListener(this);
        mList.addOnScrollListener(new EndlessScrollListener(mLayoutManager, this));
        tvNoResult.setOnClickListener(this);
        mHexagonSwipeRefreshLayout.registerListener(this);
    }

    private void initSearchView(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        svSearchTransaction = (SearchView) MenuItemCompat.getActionView(searchItem);
        svSearchTransaction.setOnQueryTextListener(this);
    }

    @Override
    public void onItemSelected(GetAnnouncementsResponseModel.Announcement item) {
        Bundle args = new Bundle();
        args.putParcelable(C.INFO_HUB_ANN_DATA, item);
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.flContainer_AH, InfoHubDetailsFragment.newInstance(args))
                .commit();
    }

    private void startFirstLoad() {
        mHexagonSwipeRefreshLayout.onLoadingStart();
        loadAnnouncementsPage(mAnnouncementsPageNum = 1);
    }

    private void loadAnnouncementsPage(final int _page) {
        mIsAnnouncementsInLoading.trueV();
        GetAnnouncementsRequest announcementsRequest = new GetAnnouncementsRequest(QueryAdapter.pageToOffset(_page, DEFAULT_OFFSET_ANNOUNCEMENTS), Locale.getDefault().getLanguage().toUpperCase());
        getSpiceManager().execute(announcementsRequest, mAnnouncementsResponseListener);
    }

    @Override
    public void onLoadMoreEvent() {
        if (mIsSearching) {
            mListAdapter.loadMoreSearchResults();
        } else if (!mIsAllAnnouncementsDownloaded && !mIsAnnouncementsInLoading.getV()) {
            loadAnnouncementsPage(++mAnnouncementsPageNum);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mIsSearching = true;
        mSearchPhrase = query;
        tvNoResult.setText(R.string.str_no_search_result);
        hideKeyboard(getView());
        mLayoutManager.scrollToPosition(0);
        mListAdapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mIsSearching = false;
        tvNoResult.setText(R.string.fragment_info_hub_no_pending_transactions);
        mListAdapter.showAnnouncements();
        if (mIsAllAnnouncementsDownloaded) {
            mListAdapter.stopLoading();
        } else {
            mListAdapter.startLoading();
        }
        return true;
    }

    @Override
    public final void showProgress() {
        mHexagonSwipeRefreshLayout.onLoadingStart();
    }

    @Override
    public final void showData() {
        mHexagonSwipeRefreshLayout.onLoadingFinished(true);
    }

    @Override
    public final void showEmptyView() {
        mHexagonSwipeRefreshLayout.onLoadingFinished(false);
    }

    @Override
    public void endLoading() { /* Not implemented */ }

    @Override
    protected int getTitle() {
        return R.string.str_info_hub_announcements;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_info_hub_announcements;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvNoResult_FIHA){
            onRefresh(mSearchPhrase);
        }
    }

    @Override
    public void onRefresh() {
        mHexagonSwipeRefreshLayout.onLoadingStart();
        loadAnnouncementsPage(mAnnouncementsPageNum = 1);
    }

    @Override
    public void onRefresh(String _contrains) {
        onQueryTextSubmit(_contrains);
    }

    public static class BooleanHolder {
        private boolean value = false;
        public void trueV() {
            value = true;
        }

        public void falseV() {
            value = false;
        }

        public boolean getV() {
            return value;
        }
    }
}