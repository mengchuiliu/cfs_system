package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xiaoxiao.widgets.CustomDialog;
import com.xiaoxiao.widgets.SwipeMenuLayout;
import com.xxjr.cfs_system.LuDan.presenter.ContractPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.ContractVInter;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;

import java.util.ArrayList;
import java.util.List;

import entity.ChooseType;
import entity.ClientInfo;
import entity.Contract;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;
import rx.Subscription;
import rx.functions.Action1;

public class ContractListActivity extends BaseListActivity<ContractPresenter, ContractListActivity> implements ContractVInter {
    private Subscription companySubscription;//门店
    private List<Contract> contracts = new ArrayList<>();
    protected int delPosition = -1;

    @Override
    protected ContractPresenter getListPresenter() {
        return new ContractPresenter();
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
        final JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("ContractStatus"), ""));
        adapter = new CommonAdapter<Contract>(ContractListActivity.this, new ArrayList<Contract>(), R.layout.item_contract) {
            @Override
            protected void convert(BaseViewHolder holder, final Contract contract, final int position) {
                final SwipeMenuLayout swipeMenuLayout = ((SwipeMenuLayout) holder.getConvertView()).setIos(true).setLeftSwipe(true);
                if (Hawk.get("UserType", "").equals("99")) {
                    swipeMenuLayout.setSwipeEnable(true);
                } else {
                    swipeMenuLayout.setSwipeEnable(false);
                }
                holder.setText(R.id.tv_pact_numb, contract.getS5());
                holder.setText(R.id.tv_salesman, contract.getClerkName() == null ? "" : contract.getClerkName());
                holder.setText(R.id.tv_customer, getCustomerName(contract));
                if (!TextUtils.isEmpty(contract.getS1())) {
                    if (contract.getS1().contains("T")) {
                        holder.setText(R.id.tv_date, contract.getS1().substring(0, contract.getS1().indexOf("T")));
                    } else {
                        holder.setText(R.id.tv_date, contract.getS1());
                    }
                }
                holder.setText(R.id.tv_pact_type, contract.getS6());
                String ids = contract.getLoanIDs();
                String string = "0笔";
                if (!TextUtils.isEmpty(ids)) {
                    String[] strings = ids.split(",");
                    string = strings.length + "笔";
                }
                holder.setText(R.id.tv_loan_nub, string);
                holder.setText(R.id.tv_contract_status, CacheProvide.getPactStatus(jsonArray, contract.getS12()));
                holder.setOnClickListener(R.id.ll_home, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ContractListActivity.this, PactDetailsActivity.class);
                        intent.putExtra("contract", contract);
                        intent.putExtra("contractType", getListType());
                        startActivityForResult(intent, 99);
                    }
                });

                holder.setOnClickListener(R.id.tv_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        swipeMenuLayout.smoothClose();
                        CustomDialog.showTwoButtonDialog(ContractListActivity.this, "确定删除此合同？", "确定", "取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        delPosition = position;
                                        presenter.delContract(contract.getID());
                                    }
                                });

                    }
                });
            }
        };
    }

    @Override
    protected void refreshData(int page, int searchType) {
        presenter.refreshData(page, searchType);
    }

    @Override
    public List<Contract> getContracts() {
        return contracts;
    }

    @Override
    public void refreshChange() {
        adapter.setNewData(contracts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void delData() {
        if (delPosition != -1) {
            contracts.remove(delPosition);
            adapter.notifyItemRemoved(delPosition);
            if (delPosition != contracts.size()) {
                adapter.notifyItemRangeChanged(delPosition, contracts.size() - delPosition);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == 99) {
            isPull = false;
            refreshData(0, searchType);
        } else if (resultCode == 909) {
            this.finish();
        }
    }

    //获取客户名字
    private String getCustomerName(Contract contract) {
        String name;
        StringBuilder builder = new StringBuilder();
        try {
            if (!TextUtils.isEmpty(contract.getCustomerInfo())) {
                List<ClientInfo> infos = JSON.parseArray(contract.getCustomerInfo(), ClientInfo.class);
                for (ClientInfo clientInfo : infos) {
                    builder.append(clientInfo.getName()).append(" ");
                }
            }
        } catch (Exception e) {
            Log.e("my_log", "===json异常====>" + Log.getStackTraceString(e));
        }
        if (TextUtils.isEmpty(builder.toString())) {
            name = contract.getCustomerNames();
        } else {
            name = builder.toString();
        }
        if (!TextUtils.isEmpty(name)) {
            if (name.contains(",")) {
                name = name.replace(",", " ");
            }
        }
        return name;
    }

    @Override
    protected void onDestroy() {
        if (companySubscription != null && companySubscription.isUnsubscribed()) {
            companySubscription.unsubscribe();
        }
        super.onDestroy();
    }
}
