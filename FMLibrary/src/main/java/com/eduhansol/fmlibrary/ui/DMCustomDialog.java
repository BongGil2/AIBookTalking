package com.eduhansol.fmlibrary.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class DMCustomDialog extends Dialog {
    public DMCustomDialog(@NonNull Context context) {
        //super(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        baseInit();
    }

    private void baseInit() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        //getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setCancelable(false);
    }

    protected void setLayout(int layout) {
        setContentView(layout);     //다이얼로그에서 사용할 레이아웃입니다.
    }

    protected void setAlpha(float alpha) {
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = alpha;
        getWindow().setAttributes(lpWindow);
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
