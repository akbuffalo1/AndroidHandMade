package com.uae.tra_smart_services.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.uae.tra_smart_services.R;

import java.util.List;

/**
 * Created by mobimaks on 09.12.2015.
 */
public class SecurityQuestionAdapter extends BaseSpinnerAdapter<String> {

    public SecurityQuestionAdapter(Context _context, List<String> _data) {
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
    protected ViewHolder<String> getDropDownViewHolder(View _view) {
        return new QuestionDropdownViewHolder(_view);
    }

    @Override
    protected ViewHolder<String> getViewHolder(View _view) {
        return new QuestionViewHolder(_view);
    }

    protected class QuestionViewHolder extends ViewHolder<String> {

        private TextView tvTitle;

        public QuestionViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view.findViewById(R.id.tvTitle_SISSP);
        }

        @Override
        public void setData(int _position, final String _data) {
            tvTitle.setText(_data);
        }
    }


    protected final class QuestionDropdownViewHolder extends ViewHolder<String> {

        private TextView tvTitle;

        public QuestionDropdownViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view;
        }

        @Override
        public void setData(final int _position, final String _data) {
            tvTitle.setText(_data);
        }
    }
}
