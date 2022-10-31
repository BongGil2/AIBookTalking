package com.eduhansol.fmlibrary.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.widget.NestedScrollView;

public class DMScrollView extends NestedScrollView {
    private final static int STATE_TOP = 0;
    private final static int STATE_MIDDLE = 1;
    private final static int STATE_BOTTOM = 2;
    private OnDMScrollListener scrollListener;
    private GestureDetector gdScrolling;
    private boolean isScrolling = false;
    private int state;

    public DMScrollView(Context context) {
        super(context);

        init();
    }

    public DMScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DMScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        setVerticalScrollBarEnabled(false);
        gdScrolling = new GestureDetector(new SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                isScrolling = true;
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gdScrolling.onTouchEvent(event)) {
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isScrolling) {
                        isScrolling = false;
                        if (scrollListener != null) {
                            scrollListener.onScrollStop();
                        }
                    }
                }

                return false;
            }
        });
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);

        if (scrollListener != null) {
            if (y <= 0 && state != STATE_TOP) {
                scrollListener.onScrollTop();
                state = STATE_TOP;
            }

            View view = getChildAt(getChildCount() - 1);

            int diff = (view.getBottom() - (getHeight() + getScrollY() + view.getTop()));
            if (diff <= 0 && state != STATE_BOTTOM) {
                scrollListener.onScrollBottom();
                state = STATE_BOTTOM;
            }

            if (diff > 20 && y > 20) {
                state = STATE_MIDDLE;
            }

            scrollListener.onScrollChanged(x, y, oldx, oldy);
        }
    }

    public void changeScrollPosition(int x, int y) {
        smoothScrollTo(x, y);
    }

    public void setOnDMScrollListener(OnDMScrollListener listener) {
        scrollListener = listener;
    }

    public interface OnDMScrollListener {
        void onScrollChanged(int x, int y, int oldx, int oldy);

        void onScrollStop();

        void onScrollTop();

        void onScrollBottom();
    }
}
