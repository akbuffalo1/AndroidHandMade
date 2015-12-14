package biz.enon.tra.uae.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import biz.enon.tra.uae.interfaces.SaveStateObject;

/**
 * Created by mobimaks on 30.11.2015.
 */
public class FadeScrollListener extends OnScrollListener implements SaveStateObject, OnChildAttachStateChangeListener {

    private final String KEY_SCROLL_POSITION = getClass().getSimpleName() + "_SCROLL_POSITION";

    public static final float ALPHA_COEFFICIENT = 3f;

    private final StaggeredGridLayoutManager mLayoutManager;
    private final int mSpanCount;

    private float mItemHeight;
    private int mScrollPosition;

    public FadeScrollListener(@NonNull StaggeredGridLayoutManager _layoutManager, @Nullable Bundle _savedInstanceState) {
        mLayoutManager = _layoutManager;
        mSpanCount = mLayoutManager.getSpanCount();
        if (_savedInstanceState != null) {
            onRestoreInstanceState(_savedInstanceState);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mScrollPosition += dy;

        if (mItemHeight == 0) {
            mItemHeight = getItemHeight();
        }

        if (mItemHeight != 0) {
            final int rowNumber = (int) (mScrollPosition / mItemHeight);
            final int[] items = new int[mSpanCount];
            for (int i = 0; i < items.length; i++) {
                items[i] = rowNumber * mSpanCount + i;
            }

            final float visibility = mScrollPosition % mItemHeight / mItemHeight;
            for (int item : items) {
                final View view = mLayoutManager.findViewByPosition(item);
                if (view != null) {
                    view.setAlpha(1 - visibility * ALPHA_COEFFICIENT);
                }
            }

            final int start = getFirstCompletelyVisibleItemPosition();
            final int end = mLayoutManager.getItemCount() - 1;//getLastVisibleItemPosition();
            for (int i = start; i <= end; i++) {
                View view = mLayoutManager.findViewByPosition(i);
                if (view != null) {
                    view.setAlpha(1);
                }
            }
        }
    }

    private int getItemHeight() {
        final int[] firstItems = new int[mSpanCount];
        mLayoutManager.findFirstVisibleItemPositions(firstItems);
        for (int firstItem : firstItems) {
            final View view = mLayoutManager.findViewByPosition(firstItem);
            if (view != null) {
                mItemHeight = Math.max(mItemHeight, view.getHeight());
                break;
            }
        }
        return Math.round(mItemHeight);
    }

    private int getFirstCompletelyVisibleItemPosition() {
        final int[] firstCompletelyVisibleItemPositions = new int[mSpanCount];
        mLayoutManager.findFirstCompletelyVisibleItemPositions(firstCompletelyVisibleItemPositions);
        return firstCompletelyVisibleItemPositions[0];
    }

    private int getLastVisibleItemPosition() {
        final int[] lastVisibleItemPositions = new int[mSpanCount];
        mLayoutManager.findLastVisibleItemPositions(lastVisibleItemPositions);
        return lastVisibleItemPositions[mSpanCount - 1];
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle _outState) {
        _outState.putInt(KEY_SCROLL_POSITION, mScrollPosition);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle _savedInstanceState) {
        mScrollPosition = _savedInstanceState.getInt(KEY_SCROLL_POSITION);
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {
        view.setAlpha(0);
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
