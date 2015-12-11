package biz.enon.tra.uae.rest.gson_deserializer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import java.util.Locale;

import static biz.enon.tra.uae.global.C.ARABIC;

/**
 * Created by mobimaks on 22.10.2015.
 */
public abstract class BaseDeserializer<T> implements JsonDeserializer<T> {

    protected static final String AR = "AR";
    protected static final String EN = "EN";

    protected String getLocalisedText(final JsonObject _jsonObject) {
        final String localisationField = Locale.getDefault().toString().equalsIgnoreCase(ARABIC) ? AR : EN;
        return _jsonObject.get(localisationField).getAsString();
    }

}
