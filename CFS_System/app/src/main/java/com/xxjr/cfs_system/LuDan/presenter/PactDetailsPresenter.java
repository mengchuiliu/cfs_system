package com.xxjr.cfs_system.LuDan.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.modelimp.PactDetailsMImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.PactDetailsVInter;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import entity.ClientInfo;
import entity.LoanInfo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/25.
 * 合同详情执行类
 */

public class PactDetailsPresenter extends BasePresenter<PactDetailsVInter, PactDetailsMImp> {
    private List<ClientInfo> infos = null;

    @Override
    protected PactDetailsMImp getModel() {
        return new PactDetailsMImp();
    }

    @Override
    public void setDefaultValue() {
        try {
            infos = JSON.parseArray(getView().getContract().getCustomerInfo(), ClientInfo.class);
        } catch (Exception e) {
            Log.e("my_log", "===用户信息json异常====>" + Log.getStackTraceString(e));
        }
        if (infos == null) {
            infos = new ArrayList<>();
            String name = getView().getContract().getCustomerNames();
            if (!TextUtils.isEmpty(name)) {
                String[] strings = name.split(",");
                ClientInfo clientInfo;
                for (String str : strings) {
                    clientInfo = new ClientInfo();
                    clientInfo.setName(str);
                    infos.add(clientInfo);
                }
            }
        }
        getView().initRecycler(model.getItemList(getView().getContract(), infos));
        postAndRefreshData();
    }

    public List<ClientInfo> getInfos() {
        return infos;
    }

    //请求数据刷新
    public void postAndRefreshData() {
        getData(0, model.getLoanParam(getListParam(), "GetPermitData"), true);
    }

    public void getPactDataNum() {
        getData(1, model.getParam(getReadListParam(), "GET_NOVIEWFILECOUNT"), false);//请求为查看资料数
    }

    private List<Object> getListParam() {
        List<Object> list = new ArrayList<>();
        list.add("t_loan_record");
        list.add("contractid = " + getView().getContract().getID() + " and DelMarker = 0");
        return list;
    }

    private List<Object> getReadListParam() {
        List<Object> list = new ArrayList<>();
        list.add(getView().getContract().getID());
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, final ResponseData data) {
        if (resultCode == 0) {
            final JSONArray jsonArray = JSON.parseArray(data.getData());
            if (jsonArray != null && jsonArray.size() != 0) {
                Observable.create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        model.getArray();
                        subscriber.onNext("");
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Object>() {

                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                showView(jsonArray);
                            }

                            @Override
                            public void onNext(Object o) {
                                showView(jsonArray);
                            }
                        });
            }
        } else if (resultCode == 1) {
            if (!data.getReturnString().equals("0"))
                getView().refreshReadData(15, data.getReturnString());
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }

    private void showView(JSONArray jsonArray) {
        List<LoanInfo> loanInfoList = new ArrayList<>();
        StringBuilder idBuilder = new StringBuilder();
        StringBuilder contentBuilder = new StringBuilder();
        LoanInfo loanInfo;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            loanInfo = model.getLoanInfo(object);
            loanInfoList.add(loanInfo);

            idBuilder.append(String.valueOf(object.getIntValue("ID")));
            String s = "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() + "(" + loanInfo.getLoanTypeName().substring(0, 1) + ")" + "】" +
                    "申请: " + Utils.div(loanInfo.getAmount()) + "万" + " -- " + "批复:" + Utils.div(loanInfo.getReplyAmount()) + "万 " +
                    (CacheProvide.getMortgageName(loanInfo.getMortgage()) == null ? "" : CacheProvide.getMortgageName(loanInfo.getMortgage())) + " " +
                    loanInfo.getSchedule() + " " + (TextUtils.isEmpty(object.getString("InsertTime")) ? "" : Utils.getTime(object.getString("InsertTime")));
            contentBuilder.append(s);
            if (i != jsonArray.size() - 1) {
                idBuilder.append(",");
                contentBuilder.append(",");
            }
        }
        getView().setIDs(idBuilder.toString());
        getView().setLoanDescription(contentBuilder.toString());
        getView().refreshData(16, loanInfoList);
    }
}
