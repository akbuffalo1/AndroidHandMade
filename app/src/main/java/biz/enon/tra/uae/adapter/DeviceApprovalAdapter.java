package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.DeviceApprovalAdapter.ViewHolder;
import biz.enon.tra.uae.customviews.HexagonView;
import biz.enon.tra.uae.rest.model.response.SearchDeviceResponseModel;

/**
 * Created by mobimaks on 29.09.2015.
 */
public final class DeviceApprovalAdapter extends Adapter<ViewHolder> implements Filterable {

    private final LayoutInflater mInflater;
    private final List<SearchDeviceResponseModel> mData, mFilteredData;

    private DeviceFilter mDeviceFilter;

    public DeviceApprovalAdapter(final Context _context) {
        this(_context, new ArrayList<SearchDeviceResponseModel>());
    }

    public DeviceApprovalAdapter(final Context _context, final List<SearchDeviceResponseModel> _data) {
        mInflater = LayoutInflater.from(_context);
        mData = _data;
        mFilteredData = new ArrayList<>(mData);
    }

    @Override
    public final ViewHolder onCreateViewHolder(final ViewGroup _parent, final int _viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.list_item_device, _parent, false));
    }

    @Override
    public final void onBindViewHolder(final ViewHolder _holder, final int _position) {
        _holder.setData(_position, mFilteredData.get(_position));
    }

    private void setFilteredData(final List<SearchDeviceResponseModel> _filteredData) {
        mFilteredData.clear();
        mFilteredData.addAll(_filteredData);
        notifyDataSetChanged();
    }

    @Override
    public final int getItemCount() {
        return mFilteredData.size();
    }

    @Override
    public Filter getFilter() {
        if (mDeviceFilter == null) {
            mDeviceFilter = new DeviceFilter(mData);
        }
        return mDeviceFilter;
    }

    protected final class ViewHolder extends RecyclerView.ViewHolder {

        private Space sStartOffset;
        private HexagonView hvDeviceImage;
        private TextView tvTitle, tvDescription;

        public ViewHolder(final View _itemView) {
            super(_itemView);
            sStartOffset = (Space) _itemView.findViewById(R.id.sStartOffset_LID);
            hvDeviceImage = (HexagonView) _itemView.findViewById(R.id.hvDeviceImage_LID);
            tvTitle = (TextView) _itemView.findViewById(R.id.tvTitle_LID);
            tvDescription = (TextView) _itemView.findViewById(R.id.tvDescription_LID);
        }

        public final void setData(final int _position, final SearchDeviceResponseModel _data) {
            sStartOffset.setVisibility(_position % 2 == 0 ? View.GONE : View.INVISIBLE);
            tvTitle.setText(_data.manufacturer + " " + _data.marketingName);
            tvDescription.setText(_data.bands);

        }
    }

    private class DeviceFilter extends Filter{

        private final ArrayList<SearchDeviceResponseModel> listData;

        public DeviceFilter(List<SearchDeviceResponseModel> _listData) {
            listData = new ArrayList<>(_listData);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.count = listData.size();
                results.values = listData;
            } else {
                List<SearchDeviceResponseModel> filteredList = getFilteredList(constraint);
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        private List<SearchDeviceResponseModel> getFilteredList(CharSequence _constraint) {
            List<SearchDeviceResponseModel> serviceList = new ArrayList<>();
            for (int i = 0; i < listData.size(); i++) {
                final SearchDeviceResponseModel responseModel = listData.get(i);
                final String name = responseModel.manufacturer + " " + responseModel.marketingName;
                if (name.toLowerCase().contains(_constraint.toString().toLowerCase())) {
                    serviceList.add(responseModel);
                }
            }
            return serviceList;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<SearchDeviceResponseModel> filteredData = (List<SearchDeviceResponseModel>) results.values;
            setFilteredData(filteredData);
        }
    }

}
