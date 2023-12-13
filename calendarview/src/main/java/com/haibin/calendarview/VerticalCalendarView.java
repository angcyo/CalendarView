package com.haibin.calendarview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * 垂直列表排列的日历
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2021/10/21
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
public class VerticalCalendarView extends CalendarView {

    public VerticalMonthRecyclerView monthRecyclerView;

    public VerticalCalendarView(@NonNull Context context) {
        super(context);
    }

    public VerticalCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context, @Nullable AttributeSet attrs) {
        //mDelegate = new VerticalCalendarViewDelegate(context, attrs);
        //super.init(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalCalendarView);
        int verticalMonthItemLayoutId = array.getResourceId(R.styleable.VerticalCalendarView_vertical_month_item_layout_id, R.layout.cv_layout_vertical_month_view);
        array.recycle();

        calendarLayoutId = R.layout.cv_layout_vertical_calendar_view;
        //填充布局
        LayoutInflater.from(context).inflate(calendarLayoutId, this, true);

        //周视图
        FrameLayout frameContent = findViewById(R.id.frameContent);
        this.mWeekPager = findViewById(R.id.vp_week);
        this.mWeekPager.setup(mDelegate);
        try {
            Constructor constructor = mDelegate.getWeekBarClass().getConstructor(Context.class);
            mWeekBar = (WeekBar) constructor.newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frameContent.addView(mWeekBar, 2);
        mWeekBar.setup(mDelegate);
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());

        CalendarView.OnClassInitializeListener listener = mDelegate.mClassInitializeListener;
        if (listener != null) {
            listener.onClassInitialize(mDelegate.getWeekBarClass(), mWeekBar);
        }

        //横线
        this.mWeekLine = findViewById(R.id.line);
        this.mWeekLine.setBackgroundColor(mDelegate.getWeekLineBackground());
        LayoutParams lineParams = (LayoutParams) this.mWeekLine.getLayoutParams();
        lineParams.setMargins(mDelegate.getWeekLineMargin(),
                mDelegate.getWeekBarHeight(),
                mDelegate.getWeekLineMargin(),
                0);
        this.mWeekLine.setLayoutParams(lineParams);

        //月视图
        mMonthPager = new MonthViewPager(context); //提供一个占位用的视图, 防止库NPE异常
        this.monthRecyclerView = findViewById(R.id.rv_month);
        monthRecyclerView.verticalMonthItemLayoutId = verticalMonthItemLayoutId;
        //this.mMonthPager.mWeekPager = mWeekPager;
        //this.mMonthPager.mWeekBar = mWeekBar;
        //mMonthPager.setup(mDelegate);
        LayoutParams params = (LayoutParams) this.monthRecyclerView.getLayoutParams();
        params.setMargins(0, mDelegate.getWeekBarHeight() + CalendarUtil.dipToPx(context, 1), 0, 0);
        mWeekPager.setLayoutParams(params);

        //年视图
        mYearViewPager = findViewById(R.id.selectLayout);
        mYearViewPager.setPadding(mDelegate.getYearViewPaddingLeft(), 0, mDelegate.getYearViewPaddingRight(), 0);
        mYearViewPager.setBackgroundColor(mDelegate.getYearViewBackground());
        mYearViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mWeekPager.getVisibility() == VISIBLE) {
                    return;
                }
                if (mDelegate.mYearChangeListener != null) {
                    mDelegate.mYearChangeListener.onYearChange(position + mDelegate.getMinYear());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDelegate.mInnerListener = new OnInnerDateSelectedListener() {
            /**
             * 月视图选择事件
             * @param calendar calendar
             * @param isClick  是否是点击
             */
            @Override
            public void onMonthDateSelected(Calendar calendar, boolean isClick) {

                /*if (calendar.getYear() == mDelegate.getCurrentDay().getYear() &&
                        calendar.getMonth() == mDelegate.getCurrentDay().getMonth()
                    *//*&& mMonthPager.getCurrentItem() != mDelegate.mCurrentMonthViewItem*//*) {
                    return;
                }*/
                mDelegate.mIndexCalendar = calendar;
                if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick) {
                    mDelegate.mSelectedCalendar = calendar;
                }
                mWeekPager.updateSelected(mDelegate.mIndexCalendar, false);
                //mMonthPager.updateSelected();
                monthRecyclerView.updateSelected();
                if (mWeekBar != null &&
                        (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick)) {
                    mWeekBar.onDateSelected(calendar, mDelegate.getWeekStart(), isClick);
                }
            }

            /**
             * 周视图选择事件
             * @param calendar calendar
             * @param isClick 是否是点击
             */
            @Override
            public void onWeekDateSelected(Calendar calendar, boolean isClick) {
                mDelegate.mIndexCalendar = calendar;
                if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick
                        || mDelegate.mIndexCalendar.equals(mDelegate.mSelectedCalendar)) {
                    mDelegate.mSelectedCalendar = calendar;
                }
                int y = calendar.getYear() - mDelegate.getMinYear();
                int position = 12 * y + mDelegate.mIndexCalendar.getMonth() - mDelegate.getMinYearMonth();
                mWeekPager.updateSingleSelect();
                //mMonthPager.setCurrentItem(position, false);
                //mMonthPager.updateSelected();
                monthRecyclerView.updateSelected();
                if (mWeekBar != null &&
                        (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT
                                || isClick
                                || mDelegate.mIndexCalendar.equals(mDelegate.mSelectedCalendar))) {
                    mWeekBar.onDateSelected(calendar, mDelegate.getWeekStart(), isClick);
                }
            }
        };

        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            if (isInRange(mDelegate.getCurrentDay())) {
                mDelegate.mSelectedCalendar = mDelegate.createCurrentDate();
            } else {
                mDelegate.mSelectedCalendar = mDelegate.getMinRangeCalendar();
            }
        } else {
            mDelegate.mSelectedCalendar = new Calendar();
        }

        mDelegate.mIndexCalendar = mDelegate.mSelectedCalendar;

        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);

        monthRecyclerView.setup(mDelegate);
        //mMonthPager.setCurrentItem(mDelegate.mCurrentMonthViewItem);
        monthRecyclerView.setCurrentItem(mDelegate.mCurrentMonthViewItem, false);
        mYearViewPager.setOnMonthSelectedListener(new YearRecyclerView.OnMonthSelectedListener() {
            @Override
            public void onMonthSelected(int year, int month) {
                int position = 12 * (year - mDelegate.getMinYear()) + month - mDelegate.getMinYearMonth();
                closeSelectLayout(position);
                mDelegate.isShowYearSelectedLayout = false;
            }
        });
        mYearViewPager.setup(mDelegate);
        mWeekPager.updateSelected(mDelegate.createCurrentDate(), false);

    }

    @Override
    public void setSchemeDate(Map<String, Calendar> mSchemeDates) {
        super.setSchemeDate(mSchemeDates);
        monthRecyclerView.update();
    }

    @Override
    public void clearSchemeDate() {
        super.clearSchemeDate();
        monthRecyclerView.update();
    }

    @Override
    public void setRange(int minYear, int minYearMonth, int minYearDay, int maxYear, int maxYearMonth, int maxYearDay) {
        super.setRange(minYear, minYearMonth, minYearDay, maxYear, maxYearMonth, maxYearDay);
        monthRecyclerView.updateRange();
    }

    @Override
    protected void showSelectLayout(int year) {
        super.showSelectLayout(year);

        monthRecyclerView.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(260)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (mDelegate.mYearViewChangeListener != null) {
                            mDelegate.mYearViewChangeListener.onYearViewChange(false);
                        }
                    }
                });
    }

    @Override
    protected void closeSelectLayout(int position) {
        super.closeSelectLayout(position);

        monthRecyclerView.setCurrentItem(position, false);

        monthRecyclerView.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(180)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (mDelegate.mYearViewChangeListener != null) {
                            mDelegate.mYearViewChangeListener.onYearViewChange(true);
                        }
                        if (mParentLayout != null) {
                            mParentLayout.showContentView();
                            if (mParentLayout.isExpand()) {
                                mMonthPager.setVisibility(VISIBLE);
                            } else {
                                mWeekPager.setVisibility(VISIBLE);
                                mParentLayout.shrink();
                            }
                        } else {
                            mMonthPager.setVisibility(VISIBLE);
                        }
                        mMonthPager.clearAnimation();
                    }
                });
    }

    @Override
    public void scrollToCurrent(boolean smoothScroll) {
        super.scrollToCurrent(smoothScroll);
        monthRecyclerView.scrollToCurrent(smoothScroll);
    }

    @Override
    public void scrollToNext(boolean smoothScroll) {
        super.scrollToNext(smoothScroll);
        monthRecyclerView.scrollToNext(smoothScroll);
    }

    @Override
    public void scrollToPre(boolean smoothScroll) {
        super.scrollToPre(smoothScroll);
        monthRecyclerView.scrollToPre(smoothScroll);
    }

    @Override
    public void scrollToCalendar(int year, int month, int day, boolean smoothScroll, boolean invokeListener) {
        super.scrollToCalendar(year, month, day, smoothScroll, invokeListener);
        monthRecyclerView.scrollToCalendar(year, month, day, smoothScroll, invokeListener);
    }

    @Override
    public void setMonthView(Class<?> cls) {
        super.setMonthView(cls);
        monthRecyclerView.updateMonthViewClass();
    }
}
