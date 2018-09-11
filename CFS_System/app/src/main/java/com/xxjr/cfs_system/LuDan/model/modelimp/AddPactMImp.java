package com.xxjr.cfs_system.LuDan.model.modelimp;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.ChooseType;
import entity.ClientInfo;
import entity.CommonItem;
import entity.Contract;

/**
 * Created by Administrator on 2017/8/9.
 * 新增报单数据处理类
 */

public class AddPactMImp extends ModelImp {

    //请求参数
    public String getContractParam(List<Object> list, String tranName) {
        Map<String, Object> map = new HashMap<>();
        map.put("Marker", "HQServer");
        map.put("IsUseZip", "false");
        map.put("Function", "Pager");
        map.put("Action", "Default");
        map.put("DBMarker", "DB_CFS");
        map.put("ParamString", list);
        map.put("TranName", tranName);
        Logger.e("==添加合同==> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    //获取显示列表数据
    public List<CommonItem> getItemList(Contract contract, String userType, JSONArray jsonArray) {
        List<CommonItem> list = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 11; i++) {
            commonItem = new CommonItem();
            switch (i) {
                case 0:
                    commonItem.setName("合同信息");
                    commonItem.setType(1);
                    commonItem.setIcon(R.mipmap.icon_compact);
                    break;
                case 1:
                    commonItem.setName("签约门店");
                    commonItem.setType(2);
                    commonItem.setContent(Hawk.get("CompanyFullName", ""));
                    break;
                case 2:
                    commonItem.setName("所属地区");
                    commonItem.setClick(true);
                    commonItem.setType(3);
                    commonItem.setContent(getZoneName(jsonArray, contract.getZoneId()));
                    break;
                case 3:
                    commonItem.setName("签约日期");
                    commonItem.setType(2);
                    commonItem.setContent(contract.getS1().contains("T") ? Utils.getTime(contract.getS1()) : contract.getS1());
                    break;
                case 4:
                    commonItem.setName("客户经理");
                    commonItem.setType(3);
                    if (contract.getID() == 0) {
                        if (!TextUtils.isEmpty(userType) && userType.equals("1")) {
                            commonItem.setContent(Hawk.get("UserRealName", ""));
                            commonItem.setClick(false);
                        } else {
                            commonItem.setClick(true);
                        }
                    } else {
                        commonItem.setContent(contract.getClerkName());
                        commonItem.setClick(false);
                    }
                    break;
                case 5:
                    commonItem.setName("谈单员");
                    commonItem.setType(3);
                    if (contract.getID() == 0) {
                        commonItem.setClick(true);
                    } else {
                        commonItem.setClick(true);
                        commonItem.setContent(contract.getAccompanyPeopleName());
                    }
                    break;
                case 6:
                    commonItem.setName("签约见证人");
                    commonItem.setType(3);
                    if (contract.getID() == 0) {
                        commonItem.setClick(true);
                    } else {
                        commonItem.setClick(true);
                        commonItem.setContent(contract.getWitnessName());
                    }
                    break;
                case 7:
                    commonItem.setName("合同费用");
                    commonItem.setType(4);
                    commonItem.setContent(contract.getS7());
                    commonItem.setHintContent("收费情况");
                    break;
                case 8:
                    commonItem.setName("合同项目");
                    commonItem.setType(5);
                    commonItem.setContent(contract.getS6());
                    break;
                case 9:
                    commonItem.setName("客户列表");
                    commonItem.setType(1);
                    commonItem.setIcon(R.mipmap.icon_client);
                    break;
                case 10:
                    commonItem.setType(6);
                    if (!TextUtils.isEmpty(contract.getCustomerInfo())) {
                        try {
                            List<ClientInfo> clientInfos = JSON.parseArray(contract.getCustomerInfo(), ClientInfo.class);
                            commonItem.setList(clientInfos);
                        } catch (Exception e) {
                            Log.e("my_log", "===json转换错误====>" + e.getMessage());
                        }
                    } else {
                        commonItem.setList(new ArrayList());
                    }
                    break;
            }
            list.add(commonItem);
        }
        return list;
    }

    private String getZoneName(JSONArray jsonArray, String zone) {
        String name = "";
        if (jsonArray != null && jsonArray.size() != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (zone.equals(object.getString("Value"))) {
                    name = object.getString("Name");
                    break;
                }
            }
        }
        return name;
    }

    public List<ChooseType> getTypeDataList(JSONArray jsonArray) {
        List<ChooseType> list = new ArrayList<>();
        if (jsonArray != null && jsonArray.size() != 0) {
            ChooseType chooseType;
            for (int i = 0; i < jsonArray.size(); i++) {
                chooseType = new ChooseType();
                JSONObject object = jsonArray.getJSONObject(i);
                chooseType.setIds(object.getString("Value"));
                chooseType.setContent(object.getString("Name"));
                list.add(chooseType);
            }
        }
        return list;
    }

    public String getMemberName(String name) {
        String member = "";
        if (!TextUtils.isEmpty(name)) {
            String[] strings = name.split("-");
            if (strings.length > 0)
                member = strings[strings.length - 1];
        }
        return member;
    }
}
