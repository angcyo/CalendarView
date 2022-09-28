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
     * @see MonthView#onDrawSelected(Canvas, Calendar, float, float, boolean)
     */
    boolean onDrawSelected(BaseView baseView, Canvas canvas, Calendar calendar, float x, float y, boolean hasScheme);

    /**
     * @see MonthView#onDrawScheme(Canvas, Calendar, float, float)
     */
    void onDrawScheme(BaseView baseView, Canvas canvas, Calendar calendar, float x, float y);

    /**
     * @see MonthView#onDrawText(Canvas, Calendar, float, float, boolean, boolean)
     */
    void onDrawText(BaseView baseView, Canvas canvas, Calendar calendar, float x, float y, boolean hasScheme, boolean isSelected);
}
