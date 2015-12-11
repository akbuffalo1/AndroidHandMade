package biz.enon.tra.uae.entities.dynamic_service;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import biz.enon.tra.uae.interfaces.SaveStateObject;

/**
 * Created by mobimaks on 22.10.2015.
 */
public class DynamicService implements SaveStateObject {

    //region Save keys
    private static final String KEY_PREFIX = DynamicService.class.getSimpleName();
    private static final String KEY_ID = KEY_PREFIX + "_ID";
    private static final String KEY_BUTTON_TEXT = KEY_PREFIX + "_BUTTON_TEXT";
    private static final String KEY_SERVICE_NAME = KEY_PREFIX + "_SERVICE_NAME";
    private static final String KEY_PAGE = KEY_PREFIX + "_PAGE";
    private static final String KEY_PAGES_NUMBER = KEY_PREFIX + "_PAGES_NUMBER";
    //endregion

    @Expose
    public String id;

    @Expose
    public List<InputItemsPage> pages;

    @Expose
    public String buttonText;

    @Expose
    public String serviceName;

    @Override
    public final void onRestoreInstanceState(@NonNull final Bundle _savedInstanceState) {
        id = _savedInstanceState.getString(KEY_ID);
        buttonText = _savedInstanceState.getString(KEY_BUTTON_TEXT);
        serviceName = _savedInstanceState.getString(KEY_SERVICE_NAME);

        final int pageNumber = _savedInstanceState.getInt(KEY_PAGES_NUMBER);
        pages = new ArrayList<>(pageNumber);
        for (int i = 0; i < pageNumber; i++) {
            final Bundle savedPageState = _savedInstanceState.getBundle(KEY_PAGE + i);
            if (savedPageState != null) {
                final InputItemsPage inputItemsPage = new InputItemsPage();
                inputItemsPage.onRestoreInstanceState(savedPageState);
                pages.add(inputItemsPage);
            }
        }
    }

    @Override
    public final void onSaveInstanceState(@NonNull final Bundle _outState) {
        _outState.putString(KEY_ID, id);
        _outState.putString(KEY_BUTTON_TEXT, buttonText);
        _outState.putString(KEY_SERVICE_NAME, serviceName);

        final int pagesNumber = pages.size();
        _outState.putInt(KEY_PAGES_NUMBER, pagesNumber);
        for (int i = 0; i < pagesNumber; i++) {
            final InputItemsPage itemsPage = pages.get(i);
            final Bundle outState = new Bundle();
            itemsPage.onSaveInstanceState(outState);
            _outState.putBundle(KEY_PAGE + i, outState);
        }
    }

    public boolean isDataValid() {
        for (final InputItemsPage itemsPage : pages) {
            if (!itemsPage.isDataValid()) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    public JsonObject getJsonData() {
        final JsonObject object = new JsonObject();
        for (final InputItemsPage page : pages) {
            final Map<String, JsonPrimitive> pageValuesMap = page.getValuesMap();
            for (Map.Entry<String, JsonPrimitive> entry : pageValuesMap.entrySet()) {
                object.add(entry.getKey(), entry.getValue());
            }
        }
        return object;
    }

    @NonNull
    public List<Attachment> getAttachments() {
        final List<Attachment> attachments = new ArrayList<>();
        for (final InputItemsPage page : pages) {
            attachments.addAll(page.getAttachments());
        }
        return attachments;
    }
}
