package com.siziksu.acv;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {

    private boolean pointsAlreadyReceived;

    private Paint progressPaint;
    private RectF progressBounds;
    private int progressWidth = 20;
    private int progressColor = 0xFFFFFFFF;
    private float progressValue;
    private ValueAnimator progressAnimation;
    private int progressDuration = 1800;
    private boolean secondStep;

    private int scaleDuration = 200;
    private float scaleValue = 1f;

    private Paint primaryPaint;
    private RectF primaryBounds;
    private Paint secondaryPaint;
    private RectF secondaryBounds;

    private int primaryColor = 0x00000000;
    private int secondaryColor = 0x00000000;
    private int layoutWidth;
    private int layoutHeight;
    private int horizontalCompensation = 0;
    private int verticalCompensation = 0;
    private int circlePercentSeparation = 100;

    private RectF mainBounds;
    private int radius;
    private int diameter;
    private int primaryCenterX;
    private int primaryCenterY;
    private int secondaryCenterX;
    private int secondaryCenterY;

    private int points;
    private int extras;
    private int total;
    private int primaryTextColor = 0xFFFFFFFF;
    private int secondaryTextColor = 0xFFFFFFFF;
    private float normalTextSize;
    private TextPaint primaryTextPaint = new TextPaint();
    private TextPaint secondaryTextPaint = new TextPaint();
    private int primaryTextHeight;
    private int secondaryTextHeight;

    private String pointsText = "POINTS";
    private String plusText = "PLUS";
    private String extraText = "EXTRA";
    private TextPaint mediumTextPaint = new TextPaint();
    private TextPaint smallTextPaint = new TextPaint();
    private float mediumTextSize;
    private float smallTextSize;
    private int mediumTextHeight;
    private int smallTextHeight;
    private int scaleOffsetCompensation;

    public CustomView(Context context) {
        super(context);
        init(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);
        progressWidth = (int) attributes.getDimension(R.styleable.CustomView_progressWidth, progressWidth);
        progressColor = attributes.getColor(R.styleable.CustomView_progressColor, progressColor);
        progressDuration = attributes.getInt(R.styleable.CustomView_progressDuration, progressDuration);
        circlePercentSeparation = attributes.getInt(R.styleable.CustomView_circlePercentSeparation, circlePercentSeparation);
        scaleValue = attributes.getFloat(R.styleable.CustomView_scaleValue, scaleValue);
        scaleDuration = attributes.getInt(R.styleable.CustomView_scaleDuration, scaleDuration);
        primaryColor = attributes.getColor(R.styleable.CustomView_primaryColor, primaryColor);
        secondaryColor = attributes.getColor(R.styleable.CustomView_secondaryColor, secondaryColor);
        primaryTextColor = attributes.getColor(R.styleable.CustomView_primaryTextColor, primaryTextColor);
        secondaryTextColor = attributes.getColor(R.styleable.CustomView_secondaryTextColor, secondaryTextColor);
        attributes.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutWidth = w - this.getPaddingLeft() - this.getPaddingRight();
        layoutHeight = h - this.getPaddingTop() - this.getPaddingBottom();
        setUpBounds();
        setUpPaints();
        setUpTextBounds();
        progressValueAnimation();
    }

    private void setUpBounds() {
        calculateCompensations();
        int left = (horizontalCompensation / 2) + (progressWidth / 2) + this.getPaddingLeft() + this.getPaddingRight();
        int top = (verticalCompensation / 2) + (progressWidth / 2) + this.getPaddingTop() + this.getPaddingBottom();
        int right = (layoutWidth - (horizontalCompensation / 2)) - (progressWidth / 2);
        int bottom = (layoutHeight - (verticalCompensation / 2)) - (progressWidth / 2);
        progressBounds = new RectF(left, top, right, bottom);
        left -= progressWidth / 2;
        top -= progressWidth / 2;
        right += progressWidth / 2;
        bottom += progressWidth / 2;
        mainBounds = new RectF(left, top, right, bottom);
        primaryBounds = new RectF(mainBounds);
        secondaryBounds = new RectF(mainBounds);
        diameter = right - left;
        radius = diameter / 2;
        primaryCenterX = (int) mainBounds.centerX();
        primaryCenterY = (int) mainBounds.centerY();
        secondaryCenterX = (int) mainBounds.centerX();
        secondaryCenterY = (int) mainBounds.centerY();
        normalTextSize = diameter * 30 / 100;
        mediumTextSize = normalTextSize / 2.5f;
        smallTextSize = normalTextSize / 3f;
    }

    private void calculateCompensations() {
        if (layoutWidth != layoutHeight) {
            if (layoutWidth > layoutHeight) {
                horizontalCompensation = layoutWidth - layoutHeight;
            }
            if (layoutHeight > layoutWidth) {
                verticalCompensation = layoutHeight - layoutWidth;
            }
        }
    }

    private void setUpPaints() {
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(progressWidth);

        primaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        primaryPaint.setStyle(Paint.Style.FILL);
        primaryPaint.setColor(primaryColor);

        secondaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secondaryPaint.setStyle(Paint.Style.FILL);
        secondaryPaint.setColor(secondaryColor);

        primaryTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        primaryTextPaint.setAntiAlias(true);
        primaryTextPaint.setColor(primaryTextColor);
        primaryTextPaint.setTextSize(normalTextSize);

        int textStyle = Typeface.NORMAL;

        primaryTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, textStyle));
        primaryTextPaint.setTextAlign(Paint.Align.CENTER);
        primaryTextPaint.setAlpha(0);

        secondaryTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        secondaryTextPaint.setAntiAlias(true);
        secondaryTextPaint.setColor(secondaryTextColor);
        secondaryTextPaint.setTextSize(normalTextSize);
        secondaryTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, textStyle));
        secondaryTextPaint.setTextAlign(Paint.Align.CENTER);
        secondaryTextPaint.setAlpha(0);

        mediumTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mediumTextPaint.setAntiAlias(true);
        mediumTextPaint.setColor(primaryTextColor);
        mediumTextPaint.setTextSize(mediumTextSize);
        mediumTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, textStyle));
        mediumTextPaint.setTextAlign(Paint.Align.CENTER);
        mediumTextPaint.setAlpha(0);

        smallTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        smallTextPaint.setAntiAlias(true);
        smallTextPaint.setColor(secondaryTextColor);
        smallTextPaint.setTextSize(smallTextSize);
        smallTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, textStyle));
        smallTextPaint.setTextAlign(Paint.Align.CENTER);
        smallTextPaint.setAlpha(0);
    }

    private void setUpTextBounds() {
        Rect bounds = new Rect();
        primaryTextPaint.getTextBounds(String.valueOf(total), 0, String.valueOf(total).length(), bounds);
        primaryTextHeight = bounds.height();
        secondaryTextPaint.getTextBounds(String.valueOf(extras), 0, String.valueOf(extras).length(), bounds);
        secondaryTextHeight = bounds.height();
        mediumTextPaint.getTextBounds(pointsText, 0, pointsText.length(), bounds);
        mediumTextHeight = bounds.height();
        smallTextPaint.getTextBounds(plusText, 0, plusText.length(), bounds);
        smallTextHeight = bounds.height();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float startAngle;
        float sweepAngle;
        if (progressValue > 360) {
            startAngle = (((progressValue - 360) * 0.5f) + 90);
            startAngle = startAngle > 360 ? startAngle - 360 : startAngle;
            sweepAngle = ((360 - (progressValue - 360)));
        } else {
            startAngle = ((progressValue * 0.5f) - 90);
            sweepAngle = progressValue;
        }
        canvas.drawArc(progressBounds, startAngle, sweepAngle, false, progressPaint);
        if (secondStep) {
            canvas.drawArc(secondaryBounds, 0, 360, false, secondaryPaint);
            canvas.drawText(String.valueOf(extras), getSecondaryPositionX(), getExtraPointsPositionY(), secondaryTextPaint);
            canvas.drawText(plusText, getSecondaryPositionX(), getPlusTextPositionY(), smallTextPaint);
            canvas.drawText(extraText, getSecondaryPositionX(), getExtraTextPositionY(), smallTextPaint);
            canvas.drawText(pointsText, getSecondaryPositionX(), getPointsExtraTextPositionY(), smallTextPaint);
            canvas.drawArc(primaryBounds, 0, 360, false, primaryPaint);
            canvas.drawText(String.valueOf(total), getPrimaryPositionX(), getTotalPointsPositionY(), primaryTextPaint);
            canvas.drawText(pointsText, getPrimaryPositionX(), getPointsTextPositionY(), mediumTextPaint);
        }
    }

    private int getPrimaryPositionX() {
        return primaryCenterX;
    }

    private int getSecondaryPositionX() {
        return secondaryCenterX;
    }

    private int getTotalPointsPositionY() {
        return primaryCenterY + primaryTextHeight / 3 + scaleOffsetCompensation;
    }

    private int getPointsTextPositionY() {
        return primaryCenterY + primaryTextHeight + scaleOffsetCompensation;
    }

    private float getExtraPointsPositionY() {
        return secondaryCenterY + secondaryTextHeight / 3;
    }

    private float getPlusTextPositionY() {
        return secondaryCenterY - secondaryTextHeight / 2 - smallTextHeight * 1.5f;
    }

    private float getExtraTextPositionY() {
        return secondaryCenterY + secondaryTextHeight / 2 + smallTextHeight * 1.5f;
    }

    private float getPointsExtraTextPositionY() {
        return secondaryCenterY + secondaryTextHeight / 2 + smallTextHeight * 3f;
    }

    private void progressValueAnimation() {
        progressAnimation = ValueAnimator.ofFloat(0, 720);
        progressAnimation.setDuration(progressDuration);
        progressAnimation.setRepeatCount(ValueAnimator.INFINITE);
        progressAnimation.setRepeatMode(ValueAnimator.RESTART);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimation.addUpdateListener(valueAnimator -> {
            progressValue = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        progressAnimation.start();
    }

    private void alphaAnimation(boolean withExtras) {
        ValueAnimator alphaAnimation = ValueAnimator.ofInt(0, 255);
        alphaAnimation.setDuration(800);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.addUpdateListener(valueAnimator -> {
            int value = (int) valueAnimator.getAnimatedValue();
            primaryPaint.setAlpha(value);
            secondaryPaint.setAlpha(value);
            progressPaint.setAlpha(255 - value);
            invalidate();
        });
        alphaAnimation.addListener(new AnimationListener(() -> {
            AnimatorSet animation = new AnimatorSet();
            List<Animator> list = new ArrayList<>();
            ValueAnimator nextAlphaAnimation = ValueAnimator.ofInt(0, 255);
            nextAlphaAnimation.setDuration(800);
            nextAlphaAnimation.setInterpolator(new LinearInterpolator());
            nextAlphaAnimation.addUpdateListener(valueAnimator -> {
                int value = (int) valueAnimator.getAnimatedValue();
                primaryTextPaint.setAlpha(value);
                secondaryTextPaint.setAlpha(value);
                mediumTextPaint.setAlpha(value);
                smallTextPaint.setAlpha(value);
                invalidate();
            });
            ValueAnimator translation = ValueAnimator.ofInt(primaryTextHeight * 2, 0);
            nextAlphaAnimation.setDuration(800);
            translation.setInterpolator(new LinearInterpolator());
            translation.addUpdateListener(valueAnimator -> {
                scaleOffsetCompensation = (int) valueAnimator.getAnimatedValue();
                invalidate();
            });
            list.add(nextAlphaAnimation);
            list.add(translation);
            animation.playTogether(list);
            animation.addListener(new AnimationListener(() -> {
                if (withExtras) {
                    animations();
                }
            }));
            animation.start();
        }));
        alphaAnimation.start();
    }

    private void animations() {
        AnimatorSet animation = new AnimatorSet();
        List<Animator> list = new ArrayList<>();
        int left = (int) mainBounds.left;
        int right = (int) mainBounds.right;
        ValueAnimator firstTranslation = ValueAnimator.ofInt(left, left - (radius * circlePercentSeparation / 100));
        firstTranslation.setInterpolator(new LinearInterpolator());
        firstTranslation.addUpdateListener(valueAnimator -> {
            int value = (int) valueAnimator.getAnimatedValue();
            int increment = left - value;
            primaryBounds.set(value, primaryBounds.top, value + diameter, primaryBounds.bottom);
            secondaryBounds.set(left + increment, secondaryBounds.top, right + increment, secondaryBounds.bottom);
            primaryCenterX = (int) primaryBounds.centerX();
            primaryCenterY = (int) primaryBounds.centerY();
            secondaryCenterX = (int) secondaryBounds.centerX();
            secondaryCenterY = (int) secondaryBounds.centerY();
            invalidate();
        });
        ValueAnimator secondTranslation = ValueAnimator.ofInt(left - (radius * circlePercentSeparation / 100), left);
        secondTranslation.setStartDelay(1500);
        secondTranslation.setInterpolator(new LinearInterpolator());
        secondTranslation.addUpdateListener(valueAnimator -> {
            int value = (int) valueAnimator.getAnimatedValue();
            int increment = left - value;
            primaryBounds.set(value, primaryBounds.top, value + diameter, primaryBounds.bottom);
            secondaryBounds.set(left + increment, secondaryBounds.top, right + increment, secondaryBounds.bottom);
            primaryCenterX = (int) primaryBounds.centerX();
            primaryCenterY = (int) primaryBounds.centerY();
            secondaryCenterX = (int) secondaryBounds.centerX();
            secondaryCenterY = (int) secondaryBounds.centerY();
            invalidate();
        });
        secondTranslation.addListener(new AnimationListener(() -> total = points + extras));
        ValueAnimator scale = ValueAnimator.ofFloat(1.0f, scaleValue);
        scale.setInterpolator(new LinearInterpolator());
        scale.setDuration(scaleDuration);
        scale.setRepeatCount(1);
        scale.setRepeatMode(ValueAnimator.REVERSE);
        scale.addUpdateListener(valueAnimator -> {
            float scaleFactor = (float) valueAnimator.getAnimatedValue();
            float factor = (diameter * scaleFactor) - diameter;
            float newRight = mainBounds.right + factor;
            float newBottom = mainBounds.bottom + factor;
            float newLeft = mainBounds.left;
            float newTop = mainBounds.top;
            primaryBounds.set(newLeft, newTop, newRight, newBottom);
            int offsetH = (int) (mainBounds.width() - primaryBounds.width()) / 2;
            int offsetV = (int) (mainBounds.height() - primaryBounds.height()) / 2;
            primaryBounds.offset(offsetH, offsetV);
            primaryCenterX = (int) primaryBounds.centerX();
            primaryCenterY = (int) primaryBounds.centerY();
            secondaryCenterX = (int) secondaryBounds.centerX();
            secondaryCenterY = (int) secondaryBounds.centerY();
            primaryTextPaint.setTextSize(normalTextSize * scaleFactor);
            mediumTextPaint.setTextSize(mediumTextSize * scaleFactor);
            invalidate();
        });
        list.add(firstTranslation);
        list.add(secondTranslation);
        list.add(scale);
        animation.playSequentially(list);
        animation.start();
    }

    public void onPointsReceived(int points) {
        if (pointsAlreadyReceived) {
            return;
        }
        this.points = points;
        total = points;
        stopInfiniteProgress();
        secondStep = true;
        alphaAnimation(false);
        pointsAlreadyReceived = true;
    }

    public void onPointsReceived(int points, int extras) {
        if (pointsAlreadyReceived) {
            return;
        }
        this.points = points;
        this.extras = extras;
        total = points;
        stopInfiniteProgress();
        secondStep = true;
        alphaAnimation(true);
        pointsAlreadyReceived = true;
    }

    private void stopInfiniteProgress() {
        progressAnimation.setRepeatCount(0);
        progressAnimation.setRepeatMode(ValueAnimator.RESTART);
    }

    private class AnimationListener implements Animator.AnimatorListener {

        private onAnimationEndListener listener;

        AnimationListener(onAnimationEndListener listener) {
            this.listener = listener;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (listener != null) {
                listener.onAnimationEnd();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public interface onAnimationEndListener {

        void onAnimationEnd();
    }
}
