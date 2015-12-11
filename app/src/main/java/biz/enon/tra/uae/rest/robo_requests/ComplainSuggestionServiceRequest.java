package biz.enon.tra.uae.rest.robo_requests;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.IOException;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.ComplainTRAServiceModel;
import biz.enon.tra.uae.util.ImageUtils;
import retrofit.client.Response;

/**
 * Created by Mikazme on 13/08/2015.
 */
public class ComplainSuggestionServiceRequest extends BaseRequest<Response, TRAServicesAPI> {

    private final ComplainTRAServiceModel mComplainTRAServiceModel;
    private final ContentResolver mContentResolver;
    private final Uri mImageUri;

    public ComplainSuggestionServiceRequest(final ComplainTRAServiceModel _complainTRAServiceModel,
                                            final Context _context,
                                            @Nullable final Uri _imageUri) {

        super(Response.class, TRAServicesAPI.class);
        mComplainTRAServiceModel = _complainTRAServiceModel;
        mContentResolver = _context.getApplicationContext().getContentResolver();
        mImageUri = _imageUri;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        try {
            if (mImageUri != null) {
                mComplainTRAServiceModel.attachment = ImageUtils.imageToBase64(mContentResolver, mImageUri);
            }
            return getService().sendSuggestion(mComplainTRAServiceModel);
        } catch (IOException e) {
            throw new Exception("Can't load image from device");
        }
    }
}
