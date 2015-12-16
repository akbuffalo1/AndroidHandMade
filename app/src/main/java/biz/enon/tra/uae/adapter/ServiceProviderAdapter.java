package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.global.ServiceProvider;

/**
 * Created by mobimaks on 04.09.2015.
 */
public class ServiceProviderAdapter extends BaseSpinnerAdapter<ServiceProvider> {

    private static List<ServiceProvider> mProviderList;

    public ServiceProviderAdapter(final Context _context) {
        super(_context, mProviderList = Arrays.asList(ServiceProvider.values()));
    }

    protected ViewHolder<ServiceProvider> getViewHolder(View _view) {
        return new ServiceViewHolder(_view);
    }

    protected ViewHolder<ServiceProvider> getDropDownViewHolder(View _view) {
        return new ServiceViewHolder(_view);
    }

    @LayoutRes
    protected int getItemLayoutRes() {
        return R.layout.spinner_item_service_provider;
    }

    @Override
    protected int getDropdownLayoutRes() {
        return R.layout.spinner_dropdown_item_service_provider;
    }

    public List<ServiceProvider> getProviderList(){
        return mProviderList;
    }

    private class ServiceViewHolder extends ViewHolder<ServiceProvider> {

        private final TextView tvTitle;

        public ServiceViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view;
        }

        public void setData(int _position, final ServiceProvider _provider) {
            tvTitle.setText(_provider.getTitleRes());
        }

    }
}
