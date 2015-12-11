package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.HexagonView;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.global.SpannableWrapper;

/**
 * Created by mobimaks on 18.08.2015.
 */
public class FavoritesAdapter extends Adapter<ViewHolder> implements Filterable {

    @IntDef({HEADER_ITEM, SIMPLE_ITEM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ItemType {
    }

    private static final int HEADER_ITEM = 0;
    private static final int SIMPLE_ITEM = 1;

    private final LayoutInflater mInflater;
    private final List<Service> mAllData, mShowingData;
    private final int mBackgroundColor;
    private final Context mContext;

    private OnFavoriteClickListener mFavoriteClickListener;
    private Filter mServiceFilter;
    private boolean mIsAddBtnVisible;
    private boolean mIsDataFilterEnable;
    private boolean mIsOddOpaque;
    private CharSequence mConstraint = "";

    public FavoritesAdapter(final Context _context) {
        this(_context, new ArrayList<Service>());
    }

    public FavoritesAdapter(final Context _context, final @NonNull List<Service> _data) {
        mContext = _context;
        mInflater = LayoutInflater.from(_context);
        mAllData = _data;
        mShowingData = new ArrayList<>(_data);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = _context.getTheme();
        theme.resolveAttribute(R.attr.shadowBackgroundColor, typedValue, true);
        mBackgroundColor = typedValue.data;
    }

    public final List<Service> getAllData() {
        return mAllData;
    }

    public final void setData(List<Service> _data) {
        mAllData.clear();
        mAllData.addAll(_data);
        invalidateFilter();
        showData(mAllData);
    }

    private void showData(final List<Service> _data) {
        mShowingData.clear();
        mShowingData.addAll(_data);
        notifyDataSetChanged();
    }

    public final boolean isNoVisibleItems() {
        return mShowingData.isEmpty();
    }

    public final boolean isEmpty() {
        return mAllData.isEmpty();
    }

    public void setAddButtonVisibility(final boolean _isVisible) {
        if (mIsAddBtnVisible == _isVisible) {
            return;
        }

        mIsAddBtnVisible = _isVisible;
        if (mIsAddBtnVisible) {
            notifyItemInserted(0);
        } else {
            notifyItemRemoved(0);
        }
    }

    public int getHeaderCount() {
        return mIsAddBtnVisible && !mIsDataFilterEnable ? 1 : 0;
    }

    public final void addData(final List<Service> _items) {
        mAllData.addAll(_items);
        invalidateFilter();
        showData(mAllData);
    }

    public final void removeItem(final int _position) {
        final Service service = mShowingData.get(_position - getHeaderCount());
        mAllData.remove(service);
        mShowingData.remove(service);
        mIsOddOpaque = !mIsOddOpaque;
        notifyItemRemoved(_position);
        invalidateFilter();
    }

    private void invalidateFilter() {
        mServiceFilter = null;
        mIsDataFilterEnable = false;
    }

    @Override
    public Filter getFilter() {
        if (mServiceFilter == null) {
            mServiceFilter = new SearchFilter(mAllData);
        }
        return mServiceFilter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SIMPLE_ITEM) {
            return new ItemViewHolder(mInflater.inflate(R.layout.list_item_favorite, parent, false));
        } else {
            return new HeaderViewHolder(mInflater.inflate(R.layout.layout_add_service, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == SIMPLE_ITEM) {
            ((ItemViewHolder) holder).setData(mShowingData.get(position - getHeaderCount()), position - getHeaderCount());
        }
    }

    @ItemType
    @Override
    public int getItemViewType(int position) {
        return getHeaderCount() != 0 && position == 0 ? HEADER_ITEM : SIMPLE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mShowingData.size() + getHeaderCount();
    }

    public void setFavoriteClickListener(OnFavoriteClickListener _favoriteClickListener) {
        mFavoriteClickListener = _favoriteClickListener;
    }

    public Service getItem(int _position) {
        return mShowingData.get(_position - getHeaderCount());
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
                List<Service> filteredData = getFilteredList(constraint);
                results.count = filteredData.size();
                results.values = filteredData;
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
            mIsDataFilterEnable = filteredData.size() != listData.size();
            mConstraint = constraint;
            showData(filteredData);
        }
    }

    protected class ItemViewHolder extends ViewHolder implements OnClickListener, OnLongClickListener, OnTouchListener {

        private final int LONG_CLICK_DELAY = 150;

        private final View rootView;
        private final View rlItemContainer;
        private final HexagonView hvIcon;
        private final TextView tvTitle, tvServiceInfo, tvRemove;
        private LongClickRunnable mLongClickRunnable;

        public ItemViewHolder(final View _itemView) {
            super(_itemView);
            rootView = _itemView;
            rlItemContainer = _itemView.findViewById(R.id.rlItemContainer_LIF);
            hvIcon = (HexagonView) rlItemContainer.findViewById(R.id.hvIcon_LIF);
            tvTitle = (TextView) rlItemContainer.findViewById(R.id.tvTitle_LIF);
            tvServiceInfo = (TextView) rlItemContainer.findViewById(R.id.tvServiceInfo_LIF);
            tvRemove = (TextView) rlItemContainer.findViewById(R.id.tvRemove_LIF);

            rlItemContainer.setOnClickListener(this);
            tvServiceInfo.setOnClickListener(this);
            tvRemove.setOnTouchListener(this);
        }

        public final void setData(final Service _item, final int _position) {
            rootView.setVisibility(View.VISIBLE);
            if (mIsOddOpaque) {
                rlItemContainer.setBackgroundColor(_position % 2 == 0 ? Color.TRANSPARENT : mBackgroundColor);
            } else {
                rlItemContainer.setBackgroundColor(_position % 2 == 0 ? mBackgroundColor : Color.TRANSPARENT);
            }
            tvTitle.setText(
                (mConstraint.length() != 0)
                    ? SpannableWrapper.makeSelectedTextBold(mConstraint, mContext.getString(_item.getTitleRes()))
                        : mContext.getString(_item.getTitleRes())
            );
            hvIcon.setHexagonSrcDrawable(_item.getDrawableRes());
        }

        @Override
        public void onClick(View v) {
            if (mFavoriteClickListener != null) {
                switch (v.getId()) {
                    case R.id.rlItemContainer_LIF:
                        mFavoriteClickListener.onItemClick(getAdapterPosition());
                        break;
                    case R.id.tvServiceInfo_LIF:
                        mFavoriteClickListener.onServiceInfoClick(getAdapterPosition());
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mFavoriteClickListener != null) {
                mFavoriteClickListener.onRemoveLongClick(tvRemove, getAdapterPosition());
            }
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLongClickRunnable = new LongClickRunnable();
                    v.postDelayed(mLongClickRunnable, LONG_CLICK_DELAY);
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    mLongClickRunnable.setTouchLoss(true);
                    return false;
                case MotionEvent.ACTION_UP:
                    mLongClickRunnable.setTouchLoss(true);
                    return false;
                default:
                    return true;
            }
        }

        private final class LongClickRunnable implements Runnable {

            private boolean mIsTouchLoss;

            public void setTouchLoss(boolean _isTouchLoss) {
                mIsTouchLoss = _isTouchLoss;
            }

            @Override
            public void run() {
                if (!mIsTouchLoss && mFavoriteClickListener != null) {
                    tvRemove.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    mFavoriteClickListener.onRemoveLongClick(tvRemove, getAdapterPosition());
                }
            }
        }

    }

    protected class HeaderViewHolder extends ViewHolder implements OnClickListener {

        private HexagonView hvIcon;

        public HeaderViewHolder(final View _itemView) {
            super(_itemView);
            hvIcon = (HexagonView) _itemView.findViewById(R.id.hvIcon_LAS);
            _itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View _view) {
            if (mFavoriteClickListener != null) {
                mFavoriteClickListener.onAddServiceClick();
            }
        }
    }

    public interface OnFavoriteClickListener {
        void onAddServiceClick();

        void onItemClick(final int _position);

        void onServiceInfoClick(final int _position);

        void onRemoveLongClick(final View _view, final int _position);
    }

}
