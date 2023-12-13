package com.haibin.calendarviewproject.mark;

import android.content.Context;
import android.content.Intent;

import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.simple.SimpleActivity;

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/07
 */
public class SimpleMarkActivity extends SimpleActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, SimpleMarkActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_mark;
    }

}
