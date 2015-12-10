package com.uae.tra_smart_services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

import com.uae.tra_smart_services.adapter.SecurityQuestionAdapter;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse;

import java.util.List;

/**
 * Created by mobimaks on 09.12.2015.
 */
public class UserProfileQuestionAdapter extends SecurityQuestionAdapter {

    protected Context mContext;

    public UserProfileQuestionAdapter(Context _context) {
        super(_context);
        mContext = _context;
    }

    public UserProfileQuestionAdapter(Context _context, List<SecurityQuestionResponse> _data) {
        super(_context, _data);
        mContext = _context;
    }

    public void addData(@Nullable List<SecurityQuestionResponse> _data){
        if (_data != null) {
            mData.addAll(_data);
            notifyDataSetChanged();
        }
    }

    @Override
    protected ViewHolder<SecurityQuestionResponse> getViewHolder(@NonNull View _view) {
        return new UserProfileQuestionViewHolder(_view);
    }

    protected class UserProfileQuestionViewHolder extends QuestionViewHolder {

        public UserProfileQuestionViewHolder(View _view) {
            super(_view);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.fragment_edit_user_profile_text_size));
            tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.fragment_user_profile_text_color_primary));
        }


    }

}
