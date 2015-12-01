package com.uae.tra_smart_services.fragment.user_profile;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.TRAApplication;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.customviews.HexagonView.ScaleType;
import com.uae.tra_smart_services.entities.HexagonViewTarget;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.rest.model.request.LogoutRequestModel;
import com.uae.tra_smart_services.rest.model.response.UserProfileResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.LogoutRequest;
import com.uae.tra_smart_services.util.PreferenceManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit.client.Response;

/**
 * Created by mobimaks on 08.09.2015.
 */
public final class UserProfileFragment extends BaseFragment implements OnClickListener {

    private static final String KEY_LOGOUT_REQUEST = "LOGOUT_REQUEST";
    private static final String KEY_USER_PROFILE_MODEL = "USER_PROFILE_MODEL";

    public static final int USER_PROFILE_EDIT_PROFILE = 0;
    public static final int USER_PROFILE_CHANGE_PASSWORD = 1;
    public static final int USER_PROFILE_RESET_PASSWORD = 2;
    public static final int USER_PROFILE_LOGOUT = 3;

    @IntDef({USER_PROFILE_EDIT_PROFILE, USER_PROFILE_CHANGE_PASSWORD, USER_PROFILE_RESET_PASSWORD, USER_PROFILE_LOGOUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserProfileAction {
    }

    private HexagonView hvUserAvatar;
    private TextView tvUsername;
    private LinearLayout llEditProfile, llChangePassword, llLogout /* llResetPassword, */;

    private UserProfileResponseModel mUserProfile;

    private OnUserProfileClickListener mProfileClickListener;
    private LogoutRequest mLogoutRequest;

    public static UserProfileFragment newInstance(final UserProfileResponseModel _userProfile) {
        final UserProfileFragment fragment = new UserProfileFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_USER_PROFILE_MODEL, _userProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnUserProfileClickListener) {
            mProfileClickListener = (OnUserProfileClickListener) _activity;
        }
    }

    @Override
    public void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        if (_savedInstanceState == null) {
            mUserProfile = getArguments().getParcelable(KEY_USER_PROFILE_MODEL);
        } else {
            mUserProfile = _savedInstanceState.getParcelable(KEY_USER_PROFILE_MODEL);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        hvUserAvatar = findView(R.id.hvUserAvatar_FUP);
        tvUsername = findView(R.id.tvUsername_FUP);

        llEditProfile = findView(R.id.llEditProfile_FUP);
        llChangePassword = findView(R.id.llChangePassword_FUP);
        llLogout = findView(R.id.llLogout_FUP);

        if (!TextUtils.isEmpty(mUserProfile.getImageUrl())) {
            Picasso.with(getActivity()).load(mUserProfile.getImageUrl()).into(new HexagonViewTarget(hvUserAvatar));
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        llEditProfile.setOnClickListener(this);
        llChangePassword.setOnClickListener(this);
        llLogout.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mUserProfile != null) {
            tvUsername.setText(mUserProfile.getUsername());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onClick(final View _view) {
        if (mProfileClickListener != null) {
            switch (_view.getId()) {
                case R.id.llEditProfile_FUP:
                    mProfileClickListener.onUserProfileItemClick(this, mUserProfile, USER_PROFILE_EDIT_PROFILE);
                    break;
                case R.id.llChangePassword_FUP:
                    mProfileClickListener.onUserProfileItemClick(this, mUserProfile, USER_PROFILE_CHANGE_PASSWORD);
                    break;
                case R.id.llLogout_FUP:
                    logout();
                    break;
            }
        }
    }

    private void logout() {
        mLogoutRequest = new LogoutRequest(new LogoutRequestModel());
        loaderOverlayShow(getString(R.string.str_logout), null, false);
        getSpiceManager().execute(mLogoutRequest, KEY_LOGOUT_REQUEST, DurationInMillis.ALWAYS_EXPIRED, mLogoutRequestListener);
    }

    private RequestListener<Response> mLogoutRequestListener = new RequestListener<Response>() {

        @Override
        public void onRequestSuccess(final Response _result) {
            Picasso.with(getActivity()).invalidate(mUserProfile.getImageUrl());
            TRAApplication.setIsLoggedIn(false);
            PreferenceManager.setLoggedIn(getActivity(), false);

            if (isAdded()) {
                loaderOverlayDismissWithAction(new Loader.Dismiss() {
                    @Override
                    public void onLoadingDismissed() {
                        getFragmentManager().popBackStack();
                    }
                });
            }
        }

        @Override
        public void onRequestFailure(final SpiceException _spiceException) {
            processError(_spiceException);
        }
    };

    public final void updateUserProfileData(final UserProfileResponseModel _userProfile) {
        mUserProfile = _userProfile;
        showUserProfile(_userProfile);
    }

    private void showUserProfile(final UserProfileResponseModel _userProfile){
        initUserAvatar(_userProfile);
        tvUsername.setText(_userProfile.getUsername());
    }

    private void initUserAvatar(final UserProfileResponseModel _userProfile) {
        if (_userProfile.getImageUrl().isEmpty()) {
            hvUserAvatar.setScaleType(ScaleType.INSIDE_CROP);
            hvUserAvatar.setHexagonSrcDrawable(R.drawable.ic_user_placeholder);
        } else {
            Picasso.with(getActivity()).load(_userProfile.getImageUrl()).into(new HexagonViewTarget(hvUserAvatar));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_USER_PROFILE_MODEL, mUserProfile);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        mProfileClickListener = null;
        super.onDetach();
    }

    @Override
    protected int getTitle() {
        return R.string.fragment_user_profile_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_user_profile;
    }

    public interface OnUserProfileClickListener {
        void onUserProfileItemClick(Fragment _targetFragment, UserProfileResponseModel _userProfile, @UserProfileAction int _profileItem);
    }

}
