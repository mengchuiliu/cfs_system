package com.xxjr.cfs_system.LuDan.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.ViewsHolder.OnDragVHListener;
import com.xxjr.cfs_system.ViewsHolder.OnItemMoveListener;
import com.xxjr.cfs_system.services.CacheProvide;

import java.util.ArrayList;
import java.util.List;

import entity.CommonItem;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;

/**
 * 拖拽排序 + 增删
 * Created by YoKeyword on 15/12/28.
 */
@SuppressLint("ClickableViewAccessibility")
public class HomeMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemMoveListener {
    // 我的任务 标题部分
    public static final int TYPE_MY_TASK_HEADER = 4;
    // 我的频道 标题部分
    public static final int TYPE_MY_CHANNEL_HEADER = 0;
    // 我的频道
    public static final int TYPE_MY = 1;
    // 其他频道 标题部分
    public static final int TYPE_OTHER_CHANNEL_HEADER = 2;
    // 其他频道
    public static final int TYPE_OTHER = 3;

    // 我的频道之前的header数量  该demo中 即标题部分 为 1
    private static final int COUNT_PRE_MY_HEADER = 2;
    // 其他频道之前的header数量  该demo中 即标题部分 为 COUNT_PRE_MY_HEADER + 1
    private static final int COUNT_PRE_OTHER_HEADER = COUNT_PRE_MY_HEADER + 1;

    private static final long ANIM_TIME = 360L;

    // touch 点击开始时间
    private long startTime;
    // touch 间隔时间  用于分辨是否是 "点击"
    private static final long SPACE_TIME = 100;

    private Context context;
    private LayoutInflater mInflater;
    private ItemTouchHelper mItemTouchHelper;

    // 是否为 编辑 模式
    private boolean isEditMode;

    private ArrayList<CommonItem> mMyChannelItems, mOtherChannelItems;
    private CommonItem taskItems;

    // 我的频道点击事件
    private TextChangeListener textChangeListener;

    public HomeMenuAdapter(Context context, ItemTouchHelper helper, ArrayList<CommonItem> mMyChannelItems,
                           ArrayList<CommonItem> mOtherChannelItems, CommonItem mTaskItem) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mItemTouchHelper = helper;
        this.mMyChannelItems = mMyChannelItems;
        this.mOtherChannelItems = mOtherChannelItems;
        this.taskItems = mTaskItem;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_MY_TASK_HEADER;
        } else if (position == 1) {    // 我的频道 标题部分
            return TYPE_MY_CHANNEL_HEADER;
        } else if (position == mMyChannelItems.size() + 2) {    // 其他频道 标题部分
            return TYPE_OTHER_CHANNEL_HEADER;
        } else if (position > 0 && position < mMyChannelItems.size() + 2) {
            return TYPE_MY;
        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case TYPE_MY_TASK_HEADER:
                view = mInflater.inflate(R.layout.item_page_content, parent, false);
                return new MyTaskViewHolder(view);
            case TYPE_MY_CHANNEL_HEADER:
                view = mInflater.inflate(R.layout.menu_title, parent, false);
                final MyChannelHeaderViewHolder titleHolder = new MyChannelHeaderViewHolder(view);
                titleHolder.tvBtnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEditMode) {
                            startEditMode((RecyclerView) parent);
                            titleHolder.tvBtnEdit.setText(R.string.finish);
                        } else {
                            cancelEditMode((RecyclerView) parent);
                            titleHolder.tvBtnEdit.setText(R.string.edit);
                        }
                    }
                });
                return titleHolder;
            case TYPE_MY:
                view = mInflater.inflate(R.layout.item_menu, parent, false);
                final MyViewHolder myHolder = new MyViewHolder(view);

                myHolder.itemMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int position = myHolder.getAdapterPosition();
                        if (isEditMode) {
                            RecyclerView recyclerView = ((RecyclerView) parent);
                            View targetView = recyclerView.getLayoutManager().findViewByPosition(mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
                            View currentView = recyclerView.getLayoutManager().findViewByPosition(position);
                            // 如果targetView不在屏幕内,则indexOfChild为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                            // 如果在屏幕内,则添加一个位移动画
                            if (recyclerView.indexOfChild(targetView) >= 0) {
                                int targetX, targetY;

                                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                                int spanCount = ((GridLayoutManager) manager).getSpanCount();

                                // 移动后 高度将变化 (我的频道Grid 最后一个item在新的一行第一个)
                                if ((mMyChannelItems.size() - COUNT_PRE_MY_HEADER) % spanCount == 0) {
                                    View preTargetView = recyclerView.getLayoutManager().findViewByPosition(mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER - 1);
                                    targetX = preTargetView.getLeft();
                                    targetY = preTargetView.getTop();
                                } else {
                                    targetX = targetView.getLeft();
                                    targetY = targetView.getTop();
                                }

                                moveMyToOther(myHolder);
                                startAnimation(recyclerView, currentView, targetX, targetY);

                            } else {
                                moveMyToOther(myHolder);
                            }
                        } else {
                            textChangeListener.setTextChage(1, mMyChannelItems.get(position - COUNT_PRE_MY_HEADER).getRemark());
                        }
                    }
                });

                myHolder.itemMenu.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (isEditMode) {
                            switch (MotionEventCompat.getActionMasked(event)) {
                                case MotionEvent.ACTION_DOWN:
                                    startTime = System.currentTimeMillis();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    if (System.currentTimeMillis() - startTime > SPACE_TIME) {
                                        mItemTouchHelper.startDrag(myHolder);
                                    }
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                case MotionEvent.ACTION_UP:
                                    startTime = 0;
                                    break;
                            }
                        }
                        return false;
                    }
                });
                return myHolder;
            case TYPE_OTHER_CHANNEL_HEADER:
                view = mInflater.inflate(R.layout.menu_title, parent, false);
                final MyChannelHeaderViewHolder titleOtherHolder = new MyChannelHeaderViewHolder(view);
                titleOtherHolder.tvTitle.setText("其他功能");
                titleOtherHolder.tvBtnEdit.setVisibility(View.GONE);
                return titleOtherHolder;
            case TYPE_OTHER:
                view = mInflater.inflate(R.layout.item_menu, parent, false);
                final OtherViewHolder otherHolder = new OtherViewHolder(view);
                otherHolder.itemMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentPiosition = otherHolder.getAdapterPosition();
                        if (isEditMode) {
                            RecyclerView recyclerView = ((RecyclerView) parent);
                            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                            // 如果RecyclerView滑动到底部,移动的目标位置的y轴 - height
                            View currentView = manager.findViewByPosition(currentPiosition);
                            // 目标位置的前一个item  即当前MyChannel的最后一个
                            View preTargetView = manager.findViewByPosition(mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);

                            // 如果targetView不在屏幕内,则为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                            // 如果在屏幕内,则添加一个位移动画
                            if (recyclerView.indexOfChild(preTargetView) >= 0) {
                                int targetX = preTargetView.getLeft();
                                int targetY = preTargetView.getTop();

                                int targetPosition = mMyChannelItems.size() - 1 + COUNT_PRE_OTHER_HEADER;

                                GridLayoutManager gridLayoutManager = ((GridLayoutManager) manager);
                                int spanCount = gridLayoutManager.getSpanCount();
                                // target 在最后一行第一个
                                if ((targetPosition - COUNT_PRE_MY_HEADER) % spanCount == 0) {
                                    View targetView = manager.findViewByPosition(targetPosition);
                                    targetX = targetView.getLeft();
                                    targetY = targetView.getTop();
                                } else {
                                    targetX += preTargetView.getWidth();
                                    // 最后一个item可见
                                    if (gridLayoutManager.findLastVisibleItemPosition() == getItemCount() - 1) {
                                        // 最后的item在最后一行第一个位置
                                        if ((getItemCount() - 1 - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER) % spanCount == 0) {
                                            // RecyclerView实际高度 > 屏幕高度 && RecyclerView实际高度 < 屏幕高度 + item.height
                                            int firstVisiblePostion = gridLayoutManager.findFirstVisibleItemPosition();
                                            if (firstVisiblePostion == 0) {
                                                // FirstCompletelyVisibleItemPosition == 0 即 内容不满一屏幕 , targetY值不需要变化
                                                // // FirstCompletelyVisibleItemPosition != 0 即 内容满一屏幕 并且 可滑动 , targetY值 + firstItem.getTop
                                                if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                                                    int offset = (-recyclerView.getChildAt(0).getTop()) - recyclerView.getPaddingTop();
                                                    targetY += offset;
                                                }
                                            } else { // 在这种情况下 并且 RecyclerView高度变化时(即可见第一个item的 position != 0),
                                                // 移动后, targetY值  + 一个item的高度
                                                targetY += preTargetView.getHeight();
                                            }
                                        }
                                    } else {
                                        System.out.println("current--No");
                                    }
                                }

                                // 如果当前位置是otherChannel可见的最后一个
                                // 并且 当前位置不在grid的第一个位置
                                // 并且 目标位置不在grid的第一个位置

                                // 则 需要延迟250秒 notifyItemMove , 这是因为这种情况 , 并不触发ItemAnimator , 会直接刷新界面
                                // 导致我们的位移动画刚开始,就已经notify完毕,引起不同步问题
//                                if (currentPiosition == gridLayoutManager.findLastVisibleItemPosition()
//                                        && (currentPiosition - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER) % spanCount != 0
//                                        && (targetPosition - COUNT_PRE_MY_HEADER) % spanCount != 0) {
//                                    moveOtherToMyWithDelay(otherHolder);
//                                } else {
//                                    moveOtherToMy(otherHolder);
//                                }
                                moveOtherToMy(otherHolder);
                                startAnimation(recyclerView, currentView, targetX, targetY);
                            } else {
                                moveOtherToMy(otherHolder);
                            }
                        } else {
                            textChangeListener.setTextChage(2, mOtherChannelItems.get(currentPiosition - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER).getRemark());
                        }
                    }
                });
                return otherHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myHolder = (MyViewHolder) holder;
            CommonItem commonItem = mMyChannelItems.get(position - COUNT_PRE_MY_HEADER);
            myHolder.tvMenu.setText(commonItem.getName());
            myHolder.ivChange.setImageResource(R.mipmap.menu_close);
            if (isEditMode) {
                myHolder.ivChange.setVisibility(View.VISIBLE);
            } else {
                myHolder.ivChange.setVisibility(View.GONE);
            }
            myHolder.ivMenu.setImageResource(commonItem.getIcon());
        } else if (holder instanceof OtherViewHolder) {
            OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
            CommonItem commonItem = mOtherChannelItems.get(position - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER);
            otherViewHolder.tvMenu.setText(commonItem.getName());
            otherViewHolder.ivMenu.setImageResource(commonItem.getIcon());
            otherViewHolder.ivChange.setImageResource(R.mipmap.menu_add);
            if (isEditMode) {
                otherViewHolder.ivChange.setVisibility(View.VISIBLE);
            } else {
                otherViewHolder.ivChange.setVisibility(View.GONE);
            }
        } else if (holder instanceof MyChannelHeaderViewHolder) {
            MyChannelHeaderViewHolder headerHolder = (MyChannelHeaderViewHolder) holder;
            if (isEditMode) {
                headerHolder.tvBtnEdit.setText(R.string.finish);
            } else {
                headerHolder.tvBtnEdit.setText(R.string.edit);
            }
        } else if (holder instanceof MyTaskViewHolder) {
            MyTaskViewHolder taskViewHolder = (MyTaskViewHolder) holder;
            if (taskItems != null) {
                taskViewHolder.ivIcon.setImageResource(taskItems.getIcon());
                taskViewHolder.tvName.setText(taskItems.getName());
                taskViewHolder.editor.setVisibility(View.GONE);
                taskViewHolder.pageLine.setVisibility(View.GONE);
                taskViewHolder.rvTask.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
                taskViewHolder.rvTask.setAdapter(new CommonAdapter<CommonItem>(context, taskItems.getList(), R.layout.item_page_task) {
                    @Override
                    protected void convert(BaseViewHolder holder, final CommonItem item, int position) {
                        holder.setText(R.id.tv_task_num, String.valueOf(item.getPosition()));
                        holder.setText(R.id.tv_content, item.getContent());
                        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                textChangeListener.setTextChage(0, item.getRemark());
                            }
                        });
                    }
                });
            } else {
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams param = holder.itemView.getLayoutParams();
                param.height = 0;
                param.width = 0;
                holder.itemView.setLayoutParams(param);
                taskViewHolder.llPage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        // 我的频道  标题 + 我的频道.size + 其他频道 标题 + 其他频道.size
        return mMyChannelItems.size() + mOtherChannelItems.size() + COUNT_PRE_OTHER_HEADER;
    }

    /**
     * 开始增删动画
     */
    private void startAnimation(RecyclerView recyclerView, final View currentView, float targetX, float targetY) {
        final ViewGroup viewGroup = (ViewGroup) recyclerView.getParent();
        final ImageView mirrorView = addMirrorView(viewGroup, recyclerView, currentView);

        Animation animation = getTranslateAnimator(
                targetX - currentView.getLeft(), targetY - currentView.getTop());
        currentView.setVisibility(View.GONE);
        mirrorView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(mirrorView);
                if (currentView.getVisibility() == View.GONE) {
                    currentView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 我的频道 移动到 其他频道
     *
     * @param myHolder
     */
    private void moveMyToOther(MyViewHolder myHolder) {
        int position = myHolder.getAdapterPosition();

        int startPosition = position - COUNT_PRE_MY_HEADER;
        if (startPosition > mMyChannelItems.size() - 1) {
            return;
        }
        CommonItem item = mMyChannelItems.get(startPosition);
        mMyChannelItems.remove(startPosition);
        mOtherChannelItems.add(0, item);

        notifyItemMoved(position, mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
    }

    /**
     * 其他频道 移动到 我的频道
     *
     * @param otherHolder
     */
    private void moveOtherToMy(OtherViewHolder otherHolder) {
        int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        notifyItemMoved(position, mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);
    }

    /**
     * 其他频道 移动到 我的频道 伴随延迟
     *
     * @param otherHolder
     */
    private void moveOtherToMyWithDelay(OtherViewHolder otherHolder) {
        final int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyItemMoved(position, mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);
            }
        }, ANIM_TIME);
    }

    private Handler delayHandler = new Handler();

    private int processItemRemoveAdd(OtherViewHolder otherHolder) {
        int position = otherHolder.getAdapterPosition();

        int startPosition = position - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER;
        if (startPosition > mOtherChannelItems.size() - 1) {
            return -1;
        }
        CommonItem item = mOtherChannelItems.get(startPosition);
        mOtherChannelItems.remove(startPosition);
        mMyChannelItems.add(item);
        return position;
    }


    /**
     * 添加需要移动的 镜像View
     */
    private ImageView addMirrorView(ViewGroup parent, RecyclerView recyclerView, View view) {
        /**
         * 我们要获取cache首先要通过setDrawingCacheEnable方法开启cache，然后再调用getDrawingCache方法就可以获得view的cache图片了。
         buildDrawingCache方法可以不用调用，因为调用getDrawingCache方法时，若果cache没有建立，系统会自动调用buildDrawingCache方法生成cache。
         若想更新cache, 必须要调用destoryDrawingCache方法把旧的cache销毁，才能建立新的。
         当调用setDrawingCacheEnabled方法设置为false, 系统也会自动把原来的cache销毁。
         */
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        final ImageView mirrorView = new ImageView(recyclerView.getContext());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        mirrorView.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(false);
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        int[] parenLocations = new int[2];
        recyclerView.getLocationOnScreen(parenLocations);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        params.setMargins(locations[0], locations[1] - parenLocations[1], 0, 0);
        parent.addView(mirrorView, params);

        return mirrorView;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        CommonItem item = mMyChannelItems.get(fromPosition - COUNT_PRE_MY_HEADER);
        mMyChannelItems.remove(fromPosition - COUNT_PRE_MY_HEADER);
        mMyChannelItems.add(toPosition - COUNT_PRE_MY_HEADER, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 开启编辑模式
     *
     * @param parent
     */
    private void startEditMode(RecyclerView parent) {
        isEditMode = true;
        notifyDataSetChanged();
//        int visibleChildCount = parent.getChildCount();
//        for (int i = 0; i < visibleChildCount; i++) {
//            View view = parent.getChildAt(i);
//            ImageView imgEdit = view.findViewById(R.id.iv_change);
//            if (imgEdit != null) {
//                imgEdit.setVisibility(View.VISIBLE);
//            }
//        }
    }

    /**
     * 完成编辑模式
     *
     * @param parent
     */
    private void cancelEditMode(RecyclerView parent) {
        isEditMode = false;
        notifyDataSetChanged();
//        int visibleChildCount = parent.getChildCount();//只是刷新显示的页面布局
//        for (int i = 0; i < visibleChildCount; i++) {
//            View view = parent.getChildAt(i);
//            ImageView imgEdit = view.findViewById(R.id.iv_change);
//            if (imgEdit != null) {
//                imgEdit.setVisibility(View.GONE);
//            }
//        }
        savePos();
    }

    //保存完成时的menu位置
    private void savePos() {
        List<String> myList = new ArrayList<>();
        for (CommonItem item : mMyChannelItems) {
            myList.add(item.getRemark());
        }
        Hawk.put(CacheProvide.getCacheKey("MyMenu"), myList);
    }

    /**
     * 获取位移动画
     */
    private TranslateAnimation getTranslateAnimator(float targetX, float targetY) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetX,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetY);
        // RecyclerView默认移动动画250ms 这里设置360ms 是为了防止在位移动画结束后 remove(view)过早 导致闪烁
        translateAnimation.setDuration(ANIM_TIME);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }

    //任务点击监听
    public void setTaskClickListener(TextChangeListener listener) {
        this.textChangeListener = listener;
    }

    /**
     * 我的频道
     */
    class MyViewHolder extends RecyclerView.ViewHolder implements OnDragVHListener {
        private TextView tvMenu;
        private ImageView ivMenu;
        private ImageView ivChange;
        private ConstraintLayout itemMenu;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMenu = itemView.findViewById(R.id.tv_menu);
            ivMenu = itemView.findViewById(R.id.iv_menu);
            ivChange = itemView.findViewById(R.id.iv_change);
            itemMenu = itemView.findViewById(R.id.item_menu);
        }

        /**
         * item 被选中时
         */
        @Override
        public void onItemSelected() {
            itemMenu.setBackgroundResource(R.color.font_cc);
        }

        /**
         * item 取消选中时
         */
        @Override
        public void onItemFinish() {
            itemMenu.setBackgroundResource(R.drawable.clicked_white);
        }
    }

    /**
     * 其他频道
     */
    class OtherViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMenu;
        private ImageView ivMenu;
        private ImageView ivChange;
        private ConstraintLayout itemMenu;

        public OtherViewHolder(View itemView) {
            super(itemView);
            tvMenu = itemView.findViewById(R.id.tv_menu);
            ivMenu = itemView.findViewById(R.id.iv_menu);
            ivChange = itemView.findViewById(R.id.iv_change);
            itemMenu = itemView.findViewById(R.id.item_menu);
        }
    }

    /**
     * 我的频道  标题部分
     */
    class MyChannelHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvBtnEdit;

        public MyChannelHeaderViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvBtnEdit = itemView.findViewById(R.id.tv_edit);
        }
    }

    class MyTaskViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView rvTask;
        private ImageView ivIcon;
        private TextView tvName;
        private TextView editor;
        private View pageLine;
        private LinearLayout llPage;

        public MyTaskViewHolder(View itemView) {
            super(itemView);
            rvTask = itemView.findViewById(R.id.rv_home);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            editor = itemView.findViewById(R.id.menu_editor);
            pageLine = itemView.findViewById(R.id.page_line);
            llPage = itemView.findViewById(R.id.ll_page);
        }
    }
}
