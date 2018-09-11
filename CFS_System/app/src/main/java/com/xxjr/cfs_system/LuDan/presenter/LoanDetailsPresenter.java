package com.xxjr.cfs_system.LuDan.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.model.modelimp.LoanDetailsMImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.LoanDetailsVInter;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.DateUtil;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.ChooseType;
import entity.LoanInfo;
import entity.Schedule;
import rx.Subscription;
import rx.functions.Action1;
import timeselector.TimeSelector;

public class LoanDetailsPresenter extends BasePresenter<LoanDetailsVInter, LoanDetailsMImp> {
    private Subscription subscription;
    private TimeSelector timeSelector;
    private int clickPos = 0;
    private int timePos = 0;
    private List<ChooseType> chooseTypes;

    @Override
    protected LoanDetailsMImp getModel() {
        return new LoanDetailsMImp();
    }

    @Override
    public void setDefaultValue() {
        initRX();
        chooseTypes = Utils.getTypeDataList("RepaymentType");
        getView().initRecycler(model.getItemList(getView().getLoanInfo(), getView().getIsNotary(), getView().getIsRedeem()));
    }

    //获取多次放款列表
    public void getLendDatas(String loanId) {
        getData(4, model.getLendParam(getLendList(loanId), "LendingInfo", "GET"), true);
    }

    //删除放款
    public void delLend(int id) {
        getData(5, model.getLendParam(getdelLendList(id), "LendingInfo", "DEL"), true);
    }

    private List<Object> getdelLendList(int id) {
        List<Object> list = new ArrayList<>();
        list.add(id);
        return list;
    }

    private List<Object> getLendList(String loanId) {
        List<Object> list = new ArrayList<>();
        list.add("AND delMarker = 0 AND LoanId = " + loanId);
        list.add("");
        list.add("");
        return list;
    }

    private void initRX() {
        subscription = RxBus.getInstance().toObservable(Constants.REDEEM_CODE, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                getView().getLoanInfo().setMember(chooseType.getId());
                getView().refreshData(getModel().getItemList(getView().getLoanInfo(), getView().getIsNotary(), getView().getIsRedeem()));
            }
        });

        if (isViewAttached()) {
            timeSelector = new TimeSelector((Context) getView(), new TimeSelector.ResultHandler() {
                @Override
                public void handle(String time) {
                    getView().getLoanInfo().setNotaryTime(time);
                    getView().refreshTime(timePos, time);
                }
            }, "1900-01-01", DateUtil.getCurDate());
            timeSelector.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR, TimeSelector.SCROLLTYPE.MONTH, TimeSelector.SCROLLTYPE.DAY);
        }
    }

    public void showTime(int pos) {
        timePos = pos;
        if (timeSelector != null) {
            timeSelector.show();
        }
    }

    public void getScheduleData(int pos, String loanId) {
        clickPos = pos;
//        String str = "";
//        if (pos == 9) {
//            str = "CFS_Loan";
//        } else {
//            str = "DB_CFS_Loan";
//        }
//        getData(0, model.getScheduleParam(getScheduleList(pos, loanId), "Get_Info_ByWhere", str), true);
        getData(0, model.getScheduleParam(getScheduleList(pos, loanId), "GetPermitData", "DB_CFS_Loan"), true);
    }

    private List<Object> getScheduleList(int pos, String loanId) {
        List<Object> list = new ArrayList<>();
        if (pos == 9) {
            list.add("t_loan_follow_record");
            list.add("1=1" + "and LoanID = " + loanId + " and DelMarker = 0");
        } else {
            list.add("t_loan_cost");
            list.add("1=1" + "and LoanID = " + loanId + " and DelMarker = 0");
        }
        list.add("*");
        list.add("InsertTime desc");
        return list;
    }

    //公证赎楼
    public void postData(LoanInfo loanInfo) {
        if (getView().getIsNotary()) {
            if (TextUtils.isEmpty(loanInfo.getNotaryTime())) {
                getView().showMsg("请选择公证时间!");
            } else if (TextUtils.isEmpty(loanInfo.getNotaryNote())) {
                getView().showMsg("请填写公证书!");
            } else {
                getData(1, getModel().getParam(getNotaryList(loanInfo), "UpdateNotarization"), true);
            }
        } else if (getView().getIsRedeem()) {
            getData(2, getModel().getParam(getRedeemList(loanInfo), "UpdateForeclosureCls"), true);
        }
    }

    //公证参数
    private List<Object> getNotaryList(LoanInfo loanInfo) {
        List<Object> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("LoanId", loanInfo.getLoanId());
        map1.put("LoanCode", loanInfo.getLoanCode());
        map1.put("ServicePeople", Hawk.get("UserID"));
        map1.put("IsNotarization", true);
        map1.put("NotarizationDate", loanInfo.getNotaryTime());
        map1.put("NotarizationBook", loanInfo.getNotaryNote());
        String s = JSON.toJSONString(map1);
        list.add(s);
        return list;
    }

    //赎楼参数
    private List<Object> getRedeemList(LoanInfo loanInfo) {
        List<Object> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("LoanId", loanInfo.getLoanId());
        map1.put("LoanCode", loanInfo.getLoanCode());
        map1.put("ServicePeople", Hawk.get("UserID"));
        map1.put("ForeclosureFloorMemberID", loanInfo.getMember());
        map1.put("IsForeclosureFloor", loanInfo.getIsForeclosureFloor());
        String s = JSON.toJSONString(map1);
        list.add(s);
        return list;
    }

    public void submitMortgage(String loanId, String id) {
        getData(3, getModel().getParam(getMortgageListParam(loanId, id), "SubmitMortgager"), true);
    }

    private List<Object> getMortgageListParam(String loanId, String id) {
        List<Object> list = new ArrayList<>();
        list.add(loanId);
        list.add(id);
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        switch (resultCode) {
            case 0://跟踪进度
                List<Schedule> schedules = model.getScheduleList(data.getData(), clickPos);
                getView().refreshSchedule(clickPos, schedules, true);
                break;
            case 1://公证
            case 2://赎楼
                getView().complete();
                break;
            case 3://提交按揭员
                getView().submitComplete();
                break;
            case 4://获取放款列表
                getView().refreshItem(model.getLendList(data.getData(), chooseTypes));
                break;
            case 5:
                getView().showMsg("删除成功!");
                getLendDatas(getView().getLoanInfo().getLoanId());
                break;
        }
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
