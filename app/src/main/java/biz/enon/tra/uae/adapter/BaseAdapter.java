package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobimaks on 10.07.2015.
 */
abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<T> mData;

    public BaseAdapter(final Context context) {
        this(context, new ArrayList<T>());
    }

    public BaseAdapter(final Context context, final List<T> data) {
        mInflater = LayoutInflater.from(context);
        mData = data == null ? new ArrayList<T>() : data;
    }

    public void setData(final List<T> data) {
        mData.clear();
        addData(data);
    }

    public void addData(final List<T> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
        notifyDataSetInvalidated();
    }

    public final List<T> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BaseViewHolder<T> holder;
        if (convertView == null) {
            convertView = mInflater.inflate(getItemLayoutRes(), parent, false);
            holder = getViewHolder(convertView);
        } else {
            //noinspection unchecked
            holder = (BaseViewHolder) convertView.getTag();
        }
        holder.setData(position, getItem(position));
        return convertView;
    }

    @LayoutRes
    protected abstract int getItemLayoutRes();

    protected abstract BaseViewHolder<T> getViewHolder(final View view);

    protected abstract static class BaseViewHolder<T> {

        protected BaseViewHolder(final View view) {
            initViews(view);
            view.setTag(this);
        }

        protected abstract void initViews(final View view);

        protected abstract void setData(final int position, final T data);

    }
}
