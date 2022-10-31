package com.eduhansol.fmlibrary.ui;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DMBottomDialog extends BottomSheetDialog {
    public DMBottomDialog(@NonNull Context context) {
        super(context);
    }

    public DMBottomDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected DMBottomDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 팝업이 노출됨과 동시에 전체 보이기 적용
        // 가로 모드일경우 스와이프해서 올려야 전체가 보이는 문제 수정
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    protected void setLayout(int layout) {
        setContentView(layout);

    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
