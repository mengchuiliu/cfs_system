package com.xxjr.cfs_system.LuDan.model.modelimp;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.services.CacheProvide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import entity.CommonItem;

/**
 * Created by Administrator on 2017/8/2.
 * 主页数据处理类
 */

public class PageMenuMImp extends ModelImp {

    public String getPortraitParam(List<Object> list, String tranName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("DBMarker", "CFS_Loan");
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("Function", "HeadPic");
        map.put("TranName", tranName);
        map.put("Action", "GET");
        map.put("ParamString", list);
        Logger.e("==头像==> %s" + JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    public String getAccountParam(List<Object> list) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("IsUseZip", false);
        map.put("Function", "");
        map.put("TranName", "GoldAccount");
        map.put("Action", "GetGoldAccountMetaInfo");
        map.put("ParamString", list);
        Logger.e("==金账户信息==> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    //权限页面数据源
    public ArrayList getOtherData(String permissions) {
        ArrayList<CommonItem> items = new ArrayList<>();
        ArrayList<String> list = Hawk.get(CacheProvide.getCacheKey("MyMenu"));
        if (!TextUtils.isEmpty(permissions)) {
            if (list == null) {
                for (String s2 : getPermissions(permissions)) {
                    if (!s2.equals("816") && getItemMenu(s2) != null) {
                        items.add(getItemMenu(s2));
                    }
                }
            } else {
                for (String s2 : getPermissions(permissions)) {
                    boolean isOther = true;
                    for (String type : list) {
                        if (type.equals(s2)) {
                            isOther = false;
                            break;
                        }
                    }
                    if (isOther) {
                        if (getItemMenu(s2) != null) {
                            items.add(getItemMenu(s2));
                        }
                    }
                }
            }
            if (items.size() > 2) {
                if (items.get(0).getRemark().equals("801") && items.get(1).getRemark().equals("802")) {
                    Collections.swap(items, 0, 1);//元素互换位置
                }
            }
        }
        return items;
    }

    public ArrayList getMyData(String permissions) {
        ArrayList<CommonItem> items = new ArrayList<>();
        ArrayList<String> strings = Hawk.get(CacheProvide.getCacheKey("MyMenu"));
        if (strings == null) {
            CommonItem commonItem3 = new CommonItem();
            commonItem3.setName("金账户");
            commonItem3.setIcon(R.mipmap.menu_gold_account);
            commonItem3.setRemark("816");
            items.add(commonItem3);
        } else {
            for (String type : strings) {
                for (String s2 : getPermissions(permissions)) {
                    if (s2.equals(type)) {
                        items.add(getItemMenu(type));
                        break;
                    }
                }
            }
        }
        return items;
    }

    public CommonItem getTaskData(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("MyTask");
        if (jsonArray.isEmpty()) {
            return null;
        } else {
            CommonItem commonItem = new CommonItem();
            commonItem.setName("我的任务");
            commonItem.setIcon(R.mipmap.pending);
            CommonItem commonItem1;
            List<CommonItem> items1 = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                commonItem1 = new CommonItem();
                JSONObject object = jsonArray.getJSONObject(i);
                commonItem1.setContent(object.getString("MDesc"));
                commonItem1.setRemark(object.getString("AliaName"));
                commonItem1.setPosition(object.getIntValue("Value"));
                items1.add(commonItem1);
            }
            commonItem.setList(items1);
            return commonItem;
        }
    }

    //获取菜单列表
    private CommonItem getItemMenu(String type) {
        CommonItem commonItem1 = null;
        switch (type) {
            case "801":
                commonItem1 = new CommonItem();
                commonItem1.setName("报单列表");
                commonItem1.setIcon(R.mipmap.menu_bills);
                break;
            case "802":
                commonItem1 = new CommonItem();
                commonItem1.setName("快速报单");
                commonItem1.setIcon(R.mipmap.menu_fast);
                break;
            case "803":
                commonItem1 = new CommonItem();
                commonItem1.setName("合同列表");
                commonItem1.setIcon(R.mipmap.menu_contract);
                break;
            case "804":
                commonItem1 = new CommonItem();
                commonItem1.setName("贷款列表");
                commonItem1.setIcon(R.mipmap.menu_loan);
                break;
            case "805":
                commonItem1 = new CommonItem();
                commonItem1.setName("贷款跟进");
                commonItem1.setIcon(R.mipmap.menu_task);
                break;
            case "806":
                commonItem1 = new CommonItem();
                commonItem1.setName("成本列表");
                commonItem1.setIcon(R.mipmap.menu_costing);
                break;
            case "807":
                commonItem1 = new CommonItem();
                commonItem1.setName("贷款结案");
                commonItem1.setIcon(R.mipmap.menu_over);
                break;
            case "808":
                commonItem1 = new CommonItem();
                commonItem1.setName("经营报表");
                commonItem1.setIcon(R.mipmap.menu_ranking);
                break;
            case "809":
                commonItem1 = new CommonItem();
                commonItem1.setName("拆借列表");
                commonItem1.setIcon(R.mipmap.menu_borrow);
                break;
            case "810":
                commonItem1 = new CommonItem();
                commonItem1.setName("来访邀约");
                commonItem1.setIcon(R.mipmap.menu_visitor);
                break;
            case "811":
                commonItem1 = new CommonItem();
                commonItem1.setName("提成列表");
                commonItem1.setIcon(R.mipmap.menu_wage);
                break;
            case "812":
                commonItem1 = new CommonItem();
                commonItem1.setName("合同资料");
                commonItem1.setIcon(R.mipmap.menu_paper);
                break;
            case "813":
                commonItem1 = new CommonItem();
                commonItem1.setName("按揭统计");
                commonItem1.setIcon(R.mipmap.menu_charges);
                break;
            case "814":
                commonItem1 = new CommonItem();
                commonItem1.setName("绩效积分");
                commonItem1.setIcon(R.mipmap.menu_score);
                break;
            case "815":
                commonItem1 = new CommonItem();
                commonItem1.setName("历史报表");
                commonItem1.setIcon(R.mipmap.menu_history);
                break;
            case "816":
                commonItem1 = new CommonItem();
                commonItem1.setName("金账户");
                commonItem1.setIcon(R.mipmap.menu_gold_account);
                break;
            case "817":
                commonItem1 = new CommonItem();
                commonItem1.setName("待转账回款");
                commonItem1.setIcon(R.mipmap.transfer_receivable);
                break;
            case "818":
                commonItem1 = new CommonItem();
                commonItem1.setName("还款提醒");
                commonItem1.setIcon(R.mipmap.pay_remind);
                break;
            case "819":
                commonItem1 = new CommonItem();
                commonItem1.setName("贷款计算器");
                commonItem1.setIcon(R.mipmap.calculator);
                break;
            case "820":
                commonItem1 = new CommonItem();
                commonItem1.setName("代扣协议");
                commonItem1.setIcon(R.mipmap.withholding);
                break;
            case "821":
                commonItem1 = new CommonItem();
                commonItem1.setName("提成钱包");
                commonItem1.setIcon(R.mipmap.menu_wallet);
                break;
            case "822":
                commonItem1 = new CommonItem();
                commonItem1.setName("支出审核");
                commonItem1.setIcon(R.mipmap.spend_audit);
                break;
            case "823":
                commonItem1 = new CommonItem();
                commonItem1.setName("员工培训");
                commonItem1.setIcon(R.mipmap.menu_train);
                break;
            case "824":
                commonItem1 = new CommonItem();
                commonItem1.setName("回款审核");
                commonItem1.setIcon(R.mipmap.menu_returned_audit);
                break;
            case "825":
                commonItem1 = new CommonItem();
                commonItem1.setName("放款列表");
                commonItem1.setIcon(R.mipmap.menu_lending);
                break;
            case "826":
                commonItem1 = new CommonItem();
                commonItem1.setName("交易中心");
                commonItem1.setIcon(R.mipmap.menu_transaction);
                break;
        }
        if (commonItem1 != null) {
            commonItem1.setRemark(type);
        }
        return commonItem1;
    }

    private String[] getPermissions(String permissions) {
        String[] myPermissions = new String[0];
        String[] strings = permissions.split("-");
        if (strings.length > 1) {
            String[] str = strings[1].split("M");
            if (str.length != 0) {
                for (String s : str) {
                    if (s.startsWith("80")) {
                        String[] str1 = s.split("#");
                        if (str1.length >= 1) {
                            String s1 = str1[1];
                            myPermissions = s1.split("F");
                        }
                    }
                }
            }
        }
        Logger.e("==模块==> %s", Arrays.toString(myPermissions));
        return myPermissions;
    }
}
