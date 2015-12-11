package biz.enon.tra.uae.rest.gson_deserializer;

import android.support.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem;
import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem.BaseBuilder;
import biz.enon.tra.uae.entities.dynamic_service.DataSourceItem;
import biz.enon.tra.uae.entities.dynamic_service.InputItemBuilderFabric;

/**
 * Created by mobimaks on 22.10.2015.
 */
public final class InputItemDeserializer extends BaseDeserializer<BaseInputItem> {

    private static final String ID = "_id";
    private static final String TYPE = "inputType";
    private static final String NAME = "name";
    private static final String ORDER = "order";
    private static final String IS_REQUIRED = "required";
    private static final String VALIDATE_RULE = "validateAs";
    private static final String DISPLAY_NAME = "displayName";
    private static final String PLACEHOLDER = "placeHolder";
    private static final String DATA_SOURCE = "dataSource";

    @Override
    public BaseInputItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject main = json.getAsJsonObject();
        final String inputType = parseInputType(main.get(TYPE).getAsString());
        final InputItemBuilderFabric fabric = new InputItemBuilderFabric();
        final BaseBuilder builder = fabric.createBuilder(inputType)
                .setId(main.get(ID).getAsString())
                .setQueryName(main.get(NAME).getAsString())
                .setOrder(main.get(ORDER).getAsInt())
                .setIsRequired(main.get(IS_REQUIRED).getAsBoolean())
                .setDisplayName(getLocalisedText(main.getAsJsonObject(DISPLAY_NAME)))
                .setPlaceholder(getLocalisedText(main.getAsJsonObject(PLACEHOLDER)))
                .setValidationRule(parseValidationRule(main.get(VALIDATE_RULE)))
                .setDataSource(parseDataSourceList(context, main.get(DATA_SOURCE)))
                .setInputItemType(inputType);
        return builder.build();
    }

    @InputItemBuilderFabric.InputItemType
    private String parseInputType(final String _inputType) {
        if (InputItemBuilderFabric.InputItemType.BOOLEAN_ITEM.equalsIgnoreCase(_inputType)) {
            return InputItemBuilderFabric.InputItemType.BOOLEAN_ITEM;
        } else if (InputItemBuilderFabric.InputItemType.STRING_ITEM.equalsIgnoreCase(_inputType)) {
            return InputItemBuilderFabric.InputItemType.STRING_ITEM;
        } else if (InputItemBuilderFabric.InputItemType.TEXT_ITEM.equalsIgnoreCase(_inputType)) {
            return InputItemBuilderFabric.InputItemType.TEXT_ITEM;
        } else if (InputItemBuilderFabric.InputItemType.PICKER_ITEM.equalsIgnoreCase(_inputType)) {
            return InputItemBuilderFabric.InputItemType.PICKER_ITEM;
        } else if (InputItemBuilderFabric.InputItemType.FILE_ITEM.equalsIgnoreCase(_inputType)) {
            return InputItemBuilderFabric.InputItemType.FILE_ITEM;
        } else if (InputItemBuilderFabric.InputItemType.NUMBER_ITEM.equalsIgnoreCase(_inputType)) {
            return InputItemBuilderFabric.InputItemType.NUMBER_ITEM;
        }
        return InputItemBuilderFabric.InputItemType.BOOLEAN_ITEM;//default item (may be placeholder or sth like that)
    }

    @InputItemBuilderFabric.ValidationRule
    private String parseValidationRule(final JsonElement _ruleElement) {
        if (_ruleElement == null) {
            return InputItemBuilderFabric.ValidationRule.NONE;
        }

        final String _rule = _ruleElement.getAsString();
        if (InputItemBuilderFabric.ValidationRule.STRING.equalsIgnoreCase(_rule)) {
            return InputItemBuilderFabric.ValidationRule.STRING;
        } else if (InputItemBuilderFabric.ValidationRule.NUMBER.equalsIgnoreCase(_rule)) {
            return InputItemBuilderFabric.ValidationRule.NUMBER;
        } else if (InputItemBuilderFabric.ValidationRule.EMAIL.equalsIgnoreCase(_rule)) {
            return InputItemBuilderFabric.ValidationRule.EMAIL;
        } else if (InputItemBuilderFabric.ValidationRule.URL.equalsIgnoreCase(_rule)) {
            return InputItemBuilderFabric.ValidationRule.URL;
        } else {
            return InputItemBuilderFabric.ValidationRule.NONE;
        }
    }

    @NonNull
    private ArrayList<DataSourceItem> parseDataSourceList(final JsonDeserializationContext _context, final JsonElement _dataSourceObject) {
        ArrayList<DataSourceItem> data = null;
        if (_context != null) {
            final Type listType = new TypeToken<ArrayList<DataSourceItem>>() {}.getType();
            data = _context.deserialize(_dataSourceObject, listType);
        }
        if (data == null) {
            data = new ArrayList<>();
        }
        return data;
    }

}
