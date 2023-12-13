package com.haibin.calendarviewproject.mark;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.haibin.calendarview.BaseMonthView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarUtil;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarviewproject.R;

import java.util.List;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2022/05/08
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
public class SimpleMarkActivity2 extends AppCompatActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, SimpleMarkActivity2.class));
    }

    TextView mTextMonthDay;
    TextView mTextCurrentDay;
    CalendarView mCalendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_simple_mark2);

        //
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mCalendarView = findViewById(R.id.calendarView);

        //
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        List<Calendar> dayList = CalendarUtil.getBeforeCalendarList(CalendarUtil.MONTH_PRIORITY_DAY);
        StringBuilder builder = new StringBuilder();
        builder.append(CalendarUtil.MONTH_PRIORITY_DAY + "天训练");
        builder.append(" ");
        Calendar first = dayList.get(0);
        Calendar last = dayList.get(dayList.size() - 1);
        builder.append(last.getMonth() + "月" + last.getDay() + "日");
        builder.append("-");
        builder.append(first.getMonth() + "月" + first.getDay() + "日");
        mTextMonthDay.setText(builder);

        List<Calendar> schemeList = CalendarUtil.getBeforeCalendarList(CalendarUtil.MONTH_PRIORITY_DAY + 7);
        mCalendarView.addSchemeDate(schemeList.get(1));
        mCalendarView.addSchemeDate(schemeList.get(5));
        mCalendarView.addSchemeDate(schemeList.get(8));
        mCalendarView.addSchemeDate(schemeList.get(15));
        mCalendarView.addSchemeDate(schemeList.get(schemeList.size() - 1));
        mCalendarView.addSchemeDate(schemeList.get(schemeList.size() - 3));
        mCalendarView.addSchemeDate(schemeList.get(schemeList.size() - 5));
        mCalendarView.addSchemeDate(schemeList.get(schemeList.size() - 7));

        Calendar calendar = new Calendar();
        calendar.setYear(2022);
        calendar.setMonth(5);
        calendar.setDay(5);
        mCalendarView.addSchemeDate(calendar);

        mCalendarView.post(new Runnable() {
            @Override
            public void run() {
                BaseMonthView monthView = mCalendarView.getMonthViewPager().findViewWithTag(mCalendarView.getMonthViewPager().getCurrentItem());
                List<Calendar> list = mCalendarView.getCurrentMonthCalendars();
                monthView.getMeasuredHeight();
            }
        });

        //
        findViewById(R.id.fl_current).setOnClickListener(v -> mCalendarView.scrollToCurrent());

        mCalendarView.setOnCalendarInterceptListener(new CalendarView.OnCalendarInterceptListener() {
            @Override
            public boolean onCalendarIntercept(Calendar calendar) {
                //拦截选中当前日期
                return true;
            }

            @Override
            public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
                //拦截后的日期回调
            }
        });
        mCalendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {
                //no
            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                //选中日期的回调, 如果被拦截不会触发
                Log.i("angcyo", "onCalendarSelect " + calendar + " " + isClick);
                if (isClick) {
                    String text = calendar.getMonth() + "月" + calendar.getDay() + "日";
                    Toast.makeText(SimpleMarkActivity2.this, text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
