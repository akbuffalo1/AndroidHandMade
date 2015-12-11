package biz.enon.tra.uae.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Mikazme on 05/10/2015.
 */
public final class EndlessScrollListener extends OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 8;
    private boolean mIsLinearLayoutManager;

    private final OnLoadMoreListener mOnLoadMoreListener;
    private final LayoutManager mLayoutManager;

    public EndlessScrollListener(@NonNull final LinearLayoutManager _linearLayoutManager,
                                 @NonNull final OnLoadMoreListener _loadMoreListener) {
        mIsLinearLayoutManager = true;
        mLayoutManager = _linearLayoutManager;
        mOnLoadMoreListener = _loadMoreListener;
    }

    public EndlessScrollListener(@NonNull final StaggeredGridLayoutManager _layoutManager,
                                 @NonNull final OnLoadMoreListener _loadMoreListener) {
        mIsLinearLayoutManager = false;
        mLayoutManager = _layoutManager;
        mOnLoadMoreListener = _loadMoreListener;
    }

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        final int totalItemCount = mLayoutManager.getItemCount();
        final int lastVisibleItemPosition = findLastVisibleItemPosition();

        if ((totalItemCount - VISIBLE_THRESHOLD) <= lastVisibleItemPosition) {
            mOnLoadMoreListener.onLoadMoreEvent();
        }
    }

    private int findLastVisibleItemPosition() {
        if (mIsLinearLayoutManager) {
            return ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else {
            final int[] lastItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            final int lastItemPosition = lastItemPositions[lastItemPositions.length - 1];
            return lastItemPosition == RecyclerView.NO_POSITION ? 0 : lastItemPosition;
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMoreEvent();
    }
}
