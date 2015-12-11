package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobimaks on 03.10.2015.
 */
public abstract class BaseSpinnerAdapter<T> extends android.widget.BaseAdapter {

    protected List<T> mData;

    private final LayoutInflater mInflater;

    public BaseSpinnerAdapter(final Context _context) {
        this(_context, new ArrayList<T>());
    }

    public BaseSpinnerAdapter(final Context _context, final List<T> _data) {
        mInflater = LayoutInflater.from(_context);
        mData = _data;
    }

    @Override
    public View getView(final int _position, View _convertView, final ViewGroup _parent) {
        final ViewHolder<T> holder;
        if (_convertView == null) {
            _convertView = mInflater.inflate(getItemLayoutRes(), _parent, false);
            holder = getViewHolder(_convertView);
        } else {
            holder = (ViewHolder<T>) _convertView.getTag();
        }
        holder.setData(_position, getItem(_position));
        return _convertView;
    }

    @Override
    public View getDropDownView(int _position, View _convertView, ViewGroup _parent) {
        final ViewHolder<T> holder;
        if (_convertView == null) {
            _convertView = mInflater.inflate(getDropdownLayoutRes(), _parent, false);
            holder = getDropDownViewHolder(_convertView);
        } else {
            holder = (ViewHolder<T>) _convertView.getTag();
        }
        holder.setData(_position, getItem(_position));
        return _convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(final int _position) {
        return mData.get(_position);
    }

    @Override
    public long getItemId(final int _position) {
        return _position;
    }

    @LayoutRes
    protected abstract int getItemLayoutRes();

    @LayoutRes
    protected abstract int getDropdownLayoutRes();

    protected abstract ViewHolder<T> getDropDownViewHolder(View _view);

    protected abstract ViewHolder<T> getViewHolder(View _view);

    protected abstract class ViewHolder<T> {

        public ViewHolder(final View _view) {
            _view.setTag(this);
        }

        public abstract void setData(int _position, final T _data);

    }

}
