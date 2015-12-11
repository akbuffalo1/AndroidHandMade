package biz.enon.tra.uae.entities.dynamic_service.input_item;

import android.support.annotation.Nullable;

import com.google.gson.JsonPrimitive;

import biz.enon.tra.uae.entities.dynamic_service.InputItemBuilderFabric;

/**
 * Created by mobimaks on 26.10.2015.
 */
public final class NumberInputItem extends StringInputItem {

    protected NumberInputItem() {
    }

    @Override
    protected void initViews() {
        setValidationRule(InputItemBuilderFabric.ValidationRule.NUMBER);
        super.initViews();
    }

    @Override
    public boolean isDataValid() {
        return !isRequired();
    }

    @Nullable
    @Override
    public JsonPrimitive getJsonValue() {
        return super.getJsonValue();
    }

    public static final class Builder extends BaseBuilder {

        @Override
        protected NumberInputItem getInstance() {
            return new NumberInputItem();
        }

    }

}
