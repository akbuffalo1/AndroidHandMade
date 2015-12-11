package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.AddFavoritesAdapter.ViewHolder;
import biz.enon.tra.uae.customviews.HexagonView;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.global.SpannableWrapper;

/**
 * Created by mobimaks on 18.08.2015.
 */
public class AddFavoritesAdapter extends Adapter<ViewHolder> implements Filterable {

    private final LayoutInflater mInflater;
    private final List<Service> mAllData, mShowingData;
    private final SparseBooleanArray mSelectedItems;
    private final int mBackgroundColor;
    private final Context mContext;
    private SearchFilter mSearchFilter;

    private OnItemClickListener mItemClickListener;
    private CharSequence mConstraint = "";

    public AddFavoritesAdapter(final Context _context) {
        this(_context, new ArrayList<Service>());
    }

    public AddFavoritesAdapter(final Context _context, final @NonNull List<Service> _data) {
        mContext = _context;
        mInflater = LayoutInflater.from(_context);
        mAllData = _data;
        mShowingData = new ArrayList<>(_data);
        mSelectedItems = new SparseBooleanArray();

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = _context.getTheme();
        theme.resolveAttribute(R.attr.shadowBackgroundColor, typedValue, true);
        mBackgroundColor = typedValue.data;
    }

    public final SparseBooleanArray getSelectedItems() {
        return mSelectedItems;
    }

    public final void setData(final List<Service> _allData) {
        mAllData.clear();
        mAllData.addAll(_allData);
        notifyDataSetChanged();
    }

    private void showData(final List<Service> _data) {
        mShowingData.clear();
        mShowingData.addAll(_data);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (mSearchFilter == null) {
            mSearchFilter = new SearchFilter(mAllData);
        }
        return mSearchFilter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.list_item_add_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mShowingData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mShowingData.size();
    }

    public final void setItemClickListener(OnItemClickListener _itemClickListener) {
        mItemClickListener = _itemClickListener;
    }

    private class SearchFilter extends Filter {

        private final List<Service> listData;

        public SearchFilter(List<Service> _listData) {
            listData = new ArrayList<>(_listData);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.count = listData.size();
                results.values = listData;
            } else {
                List<Service> filteredList = getFilteredList(constraint);
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        private List<Service> getFilteredList(CharSequence _constraint) {
            List<Service> serviceList = new ArrayList<>();
            for (int i = 0; i < listData.size(); i++) {
                Service favoriteItem = listData.get(i);
                if (favoriteItem.getTitle(mContext).toLowerCase().contains(_constraint.toString().toLowerCase())) {
                    serviceList.add(favoriteItem);
                }
            }
            return serviceList;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Service> filteredData = (List<Service>) results.values;
            mConstraint = constraint;
            showData(filteredData);
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        private final View vContainer;
        private final HexagonView hvIcon;
        private final CheckBox cbSelection;
        private final TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            vContainer = itemView;
            hvIcon = (HexagonView) itemView.findViewById(R.id.hvIcon_LIAF);
            cbSelection = (CheckBox) itemView.findViewById(R.id.cbSelection_LIAF);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle_LIAF);
            itemView.setOnClickListener(this);
        }

        public final void setData(final Service _item, final int _position) {
            vContainer.setBackgroundColor(_position % 2 == 0 ? Color.TRANSPARENT : mBackgroundColor);
            cbSelection.setChecked(mSelectedItems.get(mAllData.indexOf(mShowingData.get(_position))));
            tvTitle.setText(
                    (mConstraint.length() != 0)
                            ? SpannableWrapper.makeSelectedTextBold(mConstraint, mContext.getString(_item.getTitleRes()))
                            : mContext.getString(_item.getTitleRes())
            );
            hvIcon.setHexagonSrcDrawable(_item.getDrawableRes());
        }

        @Override
        public void onClick(View v) {
            final int adapterPosition = getAdapterPosition();
            final int position = mAllData.indexOf(mShowingData.get(adapterPosition));
            final boolean isAlreadySelected = mSelectedItems.get(position);
            mSelectedItems.put(position, !isAlreadySelected);
            notifyItemChanged(adapterPosition);
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(mAllData.get(position), !isAlreadySelected);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Service _item, boolean isSelected);
    }
}
