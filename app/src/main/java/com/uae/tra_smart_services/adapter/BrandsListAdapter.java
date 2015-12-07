package com.uae.tra_smart_services.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Space;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.BrandsListAdapter.ViewHolder;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.fragment.MobileBrandFragment.BrandItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobimaks on 11.08.2015.
 */
public class BrandsListAdapter extends Adapter<ViewHolder> implements Filterable {

    private final List<BrandItem> mData, mFilteredData;
    private final LayoutInflater mInflater;

    private OnBrandSelectListener mBrandSelectListener;
    private Filter mBrandFilter;

    public BrandsListAdapter(final Context _context) {
        this(_context, new ArrayList<BrandItem>());
    }

    public BrandsListAdapter(final Context _context, final List<BrandItem> _data) {
        mInflater = LayoutInflater.from(_context);
        mData = _data;
        mFilteredData = new ArrayList<>(_data);
    }

    public void setOnBrandSelectListener(OnBrandSelectListener _brandSelectListener) {
        mBrandSelectListener = _brandSelectListener;
    }

    private void setFilteredData(final List<BrandItem> _filteredData) {
        mFilteredData.clear();
        mFilteredData.addAll(_filteredData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFilteredData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup _parent, final int _viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.list_item_brand, _parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder _holder, final int _position) {
        _holder.setData(_position, mFilteredData.get(_position));
    }

    @Override
    public Filter getFilter() {
        if (mBrandFilter == null) {
            mBrandFilter = new BrandFilter(mData);
        }
        return mBrandFilter;
    }

    private class BrandFilter extends Filter {

        private final List<BrandItem> listData;

        public BrandFilter(List<BrandItem> _listData) {
            listData = new ArrayList<>(_listData);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.count = listData.size();
                results.values = listData;
            } else {
                List<BrandItem> filteredList = getFilteredList(constraint);
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        private List<BrandItem> getFilteredList(CharSequence _constraint) {
            List<BrandItem> serviceList = new ArrayList<>();
            for (int i = 0; i < listData.size(); i++) {
                BrandItem name = listData.get(i);
                if (name.mName.toLowerCase().contains(_constraint.toString().toLowerCase())) {
                    serviceList.add(name);
                }
            }
            return serviceList;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<BrandItem> filteredData = (List<BrandItem>) results.values;
            setFilteredData(filteredData);
        }
    }


    protected final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Space sStartOffset;
        private HexagonView hvBrandImage;
        private ImageView ivBrandLogo;

        protected ViewHolder(final View _view) {
            super(_view);
            sStartOffset = (Space) _view.findViewById(R.id.sStartOffset_LIB);
            hvBrandImage = (HexagonView) _view.findViewById(R.id.hvBrandImage_LIB);
            ivBrandLogo = (ImageView) _view.findViewById(R.id.ivBrandLogo_LIB);

            _view.setOnClickListener(this);
        }

        protected final void setData(int position, @NonNull BrandItem data) {
            sStartOffset.setVisibility(position % 2 == 0 ? View.GONE : View.INVISIBLE);
//            hvBrandImage.setHexagonSrcDrawable(data.mBrandImageRes);
            ivBrandLogo.setImageResource(data.mBrandLogoRes);
        }

        @Override
        public final void onClick(final View _view) {
            if (mBrandSelectListener != null) {
                final int position = getAdapterPosition();
                mBrandSelectListener.onBrandSelect(mFilteredData.get(position));
            }
        }
    }

    public interface OnBrandSelectListener {
        void onBrandSelect(final BrandItem _item);
    }
}