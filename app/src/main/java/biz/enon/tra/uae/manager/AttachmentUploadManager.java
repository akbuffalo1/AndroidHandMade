package biz.enon.tra.uae.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import biz.enon.tra.uae.interfaces.AttachmentResultListener;
import biz.enon.tra.uae.rest.robo_requests.AttachmentUploadOperation;
import biz.enon.tra.uae.service.AttachmentUploadService;
import biz.enon.tra.uae.util.Logger;
import hugo.weaving.DebugLog;

/**
 * Created by mobimaks on 05.11.2015.
 */
public class AttachmentUploadManager {

    private static final int THREAD_POOL_SIZE = 0;
    private static final int THREAD_MAX_POOL_SIZE = 1;
    private static final long KEEP_ALIVE_TIME = 3L;
    private static final TimeUnit KEEP_ALIVE_UNIT = TimeUnit.MINUTES;

    private final Map<Uri, Future<String>> mTaskMap;
    private final Map<Uri, String> mResultsMap;

    private final ThreadPoolExecutor mExecutor;
    private final AttachmentHandler mHandler;

    private final AttachmentResultListener mServiceResultListener;

    public AttachmentUploadManager(@NonNull AttachmentResultListener _resultListener) {
        mResultsMap = new HashMap<>();
        mTaskMap = new HashMap<>();
        mServiceResultListener = _resultListener;
        mHandler = new AttachmentHandler(mResultListener);

        mExecutor = new ThreadPoolExecutor(
                THREAD_POOL_SIZE,
                THREAD_MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_UNIT,
                new LinkedBlockingQueue<Runnable>()) {
        };
    }

    @DebugLog
    public void uploadIfNotPendingOrLoadFromCacheAttachment(Context _context, final Uri _attachmentUri) {
        Logger.d(AttachmentUploadService.TAG, "uploadIfNotPendingOrLoadFromCacheAttachment : " + _attachmentUri + " | pending = " + mTaskMap.containsKey(_attachmentUri));
        if (!mTaskMap.containsKey(_attachmentUri)) {
            uploadOrLoadFromCacheAttachment(_context, _attachmentUri);
        }
    }

    public void uploadOrLoadFromCacheAttachment(final Context _context, final Uri _attachmentUri) {
        final String uploadedAttachmentId = mResultsMap.get(_attachmentUri);
        if (uploadedAttachmentId == null) {
            Logger.d(AttachmentUploadService.TAG, "uploadOrLoadFromCacheAttachment: upload " + _attachmentUri);
            uploadAttachment(_context, _attachmentUri);
        } else {
            Logger.d(AttachmentUploadService.TAG, "uploadOrLoadFromCacheAttachment: cache " + _attachmentUri);
            notifyAboutResult(_attachmentUri, uploadedAttachmentId);
        }
    }

    private void uploadAttachment(final Context _context, final Uri _attachmentUri) {
        AttachmentUploadOperation uploadOperation = new AttachmentUploadOperation(_context, _attachmentUri);
        uploadOperation.setResultListener(mOperationResultListener);
        Future<String> futureTask = mExecutor.submit(uploadOperation);
        mTaskMap.put(_attachmentUri, futureTask);
    }

    private void notifyAboutResult(final Uri _attachmentUri, final String _result) {
        mServiceResultListener.onResult(_attachmentUri, _result);
    }

    private void notifyAboutError(Uri _attachmentUri) {
        mServiceResultListener.onError(_attachmentUri);
    }

    public void removeAttachment(final Uri _attachmentUri) {
        final Future<String> future = mTaskMap.get(_attachmentUri);
        if (future != null) {
            boolean isCancelled = future.cancel(true);
            mTaskMap.remove(_attachmentUri);
            Logger.d(AttachmentUploadService.TAG, "isCancelled: " + isCancelled + ". Uri: " + _attachmentUri);
        }
    }

    public void cancelAllUploads() {
        mExecutor.shutdownNow();
        mTaskMap.clear();
    }

    public int getPendingTasksNumber() {
        return mTaskMap.size();
    }

    public boolean isTasksCompleted() {
        Logger.d(AttachmentUploadService.TAG, "isTasksCompleted: " + Arrays.toString(mTaskMap.keySet().toArray()));
        return getPendingTasksNumber() == 0;
    }

    private AttachmentResultListener mResultListener = new AttachmentResultListener() {

        @Override
        public void onResult(Uri _attachmentUri, String _result) {
            Logger.d(AttachmentUploadService.TAG, "mResultListener onResult: " + _attachmentUri);
            mTaskMap.remove(_attachmentUri);
            mResultsMap.put(_attachmentUri, _result);
            notifyAboutResult(_attachmentUri, _result);
        }

        @Override
        public void onError(Uri _attachmentUri) {
            Logger.d(AttachmentUploadService.TAG, "mResultListener onError: " + _attachmentUri);
            mTaskMap.remove(_attachmentUri);
            notifyAboutError(_attachmentUri);
        }
    };

    private AttachmentResultListener mOperationResultListener = new AttachmentResultListener() {

        @Override
        public void onResult(Uri _attachmentUri, String _result) {
            Message resultMessage = mHandler.obtainSuccessMessage(_attachmentUri, _result);
            resultMessage.sendToTarget();
        }

        @Override
        public void onError(Uri _attachmentUri) {
            Message resultMessage = mHandler.obtainErrorMessage(_attachmentUri);
            resultMessage.sendToTarget();
        }
    };
}