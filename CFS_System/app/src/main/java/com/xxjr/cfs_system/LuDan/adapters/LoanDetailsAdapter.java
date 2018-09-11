package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.widgets.SwipeMenuLayout;
import com.xxjr.cfs_system.LuDan.view.activitys.CustomerActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.LoanDetailsActivity;
import com.xxjr.cfs_system.tools.Utils;

import java.math.BigDecimal;
import java.util.List;

import entity.CommonItem;
import entity.LoanInfo;
import entity.Schedule;
import refresh_recyclerview.DividerItemDecoration;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;
import rvadapter.ItemViewDelegate;
import rvadapter.MultiItemAdapter;

public class LoanDetailsAdapter extends MultiItemAdapter<CommonItem> {
    private LoanDetailsActivity activity;
    private RecycleItemClickListener itemClickListener1, itemClickListener2;//显示或者隐藏进度跟踪内容
    private RecycleItemClickListener itemClickListener3;//选择赎楼员
    private RecycleItemClickListener itemClickListener4;//不选赎楼员
    private RecycleItemClickListener itemClickListener5;//选公证时间
    private RecycleItemClickListener itemDelLend;//删除放款
    private TextChangeListener textChangeListener;

    public void setItemClickListener1(RecycleItemClickListener itemClickListener) {
        this.itemClickListener1 = itemClickListener;
    }

    public void setItemClickListener2(RecycleItemClickListener itemClickListener) {
        this.itemClickListener2 = itemClickListener;
    }

    public void setItemClickListener3(RecycleItemClickListener itemClickListener) {
        this.itemClickListener3 = itemClickListener;
    }

    public void setItemClickListener4(RecycleItemClickListener itemClickListener) {
        this.itemClickListener4 = itemClickListener;
    }

    public void setItemClickListener5(RecycleItemClickListener itemClickListener) {
        this.itemClickListener5 = itemClickListener;
    }

    public void setItemDelLendListener(RecycleItemClickListener itemClickListener) {
        this.itemDelLend = itemClickListener;
    }

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    public LoanDetailsAdapter(Context context, List datas) {
        super(context, datas);
        activity = (LoanDetailsActivity) context;
        addItemViewDelegate(new BlankDelegate());
        addItemViewDelegate(new TitleDelegate());
        addItemViewDelegate(new TextDelegate());
        addItemViewDelegate(new DetailsDelegate());
        addItemViewDelegate(new RecycleDelegate());
        addItemViewDelegate(new LoanDetailsDelegate());
        addItemViewDelegate(new RedeemDelegate());
        addItemViewDelegate(new NotaryDelegate());
        addItemViewDelegate(new ManyLendDelegate());
    }

    public LoanDetailsAdapter(Context context, List datas, int flag) {
        super(context, datas);
        addItemViewDelegate(new BlankDelegate());
        addItemViewDelegate(new TitleDelegate());
        addItemViewDelegate(new TextDelegate());
    }

    private class BlankDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 0;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.blank;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            if (item.getIcon() == 0) {
                holder.setBackgroundRes(R.id.v_blank, R.color.blank_bg);
            } else {
                holder.setBackgroundRes(R.id.v_blank, item.getIcon());
            }
        }
    }

    private class TitleDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 1;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_title;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setImageResource(R.id.iv_title, item.getIcon());
            holder.setText(R.id.tv_title, item.getName());
        }
    }

    private class TextDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 2;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_show;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            ViewGroup.LayoutParams params = holder.getConvertView().getLayoutParams();
            if (item.isEnable()) {
                holder.getConvertView().setVisibility(View.VISIBLE);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                holder.getConvertView().setVisibility(View.GONE);
                params.height = 0;
                params.width = 0;
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
            return item.getType() == 3;
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
            return item.getType() == 4;
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

    private class LoanDetailsDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 5;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_details;
        }

        @Override
        public void convert(BaseViewHolder holder, CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setText(R.id.tv_content, item.getContent());
            holder.setVisible(R.id.tv_yuan, item.isClick());
            holder.setVisible(R.id.v_line, item.isLineShow());
            if (item.getPosition() < 0) {
                if (item.getPosition() == -1) {//选择赎楼员
                    holder.getConvertView().setEnabled(true);
                    holder.setVisible(R.id.iv_right, true);
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemClickListener3.onItemClick(position);
                        }
                    });
                } else if (item.getPosition() == -3) {//选择公证时间
                    holder.getConvertView().setEnabled(true);
                    holder.setVisible(R.id.iv_right, true);
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemClickListener5.onItemClick(position);
                        }
                    });
                } else {
                    holder.setVisible(R.id.iv_right, false);
                    holder.getConvertView().setEnabled(false);
                }
            } else {
                holder.setVisible(R.id.iv_right, false);
            }
        }
    }

    private class RedeemDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 6;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_redeem;
        }

        @Override
        public void convert(final BaseViewHolder holder, CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setVisible(R.id.v_line, item.isLineShow());
            if (item.isClick()) {
                holder.setBackgroundRes(R.id.tv_yes, R.color.transparent);
                holder.setBackgroundRes(R.id.tv_no, R.color.font_cc);
            } else {
                holder.setBackgroundRes(R.id.tv_yes, R.color.font_cc);
                holder.setBackgroundRes(R.id.tv_no, R.color.transparent);
            }
            if (item.getPosition() == -1) {
                holder.setOnClickListener(R.id.tv_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemClickListener4.onItemClick(-3);
                        CommonItem commonItem = (CommonItem) getDatas().get(position);
                        commonItem.setClick(true);
                        CommonItem commonItem1 = (CommonItem) getDatas().get(position + 1);
                        commonItem1.setPosition(-1);
                        notifyDataSetChanged();
                    }
                });
                holder.setOnClickListener(R.id.tv_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemClickListener4.onItemClick(-4);
                        CommonItem commonItem = (CommonItem) getDatas().get(position);
                        commonItem.setClick(false);
                        CommonItem commonItem1 = (CommonItem) getDatas().get(position + 1);
                        commonItem1.setPosition(-2);
                        commonItem1.setContent("");
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }

    private class NotaryDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 7;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_notary;
        }

        @Override
        public void convert(BaseViewHolder holder, CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            final EditText editText = holder.getView(R.id.et_content);
            editText.removeTextChangedListener((TextWatcher) holder.getConvertView().getTag(R.id.tag_first));
            editText.setText(item.getContent());
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    textChangeListener.setTextChage(position, editable.toString());
                }
            };
            editText.addTextChangedListener(textWatcher);
            holder.getConvertView().setTag(R.id.tag_first, textWatcher);
        }
    }

    private class ManyLendDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 8;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_recycle;
        }

        @Override
        public void convert(BaseViewHolder holder, final CommonItem item, int position) {
            RecyclerView recyclerView = holder.getView(R.id.recycle_item);
            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.white));
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST));
            CommonAdapter adapter = new CommonAdapter<LoanInfo>(activity, item.getList(), R.layout.item_many_lend) {
                @Override
                protected void convert(BaseViewHolder holder, final LoanInfo loanInfo, int position) {
                    SwipeMenuLayout swipeMenuLayout = ((SwipeMenuLayout) holder.getConvertView()).setIos(false).setLeftSwipe(true);
                    if (loanInfo.getIsForeclosureFloor()) {
                        swipeMenuLayout.setSwipeEnable(false);
                    } else {
                        swipeMenuLayout.setSwipeEnable(true);
                    }
                    holder.setText(R.id.tv_lend_money, Utils.parseMoney(new BigDecimal(loanInfo.getLendingAmount())) + "元");
                    holder.setText(R.id.tv_lend_date, loanInfo.getLendingTime());
                    holder.setText(R.id.tv_month_money, Utils.parseMoney(new BigDecimal(loanInfo.getMonthAmount())) + "元");
                    holder.setText(R.id.tv_month_date, "每月" + loanInfo.getMonthDate() + "日");
                    holder.setText(R.id.tv_return_date, loanInfo.getReturnTime());
                    holder.setText(R.id.tv_other_amount, Utils.parseMoney(new BigDecimal(loanInfo.getOtherAmount())) + "元");
                    holder.setText(R.id.tv_add_time, loanInfo.getOverTime());
                    holder.setText(R.id.tv_period, loanInfo.getLendingPeriod() + "期");
                    holder.setText(R.id.tv_add_name, loanInfo.getClerkName());//添加者
                    holder.setText(R.id.tv_interest, loanInfo.getInterestRate() + "%");
                    holder.setText(R.id.tv_payment_type, loanInfo.getPaymentName());
                    if (TextUtils.isEmpty(loanInfo.getNote())) {
                        holder.setVisible(R.id.ll_remark, false);
                    } else {
                        holder.setVisible(R.id.ll_remark, true);
                    }
                    if (TextUtils.isEmpty(loanInfo.getOtherPayRemark())) {
                        holder.setVisible(R.id.ll_other_remark, false);
                    } else {
                        holder.setVisible(R.id.ll_other_remark, true);
                    }
                    holder.setText(R.id.tv_other_remark, loanInfo.getOtherPayRemark());
                    holder.setText(R.id.tv_remark, loanInfo.getNote());
                    holder.setOnClickListener(R.id.tv_del, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemDelLend.onItemClick(loanInfo.getClerkID());
                        }
                    });
                }
            };
            recyclerView.setAdapter(adapter);
        }
    }
}
