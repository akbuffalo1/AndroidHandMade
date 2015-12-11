package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.ThemedImageView;
import biz.enon.tra.uae.rest.model.response.SecurityQuestionResponse;

/**
 * Created by mobimaks on 09.12.2015.
 */
public class SecurityQuestionAdapter extends BaseSpinnerAdapter<SecurityQuestionResponse> {

    private int mItemTextColor;

    public SecurityQuestionAdapter(Context _context) {
        super(_context);
    }

    public SecurityQuestionAdapter(Context _context, List<SecurityQuestionResponse> _data) {
        super(_context, _data);
        mItemTextColor = ContextCompat.getColor(_context, R.color.hex_color_middle_gray);
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
    protected ViewHolder<SecurityQuestionResponse> getDropDownViewHolder(View _view) {
        return new QuestionDropdownViewHolder(_view);
    }

    @Override
    protected ViewHolder<SecurityQuestionResponse> getViewHolder(View _view) {
        return new QuestionViewHolder(_view);
    }

    public void setItemTextColor(@ColorInt int _itemTextColor){
        mItemTextColor = _itemTextColor;
        notifyDataSetChanged();
    }

    protected class QuestionViewHolder extends ViewHolder<SecurityQuestionResponse> {

        protected TextView tvTitle;
        protected ThemedImageView tivArrowIcon;

        public QuestionViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view.findViewById(R.id.tvTitle_SISSP);
            tivArrowIcon = (ThemedImageView) _view.findViewById(R.id.tivArrowIcon_SISSP);
        }

        @Override
        public void setData(int _position, final SecurityQuestionResponse _data) {
            tvTitle.setText(_data.text);
            tvTitle.setTextColor(mItemTextColor);
            tivArrowIcon.setTintColor(mItemTextColor);
        }
    }


    protected final class QuestionDropdownViewHolder extends ViewHolder<SecurityQuestionResponse> {

        private TextView tvTitle;

        public QuestionDropdownViewHolder(final View _view) {
            super(_view);
            tvTitle = (TextView) _view;
        }

        @Override
        public void setData(final int _position, final SecurityQuestionResponse _data) {
            tvTitle.setText(_data.text);
        }
    }
}
