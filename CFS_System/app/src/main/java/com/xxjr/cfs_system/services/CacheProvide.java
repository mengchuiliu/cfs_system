package com.xxjr.cfs_system.services;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.xxjr.cfs_system.main.MyApplication;

/**
 * Created by Administrator on 2017/8/9.
 * 根据id来查询缓存数据帮助类
 */

public class CacheProvide {

    public static String UserKey = "";

    public static String getCacheKey(String key) {
        if (TextUtils.isEmpty(UserKey)) {
            UserKey = Hawk.get("UserID", "");
        }
        return UserKey + "_" + key;
    }

    //门店名字
    public static String getStoreName(String id) {
        String store = "";
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("CompanyInfoType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("ID").equals(id)) {
                    store = object.getString("Name");
                    break;
                }
            }
        }
        return store;
    }

    //合同状态
    public static String getPactStatus(JSONArray jsonArray, int id) {
        String status = "";
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("Value") == id) {
                    status = object.getString("Name");
                    break;
                }
            }
        }
        return status;
    }

    //贷款银行
    public static String getBank(int id) {
        String bank = "";
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanBankType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (Integer.valueOf(object.getString("ID")) == id) {
                    bank = object.getString("Name");
                    break;
                }
            }
        }
        return bank;
    }

    //贷款银行
    public static String getBank(JSONArray jsonArray, int id) {
        String bank = "";
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (Integer.valueOf(object.getString("ID")) == id) {
                    bank = object.getString("Name");
                    break;
                }
            }
        }
        return bank;
    }

    //银行产品
    public static String getProduct(int id) {
        String product = "";
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanProductType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("ID") == id) {
                    product = object.getString("Name");
                    break;
                }
            }
        }
        return product;
    }

    //银行产品
    public static String getProduct(JSONArray jsonArray, int id) {
        String product = "";
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("ID") == id) {
                    product = object.getString("Name");
                    break;
                }
            }
        }
        return product;
    }

    //贷款类型
    public static String getLoanType(int id) {
        String type = "";
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoansType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("Value") == id) {
                    type = object.getString("Name");
                    break;
                }
            }
        }
        return type;
    }

    //贷款类型
    public static String getLoanType(JSONArray jsonArray, int id) {
        String type = "";
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("Value") == id) {
                    type = object.getString("Name");
                    break;
                }
            }
        }
        return type;
    }

    //贷款状态
    public static String getLoanStatus(int id) {
        String status = "";
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanStateType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("Value") == id) {
                    status = object.getString("Name");
                    break;
                }
            }
        }
        return status;
    }

    //贷款状态
    public static String getLoanStatus(JSONArray jsonArray, int id) {
        String status = "";
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("Value") == id) {
                    status = object.getString("Name");
                    break;
                }
            }
        }
        return status;
    }

    //按揭员姓名
    public static String getMortgageName(int id) {
        String mortgageName = "";
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("MortgageUserType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("Value") == id) {
                    mortgageName = object.getString("Name");
                    break;
                }
            }
        }
        return mortgageName;
    }

    //成本类型
    public static String getCostType(int id) {
        String type = "";
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanCostType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("Value") == id) {
                    type = object.getString("Name");
                    break;
                }
            }
        }
        return type;
    }

    //赎楼员
    public static String getMemberName(int id) {
        String memberName = "";
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("MortgageUserType"), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getIntValue("Value") == id) {
                    memberName = object.getString("Name");
                    break;
                }
            }
        }
        return memberName;
    }
}
