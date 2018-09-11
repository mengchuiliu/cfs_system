package com.xiaoxiao.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017/6/27.
 * 解决scrollview和edittext滑动冲突
 */

public class EditLinearView extends LinearLayout {
    private ScrollView parentScrollview;
    private RecyclerView recyclerView;
    private EditText editText;
    private int showLineMax = 0;

    public void setParentScrollview(ScrollView parentScrollview) {
        this.parentScrollview = parentScrollview;
    }

    public void setParentScrollview(RecyclerView parentRecyclerView) {
        this.recyclerView = parentRecyclerView;
    }

    public void setEditeText(EditText editText) {
        this.editText = editText;
        EditLinearView.LayoutParams lp = (EditLinearView.LayoutParams) editText.getLayoutParams();
        showLineMax = lp.height / editText.getLineHeight();
    }

    public EditLinearView(Context context) {
        super(context);
    }

    public EditLinearView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (parentScrollview == null) {
            return super.onInterceptTouchEvent(ev);
        } else {
            if (ev.getAction() == MotionEvent.ACTION_DOWN && editText.getLineCount() >= showLineMax) {
                // 将父scrollview的滚动事件拦截
                setParentScrollAble(false);
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                // 把滚动事件恢复给父Scrollview
                setParentScrollAble(true);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 是否把滚动事件交给父scrollview
     *
     * @param flag
     */
    private void setParentScrollAble(boolean flag) {
        if (parentScrollview != null) {
            parentScrollview.requestDisallowInterceptTouchEvent(!flag);
        }
        if (recyclerView != null) {
            recyclerView.requestDisallowInterceptTouchEvent(!flag);
        }
    }
}
