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
public class MonthAnimHelper extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {

    float startX = 0;
    float endX = 0;
    float startY = 0;
    float endY = 0;
    Calendar calendar;
    ValueAnimator animator;
    MonthView monthView;

    static public int[] getCalendarIndexPath(BaseMonthView monthView, Calendar target) {
        int[] result = new int[2];
        result[0] = -1;
        result[1] = -1;

        int d = 0;
        for (int i = 0; i < monthView.mLineCount; i++) {
            for (int j = 0; j < 7; j++) {
                if (d >= monthView.mItems.size()) {
                    return result;
                }
                Calendar calendar = monthView.mItems.get(d);
                if (calendar == target) {
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
                ++d;
            }
        }

        return result;
    }


    /**
     * 动画前准备数据
     */
    public void initAnim(MonthView monthView, int from, int to) {
        this.monthView = monthView;
        calendar = monthView.mItems.get(to);
        int[] fromIndex = getCalendarIndexPath(monthView, monthView.mItems.get(from));
        int[] toIndex = getCalendarIndexPath(monthView, calendar);

        startX = fromIndex[1] * monthView.mItemWidth + monthView.mDelegate.getCalendarPaddingLeft();
        startY = fromIndex[0] * monthView.mItemHeight;

        endX = toIndex[1] * monthView.mItemWidth + monthView.mDelegate.getCalendarPaddingLeft();
        endY = toIndex[0] * monthView.mItemHeight;
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
    public void startAnim(final MonthView monthView, int from, int to) {
        initAnim(monthView, from, to);
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
        if (monthView != null) {
            monthView.monthAnimHelper = null;
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (monthView != null) {
            monthView.invalidate();
        }
    }

    public boolean isStarted() {
        return animator != null && animator.isStarted();
    }

    /**
     * 绘制动画中的日历
     */
    public void draw(final MonthView monthView, Canvas canvas) {
        if (!isStarted()) {
            return;
        }

        float value = (float) animator.getAnimatedValue();
        int x = (int) (startX + (endX - startX) * value);
        int y = (int) (startY + (endY - startY) * value);

        monthView.drawCalendar(canvas, calendar, x, y, true);
    }
}
