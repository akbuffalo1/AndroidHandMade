package biz.enon.tra.uae.rest;

import android.support.annotation.NonNull;

import com.google.gson.JsonElement;

import biz.enon.tra.uae.rest.model.request.AttachmentUploadRequestModel;
import biz.enon.tra.uae.rest.model.response.AttachmentUploadResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

import static biz.enon.tra.uae.global.ServerConstants.DYNAMIC_SERVICE_LIST;
import static biz.enon.tra.uae.global.ServerConstants.PATH_HOLDER;
import static biz.enon.tra.uae.global.ServerConstants.UPLOAD_ATTACHMENT;

/**
 * Created by mobimaks on 19.10.2015.
 */
public interface DynamicServicesApi {

    @POST(DYNAMIC_SERVICE_LIST + "/{" + PATH_HOLDER + "}")
    Response performPostRequest(@Path(PATH_HOLDER) String _id,
                                @NonNull final @Body JsonElement _body);

    @POST(UPLOAD_ATTACHMENT)
    AttachmentUploadResponse uploadAttachment(@NonNull final @Body AttachmentUploadRequestModel _attachment);

}