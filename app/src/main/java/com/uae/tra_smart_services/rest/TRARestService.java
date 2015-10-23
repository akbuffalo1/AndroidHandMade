package com.uae.tra_smart_services.rest;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.uae.tra_smart_services.entities.dynamic_service.BaseInputItem;
import com.uae.tra_smart_services.entities.dynamic_service.DynamicService;
import com.uae.tra_smart_services.global.ServerConstants;
import com.uae.tra_smart_services.rest.gson_deserializer.DynamicServiceDeserializer;
import com.uae.tra_smart_services.rest.gson_deserializer.InputItemDeserializer;

/**
 * Created by Mikazme on 13/08/2015.
 */
public final class TRARestService extends BaseRetrofitSpiceService {

    @Override
    protected final String getServerUrl() {
        return ServerConstants.BASE_URL;
    }

    @NonNull
    @Override
    protected GsonBuilder getGsonBuilder() {
        return super.getGsonBuilder()
                .registerTypeAdapter(BaseInputItem.class, new InputItemDeserializer())
                .registerTypeAdapter(DynamicService.class, new DynamicServiceDeserializer())
                ;
    }
}
