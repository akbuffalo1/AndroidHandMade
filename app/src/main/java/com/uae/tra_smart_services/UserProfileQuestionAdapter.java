package com.uae.tra_smart_services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

import com.uae.tra_smart_services.adapter.SecurityQuestionAdapter;

import java.util.List;

/**
 * Created by mobimaks on 09.12.2015.
 */
public class UserProfileQuestionAdapter extends SecurityQuestionAdapter {

    protected Context mContext;

    public UserProfileQuestionAdapter(Context _context, List<String> _data) {
        super(_context, _data);
        mContext = _context;
    }

    @Override
    protected ViewHolder<String> getViewHolder(@NonNull View _view) {
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
