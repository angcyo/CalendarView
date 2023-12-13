package com.haibin.calendarviewproject.mark;

import android.content.Context;
import android.graphics.Canvas;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/07
 */
public class MarkMonthView extends MonthView {

    MarkProperty markProperty = new MarkProperty();

    public MarkMonthView(Context context) {
        super(context);
    }

    @Override
    protected void onPreviewHook() {
        super.onPreviewHook();
        markProperty.onPreviewHook(this);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        return markProperty.onDrawSelected(this, canvas, calendar, x, y, hasScheme);
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        markProperty.onDrawScheme(this, canvas, calendar, x, y);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        markProperty.onDrawText(this, canvas, calendar, x, y, hasScheme, isSelected);
    }
}
