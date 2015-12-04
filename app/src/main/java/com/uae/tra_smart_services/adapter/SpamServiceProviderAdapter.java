package com.uae.tra_smart_services.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.global.ServiceProvider;

/**
 * Created by mobimaks on 24.09.2015.
 */
public class SpamServiceProviderAdapter extends ServiceProviderAdapter {

    public SpamServiceProviderAdapter(Context _context) {
        super(_context);
    }

    @Override
    protected ViewHolder<ServiceProvider> getViewHolder(View _view) {
        return new ServiceViewHolder(_view);
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.simple_arrow_spinner_item;
    }

    @Override
    protected int getDropdownLayoutRes() {
        return android.R.layout.simple_spinner_dropdown_item;
    }

    protected class ServiceViewHolder extends ViewHolder<ServiceProvider> {

        private TextView tvTitle;

        public ServiceViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view.findViewById(R.id.tvTitle_SISSP);
        }

        @Override
        public void setData(int _position, ServiceProvider _provider) {
            tvTitle.setText(_provider.getTitleRes());
        }
    }
}
