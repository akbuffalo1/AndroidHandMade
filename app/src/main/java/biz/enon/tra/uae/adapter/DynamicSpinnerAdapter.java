package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.entities.dynamic_service.DataSourceItem;

/**
 * Created by mobimaks on 27.10.2015.
 */
public final class DynamicSpinnerAdapter extends BaseSpinnerAdapter<DataSourceItem> {

    public DynamicSpinnerAdapter(Context _context, List<DataSourceItem> _data) {
        super(_context, _data);
    }

    @Override
    protected ViewHolder<DataSourceItem> getViewHolder(View _view) {
        return new DynamicViewHolder(_view);
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.spinner_item_input_item_picker;
    }

    @Override
    protected int getDropdownLayoutRes() {
        return android.R.layout.simple_spinner_dropdown_item;
    }

    @Override
    protected ViewHolder<DataSourceItem> getDropDownViewHolder(View _view) {
        return new InnovationDropdownViewHolder(_view);
    }

    protected class DynamicViewHolder extends ViewHolder<DataSourceItem> {

        private TextView tvTitle;

        public DynamicViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view.findViewById(R.id.tvTitle_SIIIP);
        }

        @Override
        public void setData(int _position, final DataSourceItem _data) {
            tvTitle.setText(_data.getName());
        }
    }


    protected final class InnovationDropdownViewHolder extends ViewHolder<DataSourceItem> {

        private TextView tvTitle;

        public InnovationDropdownViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view;
        }

        @Override
        public void setData(final int _position, final DataSourceItem _data) {
            tvTitle.setText(_data.getName());
        }
    }
}
