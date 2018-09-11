package com.xxjr.cfs_system.LuDan.model.modelimp;

import com.alibaba.fastjson.JSON;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.main.MyApplication;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entity.CommonItem;
import entity.PersonalInfo;

/**
 * Created by Administrator on 2017/8/1.
 * 个人主页数据处理类
 */

public class PersonalMImp extends ModelImp {

    //保存信息参数
    public String getSaveParam(List<Object> list, String tranName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("Function", "Info");
        map.put("Action", "UPD");
        map.put("ParamString", list);
        map.put("TranName", tranName);
        return JSON.toJSONString(map);
    }

    //头像参数
    public String getPortraitParam(List<Object> list, String tranName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("DBMarker", "CFS_Loan");
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("Function", "HeadPic");
        map.put("TranName", tranName);
        map.put("Action", "GET");
        map.put("ParamString", list);
        map.put("TranName", tranName);
        return JSON.toJSONString(map);
    }

    public List<CommonItem> getItemList(PersonalInfo info) {
        List<CommonItem> list = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 9; i++) {
            commonItem = new CommonItem();
            commonItem.setPosition(i);
            switch (i) {
                case 0:
                    commonItem.setName("姓氏");
                    commonItem.setType(3);
                    commonItem.setContent(info.getUserSurname());
                    break;
                case 1:
                    commonItem.setName("名字");
                    commonItem.setType(3);
                    commonItem.setContent(info.getUserName());
                    break;
                case 2:
                    commonItem.setName("性别");
                    commonItem.setType(2);
                    commonItem.setContent(Utils.getTypeValue(Utils.getTypeDataList("SexType"), info.getUserSex()));
                    break;
                case 3:
                    commonItem.setName("出生日期");
                    commonItem.setType(2);
                    commonItem.setContent(info.getUserBirthday());
                    break;
                case 4:
                    commonItem.setName("入职日期");
                    commonItem.setType(1);
                    commonItem.setContent(info.getUserCreateTime());
                    break;
                case 5:
                    commonItem.setName("SIP账号");
                    commonItem.setType(1);
                    commonItem.setContent(info.getSIP());
                    break;
                case 6:
                    commonItem.setName("坐席");
                    commonItem.setType(1);
                    commonItem.setContent(info.getSeat());
                    break;
                case 7:
                    commonItem.setName("手机号");
                    commonItem.setType(3);
                    commonItem.setContent(info.getPhone());
                    break;
                case 8:
                    commonItem.setName("手机号1");
                    commonItem.setType(3);
                    commonItem.setContent(info.getPhone1());
                    break;
            }
            list.add(commonItem);
        }
        return list;
    }
}
