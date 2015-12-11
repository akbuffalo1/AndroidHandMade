package biz.enon.tra.uae.customviews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import biz.enon.tra.uae.util.Logger;
import hugo.weaving.DebugLog;

/**
 * Created by mobimaks on 11.11.2015.
 */

public class MyViewPager extends ViewPager {

    private static final float NEW_PAGE_SCROLL_OFFSET = 0.35f;

    private GestureDetectorCompat mDetector;
    private boolean mIsFlingHandled;

    public MyViewPager(Context context) {
        this(context, null);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDetector = new GestureDetectorCompat(getContext(), new SwipeListener());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mIsFlingHandled) {
            return true;
        }
        Logger.d(MyViewPager.class.getSimpleName(), "⇠ dispatchTouchEvent [0ms] = " + mDetector.onTouchEvent(ev));
        handleScrollPositionIfNeed(ev);
        return super.dispatchTouchEvent(ev);
    }

    @DebugLog
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsFlingHandled) {
            return true;
        }
        Logger.d(MyViewPager.class.getSimpleName(), "⇠ onTouchEvent [0ms] = " + mDetector.onTouchEvent(ev));
        handleScrollPositionIfNeed(ev);
        return true;
    }


    private void handleScrollPositionIfNeed(MotionEvent ev) {
        if (!mIsFlingHandled && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL)) {
            final int pageWidth = getWidth();
            final int currentPageStartX = getCurrentPageX();
            final int scrollX = getScrollX();

            if (canOpenPreviousPage() && (scrollX < currentPageStartX - pageWidth * NEW_PAGE_SCROLL_OFFSET)) {
                mIsFlingHandled = true;
                openPreviousPage();
                //openPreviousPage
            } else if (canOpenNextPage() && (scrollX > currentPageStartX + pageWidth * NEW_PAGE_SCROLL_OFFSET)) {
                mIsFlingHandled = true;
                openNextPage();
                //openNextPage
            } else if (getScrollX() != currentPageStartX) {
                mIsFlingHandled = true;
                ValueAnimator animator = ValueAnimator.ofInt(getScrollX(), currentPageStartX);
                animator.setDuration(300);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        scrollTo((Integer) animation.getAnimatedValue(), 0);
                    }
                });
                animator.start();
            }
        }
    }

    @DebugLog
    @Override
    protected void onScrollChanged(final int _left, final int _top, final int _oldLeft, final int _oldTop) {
        super.onScrollChanged(_left, _top, _oldLeft, _oldTop);
        if (_left == getCurrentPageX()) {
//            Logger.d(this, "onScrollChanged: " + _left);
            mIsFlingHandled = false;
        }
    }

    private int getCurrentPageX() {
        return getCurrentItem() * getWidth();
    }

    private class SwipeListener extends SimpleOnGestureListener {

        private static final float FLING_START_THRESHOLD = 0.15f;
        private static final float MAX_SCROLL_OFFSET = 1.0f;
        private static final float BLOCK_SCROLL_OFFSET = 0.1f;

        @DebugLog
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 != null && e2 != null) {
                if (isFlingBigEnough(e1, e2)) {
                    if (e1.getX() < e2.getX()) {
                        return onSwipeRight(velocityX, velocityY);
                    } else {
                        return onSwipeLeft(velocityX, velocityY);
                    }
                }
            }
            return false;
        }

        private boolean isFlingBigEnough(MotionEvent e1, MotionEvent e2) {
            return Math.abs(e1.getX() - e2.getX()) / getWidth() >= FLING_START_THRESHOLD;
        }

        @DebugLog
        public boolean onSwipeLeft(float velocityX, float velocityY) {
            if (canOpenNextPage()) {
//                Logger.d(this, "onFling Next");
                mIsFlingHandled = true;
                openNextPage();
                return true;
            }
            return false;
        }

        @DebugLog
        public boolean onSwipeRight(float velocityX, float velocityY) {
            if (canOpenPreviousPage()) {
//                Logger.d(this, "onFling Previous");
                mIsFlingHandled = true;
                openPreviousPage();
                return true;
            }
            return false;
        }


        @DebugLog
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            final int currentScrollX = getScrollX();
            final int currentPageStartX = getCurrentPageX();
            final float maxScrollOffset = getWidth() * MAX_SCROLL_OFFSET;

            final boolean canScrollLeft = (currentScrollX > 0) && (currentScrollX > currentPageStartX - maxScrollOffset);
            final boolean canScrollRight = (currentScrollX < getLastPageX()) && (currentScrollX < currentPageStartX + maxScrollOffset);

            if (distanceX < 0 && canScrollLeft) {
                scrollBy(Math.round(distanceX), 0);
                Logger.d(MyViewPager.class.getSimpleName(), "Scroll action: onScroll left");
                return true;
            } else if (distanceX > 0 && canScrollRight) {
                scrollBy(Math.round(distanceX), 0);
                Logger.d(MyViewPager.class.getSimpleName(), "Scroll action: onScroll right");
                return true;
            }

            return false;
        }

        private int getLastPageX() {
            return getWidth() * (getAdapter().getCount() - 1);
        }

        @DebugLog
        @Override
        public boolean onDown(MotionEvent e) {
            Logger.d(MyViewPager.class.getSimpleName(), "Touch action: onDown");
            return true;
        }

    }

    private boolean canOpenNextPage() {
        return getCurrentItem() < getAdapter().getCount() - 1;
    }

    private boolean canOpenPreviousPage() {
        return getCurrentItem() > 0;
    }

    private void openPreviousPage() {
        setCurrentItem(getCurrentItem() - 1);
    }

    private void openNextPage() {
        setCurrentItem(getCurrentItem() + 1);
    }

    private String getActionName(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
            case MotionEvent.ACTION_SCROLL:
                return "ACTION_SCROLL";
            default:
                return "Other action";
        }
    }

}
