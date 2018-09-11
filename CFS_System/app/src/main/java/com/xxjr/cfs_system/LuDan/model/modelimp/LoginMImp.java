package com.xxjr.cfs_system.LuDan.model.modelimp;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.rxjavaandretrofit.HttpAction;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.HttpResult;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.services.CacheProvide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * Created by Administrator on 2017/7/26.
 * 登录数据处理类
 */

public class LoginMImp extends ModelImp {
    public void login(String param, final HttpResult result) {
        HttpAction.getInstance().login(new Subscriber<ResponseData>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                result.reusltFailed("登录失败!网络错误");
                Logger.e("==登录http错误==> %s", e.getMessage());
            }

            @Override
            public void onNext(ResponseData data) {
                if (data.getExecuteResult()) {
                    result.reusltSuccess(data);
                } else {
                    result.reusltFailed(data.getData());
                }
            }
        }, param, 0);
    }

    public String getIncreaseCacheParam() {
        //数据类型列表
        Map<String, Object> map = new HashMap<>();
        map.put("Function", "Mobile");
        map.put("TranName", "GET_INCREASE_CACHE");
        Map<String, Object> map1 = new HashMap<>();
        map1.put("Version", Hawk.get(CacheProvide.getCacheKey("Version"), -1));
        map1.put("CompanyGroup", Hawk.get(CacheProvide.getCacheKey("CompanyGroup")));
        if (Hawk.get(CacheProvide.getCacheKey("TableGroup")) != null) {
            map1.put("TableGroup", Hawk.get(CacheProvide.getCacheKey("TableGroup")));
        } else {
            map1.put("TableGroup", getTableGroup());
        }
        map1.put("ToTableGroup", getToTableGroup());
        map.put("Json", JSON.toJSONString(map1));
        Logger.e("==缓存参数==> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    public void saveCacheData(String returnMsg) {
        JSONObject jsonObject = JSONObject.parseObject(returnMsg);
        Hawk.put(CacheProvide.getCacheKey("Version"), jsonObject.getIntValue("Version"));
        Hawk.put(CacheProvide.getCacheKey("CompanyGroup"), jsonObject.getJSONArray("CompanyGroup"));
        Hawk.put(CacheProvide.getCacheKey("TableGroup"), jsonObject.getJSONArray("TableGroup"));
        int upgradeType = jsonObject.getIntValue("UpgradeType");
        JSONObject jsonData = new JSONObject();
        if (jsonObject.getJSONObject("ConfigData") != null) {
            jsonData.putAll(jsonObject.getJSONObject("ConfigData"));
        }
        if (jsonObject.getJSONObject("HQData") != null) {
            jsonData.putAll(jsonObject.getJSONObject("HQData"));
        }
        switch (upgradeType) {
            case 0:
                break;
            case 1:
                saveIncrease(jsonObject, jsonData);
                break;
            case 2:
                saveAll(jsonData);
                break;
        }
    }

    private void saveIncrease(JSONObject jsonObject, JSONObject jsonData) {
        JSONArray jsonArray = jsonObject.getJSONArray("HeadGroup");
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String tableName = object.getString("TableName");
                boolean isRebuild = object.getBooleanValue("IsRebuild");
                if (isRebuild) {
                    Hawk.remove(CacheProvide.getCacheKey(tableName));
                    if (jsonData.getJSONArray(tableName) != null) {
                        Hawk.put(CacheProvide.getCacheKey(tableName), jsonData.getJSONArray(tableName).toJSONString());
                    }
                } else {
                    JSONArray array = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey(tableName), ""));
                    String keyField = object.getString("KeyField");
                    JSONArray keyValues = object.getJSONArray("DeleteKeyGroup");
                    List list = new ArrayList<>();
                    String[] fields = new String[0];
                    if (!TextUtils.isEmpty(keyField)) {
                        fields = keyField.split(",");
                    }
                    if (array != null) {
                        for (int j = 0; j < array.size(); j++) {
                            JSONObject objectData = array.getJSONObject(j);
                            StringBuilder builder = new StringBuilder();
                            for (int g = 0; g < fields.length; g++) {
                                builder.append(objectData.getString(fields[g]));
                                if (g < fields.length - 1) {
                                    builder.append("$");
                                }
                            }
                            if (!keyValues.contains(builder.toString())) {
                                list.add(objectData);
                            }
                        }
                        JSONArray data = jsonData.getJSONArray(tableName);
                        if (data != null) {
                            JSONArray newJsonData = new JSONArray(list);
                            newJsonData.addAll(data);
                            Hawk.put(CacheProvide.getCacheKey(tableName), newJsonData.toJSONString());
                        }
                    }
                }
            }
        }
    }

    //全量更新
    private void saveAll(JSONObject jsonData) {
        Hawk.put(CacheProvide.getCacheKey("SexType"), saveJSON(jsonData.getJSONArray("SexType")));
        Hawk.put(CacheProvide.getCacheKey("IDType"), saveJSON(jsonData.getJSONArray("IDType")));
        Hawk.put(CacheProvide.getCacheKey("LoansType"), saveJSON(jsonData.getJSONArray("LoansType")));
        Hawk.put(CacheProvide.getCacheKey("ContractStatus"), saveJSON(jsonData.getJSONArray("ContractStatus")));
        Hawk.put(CacheProvide.getCacheKey("LoanStateType"), saveJSON(jsonData.getJSONArray("LoanStateType")));
        Hawk.put(CacheProvide.getCacheKey("CreditLoanOperateType"), saveJSON(jsonData.getJSONArray("CreditLoanOperateType")));
        Hawk.put(CacheProvide.getCacheKey("CompanyInfoType"), saveJSON(jsonData.getJSONArray("CompanyInfoType")));
        Hawk.put(CacheProvide.getCacheKey("MortgageUserType"), saveJSON(jsonData.getJSONArray("MortgageUserType")));
        Hawk.put(CacheProvide.getCacheKey("LoanBankType"), saveJSON(jsonData.getJSONArray("LoanBankType")));
        Hawk.put(CacheProvide.getCacheKey("LoanProductType"), saveJSON(jsonData.getJSONArray("LoanProductType")));
        Hawk.put(CacheProvide.getCacheKey("LoanCostType"), saveJSON(jsonData.getJSONArray("LoanCostType")));
        Hawk.put(CacheProvide.getCacheKey("BookTypes"), saveJSON(jsonData.getJSONArray("BookTypes")));
        Hawk.put(CacheProvide.getCacheKey("EnterAccountType"), saveJSON(jsonData.getJSONArray("EnterAccountType")));
        Hawk.put(CacheProvide.getCacheKey("T_Config"), saveJSON(jsonData.getJSONArray("T_Config")));
//        Hawk.put(CacheProvide.getCacheKey("WithholdPlatf"), saveJSON(jsonData.getJSONArray("WithholdPlatf")));
        Hawk.put(CacheProvide.getCacheKey("LendingType"), saveJSON(jsonData.getJSONArray("LendingType")));
        Hawk.put(CacheProvide.getCacheKey("LendStateType"), saveJSON(jsonData.getJSONArray("LendStateType")));
        Hawk.put(CacheProvide.getCacheKey("RePayType"), saveJSON(jsonData.getJSONArray("RePayType")));
        Hawk.put(CacheProvide.getCacheKey("ImageDataType"), saveJSON(jsonData.getJSONArray("ImageDataType")));
        Hawk.put(CacheProvide.getCacheKey("ZoneType"), saveJSON(jsonData.getJSONArray("ZoneType")));
        Hawk.put(CacheProvide.getCacheKey("ImprovementType"), saveJSON(jsonData.getJSONArray("ImprovementType")));
        Hawk.put(CacheProvide.getCacheKey("PayAisleType"), saveJSON(jsonData.getJSONArray("PayAisleType")));
        Hawk.put(CacheProvide.getCacheKey("PaymentBank"), saveJSON(jsonData.getJSONArray("PaymentBank")));
        Hawk.put(CacheProvide.getCacheKey("PayBankCardType"), saveJSON(jsonData.getJSONArray("PayBankCardType")));
        Hawk.put(CacheProvide.getCacheKey("PayAccountProp"), saveJSON(jsonData.getJSONArray("PayAccountProp")));
        Hawk.put(CacheProvide.getCacheKey("PayCardType"), saveJSON(jsonData.getJSONArray("PayCardType")));
        Hawk.put(CacheProvide.getCacheKey("SignerType"), saveJSON(jsonData.getJSONArray("SignerType")));

        Hawk.put(CacheProvide.getCacheKey("EchelonType"), saveJSON(jsonData.getJSONArray("EchelonType")));
        Hawk.put(CacheProvide.getCacheKey("MortgageScoreType"), saveJSON(jsonData.getJSONArray("MortgageScoreType")));
        Hawk.put(CacheProvide.getCacheKey("RepaymentType"), saveJSON(jsonData.getJSONArray("RepaymentType")));
    }

    private String saveJSON(JSONArray jsonArray) {
        if (jsonArray == null) {
            return "";
        } else {
            return jsonArray.toJSONString();
        }
    }

    //第一次更新的缓存表
    private List getTableGroup() {
        List<Object> list = new ArrayList<>();
        list.add("SexType");
        list.add("IDType");
        list.add("LoansType");
        list.add("ContractStatus");//合同状态，进度
        list.add("LoanStateType");//贷款状态，进度
        list.add("CreditLoanOperateType");//前进回退
        list.add("CompanyInfoType");//签约门店
        list.add("MortgageUserType");//按揭员
        list.add("LoanBankType");//银行类型
        list.add("LoanProductType");//银行产品
        list.add("LoanBankManagerType");//银行经理
        list.add("LoanCostType");
        list.add("BookTypes");
        list.add("EnterAccountType");
        list.add("T_Config");//审核配置
//        list.add("WithholdPlatf");//代扣账号
        list.add("LendingType");//拆借类型
        list.add("LendStateType");//拆借状态
        list.add("RePayType");//还款方式
        list.add("ImageDataType");//资料类型
        list.add("ZoneType");//地区
        list.add("RansomUserType");//赎楼员
        list.add("V_UP_User");//客户经理用户信息
        list.add("UserIDType");//非业务员
        return list;
    }

    //当前需要的缓存表
    private List getToTableGroup() {
        List<Object> list = new ArrayList<>();
        list.add("SexType");//性别类型
        list.add("IDType");//证件类型
        list.add("LoansType");//贷款类型
        list.add("ContractStatus");//合同状态，进度
        list.add("LoanStateType");//贷款状态，进度
        list.add("CreditLoanOperateType");//前进回退
        list.add("CompanyInfoType");//签约门店
        list.add("MortgageUserType");//按揭员
        list.add("LoanBankType");//银行类型
        list.add("LoanProductType");//银行产品
        list.add("LoanCostType");//成本类型
        list.add("BookTypes");//收支类型
        list.add("EnterAccountType");//支付方式
        list.add("T_Config");//审核配置
//        list.add("WithholdPlatf");//代扣账号
        list.add("LendingType");//拆借类型
        list.add("LendStateType");//拆借状态
        list.add("RePayType");//还款方式
        list.add("ImageDataType");//资料类型
        list.add("ZoneType");//地区
        list.add("ImprovementType");//评价按揭员标签
        list.add("PayAisleType");//代扣平台
        list.add("PaymentBank");//签约银行
        list.add("PayBankCardType");//银行卡类型
        list.add("PayAccountProp");//账户属性
        list.add("PayCardType");//证件类型
        list.add("SignerType");//签约者身份

        list.add("EchelonType");//梯队
        list.add("MortgageScoreType");//积分类型
        list.add("RepaymentType");//还款类型(先息后本)
        return list;
    }
}
