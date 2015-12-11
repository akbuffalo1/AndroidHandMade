package biz.enon.tra.uae.entities.dynamic_service.input_item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enon.tra.uae.R;
import com.google.gson.JsonPrimitive;

import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem;

/**
 * Created by mobimaks on 20.10.2015.
 */

public class BooleanInputItem extends BaseInputItem implements OnClickListener {

    private static final String KEY_PREFIX = BooleanInputItem.class.getSimpleName();
    private static final String KEY_IS_CHECKED = KEY_PREFIX + "IS_CHECKED";

    private RelativeLayout rlContainer;
    private TextView tvDisplayName, tvText;
    private SwitchCompat swSwitch;

    private boolean mIsChecked;

    protected BooleanInputItem() {
    }

    @Override
    protected void initViews() {
        super.initViews();
        rlContainer = findView(R.id.rlContainer_IIB);

        tvDisplayName = findView(R.id.tvDisplayName_IIB);
        tvDisplayName.setText(getDisplayName());

        tvText = findView(R.id.tvText_IIB);
        tvText.setText(getPlaceholder());

        swSwitch = findView(R.id.swSwitch_IIB);
        swSwitch.setChecked(mIsChecked);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlContainer.setOnClickListener(this);
    }

    @Override
    public final void onClick(final View _view) {
        swSwitch.toggle();
    }

    @Override
    public boolean isDataValid() {
        return true;
    }

    @Nullable
    @Override
    public final JsonPrimitive getJsonValue() {
        return new JsonPrimitive(swSwitch.isChecked());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle _outState) {
        _outState.putBoolean(KEY_IS_CHECKED, swSwitch.isChecked());
        super.onSaveInstanceState(_outState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle _savedInstanceState) {
        super.onRestoreInstanceState(_savedInstanceState);
        mIsChecked = _savedInstanceState.getBoolean(KEY_IS_CHECKED);
    }

    public static class Builder extends BaseBuilder<BooleanInputItem> {

        @Override
        protected BooleanInputItem getInstance() {
            return new BooleanInputItem();
        }

    }

    @Override
    protected final int getLayoutRes() {
        return R.layout.input_item_boolean;
    }

}
