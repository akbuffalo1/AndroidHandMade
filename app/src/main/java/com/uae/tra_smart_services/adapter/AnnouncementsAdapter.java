package com.uae.tra_smart_services.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.AnnouncementsAdapter.ViewHolder;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.entities.HexagonViewTarget;
import com.uae.tra_smart_services.entities.NetworkErrorHandler;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.interfaces.OnInfoHubItemClickListener;
import com.uae.tra_smart_services.interfaces.OperationStateManager;
import com.uae.tra_smart_services.rest.RestClient;
import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.response.GetAnnouncementsResponseModel;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
/**
 * Created by ak-buffalo on 23.10.15.
 */
public class AnnouncementsAdapter extends Adapter<ViewHolder> implements Filterable {

    private static final int VIEW_TYPE_ANNOUNCEMENTS = 0;
    private static final int VIEW_TYPE_LOADER = 1;

    private final Activity mActivity;
    private final OperationStateManager mOperationStateManager;
    private final List<GetAnnouncementsResponseModel.Announcement> mDataSet, mShowingData;

    private TransactionFilter mFilter;
    private boolean mIsShowingLoaderForData;
    private boolean mIsInSearchMode;
    private boolean mIsAllSearchResultDownloaded;
    private boolean mIsPreview;

    private OnInfoHubItemClickListener onItemClickListener;

    public AnnouncementsAdapter(final Activity _activity, final OperationStateManager _operationStateManager, boolean isPreview) {
        mActivity = _activity;
        mOperationStateManager = _operationStateManager;
        mIsShowingLoaderForData = true;
        mDataSet = new ArrayList<>();
        mShowingData = new ArrayList<>();
        mIsPreview = isPreview;
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

    public void addAll(final List<GetAnnouncementsResponseModel.Announcement> _announcementsResponses) {
        mDataSet.addAll(_announcementsResponses);
        if (!mIsInSearchMode) {
            int oldSize = mShowingData.size();
            mShowingData.addAll(_announcementsResponses);
            notifyItemRangeInserted(oldSize, _announcementsResponses.size());
        }
    }

    public final void setOnItemClickListener(final OnInfoHubItemClickListener _onItemClickListener) {
        onItemClickListener = _onItemClickListener;
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

    public final void showAnnouncements() {
        if (mDataSet.isEmpty()) {
            mOperationStateManager.showEmptyView();
        } else {
            mOperationStateManager.showData();
        }
        mFilter = null;
        mIsInSearchMode = false;
        mShowingData.clear();
        mShowingData.addAll(mDataSet);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mShowingData.size()) {
            return VIEW_TYPE_ANNOUNCEMENTS;
        } else {
            return VIEW_TYPE_LOADER;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LoaderView loaderView = (LoaderView) layoutInflater.inflate(R.layout.loader_view, null, true);
        if(viewType == VIEW_TYPE_LOADER){
            return new ViewHolder(loaderView, true);
        } else {
            final View view;
            if (mIsPreview){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_info_hub_second, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_info_hub, parent, false);
            }
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
            progressBarCount = mIsShowingLoaderForData ? 1 : 0;
        }
        return mShowingData.size() + (!mIsPreview ? progressBarCount : 0);
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

            final List<GetAnnouncementsResponseModel.Announcement> filteredList;
            final FilterResults filterResults = new FilterResults();
            try {
                filteredList = mTRAServicesAPI.searchAnnouncements(mSearchResultPageNum, DEFAULT_PAGE_SIZE, mSearchQuery).announcements;
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
                showNewSearchResults(results);
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
        private void showNewSearchResults(FilterResults results) {
            mOperationStateManager.showData();
            mShowingData.addAll((ArrayList<GetAnnouncementsResponseModel.Announcement>) results.values);
            notifyDataSetChanged();
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private View container;
        private HexagonView hexagonView;
        private TextView title, description, date;
        private LoaderView progressBar;
        private Space sStartOffset;
        private boolean isProgress;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            if(mIsPreview){
                sStartOffset = (Space) itemView.findViewById(R.id.sStartOffset_LIIHS);
                hexagonView = (HexagonView) itemView.findViewById(R.id.hvIcon_LIIHS);
                title = (TextView) itemView.findViewById(R.id.hvTitle_LIIHS);
                description = (TextView) itemView.findViewById(R.id.hvDescr_LIIHS);
                date = (TextView) itemView.findViewById(R.id.hvDate_LIIHS);
            } else {
                sStartOffset = (Space) itemView.findViewById(R.id.sStartOffset_LIIH);
                hexagonView = (HexagonView) itemView.findViewById(R.id.hvIcon_LIIH);
                title = (TextView) itemView.findViewById(R.id.hvTitle_LIIH);
                description = (TextView) itemView.findViewById(R.id.hvDescr_LIIH);
                date = (TextView) itemView.findViewById(R.id.hvDate_LIIH);
            }
        }

        public ViewHolder(View view, boolean _isProgress) {
            super(view);
            isProgress = _isProgress;
            progressBar = (LoaderView) view;
            progressBar.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            progressBar.requestLayout();
        }

        public void setData(int _position, final GetAnnouncementsResponseModel.Announcement _model) {
            if (!isProgress) {
                sStartOffset.setVisibility(_position % 2 == 0 ? View.GONE : View.VISIBLE);
                Picasso.with(mActivity).load(_model.image).into(new HexagonViewTarget(hexagonView));
                title.setText(_model.title);
                description.setText(_model.description);
                date.setText(_model.createdAt);
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View _view) {
                        onItemClickListener.onItemSelected(_model);
                    }
                });
            }
        }
    }
}