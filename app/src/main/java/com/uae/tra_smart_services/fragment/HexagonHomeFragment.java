package com.uae.tra_smart_services.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.TRAApplication;
import com.uae.tra_smart_services.activity.AuthorizationActivity;
import com.uae.tra_smart_services.adapter.ServicesRecyclerViewAdapter;
import com.uae.tra_smart_services.customviews.HexagonalButtonsLayout;
import com.uae.tra_smart_services.customviews.HexagonalButtonsLayout.OnServiceSelected;
import com.uae.tra_smart_services.customviews.HexagonalHeader;
import com.uae.tra_smart_services.customviews.HexagonalHeader.HexagonButton;
import com.uae.tra_smart_services.customviews.HexagonalHeader.OnButtonClickListener;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.entities.FragmentType;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.HeaderStaticService;
import com.uae.tra_smart_services.global.ServerConstants;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.interfaces.Loader.BackButton;
import com.uae.tra_smart_services.interfaces.Loader.Cancelled;
import com.uae.tra_smart_services.interfaces.Loader.Dismiss;
import com.uae.tra_smart_services.rest.model.response.UserProfileResponseModel;
import com.uae.tra_smart_services.rest.robo_requests.UserProfileRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

import static com.uae.tra_smart_services.customviews.HexagonalButtonsLayout.StaticService.DOMAIN_CHECK_FRAGMENT;
import static com.uae.tra_smart_services.customviews.HexagonalButtonsLayout.StaticService.POOR_COVERAGE_SERVICE;
import static com.uae.tra_smart_services.customviews.HexagonalButtonsLayout.StaticService.SMS_SPAM_SERVICE;
import static com.uae.tra_smart_services.customviews.HexagonalButtonsLayout.StaticService.VERIFICATION_SERVICE;
import static com.uae.tra_smart_services.global.HeaderStaticService.INNOVATIONS;
import static com.uae.tra_smart_services.global.HeaderStaticService.NOTIFICATION;
import static com.uae.tra_smart_services.global.HeaderStaticService.SEARCH;

/**
 * Created by Mikazme on 13/08/2015.
 */
public class HexagonHomeFragment extends BaseFragment implements OnServiceSelected, OnButtonClickListener {

    private static final String KEY_LOAD_USER_PROFILE = "LOAD_USER_PROFILE";
    private static final String KEY_IF_PENDING_REQUEST = "IF_PENDING_REQUEST";

    public final String RECYCLER_TAG = "RecyclerView_test";

    private HexagonalButtonsLayout mHexagonalButtonsLayout;
    private RecyclerView mRecyclerView;
    private HexagonalHeader mHexagonalHeader;

    private ServicesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Service> mDataSet;

    private OnServiceSelectListener mServiceSelectListener;
    private OnStaticServiceSelectListener mStaticServiceSelectListener;
    private OnHeaderStaticServiceSelectedListener mHeaderStaticServiceSelectedListener;
    private OnOpenUserProfileClickListener mProfileClickListener;
    private LoadProfileListener mLoadProfileListener;

    private UserProfileRequest mUserProfileRequest;

    private ValueAnimator mHexagonalHeaderAnimator, mHexagonHeaderReverseAnimator;

    private boolean isCollapsed = false;
    private boolean isScrollUp = false;

    private float mAnimationProgress = 0f;
    private Integer mPreviousDy;

    public static HexagonHomeFragment newInstance() {
        return new HexagonHomeFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        mServiceSelectListener = (OnServiceSelectListener) _activity;
        mStaticServiceSelectListener = (OnStaticServiceSelectListener) _activity;

        if (_activity instanceof OnOpenUserProfileClickListener) {
            mProfileClickListener = (OnOpenUserProfileClickListener) _activity;
        }
        if (_activity instanceof OnHeaderStaticServiceSelectedListener) {
            mHeaderStaticServiceSelectedListener = (OnHeaderStaticServiceSelectedListener) _activity;
        }
    }

    @Override
    protected void initViews() {
        mRecyclerView = findView(R.id.rvServices_FHH);
        mHexagonalButtonsLayout = findView(R.id.hblHexagonalButtons_FHH);
        mHexagonalHeader = findView(R.id.hhHeader_FHH);
        mAnimationProgress = 0f;
    }

    @Override
    protected void initListeners() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (RecyclerView.SCROLL_STATE_SETTLING == newState ||
                        RecyclerView.SCROLL_STATE_IDLE == newState
                                && mAnimationProgress != 0f && mAnimationProgress != 1f) {

                    endAnimation();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy == 0) return;

                if (mPreviousDy != null && ((mPreviousDy < 0 && dy > 0) || (mPreviousDy > 0 && dy < 0))) {
                    mPreviousDy = dy;
                    return;
                } else {
                    mPreviousDy = dy;
                }

                isScrollUp = dy > 0 || (dy >= 0 && isScrollUp);

//                if (Math.abs(dy) < 7) return;

                Log.d(RECYCLER_TAG, "Scrolled : " + dy);

                if (!mHexagonalHeaderAnimator.isRunning() && !mHexagonHeaderReverseAnimator.isRunning()
                        && ((mAnimationProgress < 1f && dy > 0) || (mAnimationProgress > 0f && dy < 0))) {
                    mAnimationProgress += dy * 0.005f;

                    if (mAnimationProgress >= 1f) {
                        mAnimationProgress = 1f;
                        isCollapsed = true;
                    } else if (mAnimationProgress <= 0f) {
                        mAnimationProgress = 0f;
                        isCollapsed = false;
                    }

                    mHexagonalHeader.setAnimationProgress(mAnimationProgress);
                    mHexagonalButtonsLayout.setAnimationProgress(mAnimationProgress);
                }
            }
        });
        mHexagonalButtonsLayout.setServiceSelectedListener(this);
        mHexagonalButtonsLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isAdded()) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
                    lp.setMargins(lp.leftMargin, (int) -mHexagonalButtonsLayout.getHalfOuterRadius(), lp.rightMargin, lp.bottomMargin);
                    mRecyclerView.setLayoutParams(lp);

                    mAdapter = new ServicesRecyclerViewAdapter(getActivity().getApplicationContext(), mDataSet,
                            mHexagonalButtonsLayout.getHalfOuterRadius());
                    mAdapter.setServiceSelectListener(mServiceSelectListener);
                    mRecyclerView.setAdapter(mAdapter);

                    mHexagonalButtonsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        mHexagonalHeader.setOnButtonClickListener(this);
    }

    private void endAnimation() {
        if ((isCollapsed && !isScrollUp) || (!isCollapsed && !isScrollUp && mAnimationProgress != 1f)) {
            mHexagonHeaderReverseAnimator.setFloatValues(mAnimationProgress, 0f);
            mHexagonHeaderReverseAnimator.setDuration((int) (600 / 2 * mAnimationProgress));
            mHexagonHeaderReverseAnimator.start();
            isCollapsed = false;
        } else if ((!isCollapsed && isScrollUp) || (isCollapsed && isScrollUp && mAnimationProgress != 0f)) {
            mHexagonalHeaderAnimator.setFloatValues(mAnimationProgress, 1f);
            mHexagonalHeaderAnimator.setDuration((int) (600 / 2 * (1 - mAnimationProgress)));
            mHexagonalHeaderAnimator.start();
            isCollapsed = true;
        }
    }

    @Override
    protected void initData() {
        isCollapsed = true;
        mHexagonalHeaderAnimator = ValueAnimator.ofFloat(0f, 1f);
        mHexagonalHeaderAnimator.setDuration(600);
        mHexagonalHeaderAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mHexagonalHeaderAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimationProgress = (float) animation.getAnimatedValue();
                mHexagonalHeader.setAnimationProgress((float) animation.getAnimatedValue());
                mHexagonalButtonsLayout.setAnimationProgress((float) animation.getAnimatedValue());
            }
        });

        mHexagonHeaderReverseAnimator = ValueAnimator.ofFloat(1f, 0f);
        mHexagonHeaderReverseAnimator.setDuration(600);
        mHexagonHeaderReverseAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mHexagonHeaderReverseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimationProgress = (float) animation.getAnimatedValue();
                mHexagonalHeader.setAnimationProgress((float) animation.getAnimatedValue());
                mHexagonalButtonsLayout.setAnimationProgress((float) animation.getAnimatedValue());
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Picasso.with(getActivity()).load(ServerConstants.BASE_URL + "/crm/profileImage").
                skipMemoryCache().error(R.drawable.ic_user_placeholder).
                placeholder(R.drawable.ic_user_placeholder).into(mHexagonalHeader);

        mLoadProfileListener = new LoadProfileListener();

        initServiceList();

        mLayoutManager = new StaggeredGridLayoutManager(4, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void initServiceList() {
        mDataSet = new ArrayList<>(Service.getSecondaryServices());
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().getFromCache(UserProfileResponseModel.class, KEY_LOAD_USER_PROFILE,
                DurationInMillis.ALWAYS_RETURNED, mLoadProfileListener);
    }

    @Override
    public final void serviceSelected(final int _id) {
        if (VERIFICATION_SERVICE.isEquals(_id)) {
            mStaticServiceSelectListener.onStaticServiceSelect(VERIFICATION_SERVICE);
        } else if (SMS_SPAM_SERVICE.isEquals(_id)) {
            mStaticServiceSelectListener.onStaticServiceSelect(SMS_SPAM_SERVICE);
        } else if (POOR_COVERAGE_SERVICE.isEquals(_id)) {
            mStaticServiceSelectListener.onStaticServiceSelect(POOR_COVERAGE_SERVICE);
        } else if (DOMAIN_CHECK_FRAGMENT.isEquals(_id)) {
            mStaticServiceSelectListener.onStaticServiceSelect(DOMAIN_CHECK_FRAGMENT);
        }
    }

    @Override
    public void onAvatarButtonClick() {
        if (mProfileClickListener != null) {
            if (TRAApplication.isLoggedIn()) {
                loadAndOpenProfile();
            } else {
                Intent intent = AuthorizationActivity.getStartForResultIntent(getActivity(), FragmentType.USER_PROFILE);
                startActivityForResult(intent, C.REQUEST_CODE_LOGIN);
            }
        }
    }

    @Override
    public void onActivityResult(final int _requestCode, final int _resultCode, final Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);
        if (_requestCode == C.REQUEST_CODE_LOGIN && _resultCode == C.LOGIN_SUCCESS) {
            loadAndOpenProfile();
        }
    }

    private void loadAndOpenProfile() {
        loaderOverlayShow(getString(R.string.str_loading), mLoadProfileListener, false);
        loaderOverlayButtonBehavior(mLoadProfileListener);
        mUserProfileRequest = new UserProfileRequest();
        getSpiceManager().execute(mUserProfileRequest, KEY_LOAD_USER_PROFILE, DurationInMillis.ALWAYS_EXPIRED, mLoadProfileListener);
    }

    @Override
    public void onHexagonButtonClick(@HexagonButton final int _hexagonButton) {
        if (NOTIFICATION.equals(_hexagonButton)) {
            mHeaderStaticServiceSelectedListener.onHeaderStaticServiceSelected(NOTIFICATION);
        } else if (INNOVATIONS.equals(_hexagonButton)) {
            mHeaderStaticServiceSelectedListener.onHeaderStaticServiceSelected(INNOVATIONS);
        } else if (SEARCH.equals(_hexagonButton)) {
            mHeaderStaticServiceSelectedListener.onHeaderStaticServiceSelected(SEARCH);
        }
    }

    @Override
    protected void setToolbarVisibility() {
        toolbarTitleManager.setToolbarVisibility(false);
    }

    @Override
    protected int getTitle() {
        return 0;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_hexagon_home;
    }

    private class LoadProfileListener implements RequestListener<UserProfileResponseModel>, Dismiss, Cancelled, BackButton {

        private UserProfileResponseModel mResult;

        @Override
        public void onRequestSuccess(UserProfileResponseModel result) {
            getSpiceManager().removeDataFromCache(Response.class, KEY_LOAD_USER_PROFILE);
            if (isAdded() && result != null) {
                mResult = result;
                loaderOverlayDismissWithAction(this);
                toolbarTitleManager.setToolbarVisibility(false);
            }
        }

        @Override
        public void onLoadingDismissed() {
            getFragmentManager().popBackStack();
            getSpiceManager().removeDataFromCache(UserProfileResponseModel.class, KEY_LOAD_USER_PROFILE);
            if (mProfileClickListener != null) {
                mProfileClickListener.onOpenUserProfileClick(mResult);
            }
        }

        @Override
        public void onBackButtonPressed(LoaderView.State _currentState) {
            toolbarTitleManager.setToolbarVisibility(false);
            getFragmentManager().popBackStack();
        }

        @Override
        public void onLoadingCanceled() {
            toolbarTitleManager.setToolbarVisibility(false);
            if (getSpiceManager().isStarted()) {
                getSpiceManager().cancel(mUserProfileRequest);
                getSpiceManager().removeDataFromCache(UserProfileResponseModel.class, KEY_LOAD_USER_PROFILE);
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            toolbarTitleManager.setToolbarVisibility(false);
            getSpiceManager().removeDataFromCache(Response.class, KEY_LOAD_USER_PROFILE);
            processError(spiceException);
        }
    }

    public interface OnOpenUserProfileClickListener {
        void onOpenUserProfileClick(final UserProfileResponseModel _userProfile);
    }

    public interface OnServiceSelectListener {
        <T> void onServiceSelect(final Service _service, T data);
    }

    public interface OnStaticServiceSelectListener {
        void onStaticServiceSelect(final HexagonalButtonsLayout.StaticService _service);
    }

    public interface OnHeaderStaticServiceSelectedListener {
        void onHeaderStaticServiceSelected(final HeaderStaticService _service);
    }
}
