package com.uae.tra_smart_services.adapter;

import android.app.Activity;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.TransactionsAdapter.ViewHolder;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.entities.NetworkErrorHandler;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.SpannableWrapper;
import com.uae.tra_smart_services.interfaces.OperationStateManager;
import com.uae.tra_smart_services.rest.RestClient;
import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.response.GetTransactionResponseModel;

import java.util.List;

import retrofit.RetrofitError;

public class TransactionsAdapter extends Adapter<ViewHolder> implements Filterable {

    private static final int VIEW_TYPE_TRANSACTION = 0;
    private static final int VIEW_TYPE_LOADER = 1;

    private final Activity mActivity;
    private final OperationStateManager mOperationStateManager;
    private final GetTransactionResponseModel.List mDataSet, mShowingData;

    private TransactionFilter mFilter;
    private boolean mIsShowingLoaderForData;
    private boolean mIsInSearchMode;
    private boolean mIsAllSearchResultDownloaded;
    private CharSequence mConstraint = "";
    private OnTransactionPressedListener mItemPressedListener;

    public TransactionsAdapter(final Activity _activity, final OperationStateManager _operationStateManager, OnTransactionPressedListener _itemPressedListener) {
        mActivity = _activity;
        mOperationStateManager = _operationStateManager;
        mIsShowingLoaderForData = true;
        mDataSet = new GetTransactionResponseModel.List();
        mShowingData = new GetTransactionResponseModel.List();
        mItemPressedListener = _itemPressedListener;
    }

    public void startLoading() {
        if (!mIsShowingLoaderForData) {
            mIsShowingLoaderForData = true;
            if (!mIsInSearchMode) {
                notifyDataSetChanged();
            }
        }
    }

    public void stopLoading() {
        if (mIsShowingLoaderForData) {
            mIsShowingLoaderForData = false;
            if (!mIsInSearchMode || mIsAllSearchResultDownloaded) {
                notifyItemRemoved(mShowingData.size());
            }
        } else if (mIsInSearchMode && mIsAllSearchResultDownloaded) {
            notifyItemRemoved(mShowingData.size());
        }
    }

    public boolean isEmpty() {
        return mDataSet.isEmpty();
    }

    public void clearData(){
        mDataSet.clear();
        mShowingData.clear();
        notifyDataSetChanged();
    }

    public void addAll(final List<GetTransactionResponseModel> _transactionResponses) {
//        mDataSet.clear();
        mDataSet.addAll(_transactionResponses);
        if (!mIsInSearchMode) {
            int oldSize = mShowingData.size();
//            mShowingData.clear();
            mShowingData.addAll(_transactionResponses);
            notifyItemRangeInserted(oldSize, _transactionResponses.size());
        }
    }

    public GetTransactionResponseModel.List getAllData(){
        return mDataSet;
    }

    @Override
    public Filter getFilter() {
        initSearchParams();
        mFilter = new TransactionFilter();
        return mFilter;
    }

    private void initSearchParams() {
        mShowingData.clear();
        mIsAllSearchResultDownloaded = false;
        mIsInSearchMode = true;
    }

    public final void loadMoreSearchResults() {
        mFilter.loadMoreSearchResults();
    }

    public final void showTransactions() {
        if (mDataSet.isEmpty()) {
            mOperationStateManager.showEmptyView();
        } else {
            mOperationStateManager.showData();
        }
        if (mFilter != null) {
            mFilter.reset();
        }
        mConstraint = "";
        mIsInSearchMode = false;
        mShowingData.clear();
        mShowingData.addAll(mDataSet);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mShowingData.size()) {
            return VIEW_TYPE_TRANSACTION;
        } else {
            return VIEW_TYPE_LOADER;
        }
    }

    public boolean isIsInSearchMode(){
        return mIsInSearchMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_LOADER:
                LoaderView loaderView = (LoaderView) LayoutInflater.from(parent.getContext()).inflate(R.layout.loader_view, null, true);
                return new ViewHolder(loaderView, true);
            case VIEW_TYPE_TRANSACTION:
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_info_hub, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < mShowingData.size()) {
            holder.setData(position, mShowingData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        final int progressBarCount;
        if (mIsInSearchMode) {
            progressBarCount = mIsAllSearchResultDownloaded ? 0 : 1;
        } else {
            progressBarCount = mIsShowingLoaderForData && mShowingData.size() != 0 ? 1 : 0;
        }
        return mShowingData.size() + progressBarCount;
    }

    private final class TransactionFilter extends Filter {

        private static final int DEFAULT_PAGE_SIZE = 10;
        private final TRAServicesAPI mTRAServicesAPI;
        private String mSearchQuery;
        private int mSearchResultPageNum;
        private RetrofitError mRetrofitError;
        private boolean mIsCurrentlyLoading;

        private TransactionFilter() {
            mTRAServicesAPI = RestClient.getInstance().getTRAServicesAPI();
            mSearchResultPageNum = 1;
        }

        public void reset(){
            mSearchResultPageNum = 1;
        }

        @UiThread
        public final void loadMoreSearchResults() {
            if (mIsCurrentlyLoading || mIsAllSearchResultDownloaded) {
                return;
            }
            mSearchResultPageNum++;
            filter(mSearchQuery);
        }

        @WorkerThread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mSearchQuery = String.valueOf(constraint);
            mIsCurrentlyLoading = true;

            final List<GetTransactionResponseModel> filteredList;
            final FilterResults filterResults = new FilterResults();
            try {
                filteredList = mTRAServicesAPI.searchTransactions(mSearchResultPageNum, DEFAULT_PAGE_SIZE, mSearchQuery);
                if (filteredList.isEmpty()) {
                    mIsAllSearchResultDownloaded = true;
                }
                filterResults.count = filteredList.size();
                filterResults.values = filteredList;
                return filterResults;
            } catch (RetrofitError _error) {
                mSearchResultPageNum--;
                mRetrofitError = _error;
                return null;
            }
        }

        @UiThread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mIsCurrentlyLoading = false;
            if (results == null) {
                tryHandleRetrofitError();
            } else if (results.count == 0) {
                handleNoResults();
            } else {
                showNewSearchResults(results, constraint);
            }
        }

        @UiThread
        private void tryHandleRetrofitError() {
            if (mRetrofitError != null) {
                String error = NetworkErrorHandler.processRetrofitError(mActivity, mRetrofitError);
                if (error != null) {
                    Toast.makeText(mActivity, error, C.TOAST_LENGTH).show();
                }
            }
        }

        @UiThread
        private void handleNoResults() {
            if (mShowingData.isEmpty()) {
                mOperationStateManager.showEmptyView();
            } else {
                stopLoading();
            }
        }

        @UiThread
        private void showNewSearchResults(FilterResults results, CharSequence _constraint) {
            mOperationStateManager.showData();
            mShowingData.addAll((GetTransactionResponseModel.List) results.values);
            notifyDataSetChanged();
            mConstraint = _constraint;
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private HexagonView hexagonView;
        private TextView title, description, date;
        private LoaderView loaderView;
        private Space sStartOffset;
        private boolean isProgress;

        public ViewHolder(View itemView) {
            super(itemView);
            sStartOffset = (Space) itemView.findViewById(R.id.sStartOffset_LIIH);
            hexagonView = (HexagonView) itemView.findViewById(R.id.hvIcon_LIIH);
            title = (TextView) itemView.findViewById(R.id.hvTitle_LIIH);
            description = (TextView) itemView.findViewById(R.id.hvDescr_LIIH);
            date = (TextView) itemView.findViewById(R.id.hvDate_LIIH);
        }

        public ViewHolder(View view, boolean _isProgress) {
            super(view);
            isProgress = _isProgress;
            loaderView = (LoaderView) view;
            loaderView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void setData(int _position, final GetTransactionResponseModel _model) {
            if (!isProgress) {
                sStartOffset.setVisibility(_position % 2 == 0 ? View.GONE : View.VISIBLE);
                //TODO - REMOVE RANDOMIZER
                final int[] icon_color = C.TRANSACTION_STATUS.get(_position % 3 == 0 ? "Waiting for Details" : _position % 2 == 0 ? _model.statusCode : "On Hold");
                hexagonView.setBorderColor(icon_color[1]);
                hexagonView.setHexagonSrcDrawable(icon_color[0]);
                if(mConstraint.length() != 0){
                    title.setText(SpannableWrapper.makeSelectedTextBold(mConstraint, _model.title));
                    description.setText(SpannableWrapper.makeSelectedTextBold(mConstraint, _model.description));
                    date.setText(SpannableWrapper.makeSelectedTextBold(mConstraint, _model.modifiedDatetime));
                } else {
                    title.setText(_model.title);
                    description.setText(_model.description);
                    date.setText(_model.modifiedDatetime);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemPressedListener.onTransactionPressed(icon_color, _model);
                    }
                });
            }
        }
    }

    public interface OnTransactionPressedListener {
        void onTransactionPressed(int[] icon_color, GetTransactionResponseModel _model);
    }
}