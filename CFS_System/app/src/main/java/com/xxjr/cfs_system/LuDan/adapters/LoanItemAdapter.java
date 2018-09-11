package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.view.activitys.PactDetailsActivity;
import com.xxjr.cfs_system.tools.Utils;

import java.util.List;

import entity.CommonItem;
import entity.Schedule;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;
import rvadapter.ItemViewDelegate;
import rvadapter.MultiItemAdapter;

/**
 * Created by Administrator on 2017/8/28.
 */

public class LoanItemAdapter extends MultiItemAdapter<CommonItem> {
    private RecycleItemClickListener itemClickListener, itemClickListener1, itemClickListener2;
    private PactDetailsActivity activity;
    private int pos = -1;

    public LoanItemAdapter(Context context, List datas) {
        super(context, datas);
        activity = (PactDetailsActivity) context;
        addItemViewDelegate(new TitleDelegate());
        addItemViewDelegate(new TextDelegate());
        addItemViewDelegate(new DetailsDelegate());
        addItemViewDelegate(new RecycleDelegate());
        addItemViewDelegate(new BlankDelegate());
    }

    public void setItemClickListener(RecycleItemClickListener itemClickListener, int pos) {
        this.itemClickListener = itemClickListener;
        this.pos = pos;
    }

    public void setItemClickListener1(RecycleItemClickListener itemClickListener) {
        this.itemClickListener1 = itemClickListener;
    }

    public void setItemClickListener2(RecycleItemClickListener itemClickListener) {
        this.itemClickListener2 = itemClickListener;
    }

    private class TitleDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 0;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_title;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setImageResource(R.id.iv_title, item.getIcon());
            holder.setText(R.id.tv_title, item.getName());
            if (item.getPosition() == 0) {
                holder.setVisible(R.id.tv_right, true);
                holder.setTextSize(R.id.tv_right, 15);
                holder.setTextColorRes(R.id.tv_right, R.color.font_home);
                holder.setText(R.id.tv_right, "编辑");
                holder.setOnClickListener(R.id.tv_right, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pos != -1) {
                            itemClickListener.onItemClick(pos);
                        }
                    }
                });
            }
        }
    }

    private class TextDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 1;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_show;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            ViewGroup.LayoutParams params = holder.getConvertView().getLayoutParams();
            if (item.isLineShow()) {
                holder.getConvertView().setVisibility(View.GONE);
                params.height = 0;
                params.width = 0;
            } else {
                holder.getConvertView().setVisibility(View.VISIBLE);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            holder.getConvertView().setLayoutParams(params);
            holder.setText(R.id.tv_content_name, item.getName());
            if (position == 8) {
                holder.setTextColorRes(R.id.tv_content, R.color.font_home);
            } else {
                holder.setTextColorRes(R.id.tv_content, R.color.font_c3);
            }
            holder.setText(R.id.tv_content, item.getContent());
        }
    }

    private class DetailsDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 2;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_details_show;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_content, item.getName());
            if (item.isClick()) {
                holder.setImageResource(R.id.iv_show, R.mipmap.icon_pack_up);
            } else {
                holder.setImageResource(R.id.iv_show, R.mipmap.icon_look_over);
            }
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!item.isClick()) {
                        itemClickListener1.onItemClick(position);
                    } else {
                        itemClickListener2.onItemClick(position);
                    }
                }
            });
        }
    }

    private class RecycleDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 3;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_recycle;
        }

        @Override
        public void convert(BaseViewHolder holder, final CommonItem item, int position) {
            RecyclerView recyclerView = holder.getView(R.id.recycle_item);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) recyclerView.getLayoutParams();
            if (item.isClick()) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                recyclerView.setPadding(Utils.dip2px(activity, 15), 0, 0, 5);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                param.height = 0;
                param.width = 0;
                recyclerView.setVisibility(View.GONE);
            }
            recyclerView.setLayoutParams(param);
            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.white));
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            if (item.getList() != null && item.getList().size() > 0) {
                CommonAdapter adapter = new CommonAdapter<Schedule>(activity, item.getList(), R.layout.item_schedule) {
                    @Override
                    protected void convert(BaseViewHolder holder, Schedule schedule, int position) {
                        holder.setText(R.id.tv_status, schedule.getStatus());
                        holder.setText(R.id.tv_name_date, schedule.getName() + "  " + schedule.getDate());
                        if (position == 0) {
                            holder.setINVISIBLE(R.id.tv_line, true);
                            holder.setImageResource(R.id.iv_dot, R.mipmap.icon_dot_new);
                        } else {
                            holder.setINVISIBLE(R.id.tv_line, false);
                            holder.setImageResource(R.id.iv_dot, R.mipmap.icon_dot);
                        }

                        if (position == item.getList().size() - 1) {
                            holder.setINVISIBLE(R.id.tv_line_1, true);
                        } else {
                            holder.setINVISIBLE(R.id.tv_line_1, false);
                        }
                    }
                };
                recyclerView.setAdapter(adapter);
            }
        }
    }


    private class BlankDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 4;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.blank;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setBackgroundRes(R.id.v_blank, R.color.blank_bg);
        }
    }
}
