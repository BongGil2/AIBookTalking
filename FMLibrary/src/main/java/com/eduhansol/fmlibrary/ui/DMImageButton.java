/**
 * 2014.09.03 JI.DaeMyung
 * <p>
 * ImageView로 버튼 만들기
 */
package com.eduhansol.fmlibrary.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

public class DMImageButton extends AppCompatImageView implements OnClickListener {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_RADIO = 1;
    public static final int TYPE_NULL = 2;
    public static final int IMAGE_TYPE_RESOURCE = 10;
    public static final int IMAGE_TYPE_BITMAP = 11;
    public static final int IMAGE_TYPE_ALPHA = 12;
    public static final int IMAGE_TYPE_NULL = 13;
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

    private boolean isPress;
    private boolean isAuto;
    private boolean isMask;
    private boolean isTouchEnable;
    private onDMImageButtonListener listener;
    private onDMImageButtonTouchListener touchListener;

    private final Rect mBoundingRect = new Rect();

    public DMImageButton(Context context) {
        super(context);

        init();
    }

    public DMImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DMImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        this.setClickable(true);
        this.setOnClickListener(this);

        buttonType = TYPE_NULL;
        imageType = IMAGE_TYPE_NULL;
        isPress = false;
        isMask = false;
        isTouchEnable = true;
    }

    private void setBasebutton() {
        imageType = IMAGE_TYPE_ALPHA;
        buttonType = TYPE_NORMAL;
    }

    public void setTouchEnable(boolean isEnable) {
        isTouchEnable = isEnable;
    }

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
        setImageButton(0, 0, makeHexColor(norColor), makeHexColor(preColor), 1.0f, TYPE_NORMAL);
    }

    public void setImageButton(int norColor, int preColor, float alpah, int type) {
        setImageButton(0, 0, makeHexColor(norColor), makeHexColor(preColor), 1.0f, type);
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

        LayoutParams params = this.getLayoutParams();
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

        changeButtonImageBitmap(false);
    }

    public void setAutoChangeImage(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public void changeButtonImage(boolean isPress) {

        this.isPress = isPress;

        setImageResource(isPress ? pressRes : normalRes);
    }

    public void changeButtonImageBitmap(boolean isPress) {

        this.isPress = isPress;

        setImageBitmap(isPress ? pressBitmap : normalBitmap);
    }

    public void changeButtonImageColor(boolean isPress) {

        this.isPress = isPress;

        setBackgroundColor(isPress ? android.graphics.Color.parseColor(pressColor) : android.graphics.Color.parseColor(normalColor));
    }

    private String makeHexColor(int color) {
        return String.format("#%06x", ContextCompat.getColor(getContext(), color) & 0xffffff);
        //return "#" + Integer.toHexString(ContextCompat.getColor(getContext(), color) & 0x00ffffff);
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

        if (isContainPoints(x, y) == false) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }

        if (buttonType == TYPE_NULL) {
            return super.onTouchEvent(event);
        }

        if (buttonType == TYPE_RADIO) {
            return super.onTouchEvent(event);
        }

        if (imageType == IMAGE_TYPE_ALPHA) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (touchListener != null) {
                        touchListener.onActionDown(this);
                    }
                    this.setBackgroundColor(android.graphics.Color.parseColor(pressColor));
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (touchListener != null) {
                        touchListener.onActionCancel(this);
                    }
                    this.setBackgroundColor(android.graphics.Color.parseColor(normalColor));
                    break;
                case MotionEvent.ACTION_UP:
                    if (touchListener != null) {
                        touchListener.onActionUp(this);
                    }
                    this.setBackgroundColor(android.graphics.Color.parseColor(normalColor));
                    break;
            }
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (imageType == IMAGE_TYPE_RESOURCE) {
                    this.setImageResource(pressRes);
                } else if (imageType == IMAGE_TYPE_BITMAP) {
                    this.setImageBitmap(pressBitmap);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (imageType == IMAGE_TYPE_RESOURCE) {
                    this.setImageResource(normalRes);
                } else if (imageType == IMAGE_TYPE_BITMAP) {
                    this.setImageBitmap(normalBitmap);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (imageType == IMAGE_TYPE_RESOURCE) {
                    this.setImageResource(normalRes);
                } else if (imageType == IMAGE_TYPE_BITMAP) {
                    this.setImageBitmap(normalBitmap);
                }
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
                if (imageType == IMAGE_TYPE_RESOURCE) {
                    changeButtonImage(!isPress);
                } else if (imageType == IMAGE_TYPE_BITMAP) {
                    changeButtonImageBitmap(!isPress);
                } else if (imageType == IMAGE_TYPE_ALPHA) {
                    changeButtonImageColor(!isPress);
                }
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

    public void setonDMImageButtonListener(onDMImageButtonListener listener) {
        this.listener = listener;
    }

    public void setonDMImageButtonTouchListener(onDMImageButtonTouchListener listener) {
        this.touchListener = listener;
    }

    public boolean getButtonState() {
        if (buttonType == TYPE_RADIO) {
            return isPress;
        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();

        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean isContainPoints(int x, int y) {
        getDrawingRect(mBoundingRect);
        return mBoundingRect.contains(x, y);
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

    public interface onDMImageButtonListener {
        void onClick(View view);
    }

    //image Type이 IMAGE_TYPE_ALPHA 일경우에만 가능
    public interface onDMImageButtonTouchListener {
        void onActionDown(View view);

        void onActionUp(View view);

        void onActionCancel(View view);
    }
}
