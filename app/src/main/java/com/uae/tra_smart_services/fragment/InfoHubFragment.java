package com.uae.tra_smart_services.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.AnnouncementsAdapter;
import com.uae.tra_smart_services.adapter.TransactionsAdapter;
import com.uae.tra_smart_services.adapter.TransactionsAdapter.OnTransactionPressedListener;
import com.uae.tra_smart_services.customviews.HexagonSwipeRefreshLayout;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.fragment.InfoHubAnnouncementsFragment.BooleanHolder;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.QueryAdapter;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.interfaces.OnInfoHubItemClickListener;
import com.uae.tra_smart_services.interfaces.OperationStateManager;
import com.uae.tra_smart_services.rest.model.response.GetAnnouncementsResponseModel;
import com.uae.tra_smart_services.rest.model.response.GetTransactionResponseModel;
import com.uae.tra_smart_services.rest.request_listeners.AnnouncementsResponseListener;
import com.uae.tra_smart_services.rest.robo_requests.GetAnnouncementsRequest;
import com.uae.tra_smart_services.rest.robo_requests.GetTransactionsRequest;
import com.uae.tra_smart_services.util.EndlessScrollListener;
import com.uae.tra_smart_services.util.EndlessScrollListener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ak-buffalo on 19.08.15.
 */
public final class InfoHubFragment extends BaseFragment
        implements OnLoadMoreListener, OnQueryTextListener, OnActionExpandListener, View.OnClickListener, HexagonSwipeRefreshLayout.Listener, OnTransactionPressedListener/*, ViewTreeObserver.OnGlobalLayoutListener*/ {

    private static final String KEY_TRANSACTIONS_REQUEST = "TRANSACTIONS_REQUEST";
    private static final int DEFAULT_PAGE_SIZE_TRANSACTIONS = 10;

    private static final String KEY_TRANSACTIONS_MODEL = "TRANSACTIONS_MODEL";
    private static final String KEY_ANNOUNCEMENTS_MODEL = "ANNOUNCEMENTS_MODEL";

    private int mTransactionPageNum;
    private boolean mIsSearching;
    private boolean mIsAllTransactionDownloaded;

    private LoaderView lvAnnouncementsLoader;
    private TextView tvSeeMoreAnnouncements;
    private RecyclerView mAnnouncementsListPreview;
    private RecyclerView mTransactionsList;
    private LinearLayoutManager mAnnouncementsLayoutManager;
    private TextView tvNoTransactions, tvNoAnnouncements;
    private SearchView svSearchTransaction;

    private LinearLayoutManager mTransactionsLayoutManager;
    private AnnouncementsAdapter mAnnouncementsListAdapter;
    private TransactionsAdapter mTransactionsListAdapter;
    private TransactionsResponseListener mTransactionsListener;
    private AnnouncementsResponseListener mAnnouncementsResponseListener;
    private EndlessScrollListener mEndlessScrollListener;
    private OnTransactionPressedListener mItemPressedListener;
    private boolean mIsTransactionsInLoading;
    private BooleanHolder mIsAnnouncementsInLoading = new BooleanHolder();
    private HexagonSwipeRefreshLayout mHexagonSwipeRefreshLayout;
    private GetTransactionsRequest transactionsRequest;
    private int loadedCount = 0;
    private String mSearchPhrase = "";

    private ArrayList<GetTransactionResponseModel> mTransactionsModel = new ArrayList<>();
    private ArrayList<GetAnnouncementsResponseModel.Announcement> mAnnouncementsModel = new ArrayList<>();

    public static InfoHubFragment newInstance() {
        return new InfoHubFragment();
    }

    private final OperationStateManager mAnnouncementsOperationStateManager = new OperationStateManager() {

        @Override
        public final void showProgress() {
            lvAnnouncementsLoader.setVisibility(View.VISIBLE);
            lvAnnouncementsLoader.startProcessing();
            mAnnouncementsListPreview.setVisibility(View.INVISIBLE);
            tvNoAnnouncements.setVisibility(View.INVISIBLE);
            tvSeeMoreAnnouncements.setVisibility(View.GONE);
        }

        @Override
        public final void showData() {
            lvAnnouncementsLoader.stopProcessing();
            lvAnnouncementsLoader.setVisibility(View.INVISIBLE);
            mAnnouncementsListPreview.setVisibility(View.VISIBLE);
            tvNoAnnouncements.setVisibility(View.INVISIBLE);
            tvSeeMoreAnnouncements.setVisibility(View.VISIBLE);
        }

        @Override
        public final void showEmptyView() {
            lvAnnouncementsLoader.stopProcessing();
            lvAnnouncementsLoader.setVisibility(View.INVISIBLE);
            mAnnouncementsListPreview.setVisibility(View.INVISIBLE);
            tvNoAnnouncements.setVisibility(View.VISIBLE);
            tvSeeMoreAnnouncements.setVisibility(View.GONE);
        }

        @Override
        public void endLoading() {
            loadedCount++;
            if (loadedCount >= 2) {
                loadedCount = 0;
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        if (isAdded()) {
                            getFragmentManager().popBackStack();
                        }
                    }
                });
            }
        }
    };

    private final OperationStateManager mTransactionsOperationStateManager = new OperationStateManager() {

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
            tvSeeMoreAnnouncements.setVisibility(View.GONE);
        }

        @Override
        public void endLoading() {
            loadedCount++;
            if (loadedCount >= 2) {
                loadedCount = 0;
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        if (isAdded()) {
                            getFragmentManager().popBackStack();
                        }
                    }
                });
            }
        }
    };

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        mItemPressedListener = (OnTransactionPressedListener) _activity;
    }

    @Override
    protected final void initViews() {
        super.initViews();
        lvAnnouncementsLoader = findView(R.id.lvLoadingAnnoncements_FIH);
        lvAnnouncementsLoader.init(Color.parseColor("#ffffff"));
        tvNoAnnouncements = findView(R.id.tvNoAnnouncements_FIH);
        tvNoTransactions = findView(R.id.tvNoTransactions_FIH);
        tvSeeMoreAnnouncements = findView(R.id.tvSeeMorebAnn_FIH);
        mAnnouncementsListPreview = findView(R.id.rvInfoHubListPrev_FIH);
        mHexagonSwipeRefreshLayout = findView(R.id.hsrlTransactionRefresher_FIH);
        mTransactionsList = findView(R.id.rvTransactionsList_FIH);
        initAnnouncementsListPreview();
        initTransactionsList();
    }

    private void initAnnouncementsListPreview() {
//        mAnnouncementsListPreview.setHasFixedSize(true);
        mAnnouncementsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mAnnouncementsListPreview.setLayoutManager(mAnnouncementsLayoutManager);
        mAnnouncementsListAdapter = new AnnouncementsAdapter(getActivity(), mAnnouncementsOperationStateManager, true);
        mAnnouncementsListPreview.setAdapter(mAnnouncementsListAdapter);
    }

    private void initTransactionsList() {
        mTransactionsLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionsList.setLayoutManager(mTransactionsLayoutManager);
        mTransactionsListAdapter = new TransactionsAdapter(getActivity(), mTransactionsOperationStateManager, this);
        mTransactionsList.setAdapter(mTransactionsListAdapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mTransactionsListener = new TransactionsResponseListener();
        mAnnouncementsResponseListener =
                new AnnouncementsResponseListener(
                        this, mAnnouncementsOperationStateManager, mAnnouncementsListAdapter,
                        mIsAnnouncementsInLoading, false, mTransactionPageNum, mAnnouncementsModel);
        mEndlessScrollListener = new EndlessScrollListener(mTransactionsLayoutManager, this);
        mTransactionsList.addOnScrollListener(mEndlessScrollListener);
        tvNoTransactions.setOnClickListener(this);
        tvNoAnnouncements.setOnClickListener(this);
        mHexagonSwipeRefreshLayout.registerListener(this);
        tvSeeMoreAnnouncements.setOnClickListener(this);
        mAnnouncementsListAdapter.setOnItemClickListener(new OnInfoHubItemClickListener<GetAnnouncementsResponseModel.Announcement>() {
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
        });
    }

    private void startFirstLoad() {
        if (isAdded()) {
            loaderOverlayCustomShow(getString(R.string.str_loading), null, false);
            loadTransactionPage(mTransactionPageNum = 1, false);
            loadAnnouncementsPage(1, false);
        }
    }

    private void loadAnnouncementsPage(final int _page, boolean _showLoader) {
        mIsAnnouncementsInLoading.trueV();
        GetAnnouncementsRequest announcementsRequest = new GetAnnouncementsRequest(QueryAdapter.pageToOffset(_page, 3), Locale.getDefault().getLanguage().toUpperCase());
        if(_showLoader){
            mAnnouncementsOperationStateManager.showProgress();
        }
        getSpiceManager().execute(announcementsRequest, mAnnouncementsResponseListener);
    }

    private void loadTransactionPage(final int _page, boolean _showLoader) {
        mIsTransactionsInLoading = true;
        transactionsRequest = new GetTransactionsRequest(_page, DEFAULT_PAGE_SIZE_TRANSACTIONS);
        if(_showLoader){
            mTransactionsOperationStateManager.showProgress();
        }
        getSpiceManager().execute(transactionsRequest, mTransactionsListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_favorites, menu);
        initSearchView(menu);
    }

    private void initSearchView(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        svSearchTransaction = (SearchView) MenuItemCompat.getActionView(searchItem);
        svSearchTransaction.setOnQueryTextListener(this);
    }

    @Override
    public void onClick(View _view) {
        switch (_view.getId()) {
            case R.id.tvSeeMorebAnn_FIH:
                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.flContainer_AH, InfoHubAnnouncementsFragment.newInstance())
                        .commit();
                break;
            case R.id.tvNoTransactions_FIH:
                if (mTransactionsListAdapter.isIsInSearchMode() && !mSearchPhrase.isEmpty()) {
                    onRefresh(mSearchPhrase);
                } else {
                    loadTransactionPage(mTransactionPageNum = 1, true);
                }
                break;
            case R.id.tvNoAnnouncements_FIH:
                loadAnnouncementsPage(1, true);
                break;
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
        tvNoTransactions.setText(R.string.str_no_search_result);
        hideKeyboard(getView());
        mTransactionsLayoutManager.scrollToPosition(0);
        mTransactionsListAdapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mIsSearching = false;
        tvNoTransactions.setText(R.string.fragment_info_hub_no_pending_transactions);
        mTransactionsListAdapter.showTransactions();
        if (mIsAllTransactionDownloaded) {
            mTransactionsListAdapter.stopLoading();
        } else {
            mTransactionsListAdapter.startLoading();
        }
        return true;
    }

    @Override
    public final void onLoadMoreEvent() {
        if (mIsSearching) {
            mTransactionsListAdapter.loadMoreSearchResults();
        } else if (!mIsAllTransactionDownloaded && !mIsTransactionsInLoading) {
            loadTransactionPage(++mTransactionPageNum, false);
        }
    }

    @Override
    public void onRefresh() {
        mHexagonSwipeRefreshLayout.onLoadingStart();
        loadTransactionPage(mTransactionPageNum = 1, true);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mTransactionsModel = savedInstanceState.getParcelableArrayList(KEY_TRANSACTIONS_MODEL);
            mAnnouncementsModel = savedInstanceState.getParcelableArrayList(KEY_ANNOUNCEMENTS_MODEL);
        }
        if (mTransactionsModel.size() == 0 && mAnnouncementsModel.size() == 0) {
            startFirstLoad();
            return;
        } else if (mTransactionsModel.size() == 0) {
            loadTransactionPage(mTransactionPageNum = 1, true);
            mAnnouncementsListAdapter.addAll(mAnnouncementsModel);
            mAnnouncementsOperationStateManager.showData();
        } else if (mAnnouncementsModel.size() == 0){
            loadAnnouncementsPage(1, true);
            mTransactionsListAdapter.addAll(mTransactionsModel);
            mTransactionsOperationStateManager.showData();
        } else {
            mAnnouncementsListAdapter.addAll(mAnnouncementsModel);
            mAnnouncementsOperationStateManager.showData();
            mTransactionsListAdapter.addAll(mTransactionsModel);
            mTransactionsOperationStateManager.showData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TRANSACTIONS_MODEL, mTransactionsModel);
        outState.putParcelableArrayList(KEY_ANNOUNCEMENTS_MODEL, mAnnouncementsModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh(String _contrains) {
        onQueryTextSubmit(_contrains);
    }

    @Override
    public void onTransactionPressed(int[] icon_color, GetTransactionResponseModel _model) {
        mIsSearching = false;
        if (mItemPressedListener != null) {
            mItemPressedListener.onTransactionPressed(icon_color, _model);
        }
    }

    private final class TransactionsResponseListener implements RequestListener<GetTransactionResponseModel.List> {
        @Override
        public final void onRequestSuccess(GetTransactionResponseModel.List result) {
            mIsTransactionsInLoading = false;
            if (isAdded()) {
                if (result != null) {
                    if (mTransactionsModel != null) mTransactionsModel.addAll(result);
                    mIsAllTransactionDownloaded = result.isEmpty();
                    if (mIsAllTransactionDownloaded) {
                        handleNoResult();
                    } else {
                        mTransactionsOperationStateManager.showData();
                        if (mTransactionPageNum == 1) {
                            mTransactionsListAdapter.clearData();
                        }
                        mTransactionsListAdapter.addAll(result);
                    }
                } else {
                    mTransactionPageNum--;
                }
                mTransactionsOperationStateManager.endLoading();
            }
        }

        private void handleNoResult() {
            if (mTransactionsListAdapter.isEmpty()) {
                mTransactionsOperationStateManager.showEmptyView();
            } else {
                mTransactionsListAdapter.stopLoading();
            }
        }

        @Override
        public final void onRequestFailure(SpiceException spiceException) {
            mTransactionsOperationStateManager.endLoading();
            mIsTransactionsInLoading = false;
            mTransactionPageNum--;
            handleNoResult();
            processError(spiceException);
            if (isAdded()) {
                mTransactionsOperationStateManager.endLoading();
            }
        }
    }

    @Override
    protected final int getTitle() {
        return R.string.str_info_hub_title;
    }

    @Override
    protected final int getLayoutResource() {
        return R.layout.fragment_info_hub;
    }
}