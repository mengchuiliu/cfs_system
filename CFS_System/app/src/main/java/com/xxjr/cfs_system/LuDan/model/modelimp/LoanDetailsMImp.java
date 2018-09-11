package com.xxjr.cfs_system.LuDan.model.modelimp;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.ChooseType;
import entity.CommonItem;
import entity.LoanInfo;
import entity.Schedule;

public class LoanDetailsMImp extends ModelImp {

    //跟踪列表参数
    public String getScheduleParam(List<Object> list, String tranName, String DBMarker) {
        Map<String, Object> map = new HashMap<>();
        map.put("Action", "GET");
        map.put("DBMarker", DBMarker);
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("ParamString", list);
        map.put("TranName", tranName);
        Logger.e("==跟踪==> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    //多次放宽列表参数
    public String getLendParam(List<Object> list, String tranName, String Action) {
        Map<String, Object> map = new HashMap<>();
        map.put("Action", Action);
        map.put("DBMarker", "DB_CFS_Loan");
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("ParamString", list);
        map.put("TranName", tranName);
        return JSON.toJSONString(map);
    }

    public List<LoanInfo> getLendList(String data, List<ChooseType> chooseTypes) {
        List<LoanInfo> loanInfos = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        if (jsonArray != null && jsonArray.size() != 0) {
            LoanInfo loanInfo;
            for (int i = 0; i < jsonArray.size(); i++) {
                loanInfo = new LoanInfo();
                JSONObject object = jsonArray.getJSONObject(i);
                loanInfo.setClerkID(object.getIntValue("ID"));
                loanInfo.setIsForeclosureFloor(object.getBooleanValue("IsMain"));
                loanInfo.setLendingAmount(object.getDoubleValue("LendAmount"));
                loanInfo.setLendingTime(Utils.getTime(object.getString("LendDate")));
                loanInfo.setMonthAmount(object.getDoubleValue("MonthPay"));
                loanInfo.setMonthDate(object.getString("MonthPayDay") == null ? "" : object.getString("MonthPayDay"));
                loanInfo.setReturnTime(Utils.getTime(object.getString("PayDeadline")));
                loanInfo.setOverTime(Utils.getTime(object.getString("InsertTime")));
                loanInfo.setClerkName(object.getString("ServicePeopleName") == null ? "" : object.getString("ServicePeopleName"));
                loanInfo.setNote(object.getString("Remark"));
                loanInfo.setInterestRate(object.getDoubleValue("InterestRate"));
                loanInfo.setPaymentName(Utils.getTypeValue(chooseTypes, object.getIntValue("PaymentMethod")));
                loanInfo.setOtherAmount(object.getDoubleValue("OtherPay"));
                loanInfo.setOtherPayRemark(object.getString("OtherPayRemark"));
                loanInfo.setLendingPeriod(object.getIntValue("RepaymentPeriods"));//还款期数
                loanInfos.add(loanInfo);
            }
        }
        return loanInfos;
    }

    //跟踪列表数据
    public List<Schedule> getScheduleList(String data, int pos) {
        List<Schedule> schedules = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        if (jsonArray != null && jsonArray.size() != 0) {
            Schedule schedule;
            for (int i = 0; i < jsonArray.size(); i++) {
                schedule = new Schedule();
                JSONObject object = jsonArray.getJSONObject(i);
                if (pos == 9) {
                    schedule.setStatus(object.getString("LF3"));
                    schedule.setName(object.getString("ServiceName"));
                    schedule.setDate(Utils.getTimeFormat(object.getString("UpdateTime")));
                } else {
                    int AuditStatus = object.getIntValue("AuditStatus");
                    String audit = "";
                    if (AuditStatus == 1) {
                        audit = "  审核通过";
                    } else if (AuditStatus == 2) {
                        audit = "  审核拒绝";
                    }
                    String string = object.getString("ClerkName") + "--" + CacheProvide.getCostType(object.getIntValue("CostType"))
                            + ":" + (new BigDecimal(object.getDoubleValue("Cost")).setScale(2, BigDecimal.ROUND_DOWN).toString()) + "元" +
                            audit + "  备注:" + object.getString("ReMark");
                    schedule.setStatus(string);
                    schedule.setName(object.getString("ServiceName"));
                    schedule.setDate(Utils.getTimeFormat(object.getString("UpdateTime")));
                }
                schedules.add(schedule);
            }
        }
        return schedules;
    }

    //数据源显示
    public List<CommonItem> getItemList(LoanInfo loanInfo, boolean IsNotary, boolean IsRedeem) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem1;
        for (int i = 0; i < 15; i++) {
            commonItem1 = new CommonItem();
            switch (i) {
                case 0:
                    commonItem1.setType(0);
                    break;
                case 1:
                    commonItem1.setType(1);
                    commonItem1.setName("贷款信息" + "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() +
                            "(" + (TextUtils.isEmpty(loanInfo.getLoanTypeName()) ? "" : loanInfo.getLoanTypeName().substring(0, 1)) + ")" + "】");
                    commonItem1.setIcon(R.mipmap.icon_loan);
                    break;
                case 2:
                    commonItem1.setType(2);
                    commonItem1.setName("贷款编号：");
                    commonItem1.setContent(loanInfo.getLoanCode());
                    break;
                case 3:
                    commonItem1.setType(2);
                    commonItem1.setName("申请金额：");
                    commonItem1.setContent(Utils.parseMoney(new BigDecimal(loanInfo.getAmount())));
                    break;
                case 4:
                    commonItem1.setType(2);
                    commonItem1.setName("银行产品：");
                    commonItem1.setContent(loanInfo.getProductName());
                    commonItem1.setEnable(false);
                    break;
                case 5:
                    commonItem1.setType(2);
                    commonItem1.setName("贷款类型：");
                    commonItem1.setContent(loanInfo.getLoanTypeName());
                    commonItem1.setEnable(false);
                    break;
                case 6:
                    commonItem1.setType(2);
                    commonItem1.setName("申请客户：");
                    commonItem1.setContent(loanInfo.getCustomer());
                    break;
                case 7:
                    commonItem1.setType(2);
                    commonItem1.setName(" 按 揭 员 ：");
                    commonItem1.setContent(CacheProvide.getMortgageName(loanInfo.getMortgage()));
                    break;
                case 8:
                    commonItem1.setType(2);
                    commonItem1.setName("最新进度：");
                    commonItem1.setContent(loanInfo.getSchedule());
                    break;
                case 9:
                    commonItem1.setType(3);
                    commonItem1.setName("进度跟踪");
                    commonItem1.setClick(false);
                    break;
                case 10:
                    commonItem1.setType(4);
                    commonItem1.setClick(false);
                    break;
                case 11:
                    commonItem1.setType(3);
                    commonItem1.setName("成本跟踪");
                    commonItem1.setClick(false);
                    break;
                case 12:
                    commonItem1.setType(4);
                    commonItem1.setClick(false);
                    break;
                case 13:
                    commonItem1.setType(0);
                    break;
                case 14:
                    commonItem1.setType(1);
                    commonItem1.setName("放款列表");
                    commonItem1.setIcon(R.mipmap.loanlist);
                    break;
            }
            commonItems.add(commonItem1);
        }
        if (IsNotary) {//公证
            commonItems.addAll(getNotaryItem());
        } else if (IsRedeem) {//赎楼
            commonItems.addAll(getRedeemItem(loanInfo));
        } else {
            CommonItem commonItem = new CommonItem();
            commonItem.setType(8);
            commonItem.setList(new ArrayList());
            commonItems.add(commonItem);
        }
        return commonItems;
    }


    //批复数据
    private List<CommonItem> getApprovalItem(LoanInfo loanInfo) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 6; i++) {
            commonItem = new CommonItem();
            switch (i) {
                case 0:
                    commonItem.setType(5);
                    commonItem.setName("批复金额");
                    commonItem.setContent(new BigDecimal(loanInfo.getReplyAmount()).toString());
                    commonItem.setClick(true);
                    commonItem.setLineShow(true);
                    break;
                case 1:
                    commonItem.setType(5);
                    commonItem.setName("收款人");
                    commonItem.setContent(loanInfo.getAccountName() == null ? "" : loanInfo.getAccountName());
                    commonItem.setClick(false);
                    commonItem.setLineShow(true);
                    break;
                case 2:
                    commonItem.setType(5);
                    commonItem.setName("收款账号");
                    commonItem.setContent(loanInfo.getAccount() == null ? "" : loanInfo.getAccount());
                    commonItem.setClick(false);
                    commonItem.setLineShow(true);
                    break;
                case 3:
                    commonItem.setType(5);
                    commonItem.setName("提供者");
                    commonItem.setContent(loanInfo.getOffer() == null ? "" : loanInfo.getOffer());
                    commonItem.setClick(false);
                    commonItem.setLineShow(true);
                    break;
                case 4:
                    commonItem.setType(5);
                    commonItem.setName("批复日期");
                    commonItem.setContent(loanInfo.getApprovalTime());
                    commonItem.setClick(false);
                    commonItem.setLineShow(false);
                    break;
                case 5:
                    commonItem.setType(0);
                    break;
            }
            commonItems.add(commonItem);
        }
        return commonItems;
    }

    private List<CommonItem> getNotaryItem() {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem = new CommonItem();
        commonItem.setType(5);
        commonItem.setName("公证时间");
        commonItem.setPosition(-3);
        commonItem.setClick(false);
        commonItem.setLineShow(true);
        CommonItem commonItem1 = new CommonItem();
        commonItem1.setType(7);
        commonItem1.setName("公证书");
        commonItem1.setContent("");
        commonItems.add(commonItem);
        commonItems.add(commonItem1);
        return commonItems;
    }

    private List<CommonItem> getRedeemItem(LoanInfo loanInfo) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem = new CommonItem();
        commonItem.setType(6);
        commonItem.setName("是否赎楼");
        commonItem.setClick(loanInfo.getIsForeclosureFloor());
        commonItem.setLineShow(true);
        commonItem.setPosition(-1);

        CommonItem commonItem1 = new CommonItem();
        commonItem1.setType(5);
        commonItem1.setName("赎楼员");
        commonItem1.setContent(CacheProvide.getMemberName(loanInfo.getMember()));
        commonItem1.setClick(false);
        commonItem1.setLineShow(false);
        if (loanInfo.getIsForeclosureFloor()) {
            commonItem1.setPosition(-1);
        } else {
            commonItem1.setPosition(-2);
        }

        commonItems.add(commonItem);
        commonItems.add(commonItem1);
        return commonItems;
    }
}
