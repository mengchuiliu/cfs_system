package com.xxjr.cfs_system.LuDan.view.activitys;

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
import com.xxjr.cfs_system.LuDan.adapters.PactDetailsAdapter;
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener;
import com.xxjr.cfs_system.LuDan.presenter.PactDetailsPresenter;
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.view.BorrowActivity;
import com.xxjr.cfs_system.LuDan.view.viewinter.PactDetailsVInter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.CFSUtils;
import com.xxjr.cfs_system.tools.ToastShow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import entity.CommonItem;
import entity.Contract;
import entity.LoanInfo;
import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;

public class PactDetailsActivity extends BaseActivity<PactDetailsPresenter, PactDetailsActivity> implements PactDetailsVInter, OptionMenuView.OnOptionMenuClickListener {
    private Contract contract;
    PactDetailsAdapter adapter;
    private String ids, loanDescription;
    private PopupMenuView mPopupMenuView;
    List<String> permits;

    @Bind(R.id.recycler_pact)
    RecyclerView recyclerPact;
    @Bind(R.id.tv_update)
    TextView tvUpdate;
    @Bind(R.id.water)
    FrameLayout water;

    @Override
    protected PactDetailsPresenter getPresenter() {
        return new PactDetailsPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pact_details;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        permits = CFSUtils.getPermitValue(Hawk.get("PermitValue", ""), 1);
        contract = (Contract) getIntent().getSerializableExtra("contract");
        ids = contract.getLoanIDs();
        loanDescription = contract.getLoanDescription();
        tvUpdate.setVisibility(View.GONE);
        if (getIntent().getIntExtra("contractType", 0) == 0) {
            getToolbarTitle().setText("报单详情");
        } else {
            getToolbarTitle().setText("合同详情");
        }
        initPop();
        final ImageView imageView = getIvRight();
        if (contract != null) {
            if (contract.getS12() != 5) {
                imageView.setImageResource(R.mipmap.icon_add);
            }
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contract != null) {
                    if (contract.getS12() != 5) {
                        mPopupMenuView.show(imageView);
                    }
                } else {
                    showMsg("数据加载失败!");
                }
            }
        });
        presenter.setDefaultValue();
    }

    private void initPop() {
        mPopupMenuView = new PopupMenuView(this);
        mPopupMenuView.setOnMenuClickListener(this);
        List<OptionMenu> menus = new ArrayList<>();
        OptionMenu menu1 = new OptionMenu();
        menu1.setId(1);
        menu1.setTitle("添加贷款");
        menu1.setDrawableLeft(getResources().getDrawable(R.mipmap.add_loan));
        menus.add(menu1);
        if (permits != null && permits.contains("E1")) {
            OptionMenu menu2 = new OptionMenu();
            menu2.setId(2);
            menu2.setTitle("申请拆借");
            menu2.setDrawableLeft(getResources().getDrawable(R.mipmap.borrow_money));
            menus.add(menu2);
        }
        mPopupMenuView.setMenuItems(menus);
        mPopupMenuView.setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        presenter.getPactDataNum();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWater(water);
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(99);
        PactDetailsActivity.this.finish();
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(PactDetailsActivity.this, msg);
    }

    @Override
    public Contract getContract() {
        return contract;
    }

    @Override
    public void initRecycler(List<CommonItem> commonItems) {
        recyclerPact.setLayoutManager(new LinearLayoutManager(PactDetailsActivity.this));
        adapter = new PactDetailsAdapter(PactDetailsActivity.this, commonItems);
        adapter.setPactItemClickListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 1) {
                    Intent intent = new Intent(PactDetailsActivity.this, AddPactActivity.class);
                    intent.putExtra("isUpdate", true);
                    intent.putExtra("contract", contract);
                    startActivityForResult(intent, 33);
                } else if (position == 15) {
                    Intent intent = new Intent(PactDetailsActivity.this, PactDataActivity.class);
                    intent.putExtra("contractId", contract.getID());
                    intent.putExtra("CompanyID", contract.getCompanyID());
                    intent.putExtra("CustomerInfo", (Serializable) presenter.getInfos());
                    startActivity(intent);
                }
            }
        });

        adapter.setLoanItemClickListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //编辑贷款
                Intent intent = new Intent(PactDetailsActivity.this, AddLoanActivity.class);
                LoanInfo loanInfo = (LoanInfo) adapter.getDatas().get(adapter.getDatas().size() - 1).getList().get(position);
                intent.putExtra("pactID", contract.getID());
                intent.putExtra("pactCode", contract.getS5());
                intent.putExtra("managerId", contract.getS3());
                intent.putExtra("CompanyID", contract.getCompanyID());
                //id要返回一起添加
                intent.putExtra("loanId", ids);
                intent.putExtra("content", loanDescription);
                intent.putExtra("CustomerInfo", (Serializable) presenter.getInfos());
                intent.putExtra("loanInfo", loanInfo);
                startActivityForResult(intent, 8);
            }
        });
        recyclerPact.setAdapter(adapter);
    }

    @Override
    public void refreshData(int pos, List<LoanInfo> loanInfos) {
        CommonItem item = adapter.getDatas().get(pos);
        item.setList(loanInfos);
        adapter.notifyItemChanged(pos, item);
    }

    @Override
    public void refreshReadData(int pos, String num) {
        CommonItem item = adapter.getDatas().get(pos);
        item.setContent(num);
        adapter.notifyItemChanged(pos, item);
    }

    @Override
    public void setIDs(String ids) {
        this.ids = ids;
    }

    @Override
    public void setLoanDescription(String loanDescription) {
        this.loanDescription = loanDescription;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 33) {
            contract = (Contract) data.getSerializableExtra("contract");
            presenter.setDefaultValue();
        } else if (resultCode == 66) {
            presenter.postAndRefreshData();
        } else if (resultCode == 88) {
            presenter.postAndRefreshData();
        } else if (resultCode == 909) {
            setResult(909);
            PactDetailsActivity.this.finish();
        }
    }

    @Override
    public boolean onOptionMenuClick(int position, OptionMenu menu) {
        switch (menu.getId()) {
            case 1://添加贷款
                Intent intent = new Intent(PactDetailsActivity.this, AddLoanActivity.class);
                intent.putExtra("pactID", contract.getID());
                intent.putExtra("pactCode", contract.getS5());
                intent.putExtra("managerId", contract.getS3());
                intent.putExtra("CompanyID", contract.getCompanyID());
                intent.putExtra("loanId", ids);
                intent.putExtra("content", loanDescription);
                intent.putExtra("CustomerInfo", (Serializable) presenter.getInfos());
                startActivityForResult(intent, 6);
                break;
            case 2://申请拆借
                Intent intent1 = new Intent(PactDetailsActivity.this, BorrowActivity.class);
                intent1.putExtra("contractId", contract.getID());
                startActivityForResult(intent1, 9);
                break;
        }
        return true;
    }
}
