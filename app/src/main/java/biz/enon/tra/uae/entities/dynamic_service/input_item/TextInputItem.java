package biz.enon.tra.uae.entities.dynamic_service.input_item;

import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;

import biz.enon.tra.uae.entities.dynamic_service.InputItemBuilderFabric;

/**
 * Created by mobimaks on 26.10.2015.
 */
public final class TextInputItem extends StringInputItem {

    protected TextInputItem() {
    }

    @Override
    protected final void initViews() {
        super.initViews();
        final EditText editText = getEditText();
        editText.setSingleLine(false);
        editText.setMinLines(3);
        editText.setMaxLines(12);
        editText.setGravity(Gravity.START | Gravity.TOP);
    }

    @Override
    protected void processValidationRule() {
        super.processValidationRule();
        if (InputItemBuilderFabric.ValidationRule.STRING.equals(getValidationRule())) {
            int inputType = getEditText().getInputType();
            inputType += InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE;
            getEditText().setInputType(inputType);

        }
    }

    public static final class Builder extends BaseBuilder {

        @Override
        protected TextInputItem getInstance() {
            return new TextInputItem();
        }

    }

}
