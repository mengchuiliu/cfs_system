package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xiaoxiao.widgets.SwipeMenuLayout;
import com.xxjr.cfs_system.LuDan.presenter.LoanPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.LoanVInter;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import entity.ChooseType;
import entity.CommonItem;
import entity.LoanInfo;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;
import rx.Subscription;
import rx.functions.Action1;

public class TaskListActivity extends BaseListActivity<LoanPresenter, TaskListActivity> implements LoanVInter {
    private Subscription companySubscription;//门店
    private List<LoanInfo> loanInfos = new ArrayList<>();
    private int loanType = 0;
    private int schedule = 0;
    private int dayNot = 0;

    @Override
    public List<LoanInfo> getLoanInfos() {
        return loanInfos;
    }

    public int getDayNo() {
        return dayNot;
    }

    public int getschedule() {
        return schedule;
    }

    public int getLoanType() {
        return loanType;
    }

    @Override
    protected LoanPresenter getListPresenter() {
        return new LoanPresenter();
    }

    @Override
    protected void initAdapter() {
        companySubscription = RxBus.getInstance().toObservable(Constants.Company_Choose, ChooseType.class)
                .subscribe(new Action1<ChooseType>() {
                    @Override
                    public void call(ChooseType chooseType) {
                        searchCompanyId = chooseType.getIds();
                        etPactSearch.setText(chooseType.getContent());
                        page = 0;
                        isPull = false;
                        refreshData(page, searchType);
                    }
                });
        schedule = getIntent().getIntExtra("Schedule", 0);
        dayNot = getIntent().getIntExtra("DayNo", 0);
        adapter = new CommonAdapter<LoanInfo>(TaskListActivity.this, new ArrayList<LoanInfo>(), R.layout.item_loan) {
            @Override
            protected void convert(BaseViewHolder holder, final LoanInfo loanInfo, int position) {
                SwipeMenuLayout swipeMenuLayout = ((SwipeMenuLayout) holder.getConvertView()).setIos(false).setLeftSwipe(true);
                swipeMenuLayout.setSwipeEnable(false);
                holder.setVisible(R.id.ll_cost, false);
                holder.setText(R.id.tv_loan_numb, loanInfo.getLoanCode());
                holder.setText(R.id.tv_date, loanInfo.getUpdateTime().contains("T") ?
                        loanInfo.getUpdateTime().substring(0, loanInfo.getUpdateTime().indexOf("T")) : loanInfo.getUpdateTime());
                holder.setText(R.id.tv_customer, loanInfo.getCustomer());
                String description = "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() +
                        "(" + (TextUtils.isEmpty(loanInfo.getLoanTypeName()) ? "" : loanInfo.getLoanTypeName().substring(0, 1)) + ")" + "】" +
                        "申请: " + Utils.div(loanInfo.getAmount()) + "万";
                holder.setText(R.id.tv_loan_content, description);
                holder.setText(R.id.tv_schedule, loanInfo.getSchedule());

                //待结算状态
                if (loanInfo.getScheduleId() == 109 || loanInfo.getScheduleId() == 5 ||
                        loanInfo.getScheduleId() == -3 || loanInfo.getScheduleId() == -4 || loanInfo.getScheduleId() == -5) {
                    holder.setVisible(R.id.ll_is_case, true);
                    holder.setText(R.id.tv_case, loanInfo.getIsCase() == 1 ? "是" : "否");
                } else {
                    holder.setVisible(R.id.ll_is_case, false);
                }
                holder.setOnClickListener(R.id.ll_home, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent;
                        if (loanInfo.getScheduleId() == 0) {
                            intent = new Intent(TaskListActivity.this, LoanDetailsActivity.class);
                            intent.putExtra("contractType", contractType);
                        } else if ((loanInfo.getLoanType() != 1 && loanInfo.getScheduleId() > 5) ||
                                (loanInfo.getLoanType() == 1 && loanInfo.getScheduleId() > 109) || loanInfo.getScheduleId() == -6) {
                            intent = new Intent(TaskListActivity.this, OverApplyActivity.class);
                            intent.putExtra("type", 3);
                        } else {
                            intent = new Intent(TaskListActivity.this, TaskDetailsActivity.class);
                        }
                        intent.putExtra("loanInfo", loanInfo);
                        startActivityForResult(intent, 99);
                    }
                });
            }
        };

        adapter0 = new CommonAdapter<CommonItem>(TaskListActivity.this, presenter.getTitles0(dayNot), R.layout.item_title) {
            @Override
            protected void convert(BaseViewHolder holder, CommonItem item, final int position) {
                holder.setText(R.id.tv_title, item.getName());
                if (item.isClick()) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home);
                    holder.setVisible(R.id.tv_line, true);
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6);
                    holder.setVisible(R.id.tv_line, false);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshTitle0(position);
                    }
                });
            }
        };

        adapter1 = new CommonAdapter<CommonItem>(TaskListActivity.this, presenter.getTitles(), R.layout.item_title) {
            @Override
            protected void convert(BaseViewHolder holder, CommonItem item, final int position) {
                holder.setText(R.id.tv_title, item.getName());
                if (item.isClick()) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home);
                    holder.setVisible(R.id.tv_line, true);
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6);
                    holder.setVisible(R.id.tv_line, false);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshTitle1(position);
                    }
                });
            }
        };
        adapter2 = new CommonAdapter<CommonItem>(TaskListActivity.this, new ArrayList<CommonItem>(), R.layout.item_title) {
            @Override
            protected void convert(BaseViewHolder holder, CommonItem item, final int position) {
                holder.setText(R.id.tv_title, item.getName());
                if (item.isClick()) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home);
                    holder.setVisible(R.id.tv_line, true);
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6);
                    holder.setVisible(R.id.tv_line, false);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshTitle2(position);
                    }
                });
            }
        };
    }

    private void refreshTitle0(int position) {
        dayNot = position;
        isPull = false;
        page = 0;
        for (int i = 0; i < adapter0.getDatas().size(); i++) {
            ((CommonItem) (adapter0.getDatas().get(i))).setClick(i == position);
        }
        adapter0.notifyDataSetChanged();
        presenter.getTaskData(page, searchType, loanType, schedule, dayNot);
    }

    private void refreshTitle1(int position) {
        loanType = position;
        isPull = false;
        page = 0;
        searchType = 0;
        schedule = 0;
        for (int i = 0; i < adapter1.getDatas().size(); i++) {
            ((CommonItem) (adapter1.getDatas().get(i))).setClick(i == position);
        }
        adapter1.notifyDataSetChanged();
        scrollTop();
        presenter.getTaskData(page, searchType, loanType, schedule, dayNot);
    }

    private void refreshTitle2(int position) {
        schedule = position;
        isPull = false;
        page = 0;
        searchType = 0;
        for (int i = 0; i < adapter2.getDatas().size(); i++) {
            ((CommonItem) (adapter2.getDatas().get(i))).setClick(i == position);
        }
        adapter2.notifyDataSetChanged();
        presenter.getTaskData(page, searchType, loanType, schedule, dayNot);
    }

    @Override
    public void refreshChange() {
        adapter.setNewData(loanInfos);
    }

    public void refreshTitleData(List<CommonItem> commonItems) {
        adapter2.setNewData(commonItems);
    }

    @Override
    protected void refreshData(int page, int searchType) {
        presenter.getTaskData(page, searchType, loanType, schedule, dayNot);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 99) {
            isPull = false;
            refreshData(0, searchType);
        }
    }

    @Override
    protected void onDestroy() {
        if (companySubscription != null && companySubscription.isUnsubscribed()) {
            companySubscription.unsubscribe();
        }
        super.onDestroy();
    }
}
