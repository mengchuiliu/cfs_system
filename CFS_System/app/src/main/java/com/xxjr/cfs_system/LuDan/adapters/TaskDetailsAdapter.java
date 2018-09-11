package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.view.activitys.TaskDetailsActivity;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import entity.CommonItem;
import entity.LoanInfo;
import entity.ScheduleNote;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;
import rvadapter.ItemViewDelegate;
import rvadapter.MultiItemAdapter;

public class TaskDetailsAdapter extends MultiItemAdapter<CommonItem> {
    private TaskDetailsActivity activity;
    private RecycleItemClickListener itemClickListener;

    public TaskDetailsAdapter(Context context, List datas) {
        super(context, datas);
        activity = (TaskDetailsActivity) context;
        addItemViewDelegate(new RecycleDelegate());
        addItemViewDelegate(new ImageDelegate());
    }

    public void setItemClickListener(RecycleItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private class RecycleDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 0;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_recycle;
        }

        @Override
        public void convert(BaseViewHolder holder, CommonItem item, int position) {
            RecyclerView recyclerView = holder.getView(R.id.recycle_item);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            if (position == 0) {//贷款详情
                LoanInfo loanInfo = (LoanInfo) item.getList().get(0);
                int pad = Utils.dip2px(activity, 5);
                recyclerView.setPadding(pad, pad, pad, pad);
                recyclerView.setBackgroundResource(R.drawable.search_bg);
                LoanDetailsAdapter loanDetailsAdapter = new LoanDetailsAdapter(activity, getLoanInfoItem(loanInfo), 1);
                recyclerView.setAdapter(loanDetailsAdapter);
            } else {
                showLoanStatus(recyclerView, item.getList(), item.getPosition());
            }
        }
    }

    private class ImageDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 1;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_image;
        }

        @Override
        public void convert(BaseViewHolder holder, CommonItem item, int position) {
        }
    }

    private List<CommonItem> getLoanInfoItem(LoanInfo loanInfo) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 6; i++) {
            commonItem = new CommonItem();
            switch (i) {
                case 0:
                    commonItem.setType(1);
                    commonItem.setName("贷款信息" + "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() +
                            "(" + (TextUtils.isEmpty(loanInfo.getLoanTypeName()) ? "" : loanInfo.getLoanTypeName().substring(0, 1)) + ")" + "】");
                    commonItem.setIcon(R.mipmap.icon_loan);
                    break;
                case 1:
                    commonItem.setType(2);
                    commonItem.setName("申请银行：");
                    commonItem.setContent(loanInfo.getBankName());
                    break;
                case 2:
                    commonItem.setType(2);
                    commonItem.setName("申请金额：");
                    commonItem.setContent(Utils.parseMoney(new BigDecimal(loanInfo.getAmount())));
                    break;
                case 3:
                    commonItem.setType(2);
                    commonItem.setName("申请客户：");
                    commonItem.setContent(loanInfo.getCustomer());
                    break;
                case 4:
                    commonItem.setType(2);
                    commonItem.setName(" 记 录 者 ：");
                    commonItem.setContent(loanInfo.getRecorder());
                    break;
                case 5:
                    commonItem.setType(2);
                    commonItem.setName(" 按 揭 员 ：");
                    commonItem.setContent(loanInfo.getMortgageName());
                    break;
            }
            commonItems.add(commonItem);
        }
        if (loanInfo.getLoanType() == 1) {
            CommonItem commonItem1 = new CommonItem();
            commonItem1.setType(2);
            commonItem1.setName("是否赎楼：");
            if (loanInfo.getIsForeclosureFloor()) {
                commonItem1.setContent("是");
            } else {
                commonItem1.setContent("否");
            }
            commonItems.add(commonItem1);
            String member = CacheProvide.getMemberName(loanInfo.getMember());
            if (loanInfo.getMember() != 0 && !TextUtils.isEmpty(member)) {
                CommonItem commonItem2 = new CommonItem();
                commonItem2.setType(2);
                commonItem2.setName(" 赎 楼 员 ：");
                commonItem2.setContent(member);
                commonItems.add(commonItem2);
            }
        }
        CommonItem commonItem3 = new CommonItem();
        commonItem3.setType(0);
        commonItem3.setIcon(R.color.transparent);
        commonItems.add(commonItem3);
        return commonItems;
    }

    //显示贷款进度
    private void showLoanStatus(RecyclerView recyclerView, final List<CommonItem> commonItems, final int scheduleId) {
        CommonAdapter adapter = new CommonAdapter<CommonItem>(activity, commonItems, R.layout.item_update_schedule) {
            @Override
            protected void convert(BaseViewHolder holder, final CommonItem commonItem, int position) {
                holder.setText(R.id.tv_schedule_title, commonItem.getName());
                RecyclerView recyclerView = holder.getView(R.id.recycle_schedule_note);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                if (position == 0) {
                    if (scheduleId > -1) {
                        holder.setVisible(R.id.tv_yellow, true);
                        holder.setVisible(R.id.tv_line_yellow, true);
                        holder.setVisible(R.id.iv_schedule, false);
                    } else {
                        holder.setINVISIBLE(R.id.iv_schedule, true);
                    }
                    recyclerView.setVisibility(View.GONE);
                    holder.setOnClickListener(R.id.tv_yellow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (scheduleId < 5 || (scheduleId > 100 && scheduleId < 109)) {
                                itemClickListener.onItemClick(-1);
                            }
                        }
                    });
                } else {
                    if (commonItem.getType() == 3) {
                        holder.setVisible(R.id.tv_yellow, false);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                if (commonItem.getList() != null && commonItem.getList().size() > 0) {
                    CommonAdapter commonAdapter = new CommonAdapter<ScheduleNote>(activity, commonItem.getList(), R.layout.item_schedule_note) {
                        @Override
                        protected void convert(BaseViewHolder holder, ScheduleNote scheduleNote, int position) {
                            holder.setText(R.id.tv_schedule_content, scheduleNote.getNote());
                            holder.setText(R.id.tv_provider, scheduleNote.getProvider());
                        }
                    };
                    holder.setVisible(R.id.iv_schedule, true);
                    recyclerView.setAdapter(commonAdapter);
                } else {
                    holder.setINVISIBLE(R.id.recycle_schedule_note, true);
                    holder.setVisible(R.id.iv_schedule, false);
                }
                switch (commonItem.getType()) {
                    case 1:
                        holder.setBackgroundRes(R.id.tv_schedule_title, R.drawable.schedule_bg_1);
                        break;
                    case 2:
                        holder.setBackgroundRes(R.id.tv_schedule_title, R.drawable.schedule_bg_2);
                        break;
                    case 3:
                        holder.setBackgroundRes(R.id.tv_schedule_title, R.drawable.schedule_bg_3);
                        break;
                    case 4:
                        holder.setBackgroundRes(R.id.tv_schedule_title, R.drawable.schedule_bg_4);
                        holder.setINVISIBLE(R.id.iv_schedule, true);
                        holder.setINVISIBLE(R.id.recycle_schedule_note, true);
                        break;
                }
                if (position == commonItems.size() - 1) {
                    holder.setVisible(R.id.tv_line, false);
                    holder.setVisible(R.id.tv_line1, false);
                    holder.setVisible(R.id.tv_line2, false);
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
