package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.widgets.CustomDialog;
import com.xxjr.cfs_system.LuDan.adapters.LoanDetailsAdapter;
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener;
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener;
import com.xxjr.cfs_system.LuDan.presenter.LoanDetailsPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.LoanDetailsVInter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.CFSUtils;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import entity.CommonItem;
import entity.LoanInfo;
import entity.Schedule;
import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;

public class LoanDetailsActivity extends BaseActivity<LoanDetailsPresenter, LoanDetailsVInter> implements LoanDetailsVInter, OptionMenuView.OnOptionMenuClickListener {
    private LoanInfo loanInfo;
    private LoanDetailsAdapter adapter;
    private PopupMenuView mPopupMenuView;
    List<String> permits;
    List<String> starPermits;

    @Bind(R.id.recycler_pact)
    RecyclerView recyclerPact;
    @Bind(R.id.tv_update)
    TextView tvUpdate;
    @Bind(R.id.water)
    FrameLayout water;

    @Override
    protected LoanDetailsPresenter getPresenter() {
        return new LoanDetailsPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pact_details;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        int contractType = getIntent().getIntExtra("contractType", 0);
        loanInfo = (LoanInfo) getIntent().getSerializableExtra("loanInfo");
        permits = CFSUtils.getPermitValue(Hawk.get("PermitValue", ""), loanInfo.getLoanType());
        starPermits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "804");
        presenter.setDefaultValue();
        if (getIsNotary()) {
            getToolbarTitle().setText("公证");
            tvUpdate.setText("公证");
        } else if (getIsRedeem()) {
            getToolbarTitle().setText("赎楼");
        } else {
            if (contractType == 2) {
                getToolbarTitle().setText("贷款详情");
                initPop();
            } else {
                getToolbarTitle().setText("更新进度");
            }
        }
    }

    private void initPop() {
        final ImageView imageView = getIvRight();
        imageView.setImageResource(R.mipmap.icon_add);
        imageView.setVisibility(View.GONE);
        tvUpdate.setVisibility(View.GONE);
        presenter.getLendDatas(loanInfo.getLoanId());//获取放款列表

        mPopupMenuView = new PopupMenuView(this);
        mPopupMenuView.setOnMenuClickListener(this);
        List<OptionMenu> menus = new ArrayList<>();
        if (permits != null && permits.contains("E2")) {
            if ((loanInfo.getScheduleId() > 4 && loanInfo.getScheduleId() < 100) || loanInfo.getScheduleId() > 108) {
                imageView.setVisibility(View.VISIBLE);
                OptionMenu menu1 = new OptionMenu();
                menu1.setId(1);
                menu1.setTitle("新增放款");
                menu1.setDrawableLeft(getResources().getDrawable(R.mipmap.icon_chuzhang));
                menus.add(menu1);
            }
        }
        if (starPermits != null && starPermits.contains("E9") && ((loanInfo.getScheduleId() == 5) || loanInfo.getScheduleId() == 109)) {
            imageView.setVisibility(View.VISIBLE);
            OptionMenu menu2 = new OptionMenu();
            menu2.setId(2);
            menu2.setTitle("   评  分   ");
            menu2.setDrawableLeft(getResources().getDrawable(R.mipmap.rating_icon));
            menus.add(menu2);
        }
        int pos = loanInfo.getScheduleId();
        if ((pos > 0 && pos <= 5) || (pos > 100 && pos <= 109) || pos == -3 || pos == -4) {
            if (permits != null && permits.contains("CS")) {
                imageView.setVisibility(View.VISIBLE);
                OptionMenu menu2 = new OptionMenu();
                menu2.setId(3);
                menu2.setTitle("   成  本   ");
                menu2.setDrawableLeft(getResources().getDrawable(R.mipmap.cost));
                menus.add(menu2);
            }
        }
        mPopupMenuView.setMenuItems(menus);
        mPopupMenuView.setOrientation(LinearLayout.VERTICAL);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupMenuView.show(imageView);
            }
        });
    }

    @Override
    public boolean onOptionMenuClick(int position, OptionMenu menu) {
        switch (menu.getId()) {
            case 1://新增放款
                Intent intent = new Intent(LoanDetailsActivity.this, ManyLendActivity.class);
                intent.putExtra("loanId", loanInfo.getLoanId());
                intent.putExtra("loanCode", loanInfo.getLoanCode());
                startActivityForResult(intent, 88);
                break;
            case 2://按揭员评分
                Intent intent1 = new Intent(LoanDetailsActivity.this, RatingActivity.class);
                intent1.putExtra("loanInfo", loanInfo);
                startActivity(intent1);
                break;
            case 3:
                Intent intent2 = new Intent(LoanDetailsActivity.this, CostDetailsActivity.class);
                intent2.putExtra("loanInfo", loanInfo);
                startActivity(intent2);
                break;
        }
        return true;
    }

    @Override
    protected boolean isShowBacking() {
        return true;
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
        recyclerPact.setLayoutManager(new LinearLayoutManager(LoanDetailsActivity.this));
        adapter = new LoanDetailsAdapter(LoanDetailsActivity.this, commonItems);
        adapter.setItemClickListener1(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                presenter.getScheduleData(position, loanInfo.getLoanId());
            }
        });

        adapter.setItemClickListener2(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                refreshSchedule(position, new ArrayList<Schedule>(), false);
            }
        });

        adapter.setItemClickListener3(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(LoanDetailsActivity.this, SearchActivity.class);
                intent.putExtra("type", Constants.REDEEM_CODE);
                intent.putExtra("hintContent", "搜索赎楼员");
                startActivity(intent);
            }
        });
        adapter.setItemClickListener4(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == -3) {//赎楼
                    loanInfo.setIsForeclosureFloor(true);
                } else {//不赎楼
                    loanInfo.setMember(0);
                    loanInfo.setIsForeclosureFloor(false);
                }
            }
        });
        adapter.setItemClickListener5(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                presenter.showTime(position);
            }
        });

        adapter.setItemDelLendListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                CustomDialog.showTwoButtonDialog(LoanDetailsActivity.this, "确定删除此放款记录？",
                        "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                presenter.delLend(position);
                            }
                        });
            }
        });
        adapter.setTextChangeListener(new TextChangeListener() {
            @Override
            public void setTextChage(int position, String text) {
                loanInfo.setNotaryNote(text);
            }
        });
        recyclerPact.setAdapter(adapter);
    }

    @Override
    public void refreshData(List<CommonItem> commonItems) {
        adapter.setNewData(commonItems);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshTime(int pos, String time) {
        CommonItem commonItem = adapter.getDatas().get(pos);
        commonItem.setContent(time);
        adapter.notifyItemChanged(pos, commonItem);
    }

    @Override
    public void refreshItem(List<LoanInfo> loanInfos) {
        CommonItem commonItem = adapter.getDatas().get(adapter.getDatas().size() - 1);
        commonItem.setList(loanInfos);
        adapter.notifyItemChanged(adapter.getDatas().size() - 1, commonItem);
    }

    @Override
    public void refreshSchedule(int pos, List<Schedule> schedules, boolean ishow) {
        CommonItem commonItem = adapter.getDatas().get(pos);
        commonItem.setClick(ishow);
        CommonItem commonItem1 = adapter.getDatas().get(pos + 1);
        commonItem1.setList(schedules);
        commonItem1.setClick(ishow);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean getIsNotary() {
        return getIntent().getBooleanExtra("IsNotary", false);
    }

    @Override
    public boolean getIsRedeem() {
        return getIntent().getBooleanExtra("IsRedeem", false);
    }

    @Override
    public void complete() {
        showMsg("更新成功!");
        setResult(Constants.REQUEST_CODE_REDEEM_AND_NOTARY);
        LoanDetailsActivity.this.finish();
    }

    @Override
    public void submitComplete() {
        showMsg("提交成功!");
        setResult(99);
        LoanDetailsActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 99) {
            submitComplete();
        } else if (resultCode == 88) {
            presenter.getLendDatas(loanInfo.getLoanId());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWater(water);
    }

    @Override
    protected void onDestroy() {
        presenter.rxDeAttach();
        super.onDestroy();
    }

    @OnClick(R.id.tv_update)
    public void onViewClicked() {
        if (getIsNotary() || getIsRedeem()) {
            presenter.postData(loanInfo);
        } else {
            if (loanInfo.getScheduleId() == 0) {
                if (loanInfo.getMortgage() == 0) {//选择按揭员
                    Intent intent = new Intent(LoanDetailsActivity.this, SearchActivity.class);
                    intent.putExtra("type", Constants.MORTGAGE_CODE);
                    intent.putExtra("hintContent", "搜索按揭员");
                    intent.putExtra("isSubmit", true);
                    intent.putExtra("loanInfo", loanInfo);
                    startActivityForResult(intent, 99);
                } else {//提交按揭员
                    CustomDialog.showTwoButtonDialog(LoanDetailsActivity.this, "确定提交按揭员，更新至下一进度?", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            presenter.submitMortgage(loanInfo.getLoanId(), String.valueOf(loanInfo.getMortgage()));
                        }
                    });
                }
            }
        }
    }
}
