package com.xxjr.cfs_system.LuDan.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.modelimp.TaskDetailsMImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.TaskDetailsVInter;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.CFSUtils;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailsPresenter extends BasePresenter<TaskDetailsVInter, TaskDetailsMImp> {
    @Override
    protected TaskDetailsMImp getModel() {
        return new TaskDetailsMImp();
    }

    @Override
    public void setDefaultValue() {
        getView().initRecycler(getModel().getItemList(new JSONArray(), getView().getLoanInfo()));
        getScheduleData();
    }

    public void getScheduleData() {
        getData(0, model.getParam(getParamList(), "GetLoanRemark"), true);
    }

    private List<Object> getParamList() {
        List<Object> list = new ArrayList<>();
        list.add(getView().getLoanInfo().getLoanId());
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        JSONObject jsonObject = JSON.parseObject(data.getReturnString());
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray("FollowRemarkList");
            int Status = jsonObject.getIntValue("Status");
            if (getView().getisUpdateLoan()) {
                upLoan(jsonObject);
            }
            getView().getLoanInfo().setClerkName(model.getClerkName(jsonArray));
            getView().getLoanInfo().setMortgageName(jsonObject.getString("MortgageMember") == null ? "" : jsonObject.getString("MortgageMember"));
            getView().refreshData(getModel().getItemList(jsonArray, getView().getLoanInfo()));
            getView().setIvShow(getModel().getScheduleBtData(Status, getView().getLoanInfo(),
                    CFSUtils.getPermitValue(Hawk.get("PermitValue", ""), getView().getLoanInfo().getLoanType())));
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }

    //更新数据源
    private void upLoan(JSONObject object) {
        getView().getLoanInfo().setIsForeclosureFloor(object.getBoolean("IsForeclosureFloor") == null ? false : object.getBoolean("IsForeclosureFloor"));
        getView().getLoanInfo().setForeclosureTime(object.get("ForeclosureTime") == null ? null : object.getString("ForeclosureTime"));
        getView().getLoanInfo().setPrepayment(object.getBoolean("IsPrepayment") == null ? false : object.getBoolean("IsPrepayment"));
        getView().getLoanInfo().setScheduleId(object.getIntValue("Status"));
        getView().getLoanInfo().setManagerId(object.getIntValue("BankManagerID"));
        getView().getLoanInfo().setUpdateTime(object.getString("LastOperateTime"));
        getView().getLoanInfo().setReplyAmount(object.getDoubleValue("ReplyMoney"));
        getView().getLoanInfo().setAccountName(object.getString("ReplyReciver"));
        getView().getLoanInfo().setAccount(object.getString("ReplyBankAccount"));
        getView().getLoanInfo().setOffer(object.getString("ReplyProvider"));
        getView().getLoanInfo().setApprovalTime(object.getString("BankReplyTime"));
        getView().getLoanInfo().setLendingAmount(object.getDoubleValue("LendingMoney"));
        getView().getLoanInfo().setLendingTime(object.getString("LendingDate"));
        getView().getLoanInfo().setMonthAmount(object.getDoubleValue("MonthPayMoney"));
        getView().getLoanInfo().setMonthDate(object.getString("MonthPayDay"));
        getView().getLoanInfo().setReturnTime(object.getString("ReimbursementDeadline"));
        if (getView().getLoanInfo().getLoanType() == 1) {
            int member = object.get("ForeclosureFloorId") != null ? object.getIntValue("ForeclosureFloorId") : 0;
            getView().getLoanInfo().setMember(member);
        }
    }
}
