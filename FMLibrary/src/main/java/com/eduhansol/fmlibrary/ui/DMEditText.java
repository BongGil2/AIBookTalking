package com.eduhansol.fmlibrary.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.regex.Pattern;

/**
 * Created by DM on 2016-07-13.
 */
public class DMEditText extends AppCompatEditText {
    public static final String INPUT_OLAY_KOREA = "^[ㄱ-ㅣ가-힣.]*$";
    public static final String INPUT_OLAY_NUMBER = "^[0-9]*$";
    public static final String INPUT_OLAY_ENGLISH = "^[a-zA-Z]*$";
    public static final String INPUT_ONLY_NUM_ENG = "^[a-zA-Z0-9]*$";
    public static final String INPUT_ONLY_EMAIL = "^[a-zA-Z0-9._%+-@]*$";
    public static final String INPUT_ONLY_DOMAIN = "^[a-zA-Z0-9.]*$";
    public static final String INPUT_ONLY_PASSWORD = "^[a-zA-Z0-9!@.#$%^&*?_~]*$";

    private int mLimitLine = 0;
    private int mLimitText = 0;
    private int mTextCount = 0;
    private onDMEditTextListener mOnDMEditTextListener;
    private OnBackPressListener mBackPressListener;
    private View mView;
    TextWatcher textWatcher = new TextWatcher() {
        String previousString = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            previousString = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (mLimitLine != 0) {
                if (getLineCount() > mLimitLine) {
                    setText(previousString);
                    setSelection(length());

                    if (mOnDMEditTextListener != null) {
                        mOnDMEditTextListener.onMaxLines(mView);
                        return;
                    }
                } else {
                    if (mOnDMEditTextListener != null) {
                        mOnDMEditTextListener.getLineCount(mView, getLineCount());
                    }
                }
            } else {
                if (mOnDMEditTextListener != null) {
                    mOnDMEditTextListener.getLineCount(mView, getLineCount());
                }
            }

            mTextCount = s.length();
            if (mLimitText != 0) {
                if (mTextCount > (mLimitText)) {
                    setText(previousString);
                    setSelection(length());

                    if (mOnDMEditTextListener != null) {
                        mOnDMEditTextListener.onMaxText(mView);
                        return;
                    }
                } else {
                    if (mOnDMEditTextListener != null) {
                        mOnDMEditTextListener.getTextCount(mView, mTextCount);
                    }
                }
            } else {
                if (mOnDMEditTextListener != null) {
                    mOnDMEditTextListener.getTextCount(mView, mTextCount);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private String mInputPattenn;
    // 키보드 입력시 해당 패턴 정규화만 노출됨.
    private final InputFilter customInput = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile(mInputPattenn);
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    public DMEditText(Context context) {
        super(context);

        init();
    }

    public DMEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DMEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBackPressListener != null) {
            mBackPressListener.onBackPress(mView);
        }

        return super.onKeyPreIme(keyCode, event);
    }

    private void init() {
        this.addTextChangedListener(textWatcher);
        mView = this;
    }

    public void setFont(String font) {
        this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), font));
    }

    /**
     * 줄수 제한
     *
     * @param line
     */
    public void setLimitLine(int line) {
        mLimitLine = line;
    }

    /**
     * 최대 Text 수 제한
     *
     * @param count
     */
    public void setLimitText(int count) {
        mLimitText = count;
    }

    /**
     * 현재 입력된 Text 갯수
     *
     * @return
     */
    public int getTextCount() {
        return mTextCount;
    }

    /**
     * keyboard 보여주기
     */
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * keyboard 숨기기
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public void setOnDMEditTextListener(onDMEditTextListener listener) {
        mOnDMEditTextListener = listener;
    }

    public void setOnBackPressListener(OnBackPressListener listener) {
        mBackPressListener = listener;
    }

    /**
     * 키보드에 노출되는 패턴
     *
     * @param pattern 정규화 패턴 (ex:DMEditText.INPUT_OLAY_XXXX)
     */
    public void setInputPattern(String pattern) {
        mInputPattenn = pattern;
        setFilters(new InputFilter[]{customInput});
    }

    public interface onDMEditTextListener {
        void getTextCount(View view, int count);

        void getLineCount(View view, int count);

        void onMaxLines(View view);

        void onMaxText(View view);
    }

    public interface OnBackPressListener {
        void onBackPress(View view);
    }
}
