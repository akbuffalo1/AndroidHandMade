package biz.enon.tra.uae.rest.gson_deserializer;

import android.text.Html;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import biz.enon.tra.uae.rest.model.response.GetTransactionResponseModel;
import biz.enon.tra.uae.util.Logger;
import biz.enon.tra.uae.util.StringUtils;

/**
 * Created by mobimaks on 07.12.2015.
 */
public class TransactionDeserializer implements JsonDeserializer<GetTransactionResponseModel> {

    private static final SimpleDateFormat SOURCE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    private static final SimpleDateFormat RESULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public GetTransactionResponseModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final GetTransactionResponseModel transaction = new Gson().fromJson(json, typeOfT);
        if (transaction != null) {
            transaction.title = Html.fromHtml(StringUtils.trim(transaction.title)).toString();
            transaction.description = Html.fromHtml(StringUtils.trim(transaction.description)).toString();
            try {
                transaction.modifiedDatetime = RESULT_DATE_FORMAT.format(SOURCE_DATE_FORMAT.parse(transaction.modifiedDatetime));
            } catch (ParseException e) {
                Logger.d(getClass().getSimpleName(), String.valueOf(e));
            }
        }
        return transaction;
    }

}
