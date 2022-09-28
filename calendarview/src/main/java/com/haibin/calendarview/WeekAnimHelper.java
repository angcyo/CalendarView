package com.haibin.calendarview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.view.animation.OvershootInterpolator;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2021/10/25
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
public class WeekAnimHelper extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {

    float startX = 0;
    float endX = 0;
    float startY = 0;
    float endY = 0;
    Calendar calendar;
    ValueAnimator animator;
    WeekView weekView;

    /**
     * 动画前准备数据
     */
    public void initAnim(WeekView weekView, int from, int to) {
        this.weekView = weekView;
        calendar = weekView.mItems.get(to);

        startX = from * weekView.mItemWidth + weekView.mDelegate.getCalendarPaddingLeft();
        startY = 0 * weekView.mItemHeight;

        endX = to * weekView.mItemWidth + weekView.mDelegate.getCalendarPaddingLeft();
        endY = 0 * weekView.mItemHeight;
    }

    /**
     * 开始动画
     */
    public void startAnim() {
        cancel();

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(this);
        animator.addListener(this);
        animator.setInterpolator(new OvershootInterpolator());
        animator.setDuration(240);
        animator.start();
    }

    /**
     * 开始动画
     */
    public void startAnim(final WeekView weekView, int from, int to) {
        initAnim(weekView, from, to);
        startAnim();
    }

    public void cancel() {
        if (animator != null) {
            animator.cancel();
            animator.removeUpdateListener(this);
            animator.removeListener(this);
        }
        animator = null;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (weekView != null) {
            weekView.weekAnimHelper = null;
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (weekView != null) {
            weekView.invalidate();
        }
    }

    public boolean isStarted() {
        return animator != null && animator.isStarted();
    }

    /**
     * 绘制动画中的日历
     */
    public void draw(final WeekView weekView, Canvas canvas) {
        if (!isStarted()) {
            return;
        }

        float value = (float) animator.getAnimatedValue();
        int x = (int) (startX + (endX - startX) * value);
        int y = (int) (startY + (endY - startY) * value);

        weekView.drawCalendar(canvas, calendar, x, true);
    }
}
