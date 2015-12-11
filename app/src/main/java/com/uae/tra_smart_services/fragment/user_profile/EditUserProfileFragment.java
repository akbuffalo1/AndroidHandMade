package com.uae.tra_smart_services.fragment.user_profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.UserProfileQuestionAdapter;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.customviews.HexagonView.ScaleType;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.customviews.ProfileController;
import com.uae.tra_smart_services.customviews.ProfileController.ControllerButton;
import com.uae.tra_smart_services.customviews.ProfileController.OnControllerButtonClickListener;
import com.uae.tra_smart_services.dialog.AlertDialogFragment.OnOkListener;
import com.uae.tra_smart_services.dialog.AttachmentPickerDialog.OnImageSourceSelectListener;
import com.uae.tra_smart_services.entities.HexagonViewTarget;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.AttachmentOption;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.interfaces.Loader.BackButton;
import com.uae.tra_smart_services.interfaces.Loader.Cancelled;
import com.uae.tra_smart_services.interfaces.OnOpenPermissionExplanationDialogListener;
import com.uae.tra_smart_services.manager.AttachmentManager;
import com.uae.tra_smart_services.manager.AttachmentManager.OnImageGetCallback;
import com.uae.tra_smart_services.rest.model.request.UserNameModel;
import com.uae.tra_smart_services.rest.model.response.SecurityQuestionResponse;
import com.uae.tra_smart_services.rest.model.response.UserProfileResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.ChangeUserProfileRequest;
import com.uae.tra_smart_services.util.StringUtils;

import java.util.List;

import retrofit.client.Response;

import static com.uae.tra_smart_services.global.C.MAX_USERNAME_LENGTH;
import static com.uae.tra_smart_services.global.C.MIN_USERNAME_LENGTH;

/**
 * Created by mobimaks on 08.09.2015.
 */
public final class EditUserProfileFragment extends BaseFragment
        implements OnClickListener, OnControllerButtonClickListener, OnImageGetCallback,
        OnOpenPermissionExplanationDialogListener, OnImageSourceSelectListener, OnOkListener, OnCheckedChangeListener {

    private static final String KEY_USER_PROFILE_MODEL = "USER_PROFILE_MODEL";
    private static final String KEY_EDIT_PROFILE_REQUEST = "EDIT_PROFILE_REQUEST";

    private HexagonView hvUserAvatar;
    private TextView tvChangePhoto;
    private EditText etFirstName, etLastName, etAddress, etPhone;

    private RelativeLayout rlEnhancedSecurity;
    private CheckBox cbEnhancedSecurity;
    private Spinner sSecurityQuestion;
    private EditText etSecurityAnswer;

    private UserProfileQuestionAdapter mQuestionAdapter;

    private AttachmentManager mAttachmentManager;
    private ProfileController pcProfileController;
    private Uri mImageUri;

    private UserProfileResponseModel mUserProfile;
    private boolean mIsEnhancedSecurityEnabled;
    private OnUserProfileDataChangeListener mProfileDataChangeListener;
    private EditUserProfileRequestListener mUserProfileRequestListener;
    private ChangeUserProfileRequest mChangeUserNameRequest;

    public static EditUserProfileFragment newInstance(@NonNull final UserProfileResponseModel _userProfile) {
        final EditUserProfileFragment fragment = new EditUserProfileFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_USER_PROFILE_MODEL, _userProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnUserProfileDataChangeListener) {
            mProfileDataChangeListener = (OnUserProfileDataChangeListener) _activity;
        }
    }

    @Override
    public void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);

        final UserProfileResponseModel userProfile = getArguments().getParcelable(KEY_USER_PROFILE_MODEL);
        mIsEnhancedSecurityEnabled = userProfile != null && userProfile.enhancedSecurity;

        if (_savedInstanceState == null) {
            mUserProfile = userProfile;
        } else {
            mUserProfile = _savedInstanceState.getParcelable(KEY_USER_PROFILE_MODEL);
        }
        mAttachmentManager = new AttachmentManager(getActivity(), this, this);
    }

    @Override
    protected final void initViews() {
        super.initViews();
        hvUserAvatar = findView(R.id.hvUserAvatar_FEUP);
        tvChangePhoto = findView(R.id.tvChangePhoto_FEUP);
        etFirstName = findView(R.id.etFirstName_FEUP);
        etLastName = findView(R.id.etLastName_FEUP);
        etAddress = findView(R.id.etAddress_FEUP);
        etPhone = findView(R.id.etPhone_FEUP);
        pcProfileController = findView(R.id.pcProfileController_FEUP);
        rlEnhancedSecurity = findView(R.id.rlEnhancedSecurity_FEUP);
        cbEnhancedSecurity = findView(R.id.cbEnhancedSecurity_FEUP);
        sSecurityQuestion = findView(R.id.sSecurityQuestion_FEUP);
        etSecurityAnswer = findView(R.id.etSecurityAnswer_FEUP);
    }

    @Override
    protected final void initListeners() {
        super.initListeners();
        tvChangePhoto.setOnClickListener(this);
        pcProfileController.setOnButtonClickListener(this);
        rlEnhancedSecurity.setOnClickListener(this);
        cbEnhancedSecurity.setOnCheckedChangeListener(this);
    }

    @Override
    public void onActivityCreated(final @Nullable Bundle _savedInstanceState) {
        super.onActivityCreated(_savedInstanceState);
        mUserProfileRequestListener = new EditUserProfileRequestListener();

        mQuestionAdapter = new UserProfileQuestionAdapter(getActivity());
        sSecurityQuestion.setAdapter(mQuestionAdapter);

        if (_savedInstanceState == null) {
            showUserProfile(mUserProfile);
        } else {
            mAttachmentManager.onRestoreInstanceState(_savedInstanceState);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setSecurityQuestionEnabled(cbEnhancedSecurity.isChecked());
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().getFromCache(UserProfileResponseModel.class, KEY_EDIT_PROFILE_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, mUserProfileRequestListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAttachmentManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onClick(final View _view) {
        switch (_view.getId()) {
            case R.id.tvChangePhoto_FEUP:
                openImagePicker();
                break;
            case R.id.rlEnhancedSecurity_FEUP:
                cbEnhancedSecurity.toggle();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cbEnhancedSecurity_FEUP) {
            setSecurityQuestionEnabled(isChecked);
        }
    }

    private void setSecurityQuestionEnabled(boolean _enabled) {
        sSecurityQuestion.setEnabled(_enabled);
        etSecurityAnswer.setEnabled(_enabled);

        final int textColor = ContextCompat.getColor(getActivity(), _enabled
                ? R.color.fragment_user_profile_text_color_primary
                : R.color.fragment_user_profile_hint_color);
        etSecurityAnswer.setTextColor(textColor);
        if (mQuestionAdapter != null) {
            mQuestionAdapter.setItemTextColor(textColor);
        }

    }

    private void openImagePicker() {
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
                initUserAvatar(mUserProfile);
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

    private void initUserAvatar(final UserProfileResponseModel _userProfile) {
        if (_userProfile.getImageUrl().isEmpty()) {
            hvUserAvatar.postScaleType(ScaleType.INSIDE_CROP);
            hvUserAvatar.setHexagonSrcDrawable(R.drawable.ic_user_placeholder);
        } else {
            Picasso.with(getActivity()).load(_userProfile.getImageUrl()).into(new HexagonViewTarget(hvUserAvatar));
        }
    }

    @Override
    public void onAttachmentGet(@NonNull final Uri _imageUri) {
        mImageUri = _imageUri;
        Glide.with(getActivity())
                .load(mImageUri)
                .into((Target) new HexagonViewTarget(hvUserAvatar, true));
//        Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap _bitmap, Picasso.LoadedFrom from) {
//                try {
//                    ExifInterface exifInterface = new ExifInterface(_imageUri.getPath());
//
//                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//                    Matrix matrix = new Matrix();
//                    switch (orientation) {
//                        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                            matrix.setScale(-1, 1);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            matrix.setRotate(180);
//                            break;
//                        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                            matrix.setRotate(180);
//                            matrix.postScale(-1, 1);
//                            break;
//                        case ExifInterface.ORIENTATION_TRANSPOSE:
//                            matrix.setRotate(90);
//                            matrix.postScale(-1, 1);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            matrix.setRotate(90);
//                            break;
//                        case ExifInterface.ORIENTATION_TRANSVERSE:
//                            matrix.setRotate(-90);
//                            matrix.postScale(-1, 1);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            matrix.setRotate(-90);
//                            break;
//                    }
//                    _bitmap = Bitmap.createBitmap(_bitmap, 0, 0, _bitmap.getWidth(), _bitmap.getHeight(), matrix, false);
//                } catch (IOException exc) {
//                    exc.printStackTrace();
//                }
//                hvUserAvatar.postScaleType(HexagonView.CENTER_CROP);
//                hvUserAvatar.setHexagonSrcDrawable(new BitmapDrawable(getResources(), _bitmap));
//                hvUserAvatar.setTag(null);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        };
//        hvUserAvatar.setTag(target);
    }

    @Override
    public void onControllerButtonClick(final View _view, final @ControllerButton int _buttonId) {
        switch (_buttonId) {
            case ProfileController.BUTTON_CANCEL:
                onBackPressed();
                break;
            case ProfileController.BUTTON_CONFIRM:
                if (validateData()) {
                    saveUserProfile();
                }
                break;
            case ProfileController.BUTTON_RESET:
                restoreUserProfile();
                break;
        }
    }

    private boolean validateData() {
        final String firstName = etFirstName.getText().toString().trim();
        final String lastName = etLastName.getText().toString().trim();


        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_fill_all_fields, C.TOAST_LENGTH).show();
            return false;
        }

        if (firstName.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_firstname_short, C.TOAST_LENGTH).show();
            return false;
        } else if (firstName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_firstname_long, C.TOAST_LENGTH).show();
            return false;
        }

        if (lastName.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_lastname_short, C.TOAST_LENGTH).show();
            return false;
        } else if (lastName.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_lastname_long, C.TOAST_LENGTH).show();
            return false;
        }

        if (!StringUtils.isAllLettersOrWhiteSpace(firstName)) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_first_name, C.TOAST_LENGTH).show();
            return false;
        }
        if (!StringUtils.isAllLettersOrWhiteSpace(lastName)) {
            Toast.makeText(getActivity(), R.string.authorization_invalid_last_name, C.TOAST_LENGTH).show();
            return false;
        }

        if (cbEnhancedSecurity.isChecked()) {
            if (etSecurityAnswer.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), R.string.error_fill_all_fields, C.TOAST_LENGTH).show();
                return false;
            }
        }
        return true;
    }

    private boolean isEnhancedSecurityStateChanged() {
        return mIsEnhancedSecurityEnabled != cbEnhancedSecurity.isChecked() || !etSecurityAnswer.getText().toString().isEmpty();
    }

    private void saveUserProfile() {
        final UserNameModel profile = new UserNameModel();
        profile.firstName = etFirstName.getText().toString();
        profile.lastName = etLastName.getText().toString();
        profile.imageUri = mImageUri;
        profile.enhancedSecurity = cbEnhancedSecurity.isChecked();
        if (profile.enhancedSecurity) {
            SecurityQuestionResponse securityQuestion = ((SecurityQuestionResponse) sSecurityQuestion.getSelectedItem());
            profile.secretQuestionAnswer = etSecurityAnswer.getText().toString();
            if (securityQuestion == null) {
                profile.secretQuestionType = mUserProfile.secretQuestionType;
            } else {
                profile.secretQuestionType = securityQuestion.id;
            }
        }
        mChangeUserNameRequest = new ChangeUserProfileRequest(getActivity(), profile);

        hideKeyboard(getView());
        loaderOverlayShow(getString(R.string.fragment_edit_user_profile_saving), mUserProfileRequestListener, false);
        loaderOverlayButtonBehavior(mUserProfileRequestListener);
        getSpiceManager().execute(mChangeUserNameRequest, KEY_EDIT_PROFILE_REQUEST,
                DurationInMillis.ALWAYS_EXPIRED, mUserProfileRequestListener);
    }

    private void restoreUserProfile() {
        etFirstName.setText(mUserProfile.firstName);
        etLastName.setText(mUserProfile.lastName);
        etPhone.setText(mUserProfile.mobile);
        mAttachmentManager.clearAttachment();
        etSecurityAnswer.setText(null);
        initUserAvatar(mUserProfile);
    }

    private void showUserProfile(final UserProfileResponseModel _userProfile) {
        etFirstName.setText(_userProfile.firstName);
        etLastName.setText(_userProfile.lastName);
        etPhone.setText(_userProfile.mobile);
        cbEnhancedSecurity.setChecked(_userProfile.enhancedSecurity);
        initUserAvatar(_userProfile);
        initSecretQuestionView(_userProfile);
    }

    private void initSecretQuestionView(UserProfileResponseModel _userProfile) {
        mQuestionAdapter.addData(_userProfile.secretQuestions);

        Integer selectedQuestionPosition = findQuestionPositionById(_userProfile.secretQuestions, _userProfile.secretQuestionType);
        if (selectedQuestionPosition != null) {
            sSecurityQuestion.setSelection(selectedQuestionPosition);
        }
        etSecurityAnswer.setText(_userProfile.secretQuestionAnswer);
    }

    @Nullable
    private Integer findQuestionPositionById(@Nullable List<SecurityQuestionResponse> _dataSource, @Nullable Integer _id) {
        if (_dataSource != null && _id != null) {
            for (int i = 0; i < _dataSource.size(); i++) {
                final SecurityQuestionResponse question = _dataSource.get(i);
                if (_id.equals(question.id)) {
                    return i;
                }
            }
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mAttachmentManager.onSaveInstanceState(outState);
        outState.putParcelable(KEY_USER_PROFILE_MODEL, mUserProfile);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onBackPressed() {
        final OnUserProfileDataChangeListener dataChangeListener = mProfileDataChangeListener;
        getFragmentManager().popBackStackImmediate();

        if (dataChangeListener != null) {
            dataChangeListener.onUserProfileDataChange(mUserProfile);
        }
        return true;
    }

    @Override
    public void onDetach() {
        mProfileDataChangeListener = null;
        super.onDetach();
    }

    private class EditUserProfileRequestListener implements RequestListener<UserProfileResponseModel>, BackButton, Cancelled {

        private boolean isRequestSuccess;

        @Override
        public void onRequestSuccess(UserProfileResponseModel result) {
            getSpiceManager().removeDataFromCache(UserProfileResponseModel.class, KEY_EDIT_PROFILE_REQUEST);
            if (isAdded() && result != null) {
                mUserProfile = result;
                Picasso.with(getActivity()).invalidate(mUserProfile.getImageUrl());
                isRequestSuccess = true;
                loaderOverlaySuccess(getString(R.string.fragment_edit_user_profile_success));
            }
        }

        @Override
        public void onBackButtonPressed(LoaderView.State _currentState) {
            getFragmentManager().popBackStackImmediate();
            if (isRequestSuccess) {
                onBackPressed();
            }
        }

        @Override
        public void onLoadingCanceled() {
            isRequestSuccess = false;
            if (getSpiceManager().isStarted()) {
                getSpiceManager().cancel(mChangeUserNameRequest);
                getSpiceManager().removeDataFromCache(UserProfileResponseModel.class, KEY_EDIT_PROFILE_REQUEST);
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(Response.class, KEY_EDIT_PROFILE_REQUEST);
            processError(spiceException);
        }
    }

    @Override
    protected final int getTitle() {
        return R.string.fragment_user_profile_title;
    }

    @Override
    protected final int getLayoutResource() {
        return R.layout.fragment_edit_user_profile;
    }

    public interface OnUserProfileDataChangeListener {
        void onUserProfileDataChange(final UserProfileResponseModel _userProfile);
    }
}
