package com.uae.tra_smart_services.rest.gson_deserializer;

import android.text.Html;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.uae.tra_smart_services.rest.model.response.GetAnnouncementsResponseModel.Announcement;
import com.uae.tra_smart_services.util.Logger;
import com.uae.tra_smart_services.util.StringUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by mobimaks on 07.12.2015.
 */
public class AnnouncementDeserializer implements JsonDeserializer<Announcement> {

    private static final SimpleDateFormat SOURCE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat RESULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
//    private static final SimpleDateFormat RESULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

    @Override
    public Announcement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        final Announcement announcement = new Gson().fromJson(json, typeOfT);
        if (announcement != null) {
            announcement.title = Html.fromHtml(StringUtils.trim(announcement.title)).toString();
            announcement.description = Html.fromHtml(StringUtils.trim(announcement.description)).toString();
            try {
                announcement.createdAt = RESULT_DATE_FORMAT.format(SOURCE_DATE_FORMAT.parse(announcement.createdAt));
            } catch (ParseException e) {
                Logger.d(getClass().getSimpleName(), String.valueOf(e));
            }
        }
        return announcement;
    }

}
