package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xiaoxiao.widgets.SwipeMenuLayout;
import com.xxjr.cfs_system.LuDan.presenter.LoanPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.LoanVInter;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;
import com.xxjr.cfs_system.tools.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import entity.ChooseType;
import entity.CommonItem;
import entity.LoanInfo;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;
import rx.Subscription;
import rx.functions.Action1;

public class LoanListActivity extends BaseListActivity<LoanPresenter, LoanListActivity> implements LoanVInter {
    private Subscription companySubscription;//门店
    private int type = 0;//0->贷款列表  1->失信贷款
    private List<LoanInfo> loanInfos = new ArrayList<>();//贷款列表

    @Override
    protected LoanPresenter getListPresenter() {
        return new LoanPresenter();
    }

    @Override
    public List<LoanInfo> getLoanInfos() {
        return loanInfos;
    }

    public int getType() {
        return type;
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

        type = getIntent().getIntExtra("Type", 0);
        presenter.setEvaluation(getIntent().getBooleanExtra("isEvaluation", false));
        adapter = new CommonAdapter<LoanInfo>(LoanListActivity.this, new ArrayList<LoanInfo>(), R.layout.item_loan) {
            @Override
            protected void convert(BaseViewHolder holder, final LoanInfo loanInfo, int position) {
                SwipeMenuLayout swipeMenuLayout = ((SwipeMenuLayout) holder.getConvertView()).setIos(false).setLeftSwipe(true);
                swipeMenuLayout.setSwipeEnable(false);
                holder.setText(R.id.tv_loan_numb, loanInfo.getLoanCode());
                holder.setText(R.id.tv_date, loanInfo.getUpdateTime().contains("T") ?
                        loanInfo.getUpdateTime().substring(0, loanInfo.getUpdateTime().indexOf("T")) : loanInfo.getUpdateTime());
                holder.setText(R.id.tv_customer, loanInfo.getCustomer());
                String description = "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() +
                        "(" + (TextUtils.isEmpty(loanInfo.getLoanTypeName()) ? "" : loanInfo.getLoanTypeName().substring(0, 1)) + ")" + "】" +
                        "申请: " + Utils.div(loanInfo.getAmount()) + "万";
                holder.setText(R.id.tv_loan_content, description);
                holder.setText(R.id.tv_cost, Utils.parseMoney(new BigDecimal(loanInfo.getPassAuditCostMoney())) + "元");
                holder.setText(R.id.tv_schedule, loanInfo.getSchedule());
                holder.setVisible(R.id.ll_is_case, true);
                holder.setText(R.id.tv_score, "服务评分：" + getScore((int) loanInfo.getMortgageScore()));
                holder.setOnClickListener(R.id.ll_home, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LoanListActivity.this, LoanDetailsActivity.class);
                        intent.putExtra("loanInfo", loanInfo);
                        intent.putExtra("contractType", contractType);
                        startActivity(intent);
                    }
                });
            }
        };

        adapter0 = new CommonAdapter<CommonItem>(LoanListActivity.this, presenter.getLoanTitles(type), R.layout.item_title) {
            @Override
            protected void convert(BaseViewHolder holder, CommonItem item, final int position) {
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) holder.getConvertView().getLayoutParams();
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                holder.getConvertView().setLayoutParams(params);
                holder.setText(R.id.tv_title, item.getName());
                holder.setTextSize(R.id.tv_title, 15f);
                TextView textview = holder.getView(R.id.tv_title);
                textview.setPadding(Utils.dip2px(LoanListActivity.this, 15f),
                        Utils.dip2px(LoanListActivity.this, 10f),
                        Utils.dip2px(LoanListActivity.this, 15f),
                        Utils.dip2px(LoanListActivity.this, 10f));
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
    }

    private void refreshTitle0(int position) {
        type = position;
        isPull = false;
        page = 0;
        for (int i = 0; i < adapter0.getDatas().size(); i++) {
            ((CommonItem) (adapter0.getDatas().get(i))).setClick(i == position);
        }
        adapter0.notifyDataSetChanged();
        presenter.loanRefresh(page, searchType, type);
    }

    @Override
    public void refreshChange() {
        adapter.setNewData(loanInfos);
    }

    @Override
    protected void refreshData(int page, int searchType) {
        presenter.loanRefresh(page, searchType, type);
    }

    private String getScore(int mortgageScore) {
        String score = "未评分";
        switch (mortgageScore) {
            case 1:
                score = "★";
                break;
            case 2:
                score = "★★";
                break;
            case 3:
                score = "★★★";
                break;
            case 4:
                score = "★★★★";
                break;
            case 5:
                score = "★★★★★";
                break;
        }
        return score;
    }

    @Override
    protected void onDestroy() {
        if (companySubscription != null && companySubscription.isUnsubscribed()) {
            companySubscription.unsubscribe();
        }
        super.onDestroy();
    }
}
