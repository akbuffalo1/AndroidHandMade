package com.uae.tra_smart_services.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.uae.tra_smart_services.R;

import java.util.List;

/**
 * Created by mobimaks on 03.10.2015.
 */
public class InnovationSpinnerAdapter extends BaseSpinnerAdapter<String> {//TODO: rename class

    public InnovationSpinnerAdapter(Context _context, List<String> _data) {
        super(_context, _data);
    }

    @Override
    protected ViewHolder<String> getViewHolder(View _view) {
        return new InnovationViewHolder(_view);
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.spinner_item_spam_service_provider;
    }

    @Override
    protected int getDropdownLayoutRes() {
        return android.R.layout.simple_spinner_dropdown_item;
    }

    @Override
    protected ViewHolder<String> getDropDownViewHolder(View _view) {
        return new InnovationDropdownViewHolder(_view);
    }

    protected class InnovationViewHolder extends ViewHolder<String> {

        private TextView tvTitle;

        public InnovationViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view.findViewById(R.id.tvTitle_SISSP);
        }

        @Override
        public void setData(final String _data) {
            tvTitle.setText(_data);
        }
    }


    protected final class InnovationDropdownViewHolder extends ViewHolder<String> {

        private TextView tvTitle;

        public InnovationDropdownViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view;
        }

        @Override
        public void setData(final String _data) {
            tvTitle.setText(_data);
        }
    }
}
