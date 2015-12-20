package biz.enon.tra.uae.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.BuildConfig;
import biz.enon.tra.uae.R;
import biz.enon.tra.uae.TRAApplication;
import biz.enon.tra.uae.activity.AuthorizationActivity;
import biz.enon.tra.uae.adapter.ServicesRecyclerViewAdapter;
import biz.enon.tra.uae.customviews.HexagonalButtonsLayout;
import biz.enon.tra.uae.customviews.HexagonalButtonsLayout.OnServiceSelected;
import biz.enon.tra.uae.customviews.HexagonalHeader;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.entities.FragmentType;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.HeaderStaticService;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader.BackButton;
import biz.enon.tra.uae.interfaces.Loader.Cancelled;
import biz.enon.tra.uae.interfaces.Loader.Dismiss;
import biz.enon.tra.uae.rest.model.response.DynamicServiceInfoResponseModel;
import biz.enon.tra.uae.rest.model.response.UserProfileResponseModel;
import biz.enon.tra.uae.rest.robo_requests.DynamicServiceListRequest;
import biz.enon.tra.uae.rest.robo_requests.UserProfileRequest;
import biz.enon.tra.uae.util.EndlessScrollListener;
import biz.enon.tra.uae.util.FadeScrollListener;
import retrofit.client.Response;

import static biz.enon.tra.uae.customviews.HexagonalButtonsLayout.StaticService.BLOCK_WEBSITE;
import static biz.enon.tra.uae.customviews.HexagonalButtonsLayout.StaticService.POOR_COVERAGE_SERVICE;
import static biz.enon.tra.uae.customviews.HexagonalButtonsLayout.StaticService.SMS_SPAM;
import static biz.enon.tra.uae.customviews.HexagonalButtonsLayout.StaticService.VERIFY_DEVICE;
import static biz.enon.tra.uae.global.HeaderStaticService.INNOVATIONS;
import static biz.enon.tra.uae.global.HeaderStaticService.NOTIFICATION;
import static biz.enon.tra.uae.global.HeaderStaticService.SEARCH;

/**
 * Created by Mikazme on 13/08/2015.
 */
public class HexagonHomeFragment extends BaseFragment implements OnServiceSelected, HexagonalHeader.OnButtonClickListener, EndlessScrollListener.OnLoadMoreListener {

    private static final String KEY_LOAD_USER_PROFILE = "LOAD_USER_PROFILE";
    private static final String KEY_IF_PENDING_REQUEST = "IF_PENDING_REQUEST";
    private static final String KEY_DYNAMIC_LIST_REQUEST = "DYNAMIC_LIST_REQUEST";

    private final static float MAX_IMAGE_OFFSET = 30; //dp

    public final String RECYCLER_TAG = "RecyclerView_test";

    private HexagonalButtonsLayout mHexagonalButtonsLayout;
    private RecyclerView mRecyclerView;
    private HexagonalHeader mHexagonalHeader;
    private ImageView ivHexagonBackground;

    private ServicesRecyclerViewAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private List<Service> mDataSet;

    private OnServiceSelectListener mServiceSelectListener;
    private OnStaticServiceSelectListener mStaticServiceSelectListener;
    private OnHeaderStaticServiceSelectedListener mHeaderStaticServiceSelectedListener;
    private OnOpenUserProfileClickListener mProfileClickListener;
    private EndlessScrollListener mEndlessScrollListener;
    private FadeScrollListener mFadeScrollListener;

    private UserProfileRequest mUserProfileRequest;
    private DynamicServiceListRequest mDynamicServiceListRequest;

    private LoadProfileListener mLoadProfileListener;
    private DynamicServiceListListener mDynamicServicesListListener;

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
        ivHexagonBackground = findView(R.id.ivHexagons_FHH);
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

                    float hexagonOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_IMAGE_OFFSET * mAnimationProgress, getResources().getDisplayMetrics());
                    ivHexagonBackground.setTranslationY(hexagonOffset);

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

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int mScrollPosition;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
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
        Picasso.with(getActivity())
                .load(BuildConfig.SERVER_URL + "/crm/profileImage")
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .error(R.drawable.ic_user_placeholder)
                .placeholder(R.drawable.ic_user_placeholder)
                .into(mHexagonalHeader);

        mLoadProfileListener = new LoadProfileListener();
        mDynamicServicesListListener = new DynamicServiceListListener();

        initServiceList();

        mLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFadeScrollListener = new FadeScrollListener(mLayoutManager, savedInstanceState);
        mRecyclerView.addOnScrollListener(mFadeScrollListener);
        mRecyclerView.addOnChildAttachStateChangeListener(mFadeScrollListener);
    }

    private void initEndlessListener() {
        mEndlessScrollListener = new EndlessScrollListener(mLayoutManager, this);

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    private void initServiceList() {
        mDataSet = new ArrayList<>(Service.getSecondaryServices());
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().getFromCache(UserProfileResponseModel.class, KEY_LOAD_USER_PROFILE,
                DurationInMillis.ALWAYS_RETURNED, mLoadProfileListener);
        getSpiceManager().getFromCache(DynamicServiceInfoResponseModel.List.class, KEY_DYNAMIC_LIST_REQUEST,
                DurationInMillis.ALWAYS_RETURNED, mDynamicServicesListListener);
    }

    @Override
    public final void serviceSelected(final int _id) {
        if (BLOCK_WEBSITE.isEquals(_id)) {
            mStaticServiceSelectListener.onStaticServiceSelect(BLOCK_WEBSITE);
        } else if (POOR_COVERAGE_SERVICE.isEquals(_id)) {
            mStaticServiceSelectListener.onStaticServiceSelect(POOR_COVERAGE_SERVICE);
        } else if (VERIFY_DEVICE.isEquals(_id)) {
            mStaticServiceSelectListener.onStaticServiceSelect(VERIFY_DEVICE);
        } else if (SMS_SPAM.isEquals(_id)) {
            mStaticServiceSelectListener.onStaticServiceSelect(SMS_SPAM);
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
    public void onHexagonButtonClick(@HexagonalHeader.HexagonButton final int _hexagonButton) {
        if (NOTIFICATION.equals(_hexagonButton)) {
            mHeaderStaticServiceSelectedListener.onHeaderStaticServiceSelected(NOTIFICATION);
        } else if (INNOVATIONS.equals(_hexagonButton)) {
            mHeaderStaticServiceSelectedListener.onHeaderStaticServiceSelected(INNOVATIONS);
        } else if (SEARCH.equals(_hexagonButton)) {
            mHeaderStaticServiceSelectedListener.onHeaderStaticServiceSelected(SEARCH);
        }
    }

    @Override
    public final void onLoadMoreEvent() {
        mRecyclerView.removeOnScrollListener(mEndlessScrollListener);
        mDynamicServiceListRequest = new DynamicServiceListRequest();
        getSpiceManager().execute(mDynamicServiceListRequest, KEY_DYNAMIC_LIST_REQUEST, DurationInMillis.ALWAYS_EXPIRED,
                mDynamicServicesListListener);
    }

    @Override
    public void onSaveInstanceState(Bundle _outState) {
        super.onSaveInstanceState(_outState);
        if (mFadeScrollListener != null) {
            mFadeScrollListener.onSaveInstanceState(_outState);
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

    private class DynamicServiceListListener implements RequestListener<DynamicServiceInfoResponseModel.List> {

        @Override
        public void onRequestSuccess(DynamicServiceInfoResponseModel.List result) {
            getSpiceManager().removeDataFromCache(DynamicServiceInfoResponseModel.List.class, KEY_DYNAMIC_LIST_REQUEST);
            if (isAdded() && result != null) {
                mAdapter.addDynamicServices(result);
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(DynamicServiceInfoResponseModel.List.class, KEY_DYNAMIC_LIST_REQUEST);
            if (isAdded()) {
                mRecyclerView.addOnScrollListener(mEndlessScrollListener);
            }
        }
    }

    //region Interfaces
    public interface OnOpenUserProfileClickListener {
        void onOpenUserProfileClick(final UserProfileResponseModel _userProfile);
    }

    public interface OnServiceSelectListener {
        void onServiceSelect(final Service _service, Parcelable _data);

        void onServiceSelect(final DynamicServiceInfoResponseModel _service, Parcelable _data);

        void onServiceSelect(final Service _service, Parcelable _data, Fragment _targetFragment);
    }

    public interface OnStaticServiceSelectListener {
        void onStaticServiceSelect(final HexagonalButtonsLayout.StaticService _service);
    }

    public interface OnHeaderStaticServiceSelectedListener {
        void onHeaderStaticServiceSelected(final HeaderStaticService _service);
    }
    //endregion
}
