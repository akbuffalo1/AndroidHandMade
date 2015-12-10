package com.uae.tra_smart_services.rest.robo_requests;

import android.content.Context;
import android.net.Uri;
import android.os.Process;
import android.support.annotation.NonNull;

import com.uae.tra_smart_services.interfaces.AttachmentResultListener;
import com.uae.tra_smart_services.rest.DynamicServicesApi;
import com.uae.tra_smart_services.rest.RestClient;
import com.uae.tra_smart_services.rest.model.request.AttachmentUploadRequestModel;
import com.uae.tra_smart_services.rest.model.response.AttachmentUploadResponse;
import com.uae.tra_smart_services.util.ImageUtils;
import com.uae.tra_smart_services.util.Logger;

import java.io.InterruptedIOException;
import java.util.concurrent.Callable;

import static com.uae.tra_smart_services.service.AttachmentUploadService.TAG;

/**
 * Created by mobimaks on 05.11.2015.
 */
public class AttachmentUploadOperation implements Callable<String> {

    public final Context mContext;
    public final Uri mAttachmentUri;
    public AttachmentResultListener mResultListener;

    public AttachmentUploadOperation(@NonNull final Context _context,
                                     @NonNull final Uri _attachmentUri) {
        mContext = _context;
        mAttachmentUri = _attachmentUri;
    }

    public void setResultListener(AttachmentResultListener _resultListener) {
        mResultListener = _resultListener;
    }

    public String call() throws Exception {
        try {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Logger.d(TAG, "Thread id: " + Thread.currentThread().getId());
            Logger.d(TAG, "AttachmentUploadOperation. Start uri: " + mAttachmentUri.toString());

            final String imageBase64 = ImageUtils.imageToBase64(mContext.getContentResolver(), mAttachmentUri);
            Logger.d(TAG, "Decode end. Length = " + (imageBase64 == null ? 0 : imageBase64.length()));
            final DynamicServicesApi dynamicServicesApi = RestClient.getInstance(mContext.getApplicationContext()).getDynamicServicesApi();
            final AttachmentUploadRequestModel attachmentUploadModel = new AttachmentUploadRequestModel(imageBase64);

            AttachmentUploadResponse uploadResponse = dynamicServicesApi.uploadAttachment(attachmentUploadModel);
            Logger.d(TAG, "Upload end, id: " + uploadResponse.attachmentId);

            Logger.d(TAG, "AttachmentUploadOperation. End uri: " + mAttachmentUri.toString() + ", id: " + uploadResponse.attachmentId);
            Logger.d(TAG, " ");
            if (mResultListener != null) {
                mResultListener.onResult(mAttachmentUri, uploadResponse.attachmentId);
            }

            return uploadResponse.attachmentId;
        } catch (Exception e) {
            if (e instanceof InterruptedIOException || (e.getCause() instanceof InterruptedIOException)) {
                Logger.d(TAG, "Catch exception. Type: " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
            } else {
                Logger.d(TAG, "Simple exception");
                if (mResultListener != null) {
                    mResultListener.onError(mAttachmentUri);
                }
            }
            throw e;
        }
    }
}
