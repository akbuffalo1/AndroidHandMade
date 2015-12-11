package biz.enon.tra.uae.manager;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import biz.enon.tra.uae.interfaces.AttachmentResultListener;

/**
 * Created by mobimaks on 05.11.2015.
 */
public final class AttachmentHandler extends Handler {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;

    private final AttachmentResultListener mResultListener;

    public AttachmentHandler(@NonNull final AttachmentResultListener _resultListener) {
        super(Looper.getMainLooper());
        mResultListener = _resultListener;
    }

    @Override
    @UiThread
    public void handleMessage(Message msg) {
        if (msg.what == SUCCESS) {
            AttachmentDataWrapper dataWrapper = (AttachmentDataWrapper) msg.obj;
            mResultListener.onResult(dataWrapper.mAttachmentUri, dataWrapper.mResult);
        } else if (msg.what == ERROR) {
            AttachmentDataWrapper dataWrapper = (AttachmentDataWrapper) msg.obj;
            mResultListener.onError(dataWrapper.mAttachmentUri);
        }
    }

    public Message obtainSuccessMessage(Uri _attachmentUri, String _result) {
        return obtainMessage(SUCCESS, new AttachmentDataWrapper(_attachmentUri, _result));
    }


    public Message obtainErrorMessage(Uri _attachmentUri) {
        return obtainMessage(ERROR, new AttachmentDataWrapper(_attachmentUri));
    }

    private static final class AttachmentDataWrapper {

        final Uri mAttachmentUri;
        String mResult;

        public AttachmentDataWrapper(@NonNull Uri _attachmentUri) {
            mAttachmentUri = _attachmentUri;
        }

        public AttachmentDataWrapper(@NonNull Uri _attachmentUri, @NonNull String _result) {
            mAttachmentUri = _attachmentUri;
            mResult = _result;
        }
    }
}
