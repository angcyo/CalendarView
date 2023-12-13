
# CalenderView

基于[CalenderView](https://github.com/huanghaibin-dev/CalendarView)`3.7.1`的版本修改, 实现了如下功能:

-  `垂直列表日历`: 基于`RecyclerView`实现

> 月份的排列方式是上下结构, `上一个月` `当前月` `下一个月`在垂直方向排列

- `垂直滚动日历`: 通过交换`ViewPager`的`TouchEvent`实现

> 可以通过`下滑` `上滑`的方式, 切换`上一个月` `下一个月`

- `touchDown`时的效果提示支持, 需要在`onDraw`方法中自定义实现

> 通过此标识, 可以实现`手指按下时`缩放背景的效果

- `周/月视图选择日历的动画`在月/周视图中, 切换不同日的日历时的动画支持

> 日期切换时, 有动画效果

## 效果图

垂直滚动 | 垂直列表
 ---    | ---
![](https://raw.githubusercontent.com/angcyo/CalendarView/master/png/gif_vertical_scroll.gif)|![](https://raw.githubusercontent.com/angcyo/CalendarView/master/png/gif_vertical_list.gif)

月视图动画 | 周视图动画
 ---      | ---
![](https://raw.githubusercontent.com/angcyo/CalendarView/master/png/gif_month_anim.gif)|![](https://raw.githubusercontent.com/angcyo/CalendarView/master/png/gif_week_anim.gif)

按下效果 | 其它月份预览
 ---    | ---
![](https://raw.githubusercontent.com/angcyo/CalendarView/master/png/gif_touch_down.gif) | ![](https://raw.githubusercontent.com/angcyo/CalendarView/master/png/mark_month_view.png)

# 使用方式如下:

## 加入仓库地址

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

## 加入依赖

```
implementation 'com.github.angcyo:CalendarView:3.7.1.37'
```

### `垂直滚动日历`使用方式

```
mCalendarView.getMonthViewPager().setOrientation(LinearLayout.VERTICAL);
```

### `垂直列表日历`使用方式

使用`VerticalCalendarView`控件即可.

```
com.haibin.calendarview.VerticalCalendarView
```

### `按下效果`实现参考代码

自定义`MonthView`控件, 实现如下方法:

```
@Override
protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
    int cx = x + mItemWidth / 2;
    int cy = y + mItemHeight / 2;
    if (isTouchDown && mCurrentItem == mItems.indexOf(getIndex())) {
        //点击当前选中的item, 缩放效果提示
        canvas.drawCircle(cx, cy, mRadius - dipToPx(getContext(), 4), mSelectedPaint);
    } else {
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
    }
    return true;
}
```

## 开源地址

感谢作者的开源库!

https://github.com/angcyo/CalendarView

---

## 周视图直接滚动到`上一个月`或`下一个月`

### `上一个月`

```
int currentItem = mWeekPager.getCurrentItem();
BaseView baseView = mWeekPager.findViewWithTag(currentItem);
Calendar firstCalendar = baseView.mItems.get(0);
int days = CalendarUtil.getMonthDaysCount(firstCalendar.getYear(), firstCalendar.getMonth());
Calendar targetCalendar = CalendarUtil.getCalendarWidthDiffer(firstCalendar, - days * ONE_DAY);
mWeekPager.scrollToCalendar(targetCalendar.getYear(), targetCalendar.getMonth(), targetCalendar.getDay(), smoothScroll, false);

```

### `下一个月`

```
int currentItem = mWeekPager.getCurrentItem();
BaseView baseView = mWeekPager.findViewWithTag(currentItem);
Calendar firstCalendar = baseView.mItems.get(0);
int days = CalendarUtil.getMonthDaysCount(firstCalendar.getYear(), firstCalendar.getMonth());
Calendar targetCalendar = CalendarUtil.getCalendarWidthDiffer(firstCalendar, days * ONE_DAY);
mWeekPager.scrollToCalendar(targetCalendar.getYear(), targetCalendar.getMonth(), targetCalendar.getDay(), smoothScroll, false);
```

**工具类**

```
/**
 * 获取指定相差天数的日历
 * [millis] 相差的毫秒数
 * */
public static Calendar getCalendarWidthDiffer(Calendar calendar, long millis) {
    java.util.Calendar date = java.util.Calendar.getInstance();

    date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay(), 12, 0, 0);//

    long timeMills = date.getTimeInMillis();//获得起始时间戳

    date.setTimeInMillis(timeMills + millis);

    Calendar preCalendar = new Calendar();
    preCalendar.setYear(date.get(java.util.Calendar.YEAR));
    preCalendar.setMonth(date.get(java.util.Calendar.MONTH) + 1);
    preCalendar.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));

    return preCalendar;
}
```

---

# CalenderView

An elegant CalendarView on Android platform.
Freely draw UI with canvas, fast、efficient and low memory.
Support month view、 week view、year view、 custom week start、lunar calendar and so on.
Hot plug UI customization!
You can't think of the calendar can be so elegant!


# 温馨提醒 Warm tips

Github代码全部开源无限制使用，免费开源最终版本为3.7.1，垂直、水平切换日历、高仿iOS日历等源码不再开源。

The final version of the free and open source part is 3.7.1, the vertical and horizontal switching calendar liked iOS calendar are no longer open source.

<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/screen_recorder.gif" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/screen_recorder_main.gif" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/screen_recorder_flip.gif" height="650"/>

<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/simple.jpg" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/range_select.jpg" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/full_calendar.jpg" height="650"/>

### AndroidStudio v3.5+

### support version if using support package
```
implementation 'com.haibin:calendarview:3.6.8'
```

### Androidx version if using Androidx
```
implementation 'com.haibin:calendarview:3.7.1'
```

```
<dependency>
  <groupId>com.haibin</groupId>
  <artifactId>calendarview</artifactId>
  <version>3.7.0</version>
  <type>pom</type>
</dependency>
```

## How to use?

[**English Doc**](https://github.com/huanghaibin-dev/CalendarView/blob/master/QUESTION.md)

[**中文使用文档**](https://github.com/huanghaibin-dev/CalendarView/blob/master/QUESTION_ZH.md)

### proguard-rules
```java
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
```

### or using this proguard-rules
``` java
-keep class your project path.MonthView {
    public <init>(android.content.Context);
}
-keep class your project path.WeekBar {
    public <init>(android.content.Context);
}
-keep class your project path.WeekView {
    public <init>(android.content.Context);
}
-keep class your project path.YearView {
    public <init>(android.content.Context);
}
```

### Effect Preview

### func
<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/main_zh_func.png" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/main_zh_list.png" height="650"/>
### YearView and Range Style
<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/year_view.png" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/range.png" height="650"/>
### Beautiful Chinese style
<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/custom_expand.png" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/custom_shrink.png" height="650"/>
### Meizu mobile phone calendar
<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/meizu_expand.png" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/meizu_shrink.png" height="650"/>
### Colorful and Full style
<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/full_calendar.png" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/color_expand.png" height="650"/>
### Progress bar style
<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/progress_expand.png" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/progress_shrink.png" height="650"/>
### Galaxy style
<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/solar_expand.png" height="650"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/huanghaibin-dev/CalendarView/blob/master/app/src/main/assets/solar_shrink.png" height="650"/>



## Licenses
- Copyright (C) 2013 huanghaibin_dev <huanghaibin_dev@163.com>
 
- Licensed under the Apache License, Version 2.0 (the "License");
- you may not use this file except in compliance with the License.
- You may obtain a copy of the License at
 
-         http://www.apache.org/licenses/LICENSE-2.0
 
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
  limitations under the License.
