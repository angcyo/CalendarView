package com.haibin.calendarviewproject.mark;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextPaint;

import com.haibin.calendarview.BaseView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarUtil;
import com.haibin.calendarview.IDrawBaseView;

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/07
 */
public class MarkProperty implements IDrawBaseView {

    /**
     * 半径
     */
    int schemeRadius;

    /**
     * 是否需要绘制月份
     */
    boolean drawMonthText = true;

    /**
     * 绘制月份
     */
    Paint monthTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 其他月份圆圈的颜色
     */
    int otherMonthSchemeColor = Color.parseColor("#A6BEF7");

    /**
     * 其他月份圆圈内文本的颜色
     */
    int otherMonthSchemeTextColor = Color.parseColor("#C9D8FA");

    /**
     * 今天 横线的宽度
     */
    float currentDayLineWidth = -1f;

    /**
     * 今天 横线的高度
     */
    float currentDayLineHeight = -1f;

    RectF lineRect = new RectF();

    /**
     * 主要的渐变背景
     */
    GradientDrawable primaryDrawable;

    /**
     * 次要的渐变背景
     */
    GradientDrawable secondDrawable;

    public MarkProperty() {
        monthTextPaint.setAntiAlias(true);
        monthTextPaint.setStyle(Paint.Style.FILL);
        monthTextPaint.setTextAlign(Paint.Align.CENTER);

        primaryDrawable = new GradientDrawable();
        primaryDrawable.setShape(GradientDrawable.OVAL);
        //primaryDrawable.setCornerRadius();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            primaryDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            primaryDrawable.setColors(new int[]{
                    Color.parseColor("#21C5F5"),
                    Color.parseColor("#0097FF")
            });
        }

        secondDrawable = new GradientDrawable();
        secondDrawable.setShape(GradientDrawable.OVAL);
        //secondDrawable.setCornerRadius();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            secondDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            secondDrawable.setColors(new int[]{
                    Color.parseColor("#6621C5F5"),
                    Color.parseColor("#660097FF")});
        }
    }

    void onPreviewHook(BaseView baseView) {
        schemeRadius = dpToPx(baseView.getContext(), 17); //Math.min(baseView.mItemWidth, baseView.mItemHeight) / 6 * 2;
        monthTextPaint.setTextSize(baseView.mCurMonthTextPaint.getTextSize() * 3 / 4);
        currentDayLineWidth = dpToPx(baseView.getContext(), 6);
        currentDayLineHeight = dpToPx(baseView.getContext(), 1.5f);
    }

    float getOffset() {
        if (!drawMonthText) {
            return 0;
        }
        return -monthTextPaint.ascent() / 2;
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    boolean isInPriorityShowWeekMode(BaseView baseView, Calendar calendar) {
        long distance = CalendarUtil.distance(baseView.mDelegate.mCurrentDate, calendar);
        return baseView.mDelegate.monthPriorityShowWeekMode && distance >= 0 &&
                distance < CalendarUtil.MONTH_PRIORITY_DAY;
    }

    @Override
    public boolean onDrawSelected(BaseView baseView, Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        float cx = x + baseView.mItemWidth / 2f;
        float cy = y + baseView.mItemHeight / 2f + getOffset();
        canvas.drawCircle(cx, cy, schemeRadius, baseView.mSelectedPaint);
        return false;
    }

    @Override
    public void onDrawScheme(BaseView baseView, Canvas canvas, Calendar calendar, int x, int y) {
        float cx = x + baseView.mItemWidth / 2f;
        float cy = y + baseView.mItemHeight / 2f + getOffset();

        Drawable drawDrawable = primaryDrawable;

        if (baseView.mDelegate.monthPriorityShowWeekMode) {
            if (!isInPriorityShowWeekMode(baseView, calendar)) {
                baseView.mSchemePaint.setColor(otherMonthSchemeColor);
                drawDrawable = secondDrawable;
            }
        } else if (!calendar.isCurrentMonth()) {
            baseView.mSchemePaint.setColor(otherMonthSchemeColor);
            drawDrawable = secondDrawable;
        }

        if (drawDrawable == null) {
            canvas.drawCircle(cx, cy, schemeRadius, baseView.mSchemePaint);
        } else {
            int left = (int) (cx - schemeRadius);
            int top = (int) (cy - schemeRadius);
            int right = (int) (cx + schemeRadius);
            int bottom = (int) (cy + schemeRadius);
            drawDrawable.setBounds(left, top, right, bottom);
            drawDrawable.draw(canvas);
        }
    }

    @Override
    public void onDrawText(BaseView baseView, Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = baseView.mTextBaseLine + y + getOffset();
        float cx = x + baseView.mItemWidth / 2f;

        boolean isInPriorityShowWeekMode = isInPriorityShowWeekMode(baseView, calendar);

        Paint paint;
        if (hasScheme || isSelected) {
            paint = baseView.mSchemeTextPaint;
            if (calendar.isCurrentMonth() || isInPriorityShowWeekMode) {
                paint.setColor(baseView.mDelegate.getSchemeTextColor());
            } else {
                paint.setColor(otherMonthSchemeTextColor);
            }
        } else {
            if (baseView.mDelegate.monthPriorityShowWeekMode) {
                if (isInPriorityShowWeekMode) {
                    paint = baseView.mCurMonthTextPaint;
                } else {
                    paint = baseView.mOtherMonthTextPaint;
                }
            } else {
                if (calendar.isCurrentMonth()) {
                    paint = baseView.mCurMonthTextPaint;
                } else {
                    paint = baseView.mOtherMonthTextPaint;
                }
            }
        }

        canvas.drawText(String.valueOf(calendar.getDay()),
                cx,
                baselineY,
                paint);

        //绘制月份提示
        if (drawMonthText) {
            if (calendar.getDrawIndex() == 0 || calendar.getDay() == 1) {
                String monthText = baseView.getResources()
                        .getStringArray(baseView.mDelegate.monthStringResId)[calendar.getMonth() - 1];
                if (calendar.isCurrentMonth()) {
                    monthTextPaint.setColor(baseView.mCurMonthTextPaint.getColor());
                } else {
                    monthTextPaint.setColor(baseView.mOtherMonthTextPaint.getColor());
                }
                canvas.drawText(monthText,
                        cx,
                        y - monthTextPaint.ascent(),
                        monthTextPaint);
            }
        }

        //绘制横线提示
        if (calendar.isCurrentDay()) {
            lineRect.set(cx - currentDayLineWidth / 2,
                    y + baseView.mItemHeight - currentDayLineHeight,
                    cx + currentDayLineWidth / 2,
                    y + baseView.mItemHeight);
            canvas.drawRoundRect(lineRect,
                    MarkProperty.dpToPx(baseView.getContext(), 2),
                    MarkProperty.dpToPx(baseView.getContext(), 2),
                    baseView.mCurDayTextPaint);
        }
    }
}
