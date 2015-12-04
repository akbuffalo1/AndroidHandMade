package com.uae.tra_smart_services.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.rest.model.response.ContactUsResponse;

import java.util.List;

/**
 * Created by mobimaks on 04.12.2015.
 */
public class ContactUsAdapter extends BaseSpinnerAdapter<ContactUsResponse> {

    public ContactUsAdapter(Context _context, List<ContactUsResponse> _data) {
        super(_context, _data);
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.simple_arrow_spinner_item;
    }

    @Override
    protected int getDropdownLayoutRes() {
        return android.R.layout.simple_spinner_dropdown_item;
    }

    @Override
    protected ViewHolder<ContactUsResponse> getDropDownViewHolder(View _view) {
        return new ContactDropdownViewHolder(_view);
    }

    @Override
    protected ViewHolder<ContactUsResponse> getViewHolder(View _view) {
        return new ContactViewHolder(_view);
    }

    protected class ContactViewHolder extends ViewHolder<ContactUsResponse> {

        private TextView tvTitle;

        public ContactViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view.findViewById(R.id.tvTitle_SISSP);
            if (getCount() == 1) {
                _view.findViewById(R.id.sSpace_SISSP).setVisibility(View.GONE);
                _view.findViewById(R.id.tivArrowIcon_SISSP).setVisibility(View.GONE);
            }
        }

        @Override
        public void setData(int _position, final ContactUsResponse _data) {
            tvTitle.setText(_data.title);
        }
    }


    protected final class ContactDropdownViewHolder extends ViewHolder<ContactUsResponse> {

        private TextView tvTitle;

        public ContactDropdownViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view;
        }

        @Override
        public void setData(final int _position, final ContactUsResponse _data) {
            tvTitle.setText(_data.title);
        }
    }
}
