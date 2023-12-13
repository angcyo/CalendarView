package com.haibin.calendarviewproject.mark;

import android.content.Context;
import android.graphics.Canvas;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/07
 */
public class MarkWeekView extends WeekView {

    MarkProperty markProperty = new MarkProperty();

    public MarkWeekView(Context context) {
        super(context);
    }

    @Override
    protected void onPreviewHook() {
        super.onPreviewHook();
        markProperty.onPreviewHook(this);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        return markProperty.onDrawSelected(this, canvas, calendar, x, 0, hasScheme);
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {
        markProperty.onDrawScheme(this, canvas, calendar, x, 0);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        markProperty.onDrawText(this, canvas, calendar, x, 0, hasScheme, isSelected);
    }
}
