package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener;
import com.xxjr.cfs_system.LuDan.adapters.TaskDetailsAdapter;
import com.xxjr.cfs_system.LuDan.presenter.TaskDetailsPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.TaskDetailsVInter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.BitmapManage;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import entity.CommonItem;
import entity.LoanInfo;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;

public class TaskDetailsActivity extends BaseActivity<TaskDetailsPresenter, TaskDetailsVInter> implements TaskDetailsVInter {
    private LoanInfo loanInfo;
    private PopupWindow popWindow;
    private TaskDetailsAdapter adapter;
    private boolean isUpdateLoan = false;
    private List<CommonItem> items = new ArrayList<>();

    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.recycler_task)
    RecyclerView recyclerTask;
    @Bind(R.id.water)
    FrameLayout water;


    @Override
    protected TaskDetailsPresenter getPresenter() {
        return new TaskDetailsPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_task_details;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        loanInfo = (LoanInfo) getIntent().getSerializableExtra("loanInfo");
        getToolbarTitle().setText("贷款详情");
        presenter.setDefaultValue();
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWater(water);
    }

    @Override
    public void onBackPressed() {
        setResult(99);
        TaskDetailsActivity.this.finish();
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(getApplicationContext(), msg);
    }

    @Override
    public LoanInfo getLoanInfo() {
        return loanInfo == null ? new LoanInfo() : loanInfo;
    }

    @Override
    public void initRecycler(List<CommonItem> commonItems) {
        recyclerTask.setLayoutManager(new LinearLayoutManager(TaskDetailsActivity.this));
        adapter = new TaskDetailsAdapter(TaskDetailsActivity.this, commonItems);
        adapter.setItemClickListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showPop(ivAdd, true);
            }
        });
        recyclerTask.setAdapter(adapter);
    }

    @Override
    public void refreshData(List<CommonItem> commonItems) {
        adapter.setNewData(commonItems);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setIvShow(List<CommonItem> commonItems) {
        items.clear();
        items.addAll(commonItems);
        if (items != null && items.size() > 0) {
            ivAdd.setVisibility(View.VISIBLE);
        } else {
            ivAdd.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean getisUpdateLoan() {
        return isUpdateLoan;
    }

    @Override
    public void showPop(View parent, boolean yellow) {
        LayoutInflater layoutInflater = LayoutInflater.from(TaskDetailsActivity.this);
        View view = layoutInflater.inflate(R.layout.item_schedule_btn, null);
        popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindow.setFocusable(true);
        screenshot(view);//高斯模糊背景
        LinearLayout llSchedule = (LinearLayout) view.findViewById(R.id.ll_schedule);
        LinearLayout llYellow = (LinearLayout) view.findViewById(R.id.ll_yellow);
        TextView confirm = (TextView) view.findViewById(R.id.tv_confirm_cost);
        view.findViewById(R.id.tv_customer_cancel).setOnClickListener(onClickListener);
        view.findViewById(R.id.tv_refuse).setOnClickListener(onClickListener);
        view.findViewById(R.id.iv_closed).setOnClickListener(onClickListener);
        if (loanInfo.getScheduleId() == 108 || loanInfo.getScheduleId() == 4) {
            confirm.setVisibility(View.VISIBLE);
        } else {
            confirm.setVisibility(View.GONE);
        }
        if (yellow) {
            llYellow.setVisibility(View.VISIBLE);
            llSchedule.setVisibility(View.GONE);
        } else {
            llYellow.setVisibility(View.GONE);
            llSchedule.setVisibility(View.VISIBLE);
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycle_schedule_bt);
        recyclerView.setLayoutManager(new GridLayoutManager(TaskDetailsActivity.this, 3));
        CommonAdapter commonAdapter = new CommonAdapter<CommonItem>(TaskDetailsActivity.this, items, R.layout.item_schedule_bt) {
            @Override
            protected void convert(BaseViewHolder holder, final CommonItem commonItem, int position) {
                if (TextUtils.isEmpty(commonItem.getContent())) {
                    holder.setVisible(R.id.tv_bt_content, false);
                }
                holder.setText(R.id.tv_bt_content, commonItem.getContent());
                holder.setBackgroundRes(R.id.tv_bt_content, commonItem.getIcon());
                holder.setOnClickListener(R.id.tv_bt_content, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hidePop();
                        scheduleClick(commonItem.getPosition());
                    }
                });
            }
        };
        recyclerView.setAdapter(commonAdapter);
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    @Override
    public void hidePop() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    @OnClick(R.id.iv_add)
    public void onViewClicked() {
        showPop(ivAdd, false);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hidePop();
            switch (view.getId()) {
                case R.id.tv_customer_cancel:
                    Intent intent5 = new Intent(TaskDetailsActivity.this, RemarkActivity.class);
                    intent5.putExtra("loanInfo", loanInfo);
                    intent5.putExtra("remarkType", 0);
                    intent5.putExtra("yellowSchedule", -3);
                    startActivityForResult(intent5, 44);
                    break;
                case R.id.tv_refuse:
                    Intent intent6 = new Intent(TaskDetailsActivity.this, RemarkActivity.class);
                    intent6.putExtra("loanInfo", loanInfo);
                    intent6.putExtra("remarkType", 0);
                    intent6.putExtra("yellowSchedule", -4);
                    startActivityForResult(intent6, 44);
                    break;
                case R.id.iv_closed:
                    break;
            }
        }
    };

    //进度点击事件
    private void scheduleClick(int pos) {
        switch (pos) {
            case 0:
                if (loanInfo.getLoanType() == 1) {
                    if (TextUtils.isEmpty(loanInfo.getForeclosureTime())) {
                        if (loanInfo.getScheduleId() == 103) {
                            showMsg("请跟进赎楼信息后再行操作!");
                            return;
                        }
                    } else {
                        if (loanInfo.getForeclosureTime().startsWith("1900")) {
                            if (loanInfo.getScheduleId() == 103) {
                                showMsg("请跟进赎楼信息后再行操作!");
                                return;
                            }
                        }
                    }
                    if (loanInfo.getIsForeclosureFloor()) {
                        if (loanInfo.getScheduleId() == 105) {
                            if (!loanInfo.isPrepayment()) {
                                showMsg("请先提前还款后再行操作!");
                                return;
                            }
                        }
                    }
                }
                Intent intent;
                if (loanInfo.getScheduleId() == 109 || loanInfo.getScheduleId() == 5 || loanInfo.getScheduleId() == -3 || loanInfo.getScheduleId() == -4 || loanInfo.getScheduleId() == -5) {
                    intent = new Intent(TaskDetailsActivity.this, OverApplyActivity.class);
                } else if (loanInfo.getScheduleId() == 4 || loanInfo.getScheduleId() == 108) {
                    intent = new Intent(TaskDetailsActivity.this, LendScheduleActivity.class);
                } else {
                    intent = new Intent(TaskDetailsActivity.this, UpdateScheduleActivity.class);
                }
                intent.putExtra("loanInfo", loanInfo);
                startActivityForResult(intent, 66);
                break;
            case 1://备注
                Intent intent2 = new Intent(TaskDetailsActivity.this, RemarkActivity.class);
                intent2.putExtra("loanInfo", loanInfo);
                intent2.putExtra("remarkType", 1);
                startActivityForResult(intent2, 55);
                break;
            case 2://成本
                Intent intent3 = new Intent(TaskDetailsActivity.this, CostDetailsActivity.class);
                intent3.putExtra("loanInfo", loanInfo);
                startActivity(intent3);
                break;
            case 3://提前还款
                Intent intent4 = new Intent(TaskDetailsActivity.this, RemarkActivity.class);
                intent4.putExtra("loanInfo", loanInfo);
                intent4.putExtra("remarkType", 2);
                startActivityForResult(intent4, 77);
                break;
            case 4://公证
                Intent intent1 = new Intent(TaskDetailsActivity.this, LoanDetailsActivity.class);
                intent1.putExtra("contractType", 3);
                intent1.putExtra("loanInfo", loanInfo);
                intent1.putExtra("IsNotary", true);
                startActivityForResult(intent1, 33);
                break;
            case 5://赎楼
                Intent intent5 = new Intent(TaskDetailsActivity.this, LoanDetailsActivity.class);
                intent5.putExtra("contractType", 3);
                intent5.putExtra("loanInfo", loanInfo);
                intent5.putExtra("IsRedeem", true);
                startActivityForResult(intent5, 333);
                break;
            case 6://入账
                Intent intent6 = new Intent(TaskDetailsActivity.this, BooksActivity.class);
                intent6.putExtra("loanInfo", loanInfo);
                intent6.putExtra("type", 1);
                startActivity(intent6);
                break;
            case 7://出账
                Intent intent7 = new Intent(TaskDetailsActivity.this, BooksActivity.class);
                intent7.putExtra("loanInfo", loanInfo);
                intent7.putExtra("type", 2);
                startActivity(intent7);
                break;
            case 8://修改贷款信息
                Intent intent8 = new Intent(TaskDetailsActivity.this, AddLoanActivity.class);
                intent8.putExtra("loanInfo", loanInfo);
                intent8.putExtra("pactID", loanInfo.getContractId());
                intent8.putExtra("pactCode", loanInfo.getPactCode());
                intent8.putExtra("managerId", loanInfo.getClerkID());
                intent8.putExtra("CompanyID", loanInfo.getCompanyID());
                //id要返回一起添加
                intent8.putExtra("loanId", loanInfo.getLoanIDs());
                intent8.putExtra("content", loanInfo.getLoanDescription());
                intent8.putExtra("scheduleUp", true);
                startActivityForResult(intent8, 111);
                break;
        }
    }

    //截屏模糊背景，体现毛玻璃高斯模糊效果
    private void screenshot(View view) {
        // 获取屏幕
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap sentBitmap = dView.getDrawingCache();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(sentBitmap, sentBitmap.getWidth() / 20, sentBitmap.getHeight() / 20, false);
        Bitmap blurBitmap = BitmapManage.doBlur(scaledBitmap, 8);
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), blurBitmap));
        sentBitmap.recycle();
        scaledBitmap.recycle();
        dView.destroyDrawingCache();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.REQUEST_CODE_REDEEM_AND_NOTARY:
            case Constants.REQUEST_CODE_YELLOW:
            case Constants.REQUEST_CODE_REMARK:
            case Constants.REQUEST_CODE_PREPAYMENT:
            case Constants.REQUEST_CODE_UPDATE_SCHEDULE:
                isUpdateLoan = true;
                if (requestCode == 33) {
                    loanInfo.setIsNotary("1");
                }
                presenter.getScheduleData();
                break;
            case Constants.REQUEST_CODE_BACK:
            case 111:
                onBackPressed();
                break;
        }
    }
}
