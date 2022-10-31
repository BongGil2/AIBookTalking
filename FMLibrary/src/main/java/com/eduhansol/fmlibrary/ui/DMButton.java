package com.eduhansol.fmlibrary.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class DMButton extends AppCompatButton implements OnClickListener {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_RADIO = 1;
    public static final int TYPE_NULL = 2;
    public static final int IMAGE_TYPE_RESOURCE = 10;
    public static final int IMAGE_TYPE_BITMAP = 11;
    public static final int IMAGE_TYPE_ALPHA = 12;
    public static final int IMAGE_TYPE_NULL = 13;
    public static final int IMAGE_TYPE_STYLE = 14;
    private static final long MIN_CLICK_INTERVAL = 600;
    //마지막으로 클릭한 시간
    private long mLastClickTime = 0;
    private int buttonType;
    private int imageType;

    private int normalRes;
    private int pressRes;

    private Bitmap normalBitmap;
    private Bitmap pressBitmap;

    private Bitmap maskTargetBitmap;
    private Bitmap maskBaseBitmap;

    private String normalColor;
    private String pressColor;

    private String mNormalText;
    private String mPressText;

    private String mNormalTextColor;
    private String mPressTextColor;

    private Drawable normalDrawable;
    private Drawable pressDrawable;

    private boolean isPress;
    private boolean isAuto;
    private boolean isMask;
    private boolean isTouchEnable;
    private boolean isText;
    private int buttonStyle;
    private int mRadius = -1;

    private onDMButtonListener listener;
    private onDMButtonTouchListener touchListener;

    private final Rect mBoundingRect = new Rect();

    public DMButton(Context context) {
        super(context);

        init();
    }

    public DMButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DMButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        this.setClickable(true);
        this.setOnClickListener(this);

        buttonType = TYPE_NORMAL;
        imageType = IMAGE_TYPE_NULL;
        isPress = false;
        isMask = false;
        isTouchEnable = true;
        isText = false;
    }

    /**
     * 폰트를 지정한다.
     *
     * @param font 폰트 이름
     */
    public void setTextFont(String font) {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), font));
    }

    public void setTextFont(String font, int style) {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), font), style);
    }

    public void setTextFontStyle(int style) {
        setTypeface(getTypeface(), style);
    }

    // ---------------------        버튼을 Color로 만들때   ---------------------------------------
    public void setImageButton(String colorString) {
        setImageButton(0, 0, colorString, colorString, 1.0f, TYPE_NORMAL);
    }

    public void setImageButton(String norColor, String preColor) {
        setImageButton(0, 0, norColor, preColor, 1.0f, TYPE_NORMAL);
    }

    public void setImageButton(String norColor, String preColor, int type) {
        setImageButton(0, 0, norColor, preColor, 1.0f, type);
    }

    public void setImageButton(int norColor, int preColor, float alpah) {
        setImageButton(0, 0, makeHexColor(norColor), makeHexColor(preColor), alpah, TYPE_NORMAL);
    }

    public void setImageButton(int norColor, int preColor, float alpah, int type) {
        setImageButton(0, 0, makeHexColor(norColor), makeHexColor(preColor), alpah, type);
    }

    public void setImageButton(int width, int height, String colorString, float alpha, int type) {
        setImageButton(width, height, colorString, colorString, alpha, type);
    }

    public void setImageButton(int width, int height, int norColor, int preColor, float alpha, int type) {
        setImageButton(width, height, makeHexColor(norColor), makeHexColor(preColor), alpha, type);
    }

    public void setImageButton(int width, int height, String norColor, String preColor, float alpha, int type) {
        imageType = IMAGE_TYPE_ALPHA;
        buttonType = type;

        ViewGroup.LayoutParams params = this.getLayoutParams();
        if (width != 0) {
            params.width = width;
        }

        if (height != 0) {
            params.height = height;
        }

        normalColor = norColor;
        pressColor = preColor;

        this.setBackgroundColor(android.graphics.Color.parseColor(norColor));
        if (width != 0 || height != 0) {
            this.setLayoutParams(params);
        }
        this.setAlpha(alpha);
    }

    // ---------------------        버튼을 Resource로 만들때   ---------------------------------------
    public void setImageButton(int res) {
        setImageButton(res, res, TYPE_NORMAL);
    }

    public void setImageButton(int normal, int press) {
        setImageButton(normal, press, TYPE_NORMAL);
    }

    public void setImageButton(int normal, int press, int type) {
        imageType = IMAGE_TYPE_RESOURCE;
        buttonType = type;

        normalRes = normal;
        pressRes = press;

        changeButtonImage(false);
    }

    // ---------------------        버튼을 Bitmap으로 만들때   ---------------------------------------
    public void setImageButton(Bitmap bitmap) {
        setImageButton(bitmap, bitmap, TYPE_NORMAL);
    }

    public void setImageButton(Bitmap normal, Bitmap press) {
        setImageButton(normal, press, TYPE_NORMAL);
    }

    public void setImageButton(Bitmap normal, Bitmap press, int type) {
        imageType = IMAGE_TYPE_BITMAP;
        buttonType = type;

        normalBitmap = normal;
        pressBitmap = press;
        Drawable ndrawable = new BitmapDrawable(getResources(), normalBitmap);
        Drawable pdrawable = new BitmapDrawable(getResources(), pressBitmap);

        normalDrawable = ndrawable;
        pressDrawable = pdrawable;

        changeButtonImageBitmap(false);
    }

    // ---------------------        버튼에 Style를 적용할때   ---------------------------------------

    public void setImageButtonStyle(int style, String color) {
        setImageButtonStyle(0, 0, style, color, color, 1.0f);
    }

    public void setImageButtonStyle(int style, String norColor, String pressColor) {
        setImageButtonStyle(0, 0, style, norColor, pressColor, 1.0f);
    }

    public void setImageButtonStyle(int width, int height, int style, String color, float alpha) {
        setImageButtonStyle(width, height, style, color, color, alpha);
    }

    public void setImageButtonStyle(int style, int color) {
        setImageButtonStyle(0, 0, style, makeHexColor(color), makeHexColor(color), 1.0f);
    }

    public void setImageButtonStyle(int style, int norColor, int pressColor) {
        setImageButtonStyle(0, 0, style, makeHexColor(norColor), makeHexColor(pressColor), 1.0f);
    }

    public void setImageButtonStyle(int width, int height, int style, int color, float alpha) {
        setImageButtonStyle(width, height, style, makeHexColor(color), makeHexColor(color), alpha);
    }

    public void setImageButtonStyle(int width, int height, int style, String norColor, String pressColor, float alpha) {
        this.imageType = IMAGE_TYPE_STYLE;
        this.buttonStyle = style;
        this.normalColor = norColor;
        this.pressColor = pressColor;

        ViewGroup.LayoutParams params = this.getLayoutParams();
        if (width != 0) {
            params.width = width;
        }

        if (height != 0) {
            params.height = height;
        }

        changeButtonStyleColor(false);
        this.setAlpha(alpha);
    }

    // ---------------------        버튼에 Text를 넣을때 만들때   ---------------------------------------
    public void setButtonText(int normalColor, int pressColor) {
        setButtonText(getText().toString(), getText().toString(), makeHexColor(normalColor), makeHexColor(pressColor));
    }

    public void setButtonText(int text, int normalColor, int pressColor) {
        setButtonText(getResources().getString(text), getResources().getString(text), makeHexColor(normalColor), makeHexColor(pressColor));
    }

    public void setButtonText(int normalText, int pressText, int normalColor, int pressColor) {
        setButtonText(getResources().getString(normalText), getResources().getString(pressText), makeHexColor(normalColor), makeHexColor(pressColor));
    }

    /**
     * 버튼의 색상을 변경 하였을경우 setText()를 사용하지 말고 이 메소드 사용필요
     * 버튼의 기본 text로 변경됨
     *
     * @param text
     */
    public void setButtonText(String text) {
        setButtonText(text, text, mNormalTextColor, mNormalTextColor);
    }

    public void setButtonText(String normalText, String pressText, String normalColor, String pressColor) {
        mNormalText = normalText;
        mPressText = pressText;

        mNormalTextColor = normalColor;
        mPressTextColor = pressColor;

        isText = true;
        changeButtonText();
    }

    /**
     * 버튼 터치 가능 여부
     *
     * @param isEnable
     */
    public void setTouchEnable(boolean isEnable) {
        isTouchEnable = isEnable;
    }

    /**
     * 버튼의 상태를 자동으로 변경해 줄지 여부
     *
     * @param isAuto
     */
    public void setAutoChangeImage(boolean isAuto) {
        this.isAuto = isAuto;
    }

    /**
     * 현재 버튼의 상태값을 가져온다
     *
     * @return
     */
    public boolean getButtonState() {
        if (buttonType == TYPE_RADIO) {
            return isPress;
        }

        return false;
    }

    /**
     * 버튼의 상태를 변경해줌
     *
     * @param isPress 눌림, 일반 상태값
     */
    public void changeButton(boolean isPress) {
        switch (imageType) {
            case IMAGE_TYPE_RESOURCE:
                changeButtonImage(isPress);
                break;
            case IMAGE_TYPE_BITMAP:
                changeButtonImageBitmap(isPress);
                break;
            case IMAGE_TYPE_ALPHA:
                changeButtonImageColor(isPress);
                break;
            case IMAGE_TYPE_STYLE:
                changeButtonStyleColor(isPress);
                break;
        }

        changeButtonText();
    }

    @Deprecated
    private void changeButtonImage(boolean isPress) {

        this.isPress = isPress;
        setBackgroundResource(isPress ? pressRes : normalRes);
    }

    @Deprecated
    private void changeButtonImageBitmap(boolean isPress) {

        this.isPress = isPress;

        setBackground(isPress ? pressDrawable : normalDrawable);
    }

    @Deprecated
    private void changeButtonImageColor(boolean isPress) {

        this.isPress = isPress;

        setBackgroundColor(isPress ? android.graphics.Color.parseColor(pressColor) : android.graphics.Color.parseColor(normalColor));
    }

    private void changeButtonStyleColor(boolean isPress) {
        this.isPress = isPress;

        //테두리 스타일을 적용한 상태에서 BG만 변경해야 할 경우 때문에 적용
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(getContext(), buttonStyle);
        drawable.setColor(isPress ? android.graphics.Color.parseColor(pressColor) : android.graphics.Color.parseColor(normalColor));
        if (mRadius > 0) {
            drawable.setCornerRadius(mRadius);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    /**
     * style 이 적용된 bg에 radius를 변경한다.
     * style 설정 전에 미리 변경해 둬야 한다.
     *
     * @param radius
     */
    public void setRadius(int radius) {
        mRadius = radius;
    }

    /**
     * 버튼 비활성화 적용
     *
     * @param bgColor   버튼 배경 색상
     * @param textColor 버튼 내부 text 색
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void buttonLockMode(boolean isLock, int bgColor, int textColor) {
        if (isLock) {
            this.isTouchEnable = false;

            Drawable roundDrawable = getResources().getDrawable(buttonStyle);
            roundDrawable.setColorFilter(android.graphics.Color.parseColor(makeHexColor(bgColor)), PorterDuff.Mode.SRC_ATOP);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                setBackgroundDrawable(roundDrawable);
            } else {
                setBackground(roundDrawable);
            }

            setTextColor(android.graphics.Color.parseColor(makeHexColor(textColor)));


        } else {
            this.isTouchEnable = true;
            changeButton(false);
            changeButtonText();
        }

    }

    public void setButtonType(int buttonType) {
        this.buttonType = buttonType;
    }

    public void changeButtonText() {
        if (!isText) {
            return;
        }

        setText(isPress ? mPressText : mNormalText);
        setTextColor(android.graphics.Color.parseColor(isPress ? mPressTextColor : mNormalTextColor));
    }

    public void setImageMask(int baseRes, int maskRes) {
        isMask = true;

        maskTargetBitmap = BitmapFactory.decodeResource(getResources(), baseRes);
        maskBaseBitmap = BitmapFactory.decodeResource(getResources(), maskRes);
    }

    public void setImageMask(Bitmap baseBitmap, Bitmap maskBitmap) {
        isMask = true;

        maskTargetBitmap = baseBitmap;
        maskBaseBitmap = maskBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchEnable) {
            return super.onTouchEvent(event);
        }

        if (isMask) {
            return super.onTouchEvent(event);
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (!isContainPoints(x, y)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }

        if (buttonType == TYPE_NULL) {
            return super.onTouchEvent(event);
        }

        if (buttonType == TYPE_RADIO) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchListener != null) {
                    touchListener.onActionDown(this);
                }

                changeButton(true);

                break;
            case MotionEvent.ACTION_CANCEL:
                if (touchListener != null) {
                    touchListener.onActionCancel(this);
                }

                changeButton(false);
                break;
            case MotionEvent.ACTION_UP:
                if (touchListener != null) {
                    touchListener.onActionUp(this);
                }

                changeButton(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (!isTouchEnable) {
            return;
        }

        if (!isMask) {
            if (buttonType == TYPE_RADIO && isAuto) {
                changeButton(!isPress);
            }
        }

        //현재 클릭한 시간
        long currentClickTime = SystemClock.uptimeMillis();
        //이전에 클릭한 시간과 현재시간의 차이
        long elapsedTime = currentClickTime - mLastClickTime;
        //마지막클릭시간 업데이트
        mLastClickTime = currentClickTime;

        //내가 정한 중복클릭시간 차이를 안넘었으면 클릭이벤트 발생못하게 return
        if (elapsedTime <= MIN_CLICK_INTERVAL)
            return;

        if (listener != null) {
            listener.onClick(this);
        }
    }

    public void setonDMButtonListener(onDMButtonListener listener) {
        this.listener = listener;
    }

    public void setonDMButtonTouchListener(onDMButtonTouchListener listener) {
        this.touchListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (!isMask) {
            super.onDraw(canvas);
            return;
        }

        Canvas c = new Canvas();
        Bitmap result = Bitmap.createBitmap(maskTargetBitmap.getWidth(), maskTargetBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        c.setBitmap(result);
        c.drawBitmap(maskTargetBitmap, 0, 0, null);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN)); // DST_OUT

        c.drawBitmap(maskBaseBitmap, 0, 0, paint);
        paint.setXfermode(null);
        canvas.drawBitmap(result, 0, 0, null);
    }

    /**
     * int형 컬러값을 String 형으로 변경해줌.
     *
     * @param color
     * @return
     */
    private String makeHexColor(int color) {
        return String.format("#%06x", ContextCompat.getColor(getContext(), color) & 0xffffff);
        //return "#" + Integer.toHexString(ContextCompat.getColor(getContext(), color) & 0x00ffffff);
    }

    private boolean isContainPoints(int x, int y) {
        getDrawingRect(mBoundingRect);
        return mBoundingRect.contains(x, y);
    }

    public interface onDMButtonListener {
        void onClick(View view);
    }

    public interface onDMButtonTouchListener {
        void onActionDown(View view);

        void onActionUp(View view);

        void onActionCancel(View view);
    }
}
