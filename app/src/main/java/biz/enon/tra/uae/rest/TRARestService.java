package biz.enon.tra.uae.rest;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;

import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem;
import biz.enon.tra.uae.entities.dynamic_service.DynamicService;
import biz.enon.tra.uae.entities.dynamic_service.InputItemsPage;
import biz.enon.tra.uae.global.ServerConstants;
import biz.enon.tra.uae.rest.gson_deserializer.AnnouncementDeserializer;
import biz.enon.tra.uae.rest.gson_deserializer.DynamicServiceDeserializer;
import biz.enon.tra.uae.rest.gson_deserializer.InputItemDeserializer;
import biz.enon.tra.uae.rest.gson_deserializer.InputItemsPageDeserializer;
import biz.enon.tra.uae.rest.gson_deserializer.TransactionDeserializer;
import biz.enon.tra.uae.rest.model.response.GetAnnouncementsResponseModel;
import biz.enon.tra.uae.rest.model.response.GetTransactionResponseModel;

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
                .registerTypeAdapter(InputItemsPage.class, new InputItemsPageDeserializer())
                .registerTypeAdapter(DynamicService.class, new DynamicServiceDeserializer())
                .registerTypeAdapter(GetTransactionResponseModel.class, new TransactionDeserializer())
                .registerTypeAdapter(GetAnnouncementsResponseModel.Announcement.class, new AnnouncementDeserializer())
                ;
    }
}
