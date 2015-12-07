package com.uae.tra_smart_services.rest.gson_deserializer;

import android.text.Html;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.uae.tra_smart_services.rest.model.response.GetTransactionResponseModel;
import com.uae.tra_smart_services.util.StringUtils;

import java.lang.reflect.Type;

/**
 * Created by mobimaks on 07.12.2015.
 */
public class TransactionDeserializer implements JsonDeserializer<GetTransactionResponseModel> {

    @Override
    public GetTransactionResponseModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final GetTransactionResponseModel transaction = new Gson().fromJson(json, typeOfT);
        if (transaction != null) {
            transaction.title = Html.fromHtml(StringUtils.trim(transaction.title)).toString();
            transaction.description = Html.fromHtml(StringUtils.trim(transaction.description)).toString();
        }
        return transaction;
    }

}
