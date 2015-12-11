package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.global.Service;

/**
 * Created by mobimaks on 10.08.2015.
 */
public class ServiceListAdapter extends BaseAdapter<Service> implements Filterable {

    private Filter mServiceFilter;
    private final Context mContext;
    private boolean mIsDataChanged = true;

    public ServiceListAdapter(final Context _context) {
        super(_context);
        mContext = _context;
    }

    public ServiceListAdapter(final Context _context, final List<Service> _data) {
        super(_context, _data);
        mContext = _context;
    }

    @Override
    public void addData(final List<Service> _data) {
        super.addData(_data);
        mIsDataChanged = true;
    }

    @Override
    public void clearData() {
        super.clearData();
        mIsDataChanged = true;
    }

    private void setFilteredData(final List<Service> _filteredData) {
        getData().clear();
        getData().addAll(_filteredData);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (mServiceFilter == null) {
            mServiceFilter = new ServiceFilter(getData());
            mIsDataChanged = false;
        }
        return mServiceFilter;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.list_item_service_list;
    }

    @Override
    protected BaseViewHolder<Service> getViewHolder(View view) {
        return new ViewHolder(view);
    }

    private class ServiceFilter extends Filter {

        private final List<Service> listData;

        public ServiceFilter(List<Service> _listData) {
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
                Service service = listData.get(i);
                if (service.getTitle(mContext).toLowerCase().contains(_constraint.toString().toLowerCase())) {
                    serviceList.add(service);
                }
            }
            return serviceList;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Service> filteredData = (List<Service>) results.values;
            setFilteredData(filteredData);
        }
    }

    private static class ViewHolder extends BaseViewHolder<Service> {

        private TextView tvServiceName;

        protected ViewHolder(View view) {
            super(view);
        }

        @Override
        protected void initViews(View view) {
            tvServiceName = (TextView) view.findViewById(R.id.tvServiceName_LISL);
        }

        @Override
        protected void setData(int position, Service data) {
            tvServiceName.setText(data.getTitleRes());
        }
    }
}
