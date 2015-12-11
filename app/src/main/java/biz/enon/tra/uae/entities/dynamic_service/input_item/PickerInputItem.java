package biz.enon.tra.uae.entities.dynamic_service.input_item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonPrimitive;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.DynamicSpinnerAdapter;
import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem;

/**
 * Created by mobimaks on 26.10.2015.
 */
public class PickerInputItem extends BaseInputItem implements OnClickListener, OnItemSelectedListener {

    private static final String KEY_PREFIX = PickerInputItem.class.getSimpleName();
    private static final String KEY_IS_SPINNER_CLICKED = KEY_PREFIX + "_IS_SPINNER_CLICKED";
    private static final String KEY_IS_USER_CLICK = KEY_PREFIX + "_IS_USER_CLICK";
    private static final String KEY_IS_NOTHING_SELECTED = KEY_PREFIX + "_IS_NOTHING_SELECTED";
    private static final String KEY_SELECTED_ITEM = KEY_PREFIX + "_SELECTED_ITEM";

    private TextView tvDisplayName, tvPlaceHolder;
    private ImageView tivArrowIcon;
    private Spinner sPicker;

    private DynamicSpinnerAdapter mAdapter;
    private boolean mIsSpinnerClicked, mIsUserClick, mNothingSelected;
    private int mSelectedItem;

    protected PickerInputItem() {
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvDisplayName = findView(R.id.tvDisplayName_IIP);
        tvDisplayName.setText(getDisplayName());

        tvPlaceHolder = findView(R.id.tvPickerPlaceholder_IIP);
        tvPlaceHolder.setText(getPlaceholder());

        tivArrowIcon = findView(R.id.tivArrowIcon_IIP);
        initPicker();
    }

    private void initPicker() {
        sPicker = findView(R.id.sPicker_IIP);
        mAdapter = new DynamicSpinnerAdapter(sPicker.getContext(), getDataSourceList());
        sPicker.setAdapter(mAdapter);
        sPicker.setSelection(mSelectedItem);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tvPlaceHolder.setOnClickListener(this);
        tivArrowIcon.setOnClickListener(this);
        sPicker.setOnItemSelectedListener(this);
    }

    @Override
    public final void onClick(final View _view) {
        switch (_view.getId()) {
            case R.id.tvPickerPlaceholder_IIP:
            case R.id.tivArrowIcon_IIP:
                openPicker();
                break;
        }
    }

    private void openPicker() {
        mIsUserClick = true;
        sPicker.performClick();
    }

    @Override
    public final void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mIsUserClick || mIsSpinnerClicked) {
            tvPlaceHolder.setVisibility(View.INVISIBLE);
            sPicker.setVisibility(View.VISIBLE);
            mIsSpinnerClicked = true;
            mNothingSelected = false;
            mSelectedItem = position;
        } else {
            mNothingSelected = true;
        }
    }

    @Override
    public boolean isDataValid() {
        return !isRequired() || mIsSpinnerClicked;
    }

    @Override
    public final void onNothingSelected(AdapterView<?> parent) {

    }

    @Nullable
    @Override
    public JsonPrimitive getJsonValue() {
        final String value = mAdapter.getItem(sPicker.getSelectedItemPosition()).value;
        return mIsSpinnerClicked ? new JsonPrimitive(value) : null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle _outState) {
        _outState.putBoolean(KEY_IS_SPINNER_CLICKED, mIsSpinnerClicked);
        _outState.putBoolean(KEY_IS_USER_CLICK, mIsUserClick);
        _outState.putBoolean(KEY_IS_NOTHING_SELECTED, mNothingSelected);
        _outState.putInt(KEY_SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(_outState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle _savedInstanceState) {
        super.onRestoreInstanceState(_savedInstanceState);
        mIsSpinnerClicked = _savedInstanceState.getBoolean(KEY_IS_SPINNER_CLICKED);
        mIsUserClick = _savedInstanceState.getBoolean(KEY_IS_USER_CLICK);
        mNothingSelected = _savedInstanceState.getBoolean(KEY_IS_NOTHING_SELECTED);
        mSelectedItem = _savedInstanceState.getInt(KEY_SELECTED_ITEM);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.input_item_picker;
    }

    public static class Builder extends BaseBuilder {

        @Override
        protected BaseInputItem getInstance() {
            return new PickerInputItem();
        }

    }
}
