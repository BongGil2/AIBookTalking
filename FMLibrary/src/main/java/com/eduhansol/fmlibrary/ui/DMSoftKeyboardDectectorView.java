package com.eduhansol.fmlibrary.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by DM on 2016-07-11.
 * AndroidManifest.xml -> Activity -> android:windowSoftInputMode="adjustResize" 추가
 * EditText 가 있는 View는 ScrollView로 개발 필요
 * view에 추가 후 사용
 */
public class DMSoftKeyboardDectectorView extends RelativeLayout {
    private static final float DETECT_ON_SIZE_PERCENT = 0.8f;
    private boolean isKeyboardShown = false;
    private onSoftKeyboardListener mListener = null;
    private float layoutMaxH = 0f; // max measured height is considered layout normal size

    public DMSoftKeyboardDectectorView(Context context) {
        super(context);
    }

    public DMSoftKeyboardDectectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public DMSoftKeyboardDectectorView(Context context, AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int newH = MeasureSpec.getSize(heightMeasureSpec);

        if (newH > layoutMaxH) {
            layoutMaxH = newH;
        }
        if (layoutMaxH != 0f) {
            final float sizePercent = newH / layoutMaxH;
            if (!isKeyboardShown && sizePercent <= DETECT_ON_SIZE_PERCENT) {
                isKeyboardShown = true;
                mListener.onSoftKeyboardShow();

            } else if (isKeyboardShown && sizePercent > DETECT_ON_SIZE_PERCENT) {
                isKeyboardShown = false;
                mListener.onSoftKeyboardHide();
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnSoftKeyboardListener(onSoftKeyboardListener listener) {
        mListener = listener;
    }

    // Callback
    public interface onSoftKeyboardListener {
        void onSoftKeyboardShow();

        void onSoftKeyboardHide();
    }
}
