package refresh_recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoxiao.widgets.R;

/**
 * Created by Chuiliu Meng on 2016/8/11.
 *
 * @author Chuiliu Meng
 */
public class HeaderView extends FrameLayout {
    private TextView mTipTv;
    private LoadingView mImageView;

    public HeaderView(Context context) {
        super(context);
        init(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void onPullToRefresh(int pullheight) {
        mImageView.updateView((float) pullheight / getHeight());
        mTipTv.setText(R.string.pull_refreshing);
        mImageView.rotateAnimation(false);
    }

    public void onReleaseToRefresh(int pullheight) {
        mImageView.updateView((float) pullheight / getHeight());
        mTipTv.setText(R.string.loosen_refreshing);
        mImageView.rotateAnimation(false);
    }

    public void onRefreshing() {
        mTipTv.setText(R.string.refreshing);
        mImageView.updateView(1);
        mImageView.rotateAnimation(true);
    }

    public void onNormal() {
        mTipTv.setText(R.string.pull_refreshing);
        mImageView.rotateAnimation(false);
    }

    public void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.header_view, null);
        mTipTv = (TextView) view.findViewById(R.id.text);
        mImageView = (LoadingView) view.findViewById(R.id.imageView);
        addView(view);
    }
}
