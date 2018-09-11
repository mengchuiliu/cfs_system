package com.xxjr.cfs_system.LuDan.model.modelimp;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.main.MyApplication;
import com.xxjr.cfs_system.services.CacheProvide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entity.ChooseType;
import okhttp3.Cache;

/**
 * Created by Administrator on 2017/8/24.
 * 搜索数据类
 */

public class SearchMImp extends ModelImp {

    //通用参数组装
    public String getSearchParam(List<Object> list, String tranName, String Function, String DBMarker) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Action", "Default");
        map.put("DBMarker", DBMarker);//DB_CFS
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("Function", Function);
        map.put("ParamString", list);
        map.put("TranName", tranName);
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    //获取客户经理列表
    public List<ChooseType> getManagerData(String data) {
        List<ChooseType> chooseTypes = null;
        if (!TextUtils.isEmpty(data)) {
            JSONArray jsonArray = JSONArray.parseArray(data);
            if (jsonArray != null && jsonArray.size() != 0) {
                chooseTypes = new ArrayList<>();
                ChooseType chooseType;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    chooseType = new ChooseType();
                    chooseType.setId(jsonObject.getIntValue("UserID"));
                    chooseType.setContent(jsonObject.getString("UserRealName"));
                    chooseType.setIds(jsonObject.getString("CompanyID"));
                    chooseTypes.add(chooseType);
                }
            }
        }
        return chooseTypes;
    }

    //获取签单员和见证人
    public List<ChooseType> getMemberData(String data) {
        List<ChooseType> chooseTypes = null;
        if (!TextUtils.isEmpty(data)) {
            JSONArray jsonArray = JSONArray.parseArray(data);
            if (jsonArray != null && jsonArray.size() != 0) {
                chooseTypes = new ArrayList<>();
                ChooseType chooseType;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    chooseType = new ChooseType();
                    chooseType.setId(jsonObject.getIntValue("value"));
                    chooseType.setContent(jsonObject.getString("name"));
                    chooseTypes.add(chooseType);
                }
            }
        }
        return chooseTypes;
    }

    //获取银行列表
    public List<ChooseType> getBankData() {
        List<ChooseType> chooseTypes = null;
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanBankType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            chooseTypes = new ArrayList<>();
            ChooseType chooseType;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                chooseType = new ChooseType();
                chooseType.setId(Integer.valueOf(object.getString("ID")));
                chooseType.setContent(object.getString("Name"));
                chooseTypes.add(chooseType);
            }
        }
        return chooseTypes;
    }

    //获取银行产品列表
    public List<ChooseType> getProductsData(int bankId) {
        List<ChooseType> chooseTypes = null;
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanProductType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            chooseTypes = new ArrayList<>();
            ChooseType chooseType;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String company = object.getString("Company");
                if (!TextUtils.isEmpty(company)) {
                    if (Integer.valueOf(company) == bankId) {
                        chooseType = new ChooseType();
                        chooseType.setId(object.getIntValue("ID"));
                        chooseType.setContent(object.getString("Name"));
                        chooseType.setIds(object.getString("LoanType"));
                        chooseTypes.add(chooseType);
                    }
                }
            }
        }
        return chooseTypes;
    }

    //获取按揭员列表
    public List<ChooseType> getMortgageData() {
        List<ChooseType> chooseTypes = null;
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("MortgageUserType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            chooseTypes = new ArrayList<>();
            ChooseType chooseType;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String userType = object.getString("UserType");
                if (!TextUtils.isEmpty(userType)) {
                    chooseType = new ChooseType();
                    chooseType.setId(object.getIntValue("Value"));
                    chooseType.setContent(object.getString("Name"));
                    chooseTypes.add(chooseType);
                }
            }
        }
        return chooseTypes;
    }

    //获取银行经理列表
    public List<ChooseType> getBankManagerData(String data) {
        List<ChooseType> chooseTypes = null;
        if (!TextUtils.isEmpty(data)) {
            JSONArray jsonArray = JSONArray.parseArray(data);
            if (jsonArray != null && jsonArray.size() != 0) {
                chooseTypes = new ArrayList<>();
                ChooseType chooseType;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    chooseType = new ChooseType();
                    chooseType.setId(jsonObject.getIntValue("ID"));
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(jsonObject.getString("BankManagerName"));
                    String branchName = jsonObject.getString("BranchBankName");
                    if (!TextUtils.isEmpty(branchName)) {
                        stringBuilder.append("(").append(branchName).append(")");
                    }
                    chooseType.setContent(stringBuilder.toString());
                    chooseTypes.add(chooseType);
                }
            }
        }
        return chooseTypes;
    }

    /**
     * 根据签约平台获取签约银行
     *
     * @param aisleType 签约平台
     * @return
     */
    public List<ChooseType> getSignBankData(String aisleType) {
        List<ChooseType> chooseTypes = null;
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("PaymentBank"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            chooseTypes = new ArrayList<>();
            ChooseType chooseType;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                chooseType = new ChooseType();
                if (aisleType.equals(object.getString("PayAisleType"))) {
                    chooseType.setIds(object.getString("ID"));
                    chooseType.setContent(object.getString("Name"));
                    chooseTypes.add(chooseType);
                }
            }
        }
        return chooseTypes;
    }

    //获取门店列表
    public List<ChooseType> getCompanyData() {
        List<ChooseType> chooseTypes = null;
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("CompanyInfoType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            chooseTypes = new ArrayList<>();
            ChooseType chooseType;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                chooseType = new ChooseType();
                chooseType.setIds(object.getString("ID"));
                chooseType.setContent(object.getString("Name"));
                chooseTypes.add(chooseType);
            }
        }
        return chooseTypes;
    }
}
