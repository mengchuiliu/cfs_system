package com.xiaoxiao.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Chuiliu Meng on 2016/8/9.
 *
 * @author Chuiliu Meng
 */
public class HomeViewPager extends ViewPager {

    private boolean isCanScroll = true;

    public HomeViewPager(Context context) {
        super(context);
    }

    public HomeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    //触摸没有反应
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isCanScroll && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isCanScroll && super.onInterceptTouchEvent(event);
    }
}
