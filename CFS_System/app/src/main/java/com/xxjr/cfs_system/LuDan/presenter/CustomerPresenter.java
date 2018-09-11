package com.xxjr.cfs_system.LuDan.presenter;

import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.BaseModelInter;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.LuDan.view.activitys.CustomerActivity;

import java.util.ArrayList;
import java.util.List;

import entity.ChooseType;
import entity.ClientInfo;

/**
 * Created by Administrator on 2017/9/1.
 *
 * @author mengchuiliu
 */

public class CustomerPresenter extends BasePresenter<CustomerActivity, ModelImp> {

    @Override
    protected ModelImp getModel() {
        return new ModelImp();
    }

    @Override
    public void setDefaultValue() {
        List<ClientInfo> infos = getView().getInfos();
        if (infos != null && infos.size() > 0) {
            ChooseType chooseType;
            for (ClientInfo clientInfo : infos) {
                chooseType = new ChooseType();
                chooseType.setId(clientInfo.getUserID());
                chooseType.setContent(clientInfo.getName());
                getView().getList().add(chooseType);
            }
        }
        getView().initRecycler();
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {

    }

    @Override
    protected void onFailed(int resultCode, String msg) {

    }
}
