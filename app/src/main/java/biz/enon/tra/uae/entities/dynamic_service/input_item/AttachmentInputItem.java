package biz.enon.tra.uae.entities.dynamic_service.input_item;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enon.tra.uae.R;
import com.google.gson.JsonPrimitive;

import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem;
import biz.enon.tra.uae.interfaces.OnOpenAttachmentPickerListener;

/**
 * Created by mobimaks on 27.10.2015.
 */
public class AttachmentInputItem extends BaseInputItem implements OnClickListener {

    private static final String KEY_PREFIX = AttachmentInputItem.class.getSimpleName();
    private static final String KEY_ATTACHMENT_URI = KEY_PREFIX + "_ATTACHMENT_URI";
    private static final String KEY_ATTACHMENT_ID = KEY_PREFIX + "_ATTACHMENT_ID";

    private RelativeLayout rlContainer;
    private TextView tvDisplayName, tvText;
    private ImageView ivAttachment;

    private Uri mAttachmentUri;
    private String mAttachmentId;
    private OnOpenAttachmentPickerListener mListener;

    protected AttachmentInputItem() {
    }

    @Override
    protected void initViews() {
        super.initViews();
        rlContainer = findView(R.id.rlContainer_IIA);

        tvDisplayName = findView(R.id.tvDisplayName_IIA);
        tvDisplayName.setText(getDisplayName());

        ivAttachment = findView(R.id.ivAttachment_IIA);
        tvText = findView(R.id.tvText_IIA);

        invalidateAttachmentIndicator();
    }

    private void invalidateAttachmentIndicator() {
        if (mAttachmentUri == null) {
            ivAttachment.setImageResource(R.drawable.ic_action_attachment);
            tvText.setText(getPlaceholder());
        } else {
            ivAttachment.setImageResource(R.drawable.ic_check);
            tvText.setText(mAttachmentUri.toString());
        }

        invalidateUploadIndicator();
    }

    private void invalidateUploadIndicator() {
        if (mAttachmentUri != null) {
            if (mAttachmentId == null) {
                ivAttachment.setBackgroundColor(Color.RED);
            } else {
                ivAttachment.setBackgroundColor(Color.GREEN);
            }
        } else {
            ivAttachment.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlContainer.setOnClickListener(this);
    }

    @Override
    public final void onClick(final View _view) {
        switch (_view.getId()) {
            case R.id.rlContainer_IIA:
                mListener.onOpenAttachmentPicker(this);
                break;
        }
    }

    public Uri getAttachmentUri() {
        return mAttachmentUri;
    }

    public String getAttachmentId() {
        return mAttachmentId;
    }

    public void setAttachmentId(final String _attachmentId) {
        mAttachmentId = _attachmentId;
        invalidateUploadIndicator();
    }

    public void setAttachmentCallback(OnOpenAttachmentPickerListener _listener) {
        mListener = _listener;
    }

    public boolean isAttachmentUploaded() {
        return mAttachmentId != null;
    }

    public void onAttachmentStateChanged(@Nullable Uri _uri) {
        mAttachmentUri = _uri;
        mAttachmentId = null;
        invalidateAttachmentIndicator();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle _outState) {
        _outState.putString(KEY_ATTACHMENT_ID, mAttachmentId);
        _outState.putParcelable(KEY_ATTACHMENT_URI, mAttachmentUri);
        super.onSaveInstanceState(_outState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle _savedInstanceState) {
        super.onRestoreInstanceState(_savedInstanceState);
        mAttachmentUri = _savedInstanceState.getParcelable(KEY_ATTACHMENT_URI);
        mAttachmentId = _savedInstanceState.getString(KEY_ATTACHMENT_ID);
    }

    @Override
    public boolean isDataValid() {
        if (isRequired()) {
            return mAttachmentId != null;
        } else {
            return mAttachmentUri == null || mAttachmentId != null;
        }
    }

    @Nullable
    @Override
    public JsonPrimitive getJsonValue() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.input_item_attachment;
    }

    public static final class Builder extends BaseBuilder {

        @Override
        protected BaseInputItem getInstance() {
            return new AttachmentInputItem();
        }

    }
}
