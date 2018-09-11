package refresh_recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.xiaoxiao.widgets.R;

/**
 * Created by Administrator on 2017/3/3.
 *
 * @author mengchuiliu
 * recycleView 分割线
 */

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {
    private Context mContext;
    private int dividerHeight;
    private int headerHeight;
    private Paint dividerPaint;
    private Paint headerPaint;
    private boolean hasHead = false;

    public SimpleItemDecoration(Context context, int dpValue) {
        mContext = context;
        dividerPaint = new Paint();
        dividerPaint.setColor(context.getResources().getColor(R.color.blank_bg));
        dividerHeight = dp2px(context, dpValue);
    }

    public void setHead(boolean hasHead, int headerHeight) {
        headerPaint = new Paint();
        headerPaint.setColor(mContext.getResources().getColor(R.color.blank_bg));
        this.hasHead = hasHead;
        this.headerHeight = dp2px(mContext, headerHeight);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos == 0 && hasHead) {
            outRect.top = headerHeight;
        }
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            if (i == 0 && hasHead) {
                float topH = view.getTop() - headerHeight;
                float bottomH = view.getTop();
                c.drawRect(left, topH, right, bottomH, headerPaint);
            }
            c.drawRect(left, top, right, bottom, dividerPaint);
        }
    }

    /**
     * dp to px转换
     *
     * @param context
     * @param dpValue
     * @return
     */
    private int dp2px(Context context, int dpValue) {
        int pxValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
        return pxValue;
    }
}
