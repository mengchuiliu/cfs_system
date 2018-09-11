package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.adapters.AddPactAdapter;
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener;
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener;
import com.xxjr.cfs_system.LuDan.presenter.AddPactPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.AddPactVInter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import entity.ClientInfo;
import entity.CommonItem;
import entity.Contract;

public class AddPactActivity extends BaseActivity<AddPactPresenter, AddPactActivity> implements AddPactVInter {
    Contract contract;
    private boolean save = false, isrefresh = false;
    AddPactAdapter adapter;
    private String companyID = "";

    @Bind(R.id.recycle_add_pact)
    RecyclerView recycleAddPact;
    @Bind(R.id.water)
    FrameLayout water;

    @Override
    protected AddPactPresenter getPresenter() {
        return new AddPactPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_pact;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getToolbarTitle().setText("快速报单");
        TextView textView = getSubTitle();
        textView.setText("保存");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter.check(contract)) {
                    if (getIntent().getBooleanExtra("isUpdate", false)) {
                        presenter.getData(1, presenter.getContractParam("UpdateContractBriefInfo"), true);
                    } else {
                        presenter.getData(0, presenter.getContractParam("AddContractBriefInfo"), true);
                    }
                }
            }
        });

        contract = (Contract) getIntent().getSerializableExtra("contract");
        if (contract == null) {
            contract = new Contract();
        } else {
            save = true;
            companyID = contract.getCompanyID();
        }
        presenter.setDefaultValue();
    }

    @Override
    public void setCompanyID(String ids) {
        this.companyID = ids;
    }

    @Override
    public Contract getContract() {
        return contract;
    }

    @Override
    public void addPactOver() {
        Intent intent = new Intent(AddPactActivity.this, PactDetailsActivity.class);
        intent.putExtra("contract", contract);
        intent.putExtra("contractType", 0);
        startActivity(intent);
        AddPactActivity.this.finish();
    }

    @Override
    public void updateOver() {
        Intent intent = new Intent();
        intent.putExtra("contract", contract);
        setResult(33, intent);
        AddPactActivity.this.finish();
    }

    @Override
    public List getCustomerInfo() {
        return adapter.getDatas().get(adapter.getDatas().size() - 1).getList();
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    public void onBackPressed() {
        isSave(save, AddPactActivity.this);
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(AddPactActivity.this, msg);
    }

    @Override
    public void initReacycle(List<CommonItem> commonItems) {
        recycleAddPact.setLayoutManager(new LinearLayoutManager(AddPactActivity.this));
        adapter = new AddPactAdapter(AddPactActivity.this, commonItems);
        adapter.setChooseItemClickListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 2) {
                    presenter.showPop(recycleAddPact);
                } else if (position == 4 || position == 5 || position == 6) {
                    Intent intent = new Intent(AddPactActivity.this, SearchActivity.class);
                    switch (position) {
                        case 4:
                            intent.putExtra("type", Constants.MANAGER_CODE);
                            intent.putExtra("hintContent", "搜索客户经理");
                            break;
                        case 5:
                        case 6:
                            if (contract.getS3() == 0) {
                                showMsg("请先选择客户经理");
                                return;
                            } else if (TextUtils.isEmpty(companyID)) {
                                showMsg("客户经理门店为空");
                                return;
                            }
                            if (position == 5) {
                                intent.putExtra("type", Constants.Sign_Member);
                            } else {
                                intent.putExtra("type", Constants.Witness);
                            }
                            intent.putExtra("hintContent", "请填写搜索条件");
                            intent.putExtra("CompanyID", companyID);
                            break;
                    }
                    startActivity(intent);
                }
            }
        });

        adapter.setRecycleItemClickListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(AddPactActivity.this, AddClientActivity.class);
                CommonItem commonItem = adapter.getDatas().get(adapter.getDatas().size() - 1);
                List<ClientInfo> infos = commonItem.getList();
//                intent.putExtra("clientInfo", infos.get(position));
                intent.putExtra("infos", (Serializable) infos);
                intent.putExtra("position", position);
                startActivityForResult(intent, 6);
            }
        });
        adapter.setAddClientListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(AddPactActivity.this, AddClientActivity.class);
                CommonItem commonItem = adapter.getDatas().get(adapter.getDatas().size() - 1);
                List<ClientInfo> infos = commonItem.getList();
                intent.putExtra("infos", (Serializable) infos);
                startActivityForResult(intent, 5);
            }
        });
        adapter.setTextChangeListener(new TextChangeListener() {
            @Override
            public void setTextChage(int position, String text) {
                isrefresh = true;
                switch (position) {
                    case 7:
                        contract.setS7(text);
                        break;
                    case 8:
                        contract.setS6(text);
                        break;
                }
            }
        });
        recycleAddPact.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (pos >= 6) {
                    if (isrefresh) {
                        isrefresh = false;
                        CommonItem commonItem = adapter.getDatas().get(8);
                        commonItem.setContent(contract.getS6());
                        adapter.notifyItemChanged(6, commonItem);
                    }
                } else if (pos >= 5) {
                    if (isrefresh) {
                        CommonItem commonItem = adapter.getDatas().get(7);
                        commonItem.setContent(contract.getS7());
                        adapter.notifyItemChanged(5, commonItem);
                    }
                }
            }
        });
        recycleAddPact.setAdapter(adapter);
    }

    @Override
    public void refreshData(int position, String content) {
        CommonItem item = adapter.getDatas().get(position);
        item.setContent(content);
        adapter.notifyItemChanged(position, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 6) {
            List<ClientInfo> infos;
            ClientInfo clientInfo = (ClientInfo) data.getSerializableExtra("addClient");
            CommonItem commonItem = adapter.getDatas().get(adapter.getDatas().size() - 1);
            switch (requestCode) {
                case 5:
                    if (commonItem.getList() == null) {
                        infos = new ArrayList<>();
                    } else {
                        infos = commonItem.getList();
                    }
                    infos.add(clientInfo);
                    commonItem.setList(infos);
                    adapter.notifyItemChanged(adapter.getDatas().size() - 1, commonItem);
                    break;
                case 6:
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        infos = commonItem.getList();
                        infos.set(position, clientInfo);
                        commonItem.setList(infos);
                    }
                    adapter.notifyItemChanged(adapter.getDatas().size() - 1, commonItem);
                    break;
            }
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
}
