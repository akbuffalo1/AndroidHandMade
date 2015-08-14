package com.uae.tra_smart_services.rest.new_request;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.new_request.ComplainServiceProviderModel;
import com.uae.tra_smart_services.util.BitmapUtils;

import java.io.IOException;

import retrofit.client.Response;

/**
 * Created by Vitaliy on 13/08/2015.
 */
public class ComplainAboutServiceRequest extends RetrofitSpiceRequest<Response, TRAServicesAPI> {

    private final ComplainServiceProviderModel mComplainServiceModel;
    private final ContentResolver mContentResolver;
    private final Uri mImageUri;

    public ComplainAboutServiceRequest(final ComplainServiceProviderModel _complainServiceProviderModel,
                                       final Context _context,
                                       final Uri _imageUri) {

        super(Response.class, TRAServicesAPI.class);
        mComplainServiceModel = _complainServiceProviderModel;
        mContentResolver = _context.getApplicationContext().getContentResolver();
        mImageUri = _imageUri;
    }

    @Override
    public final Response loadDataFromNetwork() throws Exception {
        try {
            mComplainServiceModel.attachment = BitmapUtils.imageToBase64(mContentResolver, mImageUri);
            return getService().complainServiceProvider(mComplainServiceModel);
        } catch (IOException e) {
            throw new Exception("Can't load image from device");
        }
    }

}
