package refresh_recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoxiao.widgets.R;


/**
 * Created by Chuiliu Meng on 2016/8/11.
 *
 * @author Chuiliu Meng
 */
public class FooterView extends LinearLayout {
    private ProgressBar mPb;
    private TextView mTv;

    public FooterView(Context context) {
        super(context);
        init(context);
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.footer_view, null);
        LayoutParams listParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(listParams);
        addView(view);

        mPb = (ProgressBar) view.findViewById(R.id.pb);
        mTv = (TextView) view.findViewById(R.id.text);
    }


    public void onNoData() {
        mPb.setVisibility(View.GONE);
        mTv.setText("没有更多数据");
    }

    public void onRefreshing() {
        mPb.setVisibility(View.VISIBLE);
        mTv.setText("正在加载...");
    }
}
