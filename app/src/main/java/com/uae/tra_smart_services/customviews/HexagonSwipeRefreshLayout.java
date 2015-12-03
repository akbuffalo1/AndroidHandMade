package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.interfaces.OuterLayoutState;

/**
 * Created by ak-buffalo on 03.11.15.
 */
public class HexagonSwipeRefreshLayout extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener, OuterLayoutState {

    private int mDraggingState = 0;
    private ViewDragHelper mDragHelper;
    private int mDraggingBorder;
    private int mVerticalRange;
    private boolean mIsOpen;
    private LoaderView loaderView;
    private RecyclerView listview;
    private TextView noPendingTransactions;
    private HexagonSwipeRefreshLayout.Listener listener;

    public HexagonSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        mIsOpen = false;
    }

    @Override
    public void onGlobalLayout() {
        mVerticalRange = loaderView.getHeight() + 30;
    }

    @Override
    public void onLoadingStart() {
        noPendingTransactions.setVisibility(GONE);
        if(listview.getAdapter().getItemCount() == 0){
            loaderView.setLayoutParams(setTop(false));
            listview.setVisibility(GONE);
        }
        loaderView.startProcessing();
    }

    private LayoutParams setTop(boolean _bool){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL, !_bool ? RelativeLayout.TRUE : 0);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.setMargins(0, 15, 0, 5);
        return params;
    }

    boolean afterLoading;
    @Override
    public void onLoadingFinished(boolean _isSucceed) {
        afterLoading = true;
        if (loaderView.isInLoading()) {
            loaderView.stopProcessing();
        }
        mDraggingState = ViewDragHelper.STATE_IDLE;
        mDragHelper.smoothSlideViewTo(listview, 0, 0);
        if(!_isSucceed && listview.getAdapter().getItemCount() == 0) {
            listview.setVisibility(INVISIBLE);
            loaderView.setLayoutParams(setTop(false));
            noPendingTransactions.setVisibility(VISIBLE);
        } else {
            listview.setVisibility(VISIBLE);
            loaderView.setLayoutParams(setTop(true));
            noPendingTransactions.setVisibility(INVISIBLE);
        }
    }

    public void registerListener(Listener _listener){
        listener = _listener;
    }

    public class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == mDraggingState || isOpen()) {
                return;
            }
            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            float loaderPhase = (top / (float) mVerticalRange);
            if(!afterLoading){
                loaderView.setProgress(loaderPhase);
            }
            mDraggingBorder = top;
        }

        public int getViewVerticalDragRange(View child) {
            return mVerticalRange;
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view == listview) ? !(afterLoading = false) : false;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = mVerticalRange;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final float rangeToCheck = mVerticalRange;
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }
            if (mDraggingBorder == rangeToCheck) {
                mIsOpen = true;
                afterLoading = false;
                mDraggingState = ViewDragHelper.STATE_SETTLING;
                listener.onRefresh();
                return;
            }
            scrollToTop();
        }

        private void scrollToTop() {
            if(mDragHelper.settleCapturedViewAt(0, 0)) {
                ViewCompat.postInvalidateOnAnimation(HexagonSwipeRefreshLayout.this);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        loaderView = (LoaderView) getChildAt(0);
        loaderView.init(Color.parseColor("#ffffff"));
        noPendingTransactions = (TextView) getChildAt(1);
        listview = (RecyclerView) getChildAt(2);
        listview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (isMoving()) {
                    v.setTop(oldTop);
                    v.setBottom(oldBottom);
                    v.setLeft(oldLeft);
                    v.setRight(oldRight);
                }
            }
        });
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        mIsOpen = false;
        super.onFinishInflate();
    }

    private boolean canMoveList() {
        return (((LinearLayoutManager)listview.getLayoutManager()).findFirstVisibleItemPosition() == 0 || listview.getAdapter().getItemCount() == 0)
                && mDraggingState != ViewDragHelper.STATE_SETTLING
                && listview.getVisibility() == VISIBLE;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (canMoveList() && mDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canMoveList() || isMoving()) {
            mDragHelper.processTouchEvent(event);
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean isMoving() {
        return (mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                mDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    public boolean isOpen() {
        return mIsOpen;
    }

    public interface Listener {
        void onRefresh();
        void onRefresh(String _contrains);
    }
}
