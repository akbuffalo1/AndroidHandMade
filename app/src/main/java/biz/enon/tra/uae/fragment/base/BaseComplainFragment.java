package biz.enon.tra.uae.fragment.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.enon.tra.uae.R;

import biz.enon.tra.uae.dialog.AlertDialogFragment.OnOkListener;
import biz.enon.tra.uae.dialog.AttachmentPickerDialog.OnImageSourceSelectListener;
import biz.enon.tra.uae.global.AttachmentOption;
import biz.enon.tra.uae.interfaces.OnOpenPermissionExplanationDialogListener;
import biz.enon.tra.uae.manager.AttachmentManager;
import biz.enon.tra.uae.manager.AttachmentManager.OnImageGetCallback;

/**
 * Created by mobimaks on 11.08.2015.
 */
public abstract class BaseComplainFragment extends BaseServiceFragment
        implements OnImageSourceSelectListener, OnImageGetCallback,
        OnOpenPermissionExplanationDialogListener, OnOkListener {

    private AttachmentManager mAttachmentManager;

    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAttachmentManager = new AttachmentManager(getActivity(), this, this);
        setHasOptionsMenu(true);
    }

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mAttachmentManager.onRestoreInstanceState(savedInstanceState);
        }
    }

    @CallSuper
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_send, menu);
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            hideKeyboard(getView());
            sendComplainIfDataValid();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendComplainIfDataValid() {
        if (validateData()) {
            sendComplain();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAttachmentManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mAttachmentManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    protected abstract boolean validateData();

    protected abstract void sendComplain();

    @Nullable
    protected Uri getImageUri() {
        return mAttachmentManager.getImageUri();
    }

    protected void openImagePicker() {
        mAttachmentManager.openDefaultPicker(getActivity(), this);
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
                onAttachmentDeleted();
                break;
        }
    }

    protected abstract void onAttachmentDeleted();

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int _requestCode, @NonNull String[] _permissions, @NonNull int[] _grantResults) {
        if (!mAttachmentManager.onRequestPermissionsResult(this, _requestCode, _permissions, _grantResults)) {
            super.onRequestPermissionsResult(_requestCode, _permissions, _grantResults);
        }
    }

    @Override
    public final void onOpenPermissionExplanationDialog(int _requestCode, final String _explanation) {
        showMessage(_explanation);
    }

    @Override
    public void onOkPressed(int _messageId) {
        mAttachmentManager.onConfirmPermissionExplanationDialog(this);
    }

    @Override
    public void onDestroy() {
        mAttachmentManager = null;
        super.onDestroy();
    }

    @Override
    public void onLoadingCanceled() {

    }
}
