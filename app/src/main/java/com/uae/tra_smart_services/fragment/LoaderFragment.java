package com.uae.tra_smart_services.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.customviews.LoaderView;
import com.uae.tra_smart_services.customviews.ServiceRatingView;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.interfaces.Loader;
import com.uae.tra_smart_services.interfaces.LoaderMarker;
import com.uae.tra_smart_services.util.ImageUtils;

/**
 * Created by ak-buffalo on 21.09.15.
 */
public class LoaderFragment extends BaseFragment implements View.OnClickListener, Loader {
    /** Constants */
    public static final String TAG = LoaderFragment.class.getName();
    public static final String STATE = "state";
    public static final String MSG = "message";
    public static final String SHOW_RATING = "show_rating";
    /** Views */
    protected LoaderView lvLoader;
    protected ServiceRatingView srvRating;
    protected ScrollView svScrollContainer;
    protected LinearLayout llServiceRatingContainer;
    protected TextView tvBackOrCancelBtn, tvLoaderTitleText;
    protected RelativeLayout rlFragmentContainer;
    protected BackButton afterBackButton;
    protected Button btnSendRating;
    /** Listeners */
    protected static Loader.Cancelled mOnLoadingListener;
    protected static LoaderFragment.CallBacks mRatingCallbacks;
    private boolean isDone;
    private LoaderView.State mAnimationState = LoaderView.State.INITIALL;
    private boolean shouldContinueLoading;

    public static LoaderFragment newInstance(String _msg, LoaderMarker _listener, boolean _showRating) {
        ServiceRatingView.MODE = 1;
        Bundle args = new Bundle();
        args.putString(MSG, _msg);
        args.putBoolean(SHOW_RATING, _showRating);
        if(_listener instanceof Loader.Cancelled) {
            mOnLoadingListener = (Loader.Cancelled) _listener;
        }
        if(_listener instanceof LoaderFragment.CallBacks) {
            mRatingCallbacks = (LoaderFragment.CallBacks) _listener;
        }
        LoaderFragment fragment = new LoaderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setToolbarVisibility() { toolbarTitleManager.setToolbarVisibility(true); }

    @Override
    protected void initViews() {
        super.initViews();
        rlFragmentContainer = findView(R.id.rlFragmentContainer_FL);
        int bgColor = defineBGColor(rlFragmentContainer);
        if(ImageUtils.isBlackAndWhiteMode(getActivity())) {
            ImageUtils.getFilteredDrawable(getActivity(), rlFragmentContainer.getBackground());
            bgColor = Color.parseColor("#505050");
        }
        lvLoader = findView(R.id.lvLoaderView);
        lvLoader.init(bgColor);
        tvLoaderTitleText = findView(R.id.tvLoaderTitleText);
        srvRating = findView(R.id.srvRating_FL);
        llServiceRatingContainer = findView(R.id.llServiceRatingContainer_FL);
        svScrollContainer = findView(R.id.svScrollContainer_FL);
        tvBackOrCancelBtn = findView(R.id.tvLoaderBackButton);
        btnSendRating = findView(R.id.btnSendRating_LSR);
    }

    private int defineBGColor(View _view){
        Bitmap bitmap = ((BitmapDrawable)_view.getBackground()).getBitmap();
        int pixel = bitmap.getPixel(bitmap.getWidth() / 2, bitmap.getHeight() / 2);

        return Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel));
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlFragmentContainer.setOnClickListener(this);
        tvBackOrCancelBtn.setOnClickListener(this);
        btnSendRating.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvLoaderBackButton:
                if(v.getTag() == LoaderView.State.PROCESSING && mOnLoadingListener != null){
                    mOnLoadingListener.onLoadingCanceled();
                } else if(afterBackButton != null){
                    afterBackButton.onBackButtonPressed(lvLoader.getCurrentState());
                } else {
                    toolbarTitleManager.setToolbarVisibility(true);
                    getFragmentManager().popBackStackImmediate();
                }
                break;
            case R.id.btnSendRating_LSR:
                Object[] rating = srvRating.getRating();
                if (rating == null){
                    Toast.makeText((isAdded()) ? getActivity() : getRootView().getContext(), R.string.choose_rating_error_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                mRatingCallbacks.onRate((int) rating[0], (String) rating[1], lvLoader.getCurrentState());
                break;
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (savedInstanceState == null || shouldContinueLoading) {
                    startLoading(getArguments().getString(MSG));
                }
                getRootView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void startLoading(String _msg) {
        isDone = false;
        lvLoader.startProcessing();
        tvLoaderTitleText.setText(getArguments().getString(MSG));
        tvBackOrCancelBtn.setTag(mAnimationState = LoaderView.State.PROCESSING);
    }

    @Override
    public void continueLoading() {
        shouldContinueLoading = true;
    }

    @Override
    public void successLoading(String _msg) {
        lvLoader.startFilling(mAnimationState = LoaderView.State.SUCCESS);
        tvLoaderTitleText.setText(_msg);
        tvBackOrCancelBtn.setText(R.string.str_back_to_dashboard);
        tvBackOrCancelBtn.setTag(LoaderView.State.SUCCESS);
        setRatingVisibleIfNeed(true);
        isDone = true;
    }

    @Override
    public void cancelLoading(String _msg) {
        lvLoader.startFilling(mAnimationState = LoaderView.State.CANCELLED);
        tvLoaderTitleText.setText(_msg);
        tvBackOrCancelBtn.setText(R.string.str_back_to_dashboard);
        tvBackOrCancelBtn.setTag(LoaderView.State.CANCELLED);
        setRatingVisibleIfNeed(true);
        isDone = true;
    }

    @Override
    public void failedLoading(String _msg, boolean _hasToShowRating) {
        lvLoader.startFilling(mAnimationState = LoaderView.State.FAILURE);
        tvLoaderTitleText.setText(_msg);
        tvBackOrCancelBtn.setText(R.string.str_back_to_dashboard);
        tvBackOrCancelBtn.setTag(LoaderView.State.FAILURE);
        setRatingVisibleIfNeed(_hasToShowRating);
        isDone = true;
    }

    @Override
    public void dissmissLoadingWithAction(Dismiss afterDissmiss) {
        afterDissmiss.onLoadingDismissed();
    }

    @Override
    public void setButtonPressedBehavior(BackButton _afterBackButton) {
        afterBackButton = _afterBackButton;
    }

    private void setRatingVisibleIfNeed(boolean _afterFailed){
        if(getArguments().getBoolean(SHOW_RATING) && _afterFailed){
            llServiceRatingContainer.setVisibility(View.VISIBLE);
            svScrollContainer.post(new Runnable() {
                @Override
                public void run() {
                    svScrollContainer.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    @Override
    public boolean isInLoading() {
        return lvLoader.isInLoading();
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE, mAnimationState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            lvLoader.showState((LoaderView.State) savedInstanceState.getSerializable(STATE));
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public interface CallBacks extends LoaderMarker {
        void onRate(int _rate, String _description, LoaderView.State _state);
    }

    @Override
    protected int getTitle() { return 0; }

    @Override
    protected int getLayoutResource() { return R.layout.fragment_loader; }
}
