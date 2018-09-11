package rvadapter;

import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 * 一般通用的adapter
 */
public abstract class CommonAdapter<T> extends MultiItemAdapter<T> {

    public CommonAdapter(Context context, List<T> datas, final int layoutId) {
        super(context, datas);
        //默认添加一种 ItemViewDelegate
        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(BaseViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(BaseViewHolder holder, T t, int position);
}
