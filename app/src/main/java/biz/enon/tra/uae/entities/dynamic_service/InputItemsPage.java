package biz.enon.tra.uae.entities.dynamic_service;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem.BaseBuilder;
import biz.enon.tra.uae.entities.dynamic_service.input_item.AttachmentInputItem;
import biz.enon.tra.uae.interfaces.SaveStateObject;

/**
 * Created by mobimaks on 02.11.2015.
 */
public final class InputItemsPage implements SaveStateObject {

    //region Save keys
    private static final String KEY_PREFIX = InputItemsPage.class.getSimpleName();
    private static final String KEY_ID = KEY_PREFIX + "_ID";
    private static final String KEY_INPUT_ITEMS_NUMBER = KEY_PREFIX + "_INPUT_ITEMS_NUMBER";
    private static final String KEY_INPUT_ITEM = KEY_PREFIX + "_INPUT_ITEM_";
    //endregion

    @Expose
    @SerializedName("_id")
    public String id;

    @Expose
    public List<BaseInputItem> inputItems;


    @Override
    public final void onRestoreInstanceState(@NonNull final Bundle _savedInstanceState) {
        id = _savedInstanceState.getString(KEY_ID);

        final int inputItemsNumber = _savedInstanceState.getInt(KEY_INPUT_ITEMS_NUMBER);
        inputItems = new ArrayList<>(inputItemsNumber);
        final InputItemBuilderFabric fabric = new InputItemBuilderFabric();
        for (int i = 0; i < inputItemsNumber; i++) {
            final Bundle savedItemState = _savedInstanceState.getBundle(KEY_INPUT_ITEM + i);
            if (savedItemState != null) {
                final String itemType = BaseInputItem.getRestoredInputItemType(savedItemState);
                final BaseBuilder builder = fabric.createBuilder(itemType);
                final BaseInputItem inputItem = builder.build();
                inputItem.onRestoreInstanceState(savedItemState);
                inputItems.add(inputItem);
            }
        }
    }

    @Override
    public final void onSaveInstanceState(@NonNull final Bundle _outState) {
        _outState.putString(KEY_ID, id);

        final int inputItemsNumber = inputItems.size();
        _outState.putInt(KEY_INPUT_ITEMS_NUMBER, inputItemsNumber);
        for (int i = 0; i < inputItemsNumber; i++) {
            final Bundle outState = new Bundle();
            inputItems.get(i).onSaveInstanceState(outState);
            _outState.putBundle(KEY_INPUT_ITEM + i, outState);
        }
    }

    public boolean isDataValid() {
        for (final BaseInputItem inputItem : inputItems) {
            if (!inputItem.isDataValid()) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    public List<Attachment> getAttachments() {
        final List<Attachment> attachments = new ArrayList<>();
        for (final BaseInputItem inputItem : inputItems) {

            final AttachmentInputItem attachmentItem;
            if (inputItem.isAttachmentItem() &&
                    (attachmentItem = (AttachmentInputItem) inputItem).getAttachmentUri() != null) {
                attachments.add(new Attachment(attachmentItem));
            }
        }
        return attachments;
    }

    @NonNull
    public Map<String, JsonPrimitive> getValuesMap() {
        final Map<String, JsonPrimitive> valuesMap =  new HashMap<>();
        for (final BaseInputItem inputItem : inputItems) {
            final JsonPrimitive inputItemValue = inputItem.getJsonValue();
            if (inputItemValue != null) {
                valuesMap.put(inputItem.getQueryName(), inputItemValue);
            }
        }
        return valuesMap;
    }
}
