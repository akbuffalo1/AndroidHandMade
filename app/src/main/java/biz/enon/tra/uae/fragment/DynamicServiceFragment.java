package biz.enon.tra.uae.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.enon.tra.uae.R;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.LinkedHashSet;
import java.util.Set;

import biz.enon.tra.uae.dialog.AlertDialogFragment;
import biz.enon.tra.uae.dialog.AttachmentPickerDialog;
import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem;
import biz.enon.tra.uae.entities.dynamic_service.DynamicService;
import biz.enon.tra.uae.entities.dynamic_service.InputItemsPage;
import biz.enon.tra.uae.entities.dynamic_service.input_item.AttachmentInputItem;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.AttachmentOption;
import biz.enon.tra.uae.interfaces.AttachmentResultListener;
import biz.enon.tra.uae.interfaces.OnOpenAttachmentPickerListener;
import biz.enon.tra.uae.interfaces.OnOpenPermissionExplanationDialogListener;
import biz.enon.tra.uae.manager.AttachmentManager;
import biz.enon.tra.uae.manager.AttachmentUploadServiceManager;
import biz.enon.tra.uae.rest.model.response.DynamicServiceInfoResponseModel;
import biz.enon.tra.uae.rest.robo_requests.DynamicServiceDetailsRequest;
import biz.enon.tra.uae.rest.robo_requests.DynamicServiceRequest;
import biz.enon.tra.uae.service.AttachmentUploadService;
import biz.enon.tra.uae.util.Logger;
import retrofit.client.Response;

/**
 * Created by mobimaks on 21.10.2015.
 */
public final class DynamicServiceFragment extends BaseFragment
        implements OnClickListener, OnOpenAttachmentPickerListener, AttachmentManager.OnImageGetCallback,
        OnOpenPermissionExplanationDialogListener, AlertDialogFragment.OnOkListener, AttachmentPickerDialog.OnImageSourceSelectListener, AttachmentResultListener {

    //region Const
    private static final String KEY_DYNAMIC_REQUEST = "DYNAMIC_REQUEST";
    private static final String KEY_SERVICE_INFO = "SERVICE_INFO";
    private static final String KEY_DYNAMIC_SERVICE_REQUEST = "DYNAMIC_SERVICE_REQUEST";
    private static final String KEY_DYNAMIC_SERVICE = "DYNAMIC_SERVICE";
    private static final String KEY_WAITING_ATTACHMENTS_TO_UPLOAD = "WAITING_ATTACHMENTS_TO_UPLOAD";

    private static final String KEY_ATTACHMENT_CALLER_ID = "ATTACHMENT_CALLER_ID";
    //endregion

    //region Views
    private LinearLayout llContainer;
    //endregion

    //region Variables
    private DynamicServiceInfoResponseModel mServiceInfo;
    private DynamicServiceRequestListener mServiceRequestListener;
    private DynamicRequestListener mDynamicRequestListener;

    private DynamicService mDynamicService;
    private AttachmentManager mAttachmentManager;
    private String mAttachmentCallerId;
    private boolean mIsServiceLoaded;

    private AttachmentUploadServiceManager mAttachmentUploadManager;
    private boolean mIsWaitingAttachmentsToUploadBeforeSendData;
    //endregion

    public static DynamicServiceFragment newInstance(final DynamicServiceInfoResponseModel _serviceInfo) {
        final DynamicServiceFragment fragment = new DynamicServiceFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_SERVICE_INFO, _serviceInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        mAttachmentManager = new AttachmentManager(getActivity(), this, this);
        mAttachmentUploadManager = new AttachmentUploadServiceManager(getActivity());

        final Bundle args = getArguments();
        mServiceInfo = args.getParcelable(KEY_SERVICE_INFO);
    }

    @Override
    protected final void initData() {
        super.initData();
        mServiceRequestListener = new DynamicServiceRequestListener();
        mDynamicRequestListener = new DynamicRequestListener();
    }

    @Override
    protected final void initViews() {
        super.initViews();
        llContainer = findView(R.id.llContainer_FDS);
    }

    @Override
    public final void onActivityCreated(final Bundle _savedInstanceState) {
        super.onActivityCreated(_savedInstanceState);

        if (_savedInstanceState != null) {
            mAttachmentManager.onRestoreInstanceState(_savedInstanceState);
            mAttachmentCallerId = _savedInstanceState.getString(KEY_ATTACHMENT_CALLER_ID, "");
            mIsServiceLoaded = _savedInstanceState.getBoolean(KEY_DYNAMIC_SERVICE);
            mIsWaitingAttachmentsToUploadBeforeSendData = _savedInstanceState.getBoolean(KEY_WAITING_ATTACHMENTS_TO_UPLOAD);
        } else {
            mIsServiceLoaded = false;
        }

        if (mIsServiceLoaded) {
            mDynamicService = new DynamicService();
            mDynamicService.onRestoreInstanceState(_savedInstanceState);
            initDynamicViews();
        } else {
            loaderDialogShow();
            loadDynamicServiceInfo();
        }
    }

    private void loadDynamicServiceInfo() {
        final DynamicServiceDetailsRequest request = new DynamicServiceDetailsRequest(mServiceInfo.id);
        getSpiceManager().execute(request, KEY_DYNAMIC_SERVICE_REQUEST,
                DurationInMillis.ALWAYS_EXPIRED, mServiceRequestListener);
    }

    private void initDynamicViews() {
        toolbarTitleManager.setTitle(mDynamicService.serviceName);

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (InputItemsPage page : mDynamicService.pages) {//TODO: add pagination
            for (BaseInputItem inputItem : page.inputItems) {
                llContainer.addView(inputItem.getView(inflater, llContainer));
                if (inputItem.isAttachmentItem()) {
                    ((AttachmentInputItem) inputItem).setAttachmentCallback(this);
                }
            }
        }

        final Button button = (Button) inflater.inflate(R.layout.input_item_button, llContainer, false);
        button.setText(mDynamicService.buttonText);
        button.setOnClickListener(this);
        llContainer.addView(button);

        mAttachmentUploadManager.startAndConnectToService();
        mAttachmentUploadManager.subscribe(this);
    }

    @Override
    public final void onOpenAttachmentPicker(final AttachmentInputItem _inputItem) {
        mAttachmentCallerId = _inputItem.getId();
        mAttachmentManager.openDefaultPicker(getActivity(), this, _inputItem.getAttachmentUri() != null);
    }

    @Override
    public final void onImageSourceSelect(AttachmentOption _option) {
        switch (_option) {
            case GALLERY:
                mAttachmentManager.openGallery(this);
                break;
            case CAMERA:
                mAttachmentManager.tryOpenCamera(this);
                break;
            case DELETE_ATTACHMENT:
                mAttachmentManager.clearAttachment();
                notifyInputItemDataChanged(null);
                break;
        }
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int _requestCode, @NonNull String[] _permissions, @NonNull int[] _grantResults) {
        if (!mAttachmentManager.onRequestPermissionsResult(this, _requestCode, _permissions, _grantResults)) {
            super.onRequestPermissionsResult(_requestCode, _permissions, _grantResults);
        }
    }

    @Override
    public void onOpenPermissionExplanationDialog(int _requestCode, String _explanation) {
        showMessage(_explanation);
    }

    @Override
    public void onOkPressed(int _messageId) {
        mAttachmentManager.onConfirmPermissionExplanationDialog(this);
    }

    @Override
    public void onAttachmentGet(@NonNull Uri _imageUri) {
        mAttachmentManager.clearAttachment();
        notifyInputItemDataChanged(_imageUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAttachmentManager.onActivityResult(requestCode, resultCode, data);
    }

    private void notifyInputItemDataChanged(@Nullable Uri _imageUri) {
        if (mDynamicService != null) {
            searchAttachmentCaller:
            for (InputItemsPage page : mDynamicService.pages) {
                for (BaseInputItem inputItem : page.inputItems) {
                    if (inputItem.getId().equals(mAttachmentCallerId)) {
                        AttachmentInputItem attachmentInputItem = (AttachmentInputItem) inputItem;
                        attachmentInputItem.onAttachmentStateChanged(_imageUri);
                        if (_imageUri != null) {
                            Logger.d(AttachmentUploadService.TAG, "notifyInputItemDataChanged uploadAttachmentIfNotPending");
                            mAttachmentUploadManager.uploadAttachmentIfNotPending(_imageUri);
                        }
                        break searchAttachmentCaller;
                    }
                }
            }
        }
        mAttachmentCallerId = null;
    }

    @Override
    public final void onStart() {
        super.onStart();
        Logger.d(AttachmentUploadService.TAG, "onStart");
        getSpiceManager().getFromCache(DynamicService.class, KEY_DYNAMIC_SERVICE_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, mServiceRequestListener);

        getDynamicSpiceManager().getFromCache(Response.class, KEY_DYNAMIC_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, mDynamicRequestListener);

        mAttachmentUploadManager.bindToServiceAndSubscribeIfShould(this);
    }

    @Override
    public final void onClick(final View _view) {
        switch (_view.getId()) {
            case R.id.btnSubmit_IIB:
                validateAndSendData();
                break;
        }
    }

    private void validateAndSendData() {
        hideKeyboard(getView());

        if (validateAttachmentsUploadState()) {
            if (mDynamicService.isDataValid()) {
                loaderDialogShow();
                final DynamicServiceRequest request = new DynamicServiceRequest(mDynamicService);
                getDynamicSpiceManager().execute(request, KEY_DYNAMIC_REQUEST, DurationInMillis.ALWAYS_EXPIRED, mDynamicRequestListener);
            } else {
                loaderDialogDismiss();
                Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_SHORT).show();
            }
        } else {
            loaderDialogShow();
        }
    }

    private boolean validateAttachmentsUploadState() {
        Set<Uri> attachmentSetToUpload = getUnUploadedAttachments();
        mIsWaitingAttachmentsToUploadBeforeSendData = !attachmentSetToUpload.isEmpty();
        if (mIsWaitingAttachmentsToUploadBeforeSendData) {
            for (Uri attachmentUri : attachmentSetToUpload) {
                mAttachmentUploadManager.uploadAttachmentIfNotPending(attachmentUri);
            }
        }
        return !mIsWaitingAttachmentsToUploadBeforeSendData;
    }

    @NonNull
    private Set<Uri> getUnUploadedAttachments() {
        Set<Uri> attachmentSetToUpload = new LinkedHashSet<>();
        for (InputItemsPage page : mDynamicService.pages) {
            for (BaseInputItem inputItem : page.inputItems) {
                if (inputItem.isAttachmentItem() && !inputItem.isDataValid()) {
                    AttachmentInputItem attachmentInputItem = (AttachmentInputItem) inputItem;
                    Uri attachmentUri = attachmentInputItem.getAttachmentUri();
                    if (attachmentUri != null) {
                        Logger.d(AttachmentUploadService.TAG, "getUnUploadedAttachments. " + attachmentInputItem.getQueryName() + " | " + attachmentInputItem);
                        attachmentSetToUpload.add(attachmentUri);
                    }
                }
            }
        }
        return attachmentSetToUpload;
    }

    @Override
    public void onResult(Uri _attachmentUri, String _result) {
        notifyAttachmentItemAboutResult(_attachmentUri, _result);
        Logger.d(AttachmentUploadService.TAG,
                "Dynamic fragment. onResult: " + _attachmentUri +
                        " | IsWaiting = " + mIsWaitingAttachmentsToUploadBeforeSendData +
                        " | isAllUploadsCompleted = " + mAttachmentUploadManager.isAllUploadsCompleted());

        if (mIsWaitingAttachmentsToUploadBeforeSendData && mAttachmentUploadManager.isAllUploadsCompleted()) {
            mIsWaitingAttachmentsToUploadBeforeSendData = false;
            validateAndSendData();
        }
    }

    @Override
    public void onError(Uri _attachmentUri) {
        notifyAttachmentItemAboutResult(_attachmentUri, null);
        Logger.d(AttachmentUploadService.TAG,
                "Dynamic fragment. onError: " + _attachmentUri +
                        " | IsWaiting = " + mIsWaitingAttachmentsToUploadBeforeSendData +
                        " | isAllUploadsCompleted = " + mAttachmentUploadManager.isAllUploadsCompleted());
        if (mIsWaitingAttachmentsToUploadBeforeSendData && mAttachmentUploadManager.isAllUploadsCompleted()) {
            mIsWaitingAttachmentsToUploadBeforeSendData = false;
            Toast.makeText(getActivity(), "Attachment upload error", Toast.LENGTH_SHORT).show();
            loaderDialogDismiss();
        }
    }

    private void notifyAttachmentItemAboutResult(@NonNull Uri _attachmentUri, @Nullable String _result) {
        for (InputItemsPage page : mDynamicService.pages) {
            for (BaseInputItem inputItem : page.inputItems) {
                if (inputItem.isAttachmentItem()) {
                    AttachmentInputItem attachmentInputItem = (AttachmentInputItem) inputItem;
                    if (_attachmentUri.equals(attachmentInputItem.getAttachmentUri())) {
                        Logger.d(AttachmentUploadService.TAG, "notifyAttachmentItemAboutResult. " + _attachmentUri + ": " + String.valueOf(_result) + "| attachmentInputItem: " + attachmentInputItem);
                        attachmentInputItem.setAttachmentId(_result);
                    }
                }
            }
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle _outState) {
        if (mDynamicService != null) {
            mDynamicService.onSaveInstanceState(_outState);
        }
        if (mAttachmentCallerId != null) {
            _outState.putString(KEY_ATTACHMENT_CALLER_ID, mAttachmentCallerId);
        }
        mAttachmentManager.onSaveInstanceState(_outState);
        _outState.putBoolean(KEY_WAITING_ATTACHMENTS_TO_UPLOAD, mIsWaitingAttachmentsToUploadBeforeSendData);
        _outState.putBoolean(KEY_DYNAMIC_SERVICE, mIsServiceLoaded);
        super.onSaveInstanceState(_outState);
    }

    @Override
    public void onStop() {
        Logger.d(AttachmentUploadService.TAG, "onStop");
        mAttachmentUploadManager.unbindIfNeed();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mAttachmentManager = null;
        mAttachmentUploadManager = null;
        super.onDestroy();
    }

    private final class DynamicRequestListener implements RequestListener<Response> {

        @Override
        public void onRequestSuccess(Response result) {
            getDynamicSpiceManager().removeDataFromCache(Response.class, KEY_DYNAMIC_SERVICE);
            if (isAdded() && result != null) {
                loaderDialogDismiss();
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getDynamicSpiceManager().removeDataFromCache(Response.class, KEY_DYNAMIC_SERVICE);
            if (isAdded()) {
                loaderDialogDismiss();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private final class DynamicServiceRequestListener implements RequestListener<DynamicService> {

        @Override
        public final void onRequestSuccess(final DynamicService _service) {
            getSpiceManager().removeDataFromCache(DynamicService.class, KEY_DYNAMIC_SERVICE_REQUEST);
            if (isAdded() && _service != null) {
                mDynamicService = _service;
                mIsServiceLoaded = true;
                initDynamicViews();
                loaderDialogDismiss();
            }
        }

        @Override
        public final void onRequestFailure(final SpiceException _spiceException) {
            getSpiceManager().removeDataFromCache(DynamicService.class, KEY_DYNAMIC_SERVICE_REQUEST);
            if (isAdded()) {
                loaderDialogDismiss();
            }
        }

    }

    @Override
    protected final int getTitle() {
        return 0;
    }

    @Override
    protected final int getLayoutResource() {
        return R.layout.fragment_dynamic_service;
    }

}
