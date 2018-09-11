package com.xxjr.cfs_system.LuDan.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.model.modelimp.AddPactMImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.AddPactVInter;
import com.xxjr.cfs_system.ViewsHolder.PopChoose;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.ChooseType;
import entity.ClientInfo;
import entity.CommonItem;
import entity.Contract;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/8/9.
 * 新增报单执行类
 */

public class AddPactPresenter extends BasePresenter<AddPactVInter, AddPactMImp> {
    private Subscription subscription, zoneSubscription, memberSubscription, witnessSubscription;
    private PopupWindow popWindow;
    JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("ZoneType"), ""));

    @Override
    protected AddPactMImp getModel() {
        return new AddPactMImp();
    }

    @Override
    public void setDefaultValue() {
        initRX();
        String userType = Hawk.get("UserType", "");
        if (getView().getContract().getID() == 0) {
            String ids = Hawk.get("CompanyID", "");
            if (!TextUtils.isEmpty(userType) && userType.equals("1")) {
                int clerkID = Integer.valueOf(Hawk.get("UserID", "0"));
                getView().getContract().setS3(clerkID);
                getView().getContract().setClerkName(Hawk.get("UserRealName", ""));
                getView().setCompanyID(ids);
            }
            getView().getContract().setS1(DateUtil.getFormatDate(new Date()));
            getView().getContract().setCompanyID(ids);
            getView().getContract().setZoneId(Hawk.get("UserZone", ""));
        }
        getView().initReacycle(model.getItemList(getView().getContract(), userType, jsonArray));
    }

    private void initRX() {
        subscription = RxBus.getInstance().toObservable(Constants.MANAGER_CODE, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                getView().getContract().setS3(chooseType.getId());
                getView().getContract().setClerkName(chooseType.getContent());
                getView().setCompanyID(chooseType.getIds());
                getView().refreshData(4, chooseType.getContent());
            }
        });
        memberSubscription = RxBus.getInstance().toObservable(Constants.Sign_Member, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                String name = getModel().getMemberName(chooseType.getContent());
                getView().getContract().setAccompanyPeople(chooseType.getId());
                getView().getContract().setAccompanyPeopleName(name);
                getView().refreshData(5, name);
            }
        });
        witnessSubscription = RxBus.getInstance().toObservable(Constants.Witness, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                String name = getModel().getMemberName(chooseType.getContent());
                getView().getContract().setWitnessId(chooseType.getId());
                getView().getContract().setWitnessName(name);
                getView().refreshData(6, name);
            }
        });

        zoneSubscription = RxBus.getInstance().toObservable(Constants.ZONE_CODE, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                if (popWindow != null && popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                getView().getContract().setZoneId(chooseType.getIds());
                getView().refreshData(2, chooseType.getContent());
            }
        });
    }

    public void showPop(View parent) {
        if (isViewAttached()) {
            if (popWindow == null)
                popWindow = PopChoose.showChooseType((Context) getView(), parent, "所属地区", model.getTypeDataList(jsonArray),
                        Constants.ZONE_CODE, false);
            else popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
    }

    public boolean check(Contract contract) {
        if (contract.getS3() == 0) {
            getView().showMsg("请选择客户经理!");
            return false;
        } else if (TextUtils.isEmpty(contract.getZoneId())) {
            getView().showMsg("所属地区不能为空!");
            return false;
        } else if (TextUtils.isEmpty(contract.getS7())) {
            getView().showMsg("合同费用不能为空!");
            return false;
        } else if (TextUtils.isEmpty(contract.getS6())) {
            getView().showMsg("合同项目不能为空!");
            return false;
        } else if (getView().getCustomerInfo() == null || getView().getCustomerInfo().size() == 0) {
            getView().showMsg("客户列表不能为空!");
            return false;
        }
        return true;
    }

    public String getContractParam(String tranName) {
        return model.getContractParam(getContractListParam(getView().getCustomerInfo()), tranName);
    }

    private List<Object> getContractListParam(List<ClientInfo> infos) {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("ContractID", getView().getContract().getID());
        map1.put("SeviceId", getView().getContract().getS3());
        map1.put("CompanyID", Hawk.get("CompanyID", ""));
        map1.put("ContractDate", getView().getContract().getS1());
        map1.put("ContractCost", getView().getContract().getS7());
        map1.put("ContractContent", getView().getContract().getS6());
        map1.put("ZoneId", getView().getContract().getZoneId());
        map1.put("AccompanyPeople", getView().getContract().getAccompanyPeople());
        map1.put("AccompanyPeopleName", getView().getContract().getAccompanyPeopleName());
        map1.put("WitnessId", getView().getContract().getWitnessId());
        map1.put("WitnessName", getView().getContract().getWitnessName());
        List<Object> list = new ArrayList<>();
        for (ClientInfo clientInfo : infos) {
            Map<String, Object> map2 = new HashMap<>();
            map2.put("UserID", clientInfo.getUserID());
            map2.put("Name", clientInfo.getName());
            map2.put("Mobile", clientInfo.getMobile());
            map2.put("IdCode", clientInfo.getIdCode());
            map2.put("CardType", clientInfo.getCardType());
            list.add(map2);
        }
        getView().getContract().setCustomerInfo(JSON.toJSONString(list));
        map1.put("Customers", list);
        String s = JSON.toJSONString(map1);
        List<Object> lists = new ArrayList<>();
        lists.add(s);
        return lists;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        if (data.getReturnStrings() != null) {
            JSONArray jsonArray = JSON.parseArray(data.getReturnStrings());
            if (jsonArray.size() > 0) {
                getView().showMsg("保存成功,客户信息已存在,部分信息系统默认覆盖!");
            }
        } else {
            getView().showMsg("保存成功!");
        }
        switch (resultCode) {
            case 0:
                JSONArray jsonArray = JSON.parseArray(data.getData());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                getView().getContract().setS5(jsonObject.getString("Code"));
                if (!TextUtils.isEmpty(jsonObject.getString("CustomerInfo"))) {
                    List<ClientInfo> clientInfos = JSON.parseArray(jsonObject.getString("CustomerInfo"), ClientInfo.class);
                    getView().getContract().setCustomerInfo(JSON.toJSONString(clientInfos));
                }
                getView().getContract().setID(Integer.valueOf(data.getReturnString()));
                getView().getContract().setS12(0);
                getView().addPactOver();
                break;
            case 1:
                getView().updateOver();
                break;
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }

    public void rxDeAttach() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            popWindow = null;
        }
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (memberSubscription != null && !memberSubscription.isUnsubscribed()) {
            memberSubscription.unsubscribe();
        }
        if (witnessSubscription != null && !witnessSubscription.isUnsubscribed()) {
            witnessSubscription.unsubscribe();
        }
        if (zoneSubscription != null && !zoneSubscription.isUnsubscribed()) {
            zoneSubscription.unsubscribe();
        }
    }
}
