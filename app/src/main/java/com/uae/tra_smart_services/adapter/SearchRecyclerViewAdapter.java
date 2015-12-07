package com.uae.tra_smart_services.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.entities.SearchResult;
import com.uae.tra_smart_services.global.ListItemFilter;
import com.uae.tra_smart_services.global.SpannableWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ak-buffalo on 09.09.15.
 */
public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>
                                                                            implements Filterable{
    /** FIELDS */
    private SearchResult mDataSet;
    private Context mContext;
    private OnSearchResultItemClickListener listener;

    /** CONSTRUCTOR */
    public SearchRecyclerViewAdapter(final Context _context, final SearchResult _dataSet) {
        mDataSet = _dataSet;
        mContext = _context;
    }

    /** CALLBACKS */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_search_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mDataSet.getSearchResultItems().get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.getCount();
    }

    @Override
    public ListItemFilter getFilter() {
        return SearchResultFilter.getInstance(mDataSet.getSearchResultItems());
    }

    /** METHODS */
    public void setOnSearchResultItemClickListener(OnSearchResultItemClickListener _listener){
        listener = _listener;
    }

    public void clear(){
        if(getItemCount() != 0){
            mDataSet.clearAll();
        }
    }

    public void addFiltered(SearchResult.SearchResultItem filteredValue){
        mDataSet.addItem(filteredValue);
    }

    /** ENTITIES */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tvSearchResultItem_RSI);
            textView.setOnClickListener(this);
        }

        public void setData(final SearchResult.SearchResultItem searchResultItem) {
            textView.setText(searchResultItem.getSpannedText());
            textView.setTag(searchResultItem);
        }

        @Override
        public void onClick(View _view) {
            if(listener != null){
                listener.onSearchResultItemClicked((SearchResult.SearchResultItem) _view.getTag());
            }
        }
    }

    public static class SearchResultFilter extends ListItemFilter<SearchRecyclerViewAdapter, SearchResult.SearchResultItem> {

        private static ListItemFilter mInstance;

        private SearchResultFilter(List<SearchResult.SearchResultItem> originalList) {
            super(originalList);
        }
        public static ListItemFilter getInstance(List<SearchResult.SearchResultItem> originalList){
            if(mInstance == null){
                mInstance = new SearchResultFilter(originalList);
            }
            return mInstance;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<SearchResult.SearchResultItem> result = (ArrayList<SearchResult.SearchResultItem>) results.values;
            mAdapter.clear();
            for(SearchResult.SearchResultItem item : result){
                mAdapter.addFiltered(SpannableWrapper.makeSelectedTextBold(constraint, item));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public interface OnSearchResultItemClickListener{
        void onSearchResultItemClicked(SearchResult.SearchResultItem _item);
    }
}