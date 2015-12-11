package biz.enon.tra.uae.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;

import biz.enon.tra.uae.interfaces.AttachmentResultListener;
import biz.enon.tra.uae.service.AttachmentUploadService;
import biz.enon.tra.uae.util.Logger;

/**
 * Created by mobimaks on 04.11.2015.
 */
public final class AttachmentUploadServiceManager {

    private final Context mContext;

    private AttachmentUploadService mUploadService;
    private AttachmentResultListener mResultListener;
    private boolean mIsServiceBound;

    public AttachmentUploadServiceManager(@NonNull final Context _context) {
        mContext = _context.getApplicationContext();
    }

    public void startAndConnectToService() {
        final Intent _startIntent = AttachmentUploadService.getStartIntent(mContext);
        startAndConnectToService(_startIntent);
        mContext.startService(_startIntent);
        mContext.bindService(_startIntent, mServiceConnection, 0);
    }

    public void bindToServiceAndSubscribeIfShould(AttachmentResultListener _resultListener) {
        mResultListener = _resultListener;
        if (mIsServiceBound) {
            mUploadService.subscribe(mResultListener);
        } else {
            mContext.bindService(AttachmentUploadService.getStartIntent(mContext), mServiceConnection, 0);
        }
    }

    public void subscribe(final AttachmentResultListener _resultListener) {
        mResultListener = _resultListener;
        if (mIsServiceBound) {
            mUploadService.subscribe(_resultListener);
        }
    }

    public void uploadAttachmentIfNotPending(final Uri _attachmentUri) {
        Logger.d(AttachmentUploadService.TAG, "uploadAttachmentIfNotPending isBound: " + mIsServiceBound);
        if (mIsServiceBound) {
            mUploadService.uploadAttachmentIfNotPending(_attachmentUri);
        } else {
            final Intent startIntent = AttachmentUploadService.getStartIntent(mContext, _attachmentUri);
            startAndConnectToService(startIntent);
        }
    }

    public boolean isAllUploadsCompleted() {
        return mUploadService.isTasksCompleted();
    }

    private void startAndConnectToService(@NonNull final Intent _startIntent) {
        mContext.startService(_startIntent);
        mContext.bindService(_startIntent, mServiceConnection, 0);
    }

    public final void unbindIfNeed() {
        if (mIsServiceBound) {
            mUploadService.unsubscribe();
            mContext.unbindService(mServiceConnection);
            mIsServiceBound = false;
            mUploadService = null;
        }
        mResultListener = null;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder _binder) {
            AttachmentUploadService.AttachmentServiceBinder binder = (AttachmentUploadService.AttachmentServiceBinder) _binder;
            mUploadService = binder.getService();
            mUploadService.subscribe(mResultListener);
            mIsServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceBound = false;
            mUploadService = null;
        }
    };
}
