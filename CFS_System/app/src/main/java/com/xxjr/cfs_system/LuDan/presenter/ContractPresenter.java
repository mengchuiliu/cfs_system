package com.xxjr.cfs_system.LuDan.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.ContractVInter;

import java.util.ArrayList;
import java.util.List;

import entity.Contract;


/**
 * Created by Administrator on 2017/9/5.
 * 合同列表执行
 */

public class ContractPresenter extends BasePresenter<ContractVInter, ModelImp> {
    private int homePage = 0;

    @Override
    protected ModelImp getModel() {
        return new ModelImp();
    }

    @Override
    public void setDefaultValue() {
        refreshData(0, 0);
    }

    public void refreshData(int page, int searchType) {
        if (isViewAttached()) {
            homePage = page;
            getData(0, model.getParam(getParamList(page, searchType), "GetContractList"), true);
        }
    }

    public void delContract(int id) {
        getData(1, model.getParam(getDelParamList(id), "DelContract"), true);
    }

    private List<Object> getParamList(int page, int searchType) {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        builder.append(" and DelMarker = 0 ");
        if (isViewAttached()) {
            if (searchType == 1) {
                builder.append(" and CustomerNames like '%").append(getView().getSearchContent()).append("%'");
            } else if (searchType == 2) {
                builder.append(" and S5 like '%").append(getView().getSearchContent()).append("%'");
            } else if (searchType == 3) {
                if (!TextUtils.isEmpty(getView().getSearchCompany()))
                    builder.append(" and CompanyID in ('").append(getView().getSearchCompany()).append("')");
            }
            if (!TextUtils.isEmpty(getView().getChooseTime1()) && !TextUtils.isEmpty(getView().getChooseTime2())) {
                builder.append(" and S1>= '").append(getView().getChooseTime1()).append("'").
                        append(" and S1<= '").append(getView().getChooseTime2()).append("'");
            }
        }
        list.add(builder.toString());
        list.add(String.valueOf(page));
        list.add("10");
        list.add(true);
        return list;
    }

    private List<Object> getDelParamList(int id) {
        List<Object> list = new ArrayList<>();
        list.add(String.valueOf(id));
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        if (isViewAttached()) {
            if (resultCode == 0) {
                List<Contract> temp = JSON.parseArray(data.getData(), Contract.class);
                if (temp == null || temp.size() == 0) {
                    if (homePage == 0) {
                        getView().getContracts().clear();
                        getView().refreshChange();
                        if (getView().getListType() == 0) {
                            getView().showMsg("没有相关报单信息!");
                        } else {
                            getView().showMsg("没有相关合同!");
                        }
                    } else {
                        if (getView().getListType() == 0) {
                            getView().showMsg("没有更多报单了!");
                        } else {
                            getView().showMsg("没有更多合同了!");
                        }
                    }
                } else {
                    if (getView().getPull()) {
                        getView().getContracts().addAll(temp);
                    } else {
                        getView().getContracts().clear();
                        getView().getContracts().addAll(temp);
                    }
                    getView().refreshChange();
                }
                getView().completeRefresh();
            } else if (resultCode == 1) {
                getView().delData();
            }
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }
}
