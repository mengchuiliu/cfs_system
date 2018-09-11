package com.xxjr.cfs_system.LuDan.model.modelimp;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.DateUtil;
import com.xxjr.cfs_system.tools.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.CommonItem;
import entity.LoanInfo;

/**
 * Created by Administrator on 2017/9/1.
 * 添加贷款数据处理类
 */

public class AddLoanMImp extends ModelImp {

    public List<CommonItem> getItems(LoanInfo loanInfo, boolean scheduleUp) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 7; i++) {
            commonItem = new CommonItem();
            switch (i) {
                case 0:
                    commonItem.setType(0);
                    commonItem.setName("申请银行");
                    commonItem.setContent(loanInfo.getBankName());
                    break;
                case 1:
                    commonItem.setType(0);
                    commonItem.setName("银行产品");
                    commonItem.setContent(loanInfo.getProductName());
                    break;
                case 2:
                    commonItem.setType(0);
                    commonItem.setName("贷款类型");
                    commonItem.setContent(loanInfo.getLoanTypeName());
                    break;
                case 3:
                    commonItem.setType(1);
                    commonItem.setName("申请金额");
                    String s = Utils.parseMoney(new BigDecimal(loanInfo.getAmount()).setScale(2, BigDecimal.ROUND_DOWN));
                    commonItem.setContent(s.equals("0") ? "" : s);
                    commonItem.setHintContent("金额");
                    break;
                case 4:
                    if (scheduleUp) {
                        commonItem.setClick(true);
                    }
                    commonItem.setType(0);
                    commonItem.setName("申请客户");
                    commonItem.setContent(loanInfo.getCustomer());
                    break;
                case 5:
                    commonItem.setType(0);
                    commonItem.setName("按 揭 员");
                    if (loanInfo.getMortgage() == 0) {
                        commonItem.setClick(false);
                    } else {
                        commonItem.setClick(true);
                    }
                    commonItem.setContent(CacheProvide.getMortgageName(loanInfo.getMortgage()));
                    break;
                case 6:
                    commonItem.setType(2);
                    commonItem.setName("备注:");
                    commonItem.setContent(loanInfo.getNote());
                    commonItem.setPosition(50);
                    break;
            }
            commonItems.add(commonItem);
        }
        return commonItems;
    }

    /**
     * @param isUpdate  是否编辑更新
     * @param pactID    合同id
     * @param managerId 业务员id
     * @param loanId    所有贷款id
     * @param content   描述内容
     * @return 请求参数
     */
    public String getLoanParam(boolean isUpdate, int pactID, int managerId, String loanId, String content,
                               String companyID, String pactNumberId, LoanInfo loanInfo) {
        if (!isUpdate) {
            if (!TextUtils.isEmpty(loanId)) {
                loanId = loanId + ",";
            } else {
                loanId = "";
            }
        }
        Map<String, Object> map = new HashMap<>();
        if (!isUpdate) {
            map.put("Action", "ADD");
        } else {
            map.put("Action", "UPD");
        }
        map.put("DBMarker", "CFS_Loan");
        map.put("Marker", "HQServer");
        map.put("Function", "7E8BAFE2-64A6-4693-9E74-95BECCC899EE");
        Map<String, Object> map1 = new HashMap<>();
        if (isUpdate) {
            map1.put("ID", loanInfo.getLoanId());
        } else {
            map1.put("ID", "");
        }
        map1.put("L1", loanInfo.getLoanType());
        map1.put("L2", loanInfo.getBankId());
        map1.put("L3", loanInfo.getProductId());
        map1.put("L9", loanInfo.getMortgage());
        map1.put("L4", loanInfo.getAmount());
        map1.put("L8", loanInfo.getPoundage());
        map1.put("Status", loanInfo.getScheduleId());
        map1.put("CompanyID", companyID);
        map1.put("L7", loanInfo.getPercent());
        map1.put("L6", loanInfo.getChargeWay());
        map1.put("L11", loanInfo.getNote());
        map1.put("ContractID", pactID);
        map1.put("DelMarker", "");
        map1.put("InsertTime", "");
        map1.put("UpdateTime", "");
        map1.put("CustomerIDs", loanInfo.getCustomerId());//ids
        map1.put("CustomerNames", loanInfo.getCustomer());//names
        map1.put("S3", managerId);
        map1.put("S5", pactNumberId);//合同编号
        if (isUpdate) {
            map1.put("LoanCode", loanInfo.getLoanCode());
        } else {
            map1.put("LoanCode", "");
        }
        String s = JSON.toJSONString(map1);
        List<Object> list1 = new ArrayList<>();
        list1.add("");
        list1.add(pactID);//合同id
        list1.add(loanId);
        String builder;
        if (isUpdate) {
            builder = getLoanDescriptions(loanId, content, loanInfo);
        } else {
            if (TextUtils.isEmpty(content)) {
                builder = getDescriptions(loanInfo);
            } else {
                builder = content + "," + getDescriptions(loanInfo);
            }
        }
        list1.add(builder);
        String s1 = JSON.toJSONString(list1);
        List<Object> lists = new ArrayList<>();
        lists.add("Mobile");
        lists.add(s);
        lists.add(s1);
        List<Object> list2 = new ArrayList<>();
        List<Object> list3 = new ArrayList<>();
        if (!isUpdate) {
            list2.add("");
            list2.add(pactID);
            list3.add("");
            list3.add(loanInfo.getCustomerId());
            list3.add("");
            list3.add(loanInfo.getCustomer());
            list3.add(Hawk.get("UserID"));
        } else {
            list3.add("");
            list3.add(loanInfo.getLoanId());
            list3.add(loanInfo.getCustomerId());
            list3.add(loanInfo.getLoanType());
        }
        String s2 = JSON.toJSONString(list2);
        String s4 = JSON.toJSONString(list3);
        lists.add(s2);
        lists.add(s4);
        map.put("ParamString", lists);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("LoanType", loanInfo.getLoanType());
        map2.put("BankId", loanInfo.getBankId());
        map2.put("ProductId", loanInfo.getProductId());
        map2.put("ApplyMoney", loanInfo.getAmount());
        map2.put("ContractId", pactID);
        String s3 = JSON.toJSONString(map2);
        map.put("Json", s3);
        map.put("TranName", "VDG");
        Logger.e("===添加贷款===> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    public String getLoanDescriptions(String ids, String description, LoanInfo loanInfo) {
        String[] idStrings = ids.split(",");
        String[] strings = description.split(",");
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < idStrings.length; i++) {
            if (idStrings[i].equals(loanInfo.getLoanId())) {
                strings[i] = "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() + "(" + loanInfo.getLoanTypeName().substring(0, 1) + ")" + "】" +
                        "申请: " + Utils.div(loanInfo.getAmount()) + "万" + " -- " +
                        (CacheProvide.getMortgageName(loanInfo.getMortgage()) == null ? "" : CacheProvide.getMortgageName(loanInfo.getMortgage())) +
                        " " + loanInfo.getSchedule() + " " + DateUtil.getCurDate(System.currentTimeMillis());
            }
            buffer.append(strings[i]);
            if (i != idStrings.length - 1) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }

    public String getDescriptions(LoanInfo loanInfo) {
        String str = "";
        if (loanInfo != null) {
            str = "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() + "(" + loanInfo.getLoanTypeName().substring(0, 1) + ")" + "】" +
                    "申请: " + Utils.div(loanInfo.getAmount()) + "万" + " -- " + (loanInfo.getMortgageName() == null ? "" : loanInfo.getMortgageName()) +
                    " 未提交 " + DateUtil.getCurDate(System.currentTimeMillis());
        }
        return str;
    }
}
