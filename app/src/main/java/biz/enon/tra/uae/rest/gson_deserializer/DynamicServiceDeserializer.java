package biz.enon.tra.uae.rest.gson_deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import biz.enon.tra.uae.entities.dynamic_service.DynamicService;
import biz.enon.tra.uae.entities.dynamic_service.InputItemsPage;

/**
 * Created by mobimaks on 20.10.2015.
 */
public class DynamicServiceDeserializer extends BaseDeserializer<DynamicService> {

    private static final String ID = "_id";
    private static final String PAGES = "pages";
    private static final String SERVICE_NAME = "serviceName";
    private static final String BUTTON_TEXT = "buttonTitle";

    @Override
    public DynamicService deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final DynamicService model = new DynamicService();
        final JsonObject jsonObject = json.getAsJsonObject();
        model.id = jsonObject.get(ID).getAsString();

        final Type listType = new TypeToken<ArrayList<InputItemsPage>>() {}.getType();
        model.pages = context.deserialize(jsonObject.get(PAGES), listType);

        model.buttonText = getLocalisedText(jsonObject.getAsJsonObject(BUTTON_TEXT));
        model.serviceName = getLocalisedText(jsonObject.getAsJsonObject(SERVICE_NAME));
        return model;
    }

}
