package biz.enon.tra.uae.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import biz.enon.tra.uae.interfaces.AttachmentResultListener;
import biz.enon.tra.uae.manager.AttachmentUploadManager;
import biz.enon.tra.uae.util.Logger;

/**
 * Created by mobimaks on 03.11.2015.
 */
public final class AttachmentUploadService extends Service implements AttachmentResultListener {

    public static final String TAG = "Task_queue";
    private static final String KEY_ATTACHMENT_URI = "ATTACHMENT_URI";

    private AttachmentServiceBinder mServiceBinder;
    private AttachmentUploadManager mAttachmentUploadManager;

    private AttachmentResultListener mResultListener;

    private Map<Uri, String> mResultsToNotify;
    private Set<Uri> mErrorsToNotify;

    public static Intent getStartIntent(@NonNull final Context _context) {
        return new Intent(_context, AttachmentUploadService.class);
    }

    public static Intent getStartIntent(@NonNull final Context _context,
                                        @NonNull final Uri _attachmentUri) {

        final Intent startIntent = new Intent(_context, AttachmentUploadService.class);
        startIntent.putExtra(KEY_ATTACHMENT_URI, _attachmentUri);
        return startIntent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "Service created");
        mResultsToNotify = new LinkedHashMap<>();
        mErrorsToNotify = new LinkedHashSet<>();

        mServiceBinder = new AttachmentServiceBinder();
        mAttachmentUploadManager = new AttachmentUploadManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final Uri attachmentUri = intent.getParcelableExtra(KEY_ATTACHMENT_URI);
            if (attachmentUri != null) {
                uploadAttachmentIfNotPending(attachmentUri);
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    public void subscribe(final AttachmentResultListener _resultListener) {
        mResultListener = _resultListener;
        Logger.d(TAG, "subscribe = " + String.valueOf(_resultListener) +
                " | results = " + mResultsToNotify.size() + " | errors = " + mErrorsToNotify.size());
        if (mResultListener != null) {
            notifyListenerAboutResults();
            notifyListenersAboutErrors();
        }
    }

    public boolean isTasksCompleted() {
        return mAttachmentUploadManager.isTasksCompleted();
    }

    public void unsubscribe() {
        mResultListener = null;
    }

    private void notifyListenerAboutResults() {
        Set<Entry<Uri, String>> resultsToNotify = mResultsToNotify.entrySet();
        for (Entry<Uri, String> uriStringEntry : resultsToNotify) {
            Uri attachmentUri = uriStringEntry.getKey();
            String result = uriStringEntry.getValue();
            Logger.d(TAG, "Notify activity (size = " + resultsToNotify.size() + "): " + attachmentUri + " | " + result);
            mResultListener.onResult(attachmentUri, result);
        }
        Logger.d(TAG, "Clear results cache");
        mResultsToNotify.clear();
    }

    private void notifyListenersAboutErrors() {
        for (Uri attachmentUri : mErrorsToNotify) {
            Logger.d(TAG, "Notify activity error (size = " + mErrorsToNotify.size() + "): " + attachmentUri);
            mResultListener.onError(attachmentUri);
        }
        mErrorsToNotify.clear();
    }

    @Override
    public void onResult(Uri _attachmentUri, String _result) {
        if (mResultListener == null) {
            mResultsToNotify.put(_attachmentUri, _result);
            Logger.d(TAG, "Cache result: " + _attachmentUri + " | " + _result + " | size = " + mResultsToNotify.size());
        } else {
            Logger.d(TAG, "Notify activity: " + _attachmentUri + " | " + _result);
            mResultListener.onResult(_attachmentUri, _result);
        }
    }

    @Override
    public void onError(Uri _attachmentUri) {
        if (mResultListener == null) {
            Logger.d(TAG, "Cache error: " + _attachmentUri);
            mErrorsToNotify.add(_attachmentUri);
        } else {
            Logger.d(TAG, "Notify activity about error: " + _attachmentUri);
            mResultListener.onError(_attachmentUri);
        }
    }

    public void uploadAttachment(@NonNull final Uri _attachmentUri) {
        mAttachmentUploadManager.uploadOrLoadFromCacheAttachment(this, _attachmentUri);
    }

    public void uploadAttachmentIfNotPending(@NonNull final Uri _attachmentUri) {
        mAttachmentUploadManager.uploadIfNotPendingOrLoadFromCacheAttachment(this, _attachmentUri);
    }

    public void removeAttachment(@NonNull final Uri _attachmentUri) {
        mAttachmentUploadManager.removeAttachment(_attachmentUri);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "Service destroyed");
        mResultsToNotify = null;
        mErrorsToNotify = null;
        mAttachmentUploadManager.cancelAllUploads();
        mAttachmentUploadManager = null;
        mServiceBinder = null;
        super.onDestroy();
    }

    public final class AttachmentServiceBinder extends Binder {

        public AttachmentUploadService getService() {
            return AttachmentUploadService.this;
        }

    }

}
