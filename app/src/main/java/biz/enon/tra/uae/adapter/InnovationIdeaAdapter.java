package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import biz.enon.tra.uae.R;

/**
 * Created by mobimaks on 03.10.2015.
 */
public class InnovationIdeaAdapter extends BaseSpinnerAdapter<String> {

    public InnovationIdeaAdapter(Context _context, List<String> _data) {
        super(_context, _data);
    }

    @Override
    protected ViewHolder<String> getViewHolder(View _view) {
        return new InnovationViewHolder(_view);
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
    protected ViewHolder<String> getDropDownViewHolder(View _view) {
        return new InnovationDropdownViewHolder(_view);
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }

    protected class InnovationViewHolder extends ViewHolder<String> {

        private TextView tvTitle;

        public InnovationViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view.findViewById(R.id.tvTitle_SISSP);
            _view.findViewById(R.id.sSpace_SISSP).setVisibility(View.GONE);
            _view.findViewById(R.id.tivArrowIcon_SISSP).setVisibility(View.GONE);
        }

        @Override
        public void setData(int _position, final String _data) {
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
        public void setData(final int _position, final String _data) {
            tvTitle.setText(_data);
        }
    }
}
