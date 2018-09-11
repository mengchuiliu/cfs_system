package rvadapter;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 * 多布局显示adapter
 */
public abstract class MultiItemAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    public static final int ITEM_TYPE_EMPTY = 300000;
    public static final int ITEM_TYPE_LOAD_MORE = 400000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    private View mEmptyView;
    private int mEmptyLayoutId = -1;
    private View mLoadMoreView;
    private int mLoadMoreLayoutId = -1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mLoadMoreEnable = false;//是否开启加载更多

    protected Context mContext;
    protected List<T> mDatas;

    protected ItemViewDelegateManager mItemViewDelegateManager;

    public MultiItemAdapter(Context context, List<T> datas) {
        mContext = context;
        mDatas = datas == null ? new ArrayList<T>() : datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(View view) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public void setEmptyView(int layoutId) {
        mEmptyLayoutId = layoutId;
    }

    public void setLoadMoreView(View loadMoreView) {
        mLoadMoreView = loadMoreView;
    }

    public void setLoadMoreView(int layoutId) {
        mLoadMoreLayoutId = layoutId;
    }

    //打开或关闭加载更多
    public void setloadMoreEnable(boolean enable) {
        mLoadMoreEnable = enable;
    }

    //获取显示总数据
    public List<T> getDatas() {
        return mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        }
        if (isShowLoadMore(position)) {
            return ITEM_TYPE_LOAD_MORE;
        }

        if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount() - getEmptyItemCount());
        }

        if (isEmpty()) {
            return ITEM_TYPE_EMPTY;
        }
        return mItemViewDelegateManager.getItemViewType(mDatas.get(position - getHeadersCount()), position - getHeadersCount());// 根据position和T判断是什么Type
    }

    private boolean isShowLoadMore(int position) {
        boolean enable = hasLoadMore() && (position >= getHeadersCount() + getRealItemCount() + getEmptyItemCount());
        return enable;
    }

    private boolean hasLoadMore() {
        return (mLoadMoreView != null || mLoadMoreLayoutId != -1) && mLoadMoreEnable;
    }

    private boolean isEmpty() {
        return (mEmptyView != null || mEmptyLayoutId != -1) && getRealItemCount() == 0;
    }

    private int getRealItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemCount() {
        int count = getHeadersCount() + getFootersCount() + getRealItemCount() + getEmptyItemCount() + (hasLoadMore() ? 1 : 0);
        return count;
    }

    private int getEmptyItemCount() {
        if (!isEmpty()) {//如果数据不为空，即使有mEmptyView也返回0
            return 0;
        }
        return mEmptyView != null || mEmptyLayoutId != -1 ? 1 : 0;
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        //footer和loadMore是互相冲突的，只有当mLoadMoreEnable为false时才会显示出footer
        if (hasLoadMore()) {
            return 0;
        }
        return mFootViews.size();
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount() + getEmptyItemCount() + getLoadMoreItemCount(position);
    }

    public int getLoadMoreItemCount(int position) {
        return isShowLoadMore(position) ? 1 : 0;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder;
        if (mHeaderViews.get(viewType) != null) {
            holder = BaseViewHolder.createViewHolder(parent.getContext(), parent, mHeaderViews.get(viewType));
            return holder;
        }
        if (viewType == ITEM_TYPE_EMPTY) {// && !isEmptyAlreadyShow
            if (mEmptyView != null) {
                holder = BaseViewHolder.createViewHolder(parent.getContext(), mEmptyView);
            } else {
                holder = BaseViewHolder.createViewHolder(parent.getContext(), parent, mEmptyLayoutId);
            }
            return holder;

        }
        if (viewType == ITEM_TYPE_LOAD_MORE) {
            if (mLoadMoreView != null) {
                holder = BaseViewHolder.createViewHolder(parent.getContext(), mLoadMoreView);
            } else {
                holder = BaseViewHolder.createViewHolder(parent.getContext(), parent, mLoadMoreLayoutId);
            }
            return holder;
        }

        if (mFootViews.get(viewType) != null) {
            holder = BaseViewHolder.createViewHolder(parent.getContext(), parent, mFootViews.get(viewType));
            return holder;
        }
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        holder = BaseViewHolder.createViewHolder(mContext, parent, layoutId);
        setListener(parent, holder, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        if (isEmpty()) {
            return;
        }
        if (isShowLoadMore(position)) {
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMoreRequested();
            }
            return;
        }
        convert(holder, mDatas.get(position - getHeadersCount()));
    }

    public void convert(BaseViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());// manager会判断当前数据属于哪个type，并调用它的convert方法
    }

    public MultiItemAdapter<T> addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;//是否有多种布局
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (spanSizeLookup == null) {
                        if (mHeaderViews.get(viewType) != null || mFootViews.get(viewType) != null || isEmpty() || isShowLoadMore(position)) {
                            return gridLayoutManager.getSpanCount();
                        }
                        return 1;
                    } else {
                        if (mHeaderViews.get(viewType) != null || mFootViews.get(viewType) != null || isEmpty() || isShowLoadMore(position)) {
                            return gridLayoutManager.getSpanCount();
                        }
                        return spanSizeLookup.getSpanSize(position);
                    }
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position) || isEmpty() || isShowLoadMore(position)) {
            setFullSpan(holder);
        }
    }

    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    /**
     * notifyItemChanged(int position) 更新列表position位置上的数据可以调用
     * notifyItemInserted(int position) 列表position位置添加一条数据时可以调用，伴有动画效果
     * notifyItemRemoved(int position) 列表position位置移除一条数据时调用，伴有动画效果
     * notifyItemMoved(int fromPosition, int toPosition) 列表fromPosition位置的数据移到toPosition位置时调用，伴有动画效果
     * notifyItemRangeChanged(int positionStart, int itemCount) 列表从positionStart位置到itemCount数量的列表项进行数据刷新
     * notifyItemRangeInserted(int positionStart, int itemCount) 列表从positionStart位置到itemCount数量的列表项批量添加数据时调用，伴有动画效果
     * notifyItemRangeRemoved(int positionStart, int itemCount) 列表从positionStart位置到itemCount数量的列表项批量删除数据时调用，伴有动画效果
     *
     * @param position
     */
    public void remove(int position) {
        if (mDatas.size() > position) {
            mDatas.remove(position);
            notifyItemRemoved(position + getHeadersCount());
        }
    }

    //加在开头
    public void add(int position, T item) {
        if (isEmpty()) {
            mDatas.add(position, item);
            notifyDataSetChanged();
        } else {
            mDatas.add(position, item);
            notifyItemInserted(position + getHeadersCount());
        }
    }

    //加到指定位置
    public void addData(int position, List<T> data) {
        if (isEmpty()) {
            mDatas.addAll(data);
            notifyDataSetChanged();
        } else {
            mDatas.addAll(position, data);
            notifyItemRangeInserted(position + getHeadersCount(), data.size());//  mDatas.size() +getHeadersCount() - position
        }
    }

    //加到最后面
    public void addData(List<T> newData) {
        if (isEmpty()) {
            mDatas.addAll(newData);
            notifyDataSetChanged();
        } else {
            int lastSize = mDatas.size();
            mDatas.addAll(newData);
            notifyItemRangeInserted(lastSize + getHeadersCount(), newData.size());
        }
    }

    //重新设置数据
    public void setNewData(List<T> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

    public interface OnLoadMoreListener {
        void onLoadMoreRequested();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener;
        }
    }

    public interface OnItemClickListener {
        // 这里的position不算头部，
        void onItemClick(View view, BaseViewHolder holder, int position);

        //boolean onItemLongClick(View view, BaseViewHolder holder, int position);
    }

    protected OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setListener(ViewGroup parent, final BaseViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;

        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, position - getHeadersCount());
                }
            }
        });
//        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (mOnItemClickListener != null) {
//                    int position = viewHolder.getAdapterPosition();
//                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position - getHeadersCount());
//                }
//                return false;
//            }
//        });
    }

    //是否可以点击
    protected boolean isEnabled(int viewType) {
        return true;
    }
}
