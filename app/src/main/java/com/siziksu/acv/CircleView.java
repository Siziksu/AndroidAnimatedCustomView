package com.siziksu.acv;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class CircleView extends View {

    private static final long DURATION = 1800;

    private Paint circlePaint;
    private int barWidth = 20;
    private int barColor = 0xFF000000;
    private RectF circleBounds;
    private float valueToDraw;
    private ValueAnimator animation;
    private OnEndListener listener;

    public CircleView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0);
        barWidth = (int) attributes.getDimension(R.styleable.CircleView_circleWidth, barWidth);
        barColor = attributes.getColor(R.styleable.CircleView_circleColor, barColor);
        attributes.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setUpPaints();
        setUpBounds(w, h);
        anim();
    }

    private void setUpPaints() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(barColor);
        circlePaint.setStrokeWidth(barWidth);
        circlePaint.setStyle(Paint.Style.STROKE);
    }

    private void setUpBounds(int w, int h) {
        // Width should equal to Height, find the min value to setup the circle
        int minWidthHeightValue = Math.min(w, h);
        // Calc the Offset if needed
        int xOffset = w - minWidthHeightValue;
        int yOffset = h - minWidthHeightValue;
        // Add the offset
        int paddingTop = this.getPaddingTop() + (yOffset / 2);
        int paddingBottom = this.getPaddingBottom() + (yOffset / 2);
        int paddingLeft = this.getPaddingLeft() + (xOffset / 2);
        int paddingRight = this.getPaddingRight() + (xOffset / 2);

        int left = paddingLeft + barWidth / 2;
        int top = paddingTop + barWidth / 2;
        int right = this.getLayoutParams().width - paddingRight - barWidth / 2;
        int bottom = this.getLayoutParams().height - paddingBottom - barWidth / 2;
        circleBounds = new RectF(left, top, right, bottom);
        circleBounds = new RectF(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float startAngle;
        float sweepAngle;
        if (valueToDraw > 360) {
            startAngle = (((valueToDraw - 360) * 0.5f) + 90);
            startAngle = startAngle > 360 ? startAngle - 360 : startAngle;
            sweepAngle = ((360 - (valueToDraw - 360)));
        } else {
            startAngle = ((valueToDraw * 0.5f) - 90);
            sweepAngle = valueToDraw;
        }
        canvas.drawArc(circleBounds, startAngle, sweepAngle, false, circlePaint);
    }

    private void anim() {
        animation = ValueAnimator.ofFloat(0, 720);
        animation.setDuration(DURATION);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setRepeatMode(ValueAnimator.RESTART);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.addUpdateListener(valueAnimator -> {
            valueToDraw = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        animation.addListener(new CircleListener());
        animation.start();
    }

    public void stop() {
        animation.setRepeatCount(0);
        animation.setRepeatMode(ValueAnimator.RESTART);
    }

    public void setOnEndListener(OnEndListener listener) {
        this.listener = listener;
    }

    private class CircleListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (listener != null) {
                listener.onEnd();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public interface OnEndListener {

        void onEnd();
    }
}
