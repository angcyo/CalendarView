package com.haibin.calendarview;

import android.graphics.Canvas;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2022/05/08
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
public interface IDrawBaseView {

    /**
     * @see MonthView#onDrawSelected(android.graphics.Canvas, com.haibin.calendarview.Calendar, int, int, boolean)
     */
    boolean onDrawSelected(BaseView baseView, Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme);

    /**
     * @see MonthView#onDrawScheme(android.graphics.Canvas, com.haibin.calendarview.Calendar, int, int)
     */
    void onDrawScheme(BaseView baseView, Canvas canvas, Calendar calendar, int x, int y);

    /**
     * @see MonthView#onDrawText(android.graphics.Canvas, com.haibin.calendarview.Calendar, int, int, boolean, boolean)
     */
    void onDrawText(BaseView baseView, Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected);
}
