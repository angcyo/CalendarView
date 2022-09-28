package com.haibin.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 年月title吸顶的垂直日历
 *
 * @author michaellee
 * 2022/05/11
 */
public class StickyVerticalMonthRecyclerView extends RecyclerView {

    protected CalendarViewDelegate mDelegate;
    protected int mMonthCount;
    protected CalendarLayout mParentLayout;

    public StickyVerticalMonthRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public StickyVerticalMonthRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickyVerticalMonthRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(@NonNull Context context) {
        setLayoutManager(new LinearLayoutManager(context));

        if (isInEditMode()) {
            setup(new CalendarViewDelegate(context, null));
        }
    }

    GroupVerticalMonthAdapter adapter;

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
        if (adapter == null) {
            adapter = new GroupVerticalMonthAdapter(getContext());
        }
        setAdapter(adapter);
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
        if (adapter != null) {
            adapter.notifyItemRangeChanged(0, adapter.getGroupCount());
        }
    }

    class MonthHolder {
        BaseViewHolder viewHolder;
        BaseMonthView monthView;

        public MonthHolder(BaseViewHolder holder) {
            viewHolder = holder;
            if (holder.itemView.getTag(R.id.tag_group_month_view) != null) {
                monthView = (BaseMonthView) holder.itemView.getTag(R.id.tag_group_month_view);
            }
        }
    }

    /**
     * 更新选中的日期效果
     */
    void updateSelected() {
        for (MonthHolder viewHolder : allViewHolder()) {
            viewHolder.monthView.setSelectedCalendar(mDelegate.mSelectedCalendar);
            viewHolder.monthView.invalidate();
        }
    }

    /**
     * 获取界面上的ViewHolder
     */
    MonthHolder getViewHolder(int childIndex) {
        if (childIndex >= 0 && childIndex < adapter.getGroupCount()) {
            View child = getChildAt(childIndex * 2 + 1);
            ViewHolder viewHolder = getChildViewHolder(child);
            return new MonthHolder((BaseViewHolder) viewHolder);
        }
        return null;
    }

    /**
     * 界面上所有的ViewHolder
     */
    List<MonthHolder> allViewHolder() {
        ArrayList<MonthHolder> result = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ViewHolder holder = getChildViewHolder(child);
            MonthHolder monthHolder = new MonthHolder((BaseViewHolder) holder);
            if (monthHolder.monthView != null) {
                result.add(monthHolder);
            }
        }
        return result;
    }

    public void setCurrentItem(int position) {
        setCurrentItem(position, true);
    }

    public void setCurrentItem(int pos, final boolean smoothScroll) {
        final int position = pos * 2;
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        if (smoothScroll) {
            ViewHolder holder = findViewHolderForAdapterPosition(position);
            if (holder == null) {
                smoothScrollToPosition(position);
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
        updateSelected();
    }

    public void scrollToNext(boolean smoothScroll) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        int first = linearLayoutManager.findFirstVisibleItemPosition();
        int groupPosition = adapter.getGroupPositionForPosition(first);
        setCurrentItem(groupPosition + 1, smoothScroll);
    }

    public void scrollToPre(boolean smoothScroll) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        int first = linearLayoutManager.findFirstVisibleItemPosition();
        int groupPosition = adapter.getGroupPositionForPosition(first);
        setCurrentItem(groupPosition - ((first & 1) == 1 ? 0 : 1), smoothScroll);
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
        calendar.setCurrentDay(calendar.equals(mDelegate.getCurrentDay()));
        LunarCalendar.setupLunarCalendar(calendar);
        mDelegate.mIndexCalendar = calendar;
        mDelegate.mSelectedCalendar = calendar;
        mDelegate.updateSelectCalendarScheme();
        int y = calendar.getYear() - mDelegate.getMinYear();
        int position = 12 * y + calendar.getMonth() - mDelegate.getMinYearMonth();
        setCurrentItem(position, smoothScroll);
    }

    /**
     * 更新月视图Class
     */
    void updateMonthViewClass() {
        setup(mDelegate);
        setCurrentItem(mDelegate.mCurrentMonthViewItem, false);
    }

    protected class GroupVerticalMonthAdapter extends GroupedRecyclerViewAdapter {

        public GroupVerticalMonthAdapter(Context context) {
            super(context);
        }

        @Override
        public int getGroupCount() {
            return mMonthCount;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public boolean hasHeader(int groupPosition) {
            return true;
        }

        @Override
        public boolean hasFooter(int groupPosition) {
            return false;
        }

        @Override
        public int getHeaderLayout(int viewType) {
            return R.layout.cv_layout_vertical_month_sticky_title;
        }

        @Override
        public int getFooterLayout(int viewType) {
            return 0;
        }

        @Override
        public int getChildLayout(int viewType) {
            return R.layout.cv_layout_vertical_month_sticky_view;
        }

        @Override
        public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {

            int year = (groupPosition + mDelegate.getMinYearMonth() - 1) / 12 + mDelegate.getMinYear();
            int month = (groupPosition + mDelegate.getMinYearMonth() - 1) % 12 + 1;

            TextView currentMonthView = holder.itemView.findViewById(R.id.current_month_view);
            if (currentMonthView != null) {
                currentMonthView.setText(year + "年" + month + "月");
            }
        }

        @Override
        public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

        }

        @Override
        public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {

            int year = (groupPosition + mDelegate.getMinYearMonth() - 1) / 12 + mDelegate.getMinYear();
            int month = (groupPosition + mDelegate.getMinYearMonth() - 1) % 12 + 1;
            BaseMonthView monthView;
            if (holder.itemView.getTag(R.id.tag_group_month_view) == null) {
                try {
                    Constructor constructor = mDelegate.getMonthViewClass().getConstructor(Context.class);
                    monthView = (BaseMonthView) constructor.newInstance(getContext());

                    ViewGroup monthContainer = holder.itemView.findViewById(R.id.month_container);
                    monthContainer.addView(monthView);
                } catch (Exception e) {
                    e.printStackTrace();
                    monthView = new DefaultMonthView(getContext());
                }

                CalendarView.OnClassInitializeListener listener = mDelegate.mClassInitializeListener;
                if (listener != null) {
                    listener.onClassInitialize(mDelegate.getMonthViewClass(), monthView);
                }
                holder.itemView.setTag(R.id.tag_group_month_view, monthView);
            } else {
                monthView = (BaseMonthView) holder.itemView.getTag(R.id.tag_group_month_view);
            }
            monthView.mParentLayout = mParentLayout;
            monthView.setup(mDelegate);
            monthView.setTag(groupPosition);
            monthView.initMonthWithDate(year, month);
            monthView.setSelectedCalendar(mDelegate.mSelectedCalendar);

            CalendarView.OnStickyVerticalItemInitializeListener stickyVerticalItemInitializeListener = mDelegate.mStickyVerticalItemInitializeListener;
            if (stickyVerticalItemInitializeListener != null) {
                stickyVerticalItemInitializeListener.onVerticalItemInitialize(holder, groupPosition, year, month);
            }
        }
    }

}
