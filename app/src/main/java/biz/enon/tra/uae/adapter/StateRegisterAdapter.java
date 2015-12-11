package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.util.ImageUtils;

/**
 * Created by Mikazme on 15/09/2015.
 */
public class StateRegisterAdapter extends android.widget.BaseAdapter {

    private final LayoutInflater mInflater;
    private List<String> mData;
    private final Context mContext;

    public StateRegisterAdapter(final Context _context, final List<String> _states) {
        mInflater = LayoutInflater.from(_context);
        mData = _states;
        mContext = _context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(final int _position) {
        return mData.get(_position);
    }

    @Override
    public long getItemId(final int _position) {
        return _position;
    }

    @Override
    public View getView(final int _position, View _convertView, final ViewGroup _parent) {
        final ViewHolder holder;
        if (_convertView == null) {
            _convertView = mInflater.inflate(R.layout.spinner_item_state, _parent, false);
            holder = new ViewHolder(_convertView);
            holder.setDrawableColors();
        } else {
            holder = (ViewHolder) _convertView.getTag();
        }
        holder.setData(getItem(_position));
        return _convertView;
    }

    @Override
    public View getDropDownView(int _position, View _convertView, ViewGroup _parent) {
        final ViewHolder holder;
        if (_convertView == null) {
            _convertView = mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, _parent, false);
            holder = new ViewHolder(_convertView);
        } else {
            holder = (ViewHolder) _convertView.getTag();
        }
        holder.setData(getItem(_position));
        return _convertView;
    }

    private class ViewHolder {

        private final TextView tvTitle;

        public ViewHolder(final View _view) {
            tvTitle = (TextView) _view;
            _view.setTag(this);
        }

        public void setDrawableColors() {
            tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtils.getFilteredDrawableByTheme(mContext, R.drawable.ic_state, R.attr.authorizationDrawableColors), null,
                    ImageUtils.getFilteredDrawableByTheme(mContext, R.drawable.ic_profile_arrow_down, R.attr.authorizationDrawableColors), null);
        }

        public void setData(final String _state) {
            tvTitle.setText(_state);
        }
    }
}
