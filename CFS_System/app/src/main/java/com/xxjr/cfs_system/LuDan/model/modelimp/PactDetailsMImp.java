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
import com.xxjr.cfs_system.tools.CFSUtils;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.ClientInfo;
import entity.CommonItem;
import entity.Contract;
import entity.LoanInfo;

/**
 * Created by Administrator on 2017/8/25.
 * 合同详情数据处理类
 */

public class PactDetailsMImp extends ModelImp {
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

    //请求参数
    public String getLoanParam(List<Object> list, String tranName) {
        Map<String, Object> map = new HashMap<>();
        map.put("Action", "GET");
//        map.put("DBMarker", "CFS_Loan");
        map.put("DBMarker", "DB_CFS_Loan");
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("ParamString", list);
        map.put("TranName", tranName);
        Logger.e("==合同贷款列表==> %s" + JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    //界面数据源
    public List<CommonItem> getItemList(Contract contract, List<ClientInfo> infos) {
        List<String> permits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "803");
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("ContractStatus"), ""));
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 17; i++) {
            commonItem = new CommonItem();
            switch (i) {
                case 0:
                    commonItem.setType(3);
                    break;
                case 1:
                    commonItem.setType(0);
                    commonItem.setIcon(R.mipmap.icon_compact);
                    commonItem.setName("合同信息");
                    commonItem.setPosition(contract.getS12());
                    break;
                case 2:
                    commonItem.setType(1);
                    commonItem.setName("签约门店");
                    commonItem.setContent(CacheProvide.getStoreName(contract.getCompanyID()));
                    break;
                case 3:
                    commonItem.setType(1);
                    commonItem.setName("签约日期");
                    if (contract.getS1().contains("T")) {
                        commonItem.setContent(contract.getS1().substring(0, contract.getS1().indexOf("T")));
                    } else {
                        commonItem.setContent(contract.getS1());
                    }
                    break;
                case 4:
                    commonItem.setType(1);
                    commonItem.setName("客户经理");
                    commonItem.setContent(contract.getClerkName() == null ? "" : contract.getClerkName());
                    break;
                case 5:
                    commonItem.setType(1);
                    commonItem.setName(" 谈 单 员 ");
                    commonItem.setLineShow(TextUtils.isEmpty(contract.getAccompanyPeopleName()));
                    commonItem.setContent(contract.getAccompanyPeopleName());
                    break;
                case 6:
                    commonItem.setType(1);
                    commonItem.setName("签约见证人");
                    commonItem.setLineShow(TextUtils.isEmpty(contract.getWitnessName()));
                    commonItem.setContent(contract.getWitnessName());
                    break;
                case 7:
                    commonItem.setType(1);
                    commonItem.setName("合同编号");
                    commonItem.setContent(contract.getS5());
                    break;
                case 8:
                    commonItem.setType(1);
                    commonItem.setName("合同项目");
                    commonItem.setContent(contract.getS6());
                    break;
                case 9:
                    commonItem.setType(1);
                    commonItem.setName("合同费用");
                    commonItem.setContent(contract.getS7());
                    break;
                case 10:
                    commonItem.setType(1);
                    commonItem.setName("合同状态");
                    commonItem.setContent(CacheProvide.getPactStatus(jsonArray, contract.getS12()));
                    break;
                case 11:
                    commonItem.setType(0);
                    commonItem.setIcon(R.mipmap.icon_client);
                    commonItem.setName("签约客户");
                    break;
                case 12:
                    commonItem.setType(2);
                    commonItem.setList(infos);
                    break;
                case 13:
                    commonItem.setType(3);
                    break;
                case 14:
                    commonItem.setType(3);
                    if (permits != null && permits.contains("E3")) {
                        commonItem.setEnable(true);
                    } else {
                        commonItem.setEnable(false);
                    }
                    break;
                case 15:
                    commonItem.setType(4);
                    commonItem.setName("合同资料");
                    commonItem.setIcon(R.mipmap.post);
                    if (permits != null && permits.contains("E3")) {
                        commonItem.setEnable(true);
                    } else {
                        commonItem.setEnable(false);
                    }
                    break;
                case 16:
                    commonItem.setType(2);
                    break;
            }
            commonItems.add(commonItem);
        }
        return commonItems;
    }

    //贷款信息
    public LoanInfo getLoanInfo(JSONObject object) {
        LoanInfo loanInfo = new LoanInfo();
        loanInfo.setIsForeclosureFloor(object.getBooleanValue("IsForeclosureFloor"));
        loanInfo.setPrepayment(object.getBoolean("IsPrepayment"));
        loanInfo.setUpdateTime(object.getString("UpdateTime"));
        loanInfo.setLoanCode(object.getString("LoanCode"));
        loanInfo.setPactCode(object.getString("S5"));
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
        loanInfo.setReplyAmount(object.get("A1") == null ? 0 : object.getDoubleValue("A1"));
        loanInfo.setAccountName(object.getString("A2"));
        loanInfo.setAccount(object.getString("A3"));
        loanInfo.setOffer(object.getString("A4"));
        loanInfo.setLendingAmount(object.get("A16") == null ? 0 : object.getDoubleValue("A16"));
        loanInfo.setLendingTime(object.getString("A17") == null ? "" : Utils.getTime(object.getString("A17")));
        loanInfo.setMonthAmount(object.get("A5") == null ? 0 : object.getDoubleValue("A5"));
        loanInfo.setMonthDate(object.get("A6") == null ? "1" : String.valueOf(object.getIntValue("A6")));
        loanInfo.setReturnTime(object.getString("A7") == null ? "" : Utils.getTime(object.getString("A7")));
        loanInfo.setOverTime(object.getString("A10") == null ? "" : Utils.getTime(object.getString("A10")));
        loanInfo.setOverNote(object.getString("A13"));
        loanInfo.setApprovalTime(object.getString("BankReplyTime") == null ? "" : Utils.getTime(object.getString("BankReplyTime")));
        return loanInfo;
    }
}
