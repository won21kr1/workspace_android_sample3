package com.tweetlanes.android.core.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tweetlanes.android.core.widget.pulltorefresh.internal.EmptyViewMethodAccessor;

public abstract class PullToRefreshAdapterViewBase<T extends AbsListView>
        extends PullToRefreshBase<T> implements OnScrollListener {

    private int mLastSavedFirstVisibleItem = -1;
    private OnScrollListener mOnScrollListener;
    private OnLastItemVisibleListener mOnLastItemVisibleListener;
    private View mEmptyView;
    private FrameLayout mRefreshableViewHolder;

    PullToRefreshAdapterViewBase(Context context) {
        super(context);
        mRefreshableView.setOnScrollListener(this);
    }

    PullToRefreshAdapterViewBase(Context context, int mode) {
        super(context, mode);
        mRefreshableView.setOnScrollListener(this);
    }

    PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRefreshableView.setOnScrollListener(this);
    }

    abstract public ContextMenuInfo getContextMenuInfo();

    public final void onScroll(final AbsListView view,
                               final int firstVisibleItem, final int visibleItemCount,
                               final int totalItemCount) {

        if (null != mOnLastItemVisibleListener) {
            // detect if last item is visible
            if (visibleItemCount > 0
                    && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                // only process first event
                if (firstVisibleItem != mLastSavedFirstVisibleItem) {
                    mLastSavedFirstVisibleItem = firstVisibleItem;
                    mOnLastItemVisibleListener.onLastItemVisible();
                }
            }
        }

        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    public final void onScrollStateChanged(final AbsListView view,
                                           final int scrollState) {
        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * Sets the Empty View to be used by the Adapter View.
     * <p/>
     * We need it handle it ourselves so that we can Pull-to-Refresh when the
     * Empty View is shown.
     * <p/>
     * Please note, you do <strong>not</strong> usually need to call this method
     * yourself. Calling setEmptyView on the AdapterView will automatically call
     * this method and set everything up. This includes when the Android
     * Framework automatically sets the Empty View based on it's ID.
     *
     * @param newEmptyView - Empty View to be used
     */
    final void setEmptyView(View newEmptyView) {
        // If we already have an Empty View, remove it
        if (null != mEmptyView) {
            mRefreshableViewHolder.removeView(mEmptyView);
        }

        if (null != newEmptyView) {
            ViewParent newEmptyViewParent = newEmptyView.getParent();
            if (null != newEmptyViewParent
                    && newEmptyViewParent instanceof ViewGroup) {
                ((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
            }

            this.mRefreshableViewHolder.addView(newEmptyView,
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
        }

        if (mRefreshableView instanceof EmptyViewMethodAccessor) {
            ((EmptyViewMethodAccessor) mRefreshableView)
                    .setEmptyViewInternal(newEmptyView);
        } else {
            this.mRefreshableView.setEmptyView(newEmptyView);
        }
    }

    public final void setOnLastItemVisibleListener(
            OnLastItemVisibleListener listener) {
        mOnLastItemVisibleListener = listener;
    }

    public final void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    protected void addRefreshableView(Context context, T refreshableView) {
        mRefreshableViewHolder = new FrameLayout(context);
        mRefreshableViewHolder.addView(refreshableView,
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        addView(mRefreshableViewHolder, new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, 0, 1.0f));
    }

    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    private boolean isFirstItemVisible() {
        if (this.mRefreshableView.getCount() == 0) {
            return true;
        } else if (mRefreshableView.getFirstVisiblePosition() == 0) {

            final View firstVisibleChild = mRefreshableView.getChildAt(0);

            if (firstVisibleChild != null) {
                return firstVisibleChild.getTop() >= mRefreshableView.getTop();
            }
        }

        return false;
    }

    private boolean isLastItemVisible() {
        final int count = this.mRefreshableView.getCount();
        final int lastVisiblePosition = mRefreshableView
                .getLastVisiblePosition();

        if (count == 0) {
            return true;
        } else if (lastVisiblePosition == count - 1) {

            final int childIndex = lastVisiblePosition
                    - mRefreshableView.getFirstVisiblePosition();
            final View lastVisibleChild = mRefreshableView
                    .getChildAt(childIndex);

            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= mRefreshableView
                        .getBottom();
            }
        }

        return false;
    }
}
