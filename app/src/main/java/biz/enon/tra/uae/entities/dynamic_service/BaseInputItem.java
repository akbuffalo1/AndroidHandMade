package biz.enon.tra.uae.entities.dynamic_service;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.interfaces.SaveStateObject;

/**
 * Created by mobimaks on 22.10.2015.
 */
@SuppressWarnings("unchecked")
public abstract class BaseInputItem implements SaveStateObject, Comparable<BaseInputItem> {

    //region Save keys
    private static final String KEY_PREFIX = BaseInputItem.class.getSimpleName();
    private static final String KEY_ID = KEY_PREFIX + "_ID";
    private static final String KEY_QUERY_NAME = KEY_PREFIX + "_QUERY_NAME";
    private static final String KEY_DISPLAY_NAME = KEY_PREFIX + "_DISPLAY_NAME";
    private static final String KEY_PLACEHOLDER = KEY_PREFIX + "_PLACEHOLDER";
    private static final String KEY_DATA_SOURCE = KEY_PREFIX + "_DATA_SOURCE";
    private static final String KEY_IS_VALIDATION_REQUIRED = KEY_PREFIX + "_IS_VALIDATION_REQUIRED";
    private static final String KEY_ORDER = KEY_PREFIX + "_ORDER";
    private static final String KEY_VALIDATION_RULE = KEY_PREFIX + "_VALIDATION_RULE";
    private static final String KEY_ITEM_TYPE = KEY_PREFIX + "_ITEM_TYPE";
    //endregion

    @SuppressWarnings("ResourceType")
    @InputItemBuilderFabric.InputItemType
    public static String getRestoredInputItemType(final Bundle _savedState) {
        return _savedState.getString(KEY_ITEM_TYPE);
    }

    protected View rootView;

    protected String mId;
    protected String mQueryName;
    protected String mDisplayName;
    protected String mPlaceholder;
    protected ArrayList<DataSourceItem> mDataSource;
    protected boolean isRequired;
    protected int order;

    @InputItemBuilderFabric.InputItemType
    protected String mItemType;

    @InputItemBuilderFabric.ValidationRule
    protected String mValidationRule;

    public View getView(final LayoutInflater _inflater, final ViewGroup _container) {
        if (rootView == null) {
            rootView = _inflater.inflate(getLayoutRes(), _container, false);
            initViews();
            initListeners();
        }
        return rootView;
    }

    @SuppressWarnings("ResourceType")
    @CallSuper
    @Override
    public void onRestoreInstanceState(@NonNull final Bundle _savedInstanceState) {
        mId = _savedInstanceState.getString(KEY_ID);
        mQueryName = _savedInstanceState.getString(KEY_QUERY_NAME);
        mDisplayName = _savedInstanceState.getString(KEY_DISPLAY_NAME);
        mPlaceholder = _savedInstanceState.getString(KEY_PLACEHOLDER);
        mDataSource = _savedInstanceState.getParcelableArrayList(KEY_DATA_SOURCE);
        isRequired = _savedInstanceState.getBoolean(KEY_IS_VALIDATION_REQUIRED);
        order = _savedInstanceState.getInt(KEY_ORDER);
        mValidationRule = _savedInstanceState.getString(KEY_VALIDATION_RULE);
        mItemType = _savedInstanceState.getString(KEY_ITEM_TYPE);
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(@NonNull final Bundle _outState) {
        _outState.putString(KEY_ID, mId);
        _outState.putString(KEY_QUERY_NAME, mQueryName);
        _outState.putString(KEY_DISPLAY_NAME, mDisplayName);
        _outState.putString(KEY_PLACEHOLDER, mPlaceholder);
        _outState.putParcelableArrayList(KEY_DATA_SOURCE, mDataSource);
        _outState.putBoolean(KEY_IS_VALIDATION_REQUIRED, isRequired);
        _outState.putInt(KEY_ORDER, order);
        _outState.putString(KEY_VALIDATION_RULE, mValidationRule);
        _outState.putString(KEY_ITEM_TYPE, mItemType);
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    protected void initViews() {
    }

    protected void initListeners() {
    }

    public boolean isDataValid() {
        return !isRequired;
    }

    public final String getId() {
        return mId;
    }

    public final String getQueryName() {
        return mQueryName;
    }

    public final String getDisplayName() {
        return mDisplayName;
    }

    public final String getPlaceholder() {
        return mPlaceholder;
    }

    public final int getOrder() {
        return order;
    }

    public List<DataSourceItem> getDataSourceList() {
        return mDataSource;
    }

    @InputItemBuilderFabric.InputItemType
    public String getItemType() {
        return mItemType;
    }

    @Nullable
    public abstract JsonPrimitive getJsonValue();

    @InputItemBuilderFabric.ValidationRule
    protected final String getValidationRule() {
        return mValidationRule;
    }

    protected void setValidationRule(@InputItemBuilderFabric.ValidationRule String _validationRule) {
        mValidationRule = _validationRule;
    }

    protected final boolean isRequired() {
        return isRequired;
    }

    public final boolean isAttachmentItem() {
        return InputItemBuilderFabric.InputItemType.FILE_ITEM.equals(mItemType);
    }

    @Override
    public int compareTo(@NonNull final BaseInputItem _another) {
        return Integer.valueOf(order).compareTo(_another.order);
    }

    protected <V extends View> V findView(@IdRes int _id) {
        return (V) rootView.findViewById(_id);
    }

    public static abstract class BaseBuilder<T extends BaseInputItem> {

        protected final T mInstance;

        public BaseBuilder() {
            mInstance = getInstance();
        }

        protected abstract T getInstance();

        public <E extends BaseBuilder<T>> E setId(String _id) {
            mInstance.mId = _id;
            return (E) this;
        }

        public <E extends BaseBuilder<T>> E setQueryName(String _queryName) {
            mInstance.mQueryName = _queryName;
            return (E) this;
        }

        public <E extends BaseBuilder<T>> E setIsRequired(boolean _isRequired) {
            mInstance.isRequired = _isRequired;
            return (E) this;
        }

        public <E extends BaseBuilder<T>> E setDisplayName(String _displayName) {
            mInstance.mDisplayName = _displayName;
            return (E) this;
        }

        public <E extends BaseBuilder<T>> E setPlaceholder(String _placeholder) {
            mInstance.mPlaceholder = _placeholder;
            return (E) this;
        }

        public <E extends BaseBuilder<T>> E setOrder(int _order) {
            mInstance.order = _order;
            return (E) this;
        }

        public <E extends BaseBuilder<T>> E setValidationRule(@InputItemBuilderFabric.ValidationRule String _validationRule) {
            mInstance.mValidationRule = _validationRule;
            return (E) this;
        }

        public <E extends BaseBuilder<T>> E setDataSource(@InputItemBuilderFabric.ValidationRule ArrayList<DataSourceItem> _dataSource) {
            mInstance.mDataSource = _dataSource;
            return (E) this;
        }

        public <E extends BaseBuilder<T>> E setInputItemType(@InputItemBuilderFabric.InputItemType String _itemType) {
            mInstance.mItemType = _itemType;
            return (E) this;
        }

        public T build() {
            return mInstance;
        }

    }

}
