package com.haibin.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2021/10/21
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
public class VerticalMonthRecyclerView extends RecyclerView {

    protected CalendarViewDelegate mDelegate;
    protected int mMonthCount;
    protected CalendarLayout mParentLayout;

    /**
     * RecyclerView item的布局id
     */
    protected int verticalMonthItemLayoutId = R.layout.cv_layout_vertical_month_view;

    public VerticalMonthRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VerticalMonthRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerticalMonthRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(@NonNull Context context) {
        setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));

        if (isInEditMode()) {
            setup(new CalendarViewDelegate(context, null));
        }
    }

    /**
     * 初始化
     *
     * @param delegate delegate
     */
    void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;

        mMonthCount = 12 * (mDelegate.getMaxYear() - mDelegate.getMinYear())
                - mDelegate.getMinYearMonth() + 1 +
                mDelegate.getMaxYearMonth();

        setAdapter(new VerticalMonthAdapter());
    }

    void updateRange() {
        if (getVisibility() != VISIBLE) {
            return;
        }
        if (mDelegate != null) {
            setup(mDelegate);
        }
        updateSelected();
    }

    void update() {
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        }
    }

    /**
     * 更新选中的日期效果
     */
    void updateSelected() {
        for (VerticalMonthViewHolder viewHolder : allViewHolder()) {
            viewHolder.monthView.setSelectedCalendar(mDelegate.mSelectedCalendar);
            viewHolder.monthView.invalidate();
        }
    }

    /**
     * 获取界面上的ViewHolder
     */
    VerticalMonthViewHolder getViewHolder(int childIndex) {
        if (childIndex >= 0 && childIndex < getChildCount()) {
            View child = getChildAt(childIndex);
            ViewHolder viewHolder = getChildViewHolder(child);
            if (viewHolder instanceof VerticalMonthViewHolder) {
                return (VerticalMonthViewHolder) viewHolder;
            }
        }
        return null;
    }

    /**
     * 界面上所有的ViewHolder
     */
    List<VerticalMonthViewHolder> allViewHolder() {
        ArrayList<VerticalMonthViewHolder> result = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ViewHolder viewHolder = getChildViewHolder(child);
            if (viewHolder instanceof VerticalMonthViewHolder) {
                result.add((VerticalMonthViewHolder) viewHolder);
            }
        }
        return result;
    }

    public void setCurrentItem(int position) {
        setCurrentItem(position, true);
    }

    public void setCurrentItem(final int position, final boolean smoothScroll) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        if (smoothScroll) {
            ViewHolder holder = findViewHolderForAdapterPosition(position);
            if (holder == null) {
                smoothScrollToPosition(position);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewHolder holder = findViewHolderForAdapterPosition(position);
                        setCurrentItem(position, holder == null);
                    }
                }, 240);
            } else {
                int dy = layoutManager.getDecoratedTop(holder.itemView) - getPaddingTop();
                smoothScrollBy(0, dy);
            }
        } else {
            if (layoutManager instanceof LinearLayoutManager) {
                ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
            } else {
                scrollToPosition(position);
            }
        }
    }

    public void scrollToNext(boolean smoothScroll) {
        VerticalMonthViewHolder viewHolder = getViewHolder(0);
        if (viewHolder != null) {
            setCurrentItem(viewHolder.getBindingAdapterPosition() + 1, smoothScroll);
        }
    }

    public void scrollToPre(boolean smoothScroll) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        VerticalMonthViewHolder viewHolder = getViewHolder(0);
        if (viewHolder != null) {
            int top = layoutManager.getDecoratedTop(viewHolder.itemView);
            int position = viewHolder.getBindingAdapterPosition();
            if (top == 0) {
                position -= 1;
            }
            setCurrentItem(position, smoothScroll);
        }
    }

    public void scrollToCurrent(boolean smoothScroll) {
        int position = 12 * (mDelegate.getCurrentDay().getYear() - mDelegate.getMinYear()) +
                mDelegate.getCurrentDay().getMonth() - mDelegate.getMinYearMonth();
        setCurrentItem(position, smoothScroll);
    }

    public void scrollToCalendar(int year, int month, int day, boolean smoothScroll, boolean invokeListener) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        if (!calendar.isAvailable()) {
            return;
        }
        if (!isInRange(calendar)) {
            return;
        }
        if (mDelegate.mCalendarInterceptListener != null &&
                mDelegate.mCalendarInterceptListener.onCalendarIntercept(calendar)) {
            mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, false);
            return;
        }
        calendar.setCurrentDay(calendar.equals(mDelegate.getCurrentDay()));
        LunarCalendar.setupLunarCalendar(calendar);
        mDelegate.mIndexCalendar = calendar;
        mDelegate.mSelectedCalendar = calendar;
        mDelegate.updateSelectCalendarScheme();
        int y = calendar.getYear() - mDelegate.getMinYear();
        int position = 12 * y + calendar.getMonth() - mDelegate.getMinYearMonth();
        setCurrentItem(position, smoothScroll);
        updateSelected();
    }

    /**
     * 是否在日期范围内
     *
     * @param calendar calendar
     * @return 是否在日期范围内
     */
    protected final boolean isInRange(Calendar calendar) {
        return mDelegate != null && CalendarUtil.isCalendarInRange(calendar, mDelegate);
    }
    
    /**
     * 更新月视图Class
     */
    void updateMonthViewClass() {
        setup(mDelegate);
        setCurrentItem(mDelegate.mCurrentMonthViewItem, false);
    }

    public static class VerticalMonthViewHolder extends RecyclerView.ViewHolder {

        public BaseMonthView monthView;

        public VerticalMonthViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected class VerticalMonthAdapter extends RecyclerView.Adapter<VerticalMonthViewHolder> {

        @NonNull
        @Override
        public VerticalMonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new VerticalMonthViewHolder(inflater.inflate(verticalMonthItemLayoutId, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VerticalMonthViewHolder viewHolder, int position) {

            int year = (position + mDelegate.getMinYearMonth() - 1) / 12 + mDelegate.getMinYear();
            int month = (position + mDelegate.getMinYearMonth() - 1) % 12 + 1;

            if (viewHolder.monthView == null) {
                try {
                    Constructor constructor = mDelegate.getMonthViewClass().getConstructor(Context.class);
                    viewHolder.monthView = (BaseMonthView) constructor.newInstance(getContext());

                    ViewGroup monthContainer = viewHolder.itemView.findViewById(R.id.month_container);
                    monthContainer.addView(viewHolder.monthView);

                } catch (Exception e) {
                    e.printStackTrace();
                    viewHolder.monthView = new DefaultMonthView(getContext());
                }

                CalendarView.OnClassInitializeListener listener = mDelegate.mClassInitializeListener;
                if (listener != null) {
                    listener.onClassInitialize(mDelegate.getMonthViewClass(), viewHolder.monthView);
                }
            }

            //viewHolder.monthView.mMonthViewPager = MonthViewPager.this;
            viewHolder.monthView.mParentLayout = mParentLayout;
            viewHolder.monthView.setup(mDelegate);
            viewHolder.monthView.setTag(position);
            viewHolder.monthView.initMonthWithDate(year, month);
            viewHolder.monthView.setSelectedCalendar(mDelegate.mSelectedCalendar);

            TextView currentMonthView = viewHolder.itemView.findViewById(R.id.current_month_view);
            if (currentMonthView != null) {
                currentMonthView.setText(year + "年" + month + "月");
            }

            CalendarView.OnVerticalItemInitializeListener verticalItemInitializeListener = mDelegate.mVerticalItemInitializeListener;
            if (verticalItemInitializeListener != null) {
                verticalItemInitializeListener.onVerticalItemInitialize(viewHolder, position, year, month);
            }
        }

        @Override
        public int getItemCount() {
            return mMonthCount;
        }
    }

}
