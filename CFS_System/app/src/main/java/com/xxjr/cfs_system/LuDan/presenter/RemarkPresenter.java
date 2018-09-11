package com.xxjr.cfs_system.LuDan.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.RemarkVInter;
import com.xxjr.cfs_system.ViewsHolder.PopChoose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.ChooseType;
import entity.LoanInfo;
import rx.Subscription;
import rx.functions.Action1;

public class RemarkPresenter extends BasePresenter<RemarkVInter, ModelImp> {
    private Subscription subscription;
    private PopupWindow popWindow = null;
    private int fallBackType = -1;

    @Override
    protected ModelImp getModel() {
        return new ModelImp();
    }

    @Override
    public void setDefaultValue() {
        switch (getView().getRemarkType()) {
            case 0:
                if (TextUtils.isEmpty(getView().getRemark())) {
                    getView().showMsg("请填黄单备注!");
                } else {
                    getData(0, getModel().getParam(getYellowLsit(getView().getLoanInfo()),
                            getView().getLoanInfo().getLoanType() == 1 ? "UpdateLoanMortgage" : "UpdateLoanCredit"), true);
                }
                break;
            case 1:
                if (TextUtils.isEmpty(getView().getRemark())) {
                    getView().showMsg("备注不能为空!");
                } else {
                    getData(1, getModel().getParam(getScheduleNoteLsit(getView().getLoanInfo()), "AddLoanRemark"), true);
                }
                break;
            case 2:
                getData(2, getModel().getParam(getPrePaymentLsit(getView().getLoanInfo()), "UpdateApplyPrepayment"), true);
                break;
            case 3:
                if (fallBackType == -1) {
                    getView().showMsg("请先选择退款类型");
                } else if (TextUtils.isEmpty(getView().getRemark())) {
                    getView().showMsg("备注不能为空!");
                } else {
                    getData(0, getModel().getParam(getYellowLsit(getView().getLoanInfo()),
                            getView().getLoanInfo().getLoanType() == 1 ? "UpdateLoanMortgage" : "UpdateLoanCredit"), true);
                }
                break;
        }
    }

    public void initSub() {
        subscription = RxBus.getInstance().toObservable(0, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                if (popWindow != null && popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                fallBackType = chooseType.getId();
                getView().setTextContent(chooseType.getContent());
            }
        });
    }

    //黄单参数
    private List<Object> getYellowLsit(LoanInfo loanInfo) {
        List<Object> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("LoanId", loanInfo.getLoanId());
        map1.put("LoanCode", loanInfo.getLoanCode());
        map1.put("ContractID", loanInfo.getContractId());
        if (getView().getRemarkType() == 3) {
            map1.put("LoanStatus", loanInfo.getScheduleId());
        } else {
            map1.put("LoanStatus", getView().getYellowType());
        }
        map1.put("OperateType", getView().getRemarkType() == 3 ? 2 : 1);
        map1.put("ServicePeople", Hawk.get("UserID"));
        map1.put("MortgageSehedule", loanInfo.getLoanType() == 1 ? 2 : 1);
        map1.put("LastOpetateTime", loanInfo.getUpdateTime());
        map1.put("LoanFollowDescription", getView().getRemark());
        if (loanInfo.getLoanType() == 1) {//抵押贷
            map1.put("IsForeclosureFloor", loanInfo.getIsForeclosureFloor());
        }
        map1.put("BankManagerID", loanInfo.getManagerId());
        map1.put("ReplyMoney", loanInfo.getReplyAmount());
        map1.put("ReplyReciver", loanInfo.getAccountName());
        map1.put("ReplyBankAccount", loanInfo.getAccount());
        map1.put("ReplyProvider", loanInfo.getOffer());
        map1.put("BankReplyTime", loanInfo.getApprovalTime());//银行批复时间
        map1.put("LendingMoney", loanInfo.getLendingAmount());
        map1.put("LendingDate", loanInfo.getLendingTime());
        map1.put("MonthPayMoney", loanInfo.getMonthAmount());
        map1.put("MonthPayDay", loanInfo.getMonthDate());
        map1.put("ReimbursementDeadline", loanInfo.getReturnTime());
        map1.put("CaseDate", loanInfo.getOverTime());
        map1.put("CaseRemark", loanInfo.getOverNote());
        if (getView().getRemarkType() == 3) {
            map1.put("FallBackType", fallBackType);
        }
        String str = JSON.toJSONString(map1);
        list.add(str);
        return list;
    }

    //进度备注
    private List<Object> getScheduleNoteLsit(LoanInfo loanInfo) {
        List<Object> list = new ArrayList<>();
        list.add(loanInfo.getLoanId());
        list.add(loanInfo.getScheduleId());
        list.add(getView().getRemark());
        return list;
    }

    //提前还款参数
    private List<Object> getPrePaymentLsit(LoanInfo loanInfo) {
        List<Object> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("LoanId", loanInfo.getLoanId());
        map1.put("LoanCode", loanInfo.getLoanCode());
        map1.put("ServicePeople", Hawk.get("UserID"));
        map1.put("IsPrePayment", true);
        map1.put("LoanFollowDescription", getView().getRemark());
        String str = JSON.toJSONString(map1);
        list.add(str);
        return list;
    }

    public void showPop(View parent) {
        popWindow = PopChoose.showChooseType((Context) getView(), parent, "还款类型", getPopType(), 0, false);
    }

    private List<ChooseType> getPopType() {
        List<ChooseType> chooseTypes = new ArrayList<>();
        ChooseType chooseType;
        for (int i = 4; i < 6; i++) {//4->操作失误 5->客户失信
            chooseType = new ChooseType();
            chooseType.setId(i);
            if (i == 4) {
                chooseType.setContent("操作失误");
            } else {
                chooseType.setContent("客户失信");
            }
            chooseTypes.add(chooseType);
        }
        return chooseTypes;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        getView().complete();
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }

    public void rxDeAttach() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
