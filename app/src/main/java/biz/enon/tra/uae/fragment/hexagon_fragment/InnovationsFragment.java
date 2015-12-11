package biz.enon.tra.uae.fragment.hexagon_fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.InnovationIdeaAdapter;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.customviews.ThemedImageView;
import biz.enon.tra.uae.dialog.AlertDialogFragment;
import biz.enon.tra.uae.dialog.AttachmentPickerDialog;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.AttachmentOption;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.interfaces.Loader.Cancelled;
import biz.enon.tra.uae.interfaces.OnOpenPermissionExplanationDialogListener;
import biz.enon.tra.uae.manager.AttachmentManager;
import biz.enon.tra.uae.rest.model.request.PostInnovationRequestModel;
import biz.enon.tra.uae.rest.robo_requests.PostInnovationRequest;
import retrofit.client.Response;

/**
 * Created by and on 29.09.15.
 */

public class InnovationsFragment extends BaseFragment implements //region Interfaces
        OnClickListener, AttachmentManager.OnImageGetCallback, OnOpenPermissionExplanationDialogListener, AttachmentPickerDialog.OnImageSourceSelectListener,
        Cancelled, OnItemSelectedListener, AlertDialogFragment.OnOkListener /*,OnCheckedChangeListener*/ {//endregion

    private static final String KEY_IS_SPINNER_CLICKED = "IS_SPINNER_CLICKED";
    private static final String KEY_SELECTED_IDEA_POSITION = "SELECTED_IDEA_POSITION";

    private EditText etTitle, etMessageDescription;
    private ThemedImageView tivAddAttachment;
    private Button btnSubmit;
    private SwitchCompat swType;
    private TextView tvPublic, tvPrivate;
    private Spinner sInnovationSpinner;
    private Context mContext;
    private TextView tvInnovativeIdea;

    private int color;

    private AttachmentManager mAttachmentManager;
    private InnovationIdeaAdapter mIdeasAdapter;
    private PostInnovationRequest mRequest;

    private boolean mIsSpinnerClicked, mIsUserClick, nothingSelected;
    private int mSelectedIdeaPosition;

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted() && mRequest != null) {
            getSpiceManager().cancel(mRequest);
        }
    }

    public static InnovationsFragment newInstance() {
        return new InnovationsFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        mContext = _activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAttachmentManager = new AttachmentManager(getActivity(), this, this);

        if (savedInstanceState != null) {
            mIsSpinnerClicked = savedInstanceState.getBoolean(KEY_IS_SPINNER_CLICKED);
            mSelectedIdeaPosition = savedInstanceState.getInt(KEY_SELECTED_IDEA_POSITION);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        tivAddAttachment = findView(R.id.tivAddAttachment_FIS);
        etTitle = findView(R.id.etTitle_FIS);
        setCapitalizeTextWatcher(etTitle);
        etMessageDescription = findView(R.id.etMessageDescription_FIS);
        setCapitalizeTextWatcher(etMessageDescription);
        btnSubmit = findView(R.id.btnSubmit_FIS);
        sInnovationSpinner = findView(R.id.sInnovateIdea_FIS);
        tvInnovativeIdea = findView(R.id.tvInnovativeIdea_FIS);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tivAddAttachment.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etTitle.setOnFocusChangeListener(this);
        etMessageDescription.setOnFocusChangeListener(this);
        tvInnovativeIdea.setOnClickListener(this);
        sInnovationSpinner.setOnItemSelectedListener(this);
        findView(R.id.tivArrowIcon_FIS).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mAttachmentManager.onRestoreInstanceState(savedInstanceState);
        }
        initInnovationIdeaSpinner();
    }

    private void initInnovationIdeaSpinner() {
        final String[] ideas = getResources().getStringArray(R.array.fragment_innovation_ideas);
        List<String> ideasList = new ArrayList<>(Arrays.asList(ideas));
        ideasList.add(getString(R.string.fragment_innovation_idea_title));
        mIdeasAdapter = new InnovationIdeaAdapter(getActivity(), ideasList);
        sInnovationSpinner.setAdapter(mIdeasAdapter);
        if (!mIsSpinnerClicked) {
            mSelectedIdeaPosition = mIdeasAdapter.getCount();
        }
        sInnovationSpinner.setSelection(mSelectedIdeaPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAttachmentManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard(v);
        switch (v.getId()) {
            /*case R.id.tivAddAttachment_FIS:
                openImagePicker();
                break;*/
            case R.id.btnSubmit_FIS:
                if (validateData()) {
                    sendComplain();
                }
                break;
            case R.id.tvInnovativeIdea_FIS:
            case R.id.tivArrowIcon_FIS:
                openInnovateIdeasSpinner();
                break;
        }
    }

    private void openInnovateIdeasSpinner() {
        mIsUserClick = true;
        sInnovationSpinner.performClick();
    }

    /*protected void openImagePicker() {
        final boolean isGalleryAvailable = mAttachmentManager.isGalleryPickAvailable();
        final boolean canGetPhoto = mAttachmentManager.canGetCameraPicture();
        if (isGalleryAvailable && canGetPhoto) {
            AttachmentPickerDialog.newInstance(this).show(getFragmentManager());
        } else if (isGalleryAvailable) {
            mAttachmentManager.openGallery(this);
        } else if (canGetPhoto) {
            mAttachmentManager.tryOpenCamera(this);
        } else {
            Toast.makeText(getActivity(), R.string.fragment_complain_about_service_no_camera_and_app, Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public void onImageSourceSelect(AttachmentOption _option) {
        switch (_option) {
            case GALLERY:
                mAttachmentManager.openGallery(this);
                break;
            case CAMERA:
                mAttachmentManager.tryOpenCamera(this);
                break;
            case DELETE_ATTACHMENT:
                mAttachmentManager.clearAttachment();
                tivAddAttachment.setImageResource(R.drawable.ic_action_attachment);
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
    public void onOpenPermissionExplanationDialog(int _requestCode, final String _explanation) {
        showMessage(_explanation);
    }

    @Override
    public final void onOkPressed(int _messageId) {
        mAttachmentManager.onConfirmPermissionExplanationDialog(this);
    }

    private boolean validateData() {
        if (etTitle.getText().toString().trim().isEmpty() ||
                etMessageDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_fill_all_fields, C.TOAST_LENGTH).show();
            return false;
        }

        if (nothingSelected) {
            Toast.makeText(getActivity(), R.string.fragment_innovations_no_selected_item, C.TOAST_LENGTH).show();
            return false;
        }

        return true;
    }

    private void sendComplain() {
        loaderOverlayShow(mContext.getString(R.string.str_sending), this, false);
        loaderOverlayButtonBehavior(new Loader.BackButton() {
            @Override
            public void onBackButtonPressed(LoaderView.State _currentState) {
                getFragmentManager().popBackStack();
                if (_currentState == LoaderView.State.FAILURE || _currentState == LoaderView.State.SUCCESS) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        PostInnovationRequestModel model = new PostInnovationRequestModel();
        model.title = etTitle.getText().toString();
        model.message = etMessageDescription.getText().toString();
        model.type = String.valueOf(sInnovationSpinner.getSelectedItemPosition() + 1);

        getSpiceManager().execute(mRequest = new PostInnovationRequest(model), new PostInnovationRequestListener());

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loaderOverlaySuccess(mContext.getString(R.string.str_reuqest_has_been_sent));
            }
        }, 2500);*/
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mIsUserClick || mIsSpinnerClicked) {
            mSelectedIdeaPosition = position;
            tvInnovativeIdea.setVisibility(View.INVISIBLE);
            sInnovationSpinner.setVisibility(View.VISIBLE);
            mIsSpinnerClicked = true;
            nothingSelected = false;
        } else {
            nothingSelected = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onAttachmentGet(@NonNull Uri _uri) {
        tivAddAttachment.setImageResource(R.drawable.ic_check);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_IDEA_POSITION, mSelectedIdeaPosition);
        outState.putBoolean(KEY_IS_SPINNER_CLICKED, mIsSpinnerClicked);
        mAttachmentManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /*@Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        togglePrivacy(isChecked);
    }

    private void togglePrivacy(boolean isChecked) {
        if (isChecked) {
            tvPublic.setTextColor(Color.LTGRAY);
            tvPrivate.setTextColor(color);
        } else {
            tvPrivate.setTextColor(Color.LTGRAY);
            tvPublic.setTextColor(color);
        }
    }*/

    @Override
    public void onDetach() {
        mContext = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        mAttachmentManager = null;
        super.onDestroy();
    }

    private class PostInnovationRequestListener implements RequestListener<Response> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            processError(spiceException);
        }

        @Override
        public void onRequestSuccess(Response response) {
            loaderOverlaySuccess(getString(R.string.innovation_has_sent));
        }
    }

    @Override
    protected int getTitle() {
        return R.string.str_innovations;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_innovations_send;
    }
}
