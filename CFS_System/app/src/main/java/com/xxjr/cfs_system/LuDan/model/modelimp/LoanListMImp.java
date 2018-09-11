package com.xxjr.cfs_system.LuDan.model.modelimp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.CommonItem;
import entity.LoanInfo;

public class LoanListMImp extends ModelImp {
    private JSONArray bankArray;
    private JSONArray productArray;
    private JSONArray loanTypeArray;
    private JSONArray loanStateArray;

    public void getArray() {
        if (bankArray == null) {
            bankArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanBankType"), ""));
        }
        if (productArray == null) {
            productArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanProductType"), ""));
        }
        if (loanTypeArray == null) {
            loanTypeArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoansType"), ""));
        }
        if (loanStateArray == null) {
            loanStateArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanStateType"), ""));
        }
    }

    public String getLoanParam(List<Object> list, String tranName) {
        Map<String, Object> map = new HashMap<>();
        map.put("Action", "GET");
        map.put("DBMarker", "DB_CFS_Loan");
        map.put("Function", "Page");
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("ParamString", list);
        map.put("TranName", tranName);
        Logger.e("==贷款列表==> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    public String getDayNotQuery(int dayNot) {
        String query = "";
        switch (dayNot) {
            case 1:
                query = " AND DATEDIFF( DAY, UpdateTime, GETDATE() )>3";
//                query = " and UpdateTime < '" + DateUtil.getOldDate(-3) + "' AND Status NOT IN (8, 112, -1, -2) ";
                break;
            case 2:
                query = " AND DATEDIFF( DAY, UpdateTime, GETDATE() )>7";
//                query = " and UpdateTime < '" + DateUtil.getOldDate(-7) + "'  AND Status NOT IN (8, 112, -1, -2) ";
                break;
        }
        return query;
    }

    public String getTaskQuery(int loanType, int schedule) {
        String query = "";
        switch (schedule) {
            case 1:
                query = " AND Status IN(0)";
                break;
            case 2:
                query = " AND Status IN(1)";
                break;
            case 3:
                if (loanType == 0) {
                    query = " AND Status IN(2,102)";
                } else if (loanType == 1) {
                    query = " AND Status IN(102)";
                } else {
                    query = " AND Status IN(2)";
                }
                break;
            case 4:
                if (loanType == 0) {
                    query = " AND Status IN(3,103)";
                } else if (loanType == 1) {
                    query = " AND Status IN(103)";
                } else {
                    query = " AND Status IN(3)";
                }
                break;
            case 5:
                if (loanType == 0) {
                    query = " AND Status IN(104,107)";
                } else if (loanType == 1) {
                    query = " AND Status IN(104)";
                } else {
                    query = " AND Status IN(4)";
                }
                break;
            case 6:
                if (loanType == 0 || loanType == 1) {
                    query = " AND Status IN(105)";
                } else {
                    query = " AND Status IN(5,50,51,6,-3,-4,-5)";
                }
                break;
            case 7:
                if (loanType == 0 || loanType == 1) {
                    query = " AND Status IN(106)";
                } else {
                    query = " AND Status IN(8,-1,-2,-6)";
                }
                break;
            case 8:
                if (loanType == 0) {
                    query = " AND Status IN(4,108)";
                } else if (loanType == 1) {
                    query = " AND Status IN(107)";
                }
                break;
            case 9:
                if (loanType == 0) {
                    query = " AND Status IN(5,50,51,6,109,1090,1091,110,-3,-4,-5)";
                } else if (loanType == 1) {
                    query = " AND Status IN(108)";
                }
                break;
            case 10:
                if (loanType == 0) {
                    query = " AND Status IN(8,112,-1,-2,-6)";
                } else if (loanType == 1) {
                    query = " AND Status IN(109,1090,1091,110,-3,-4,-5)";
                }
                break;
            case 11:
                query = " AND Status IN(112,-1,-2,-6)";
                break;
        }
        return query;
    }

    public List<CommonItem> getTitles(JSONObject jsonObject, int loanType, int schedule) {
        List<CommonItem> list = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 5; i++) {
            commonItem = new CommonItem();
            commonItem.setClick(i == schedule);
            switch (i) {
                case 0:
                    commonItem.setName("全部(" + jsonObject.getString("AllCount") + ")");
                    break;
                case 1:
                    commonItem.setName("未提交(" + jsonObject.getString("NonSubmit") + ")");
                    break;
                case 2:
                    commonItem.setName("待面签(" + jsonObject.getString("PreSignFace") + ")");
                    break;
                case 3:
                    commonItem.setName("待补资料(" + jsonObject.getString("MaterialNotFill") + ")");
                    break;
                case 4:
                    commonItem.setName("待批复(" + jsonObject.getString("PreApproval") + ")");
                    break;
            }
            list.add(commonItem);
        }
        if (loanType == 0 || loanType == 1) {
            CommonItem commonItem1;
            for (int i = 5; i < 8; i++) {
                commonItem1 = new CommonItem();
                commonItem1.setClick(i == schedule);
                switch (i) {
                    case 5:
                        commonItem1.setName("待抵押(抵)(" + jsonObject.getString("ReplyPreMortgage") + ")");
                        break;
                    case 6:
                        commonItem1.setName("待赎楼(抵)(" + jsonObject.getString("PreForeclosureFloor") + ")");
                        break;
                    case 7:
                        commonItem1.setName("待取证(抵)(" + jsonObject.getString("PreForensics") + ")");
                        break;
                }
                list.add(commonItem1);
            }
            if (loanType == 1) {
                CommonItem commonItem2;
                for (int i = 8; i < 12; i++) {
                    commonItem2 = new CommonItem();
                    commonItem2.setClick(schedule == i);
                    switch (i) {
                        case 8:
                            commonItem2.setName("已取证待抵押(" + jsonObject.getString("ForensicsPreMortgage") + ")");
                            break;
                        case 9:
                            commonItem2.setName("待放款(" + jsonObject.getString("PreLending") + ")");
                            break;
                        case 10:
                            commonItem2.setName("待结算(" + jsonObject.getString("PreSettlement") + ")");
                            break;
                        case 11:
                            commonItem2.setName("已结案(" + jsonObject.getString("CaseSucced") + ")");
                            break;
                    }
                    list.add(commonItem2);
                }
            } else {
                CommonItem commonItem2;
                for (int i = 8; i < 11; i++) {
                    commonItem2 = new CommonItem();
                    commonItem2.setClick(schedule == i);
                    switch (i) {
                        case 8:
                            commonItem2.setName("待放款(" + jsonObject.getString("PreLending") + ")");
                            break;
                        case 9:
                            commonItem2.setName("待结算(" + jsonObject.getString("PreSettlement") + ")");
                            break;
                        case 10:
                            commonItem2.setName("已结案(" + jsonObject.getString("CaseSucced") + ")");
                            break;
                    }
                    list.add(commonItem2);
                }
            }
        } else {
            CommonItem commonItem1;
            for (int i = 5; i < 8; i++) {
                commonItem1 = new CommonItem();
                commonItem1.setClick(i == schedule);
                switch (i) {
                    case 5:
                        commonItem1.setName("待放款(" + jsonObject.getString("PreLending") + ")");
                        break;
                    case 6:
                        commonItem1.setName("待结算(" + jsonObject.getString("PreSettlement") + ")");
                        break;
                    case 7:
                        commonItem1.setName("已结案(" + jsonObject.getString("CaseSucced") + ")");
                        break;
                }
                list.add(commonItem1);
            }
        }
        return list;
    }

    public List<LoanInfo> getLoanList(String data) {
        List<LoanInfo> temp = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        if (jsonArray != null && jsonArray.size() != 0) {
            LoanInfo loanInfo;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                loanInfo = new LoanInfo();
                loanInfo.setLoanIDs(object.getString("LoanIDs") == null ? "" : object.getString("LoanIDs"));
                loanInfo.setLoanDescription(object.getString("LoanDescription") == null ? "" : object.getString("LoanDescription"));
                loanInfo.setIsForeclosureFloor(object.getBoolean("IsForeclosureFloor") == null ? false : object.getBoolean("IsForeclosureFloor"));
                loanInfo.setForeclosureTime(object.get("ForeclosureTime") == null ? null : object.getString("ForeclosureTime"));
                loanInfo.setPrepayment(object.getBoolean("IsPrepayment") == null ? false : object.getBoolean("IsPrepayment"));
                loanInfo.setUpdateTime(object.getString("UpdateTime"));
                loanInfo.setLoanCode(object.getString("LoanCode"));
                loanInfo.setPactCode(object.getString("ContractCode"));
                loanInfo.setClerkID(object.getIntValue("Clerk"));
                loanInfo.setCompanyID(object.getString("CompanyID"));
                loanInfo.setBankId(object.getIntValue("Bank"));
                loanInfo.setBankName(CacheProvide.getBank(bankArray, object.getIntValue("Bank")));
                loanInfo.setAmount(object.getDoubleValue("ApplyValue"));
                loanInfo.setProductId(object.getIntValue("BankProuduct"));
                loanInfo.setProductName(CacheProvide.getProduct(productArray, object.getIntValue("BankProuduct")));
                loanInfo.setLoanTypeName(CacheProvide.getLoanType(loanTypeArray, object.getIntValue("LoanType")));
                loanInfo.setLoanType(object.getIntValue("LoanType"));
                loanInfo.setScheduleId(object.getIntValue("Status"));
                loanInfo.setSchedule(CacheProvide.getLoanStatus(loanStateArray, object.getIntValue("Status")));
                loanInfo.setLoanId(String.valueOf(object.getIntValue("ID")));
                loanInfo.setCustomer(object.getString("CustomerNames"));
                loanInfo.setCustomerId(object.getString("CustomerIDs"));
                loanInfo.setMortgage(object.getIntValue("Mortgage"));
                loanInfo.setContractId(object.getIntValue("ContractID"));
                loanInfo.setMortgageScore(object.getFloatValue("MortgageScore"));
                loanInfo.setPassAuditCostMoney(object.get("PassAuditCostMoney") != null ? object.getDoubleValue("PassAuditCostMoney") : 0);
                temp.add(loanInfo);
            }
        }
        return temp;
    }

    public List<LoanInfo> getTaskLoanList(String data) {
        List<LoanInfo> temp = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        if (jsonArray != null && jsonArray.size() != 0) {
            LoanInfo loanInfo;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                loanInfo = new LoanInfo();
                loanInfo.setLoanIDs(object.getString("LoanIDs") == null ? "" : object.getString("LoanIDs"));
                loanInfo.setLoanDescription(object.getString("LoanDescription") == null ? "" : object.getString("LoanDescription"));
                loanInfo.setYellowTime(object.getString("YellowTime") == null ? "" : object.getString("YellowTime"));
                loanInfo.setRedTime(object.getString("RedTime") == null ? "" : object.getString("RedTime"));
                loanInfo.setIsForeclosureFloor(object.getBoolean("IsForeclosureFloor") == null ? false : object.getBoolean("IsForeclosureFloor"));
                loanInfo.setForeclosureTime(object.get("ForeclosureTime") == null ? null : object.getString("ForeclosureTime"));
                loanInfo.setPrepayment(object.getBoolean("IsPrepayment") == null ? false : object.getBoolean("IsPrepayment"));
                loanInfo.setUpdateTime(object.getString("UpdateTime"));
                loanInfo.setLoanCode(object.getString("LoanCode"));
                loanInfo.setPactCode(object.getString("ContractCode"));
                loanInfo.setClerkID(object.getIntValue("S3"));
                loanInfo.setCompanyID(object.getString("CompanyID"));
                loanInfo.setBankId(object.getIntValue("L2"));
                loanInfo.setBankName(CacheProvide.getBank(bankArray, object.getIntValue("L2")));
                loanInfo.setAmount(object.getDoubleValue("L4"));
                loanInfo.setProductId(object.getIntValue("L3"));
                loanInfo.setProductName(CacheProvide.getProduct(productArray, object.getIntValue("L3")));
                loanInfo.setChargeWay(object.getIntValue("L6"));
                loanInfo.setLoanTypeName(CacheProvide.getLoanType(loanTypeArray, object.getIntValue("L1")));
                loanInfo.setLoanType(object.getIntValue("L1"));
                loanInfo.setScheduleId(object.getIntValue("Status"));
                loanInfo.setSchedule(CacheProvide.getLoanStatus(loanStateArray, object.getIntValue("Status")));
                loanInfo.setLoanId(String.valueOf(object.getIntValue("ID")));
                loanInfo.setPoundage(object.getDoubleValue("L8"));
                loanInfo.setCustomer(object.getString("CustomerNames"));
                loanInfo.setCustomerId(object.getString("CustomerIDs"));
                loanInfo.setMortgage(object.getIntValue("L9"));
                loanInfo.setNote(object.getString("L11"));
                loanInfo.setPercent(object.getFloatValue("L7"));
                loanInfo.setContractId(object.getIntValue("ContractID"));
                if (object.get("L16") == null) {
                    loanInfo.setManagerId(0);
                } else {
                    loanInfo.setManagerId(object.getIntValue("L16"));
                }

                loanInfo.setMember(object.get("L13") == null ? 0 : object.getIntValue("L13"));
                if (object.get("L14") != null && object.getBooleanValue("L14")) {
                    loanInfo.setIsNotary("1");
                } else {
                    loanInfo.setIsNotary("0");
                }
                loanInfo.setNotaryTime(object.getString("L15") == null ? "" : Utils.getTime(object.getString("L15")));
                loanInfo.setNotaryNote(object.getString("L19"));
                loanInfo.setReplyAmount(object.get("A1") == null ? 0 : object.getDouble("A1"));
                loanInfo.setAccountName(object.getString("A2"));
                loanInfo.setAccount(object.getString("A3"));
                loanInfo.setOffer(object.getString("A4"));
                loanInfo.setLendingAmount(object.get("A16") == null ? 0 : object.getDouble("A16"));
                loanInfo.setLendingTime(object.getString("A17") == null ? "" : Utils.getTime(object.getString("A17")));
                loanInfo.setMonthAmount(object.get("A5") == null ? 0 : object.getDouble("A5"));
                loanInfo.setMonthDate(object.get("A6") == null ? "" : String.valueOf(object.getIntValue("A6")));
                loanInfo.setReturnTime(object.getString("A7") == null ? "" : Utils.getTime(object.getString("A7")));
                loanInfo.setOverTime(object.getString("A10") == null ? "" : Utils.getTime(object.getString("A10")));
                loanInfo.setOverNote(object.getString("A13"));
                loanInfo.setApprovalTime(object.getString("BankReplyTime") == null ? "" : Utils.getTime(object.getString("BankReplyTime")));
                loanInfo.setPassAuditCostMoney(object.get("PassAuditCostMoney") != null ? object.getDoubleValue("PassAuditCostMoney") : 0);
                loanInfo.setIsCase(object.getIntValue("IsCase"));
                temp.add(loanInfo);
            }
        }
        return temp;
    }

}
