package com.uae.tra_smart_services.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.global.AttachmentOption;

import java.util.List;

import static com.uae.tra_smart_services.global.AttachmentOption.ATTACHMENT;

/**
 * Created by mobimaks on 06.10.2015.
 */
public class AttachmentOptionsAdapter extends ArrayAdapter<AttachmentOption> {

    @StringRes
    private final int mDeleteAttachmentTitleRes;

    public AttachmentOptionsAdapter(Context context, List<AttachmentOption> _options) {
        super(context, android.R.layout.simple_list_item_1, _options);
        final boolean containAttachmentOption = _options.contains(ATTACHMENT);
        mDeleteAttachmentTitleRes = containAttachmentOption?ATTACHMENT.getTitleRes(): R.string.attachment_manager_delete_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView textView = (TextView) super.getView(position, convertView, parent);
        final AttachmentOption option = getItem(position);

        if (option == AttachmentOption.DELETE_ATTACHMENT) {
            textView.setTextColor(Color.RED);
            textView.setText(mDeleteAttachmentTitleRes);
        } else {
            textView.setTextColor(Color.BLACK);
            textView.setText(option.getTitleRes());
        }
        return textView;
    }
}