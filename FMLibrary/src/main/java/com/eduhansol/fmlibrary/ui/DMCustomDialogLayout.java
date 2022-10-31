package com.eduhansol.fmlibrary.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by DM on 2016-05-11.
 */
public class DMCustomDialogLayout extends RelativeLayout {
    private int mLayoutID;
    private final String mColor = "#000000";
    private float mAlpha = 0.8f;

    public DMCustomDialogLayout(Context context) {
        super(context);
    }

    public DMCustomDialogLayout(Context context, int layoutid) {
        super(context);

        mLayoutID = layoutid;

        baseInit();
    }

    public DMCustomDialogLayout(Context context, int layoutid, float alpah) {
        super(context);

        mLayoutID = layoutid;
        mAlpha = alpah;

        baseInit();
    }

    public DMCustomDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DMCustomDialogLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void baseInit() {
        String infServiece = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater) getContext().getSystemService(infServiece);
        li.inflate(mLayoutID, this, true);

        ImageView iv = new ImageView(getContext());
        iv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setBackgroundColor(Color.parseColor(mColor));
        iv.setAlpha(mAlpha);
        iv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        addView(iv, 0);
    }
}
