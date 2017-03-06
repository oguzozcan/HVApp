package com.armut.armuthv.components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.armut.armuthv.R;

/**
 * Created by oguzemreozcan on 12/07/16.
 */

public class StrokeCircleButton extends ImageView {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
    private int pressedRingAlpha = 255;//100;//75;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
    private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

    private int centerY;
    private int centerX;
    private int outerRadius;
    private int pressedRingRadius;

    private Paint circlePaint;
    private Paint focusPaint;

    private float animationProgress;

    private int pressedRingWidth;
    private int defaultColor = 0xFF585965;//Color.BLACK;
    private int pressedColor;
    private ObjectAnimator pressedAnimator;

    private boolean colorFill = false;

    public StrokeCircleButton(Context context) {
        super(context);
        init(context, null);
    }

    public StrokeCircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StrokeCircleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (circlePaint != null) {
            circlePaint.setColor(pressed ? pressedColor : defaultColor);
        }

        if (pressed) {
            showPressedRing();
        } else {
            hidePressedRing();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
        canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);

        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = Math.min(w, h) / 2;
        pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public boolean isColorFill() {
        return colorFill;
    }

    public void setColorFill(boolean colorFill) {
        this.colorFill = colorFill;
    }

    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
        this.invalidate();
    }

    public void setColor(int color) {
        this.defaultColor = color;
        this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

        circlePaint.setColor(defaultColor);
        focusPaint.setColor(defaultColor);
        focusPaint.setAlpha(pressedRingAlpha);

        this.invalidate();
    }

    private void hidePressedRing() {
        pressedAnimator.setFloatValues(pressedRingWidth, 0f);
        pressedAnimator.start();
    }

    private void showPressedRing() {
        pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
        pressedAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        this.setFocusable(true);
        this.setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(colorFill ? Paint.Style.FILL : Paint.Style.STROKE);//FILL
        //circlePaint.setStrokeWidth();

        focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusPaint.setStyle(Paint.Style.STROKE);

        pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());
        //pressedRingAlpha = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pressed, getResources()
        //      .getDisplayMetrics());

        int color = defaultColor;
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokeCircleButton);
            color = a.getColor(R.styleable.StrokeCircleButton_scb_color, defaultColor);
            colorFill = a.getInt(R.styleable.StrokeCircleButton_scb_colorStyle, 0) != 0 ;
            pressedRingWidth = (int) a.getDimension(R.styleable.StrokeCircleButton_scb_pressedRingWidth, pressedRingWidth);
            pressedRingAlpha =  a.getInt(R.styleable.StrokeCircleButton_scb_pressedRingAlpha, pressedRingAlpha);
            a.recycle();
        }

        setColor(color);

        focusPaint.setStrokeWidth(pressedRingWidth);
        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
        pressedAnimator.setDuration(pressedAnimationTime);
    }

    private int getHighlightColor(int color, int amount) {
        return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
                Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
    }

    public int getPressedRingRadius() {
        return pressedRingRadius;
    }

    public void setPressedRingRadius(int pressedRingRadius) {
        this.pressedRingRadius = pressedRingRadius;
    }

    public int getPressedRingAlpha() {
        return pressedRingAlpha;
    }

    public void setPressedRingAlpha(int pressedRingAlpha) {
        this.pressedRingAlpha = pressedRingAlpha;
    }
}

