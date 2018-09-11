package com.xxjr.cfs_system.LuDan.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.modelimp.LoanListMImp;
import com.xxjr.cfs_system.LuDan.view.activitys.LoanListActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.TaskListActivity;
import com.xxjr.cfs_system.LuDan.view.viewinter.LoanVInter;

import java.util.ArrayList;
import java.util.List;

import entity.CommonItem;
import entity.LoanInfo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoanPresenter extends BasePresenter<LoanVInter, LoanListMImp> {
    private int homePage = 0;
    private int loanType = 0;//贷款跟进
    private int schedule = 0;//贷款跟进
    private boolean isTask = false;
    private boolean isEvaluation = false;//是否过滤带评分贷款

    @Override
    protected LoanListMImp getModel() {
        return new LoanListMImp();
    }

    @Override
    public void setDefaultValue() {
        if (isViewAttached()) {
            if (getView().getListType() == 2) {
                getData(0, model.getLoanParam(getLoanListParam(0, 0, ((LoanListActivity) getView()).getType()), "GetLoanInfoList"), true);
            } else if (getView().getListType() == 3) {
                getTaskData(0, 0, 0, ((TaskListActivity) getView()).getschedule(), ((TaskListActivity) getView()).getDayNo());
            }
        }
    }

    public void setEvaluation(boolean isEvaluation) {
        this.isEvaluation = isEvaluation;
    }

    public void loanRefresh(int page, int searchType, int type) {
        if (isViewAttached()) {
            homePage = page;
            getData(0, model.getLoanParam(getLoanListParam(page, searchType, type), "GetLoanInfoList"), false);
        }
    }

    public void getTaskData(int page, int searchType, int loanType, int schedule, int dayNot) {
        if (isViewAttached()) {
            isTask = true;
            homePage = page;
            this.loanType = loanType;
            this.schedule = schedule;
            getData(1, model.getParam(getTitleListParam(searchType, loanType, dayNot), "GetLoanInfoStatistic"), true);
            getData(0, model.getParam(getTaskListParam(page, searchType, loanType, schedule, dayNot), "GetTodoLoanList"), false);
        }
    }

    //贷款列表参数
    private List<Object> getLoanListParam(int page, int searchType, int type) {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (isViewAttached()) {
            if (searchType == 1) {
                builder.append(" and CustomerNames like '%").append(getView().getSearchContent()).append("%'");
            } else if (searchType == 2) {
                builder.append(" and LoanCode like '%").append(getView().getSearchContent()).append("%'");
            } else if (searchType == 3) {
                if (!TextUtils.isEmpty(getView().getSearchCompany()))
                    builder.append(" and CompanyID in ('").append(getView().getSearchCompany()).append("')");
            }
            if (!TextUtils.isEmpty(getView().getChooseTime1()) && !TextUtils.isEmpty(getView().getChooseTime2())) {
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)>= '").append(getView().getChooseTime1()).append("'");
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)<= '").append(getView().getChooseTime2()).append("'");
            }
        }
        if (isEvaluation) {
            builder.append(" and MortgageScore IS NULL and yellowType <= 0 and Status in (5,109) and MortgageIsMortgage = '1' ");
        }
        if (type == 1) {
            builder.append(" and yellowType = 3 ");
        }
        list.add(builder.toString());
        list.add(String.valueOf(page));
        list.add("10");
        list.add(false);
        return list;
    }

    private List<Object> getTitleListParam(int searchType, int loanType, int dayNot) {
        List<Object> list = new ArrayList<>();
        list.add(loanType);
        StringBuilder builder = new StringBuilder();
        if (Integer.valueOf(Hawk.get("UserType", "0")) > 81) {
            builder.append(" and DelMarker = 0 and DATEDIFF(mm ,UpdateTime,GETDATE())<=3 ");
        }
        builder.append(model.getDayNotQuery(dayNot));
        if (isViewAttached()) {
            if (searchType == 1) {
                builder.append(" and CustomerNames like '%").append(getView().getSearchContent()).append("%'");
            } else if (searchType == 2) {
                builder.append(" and LoanCode like '%").append(getView().getSearchContent()).append("%'");
            } else if (searchType == 3) {
                if (!TextUtils.isEmpty(getView().getSearchCompany()))
                    builder.append(" and CompanyID in ('").append(getView().getSearchCompany()).append("')");
            }
            if (!TextUtils.isEmpty(getView().getChooseTime1()) && !TextUtils.isEmpty(getView().getChooseTime2())) {
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)>= '").append(getView().getChooseTime1()).append("'");
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)<= '").append(getView().getChooseTime2()).append("'");
            }
        }
        list.add(builder.toString());
        list.add(1);
        return list;
    }

    //待办任务列表参数
    private List<Object> getTaskListParam(int page, int searchType, int loanType, int schedule, int dayNot) {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        builder.append(" and DelMarker = 0 ");
        if (loanType != 0) {
            builder.append(" and L1=").append(loanType);
        }
        builder.append(model.getTaskQuery(loanType, schedule));
        builder.append(model.getDayNotQuery(dayNot));
        if (isViewAttached()) {
            if (searchType == 1) {
                builder.append(" and CustomerNames like '%").append(getView().getSearchContent()).append("%'");
            } else if (searchType == 2) {
                builder.append(" and LoanCode like '%").append(getView().getSearchContent()).append("%'");
            } else if (searchType == 3) {
                if (!TextUtils.isEmpty(getView().getSearchCompany()))
                    builder.append(" and CompanyID in ('").append(getView().getSearchCompany()).append("')");
            }
            if (!TextUtils.isEmpty(getView().getChooseTime1()) && !TextUtils.isEmpty(getView().getChooseTime2())) {
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)>= '").append(getView().getChooseTime1()).append("'");
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)<= '").append(getView().getChooseTime2()).append("'");
            }
        }
        list.add(builder.toString());
        list.add(String.valueOf(page));
        list.add("10");
        list.add(true);
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, final ResponseData data) {
        if (isViewAttached()) {
            if (resultCode == 0) {
                Observable.create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        model.getArray();
                        subscriber.onNext("");
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Object>() {

                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                showView(data);
                            }

                            @Override
                            public void onNext(Object o) {
                                showView(data);
                            }
                        });
            } else if (resultCode == 1) {
                JSONArray jsonArray = JSON.parseArray(data.getData());
                if (jsonArray != null && jsonArray.size() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    ((TaskListActivity) getView()).refreshTitleData(model.getTitles(jsonObject, loanType, schedule));
                }
            }
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }

    private void showView(ResponseData data) {
        List<LoanInfo> temp;
        if (isTask) {
            temp = model.getTaskLoanList(data.getData());
        } else {
            temp = model.getLoanList(data.getData());
        }
        if (temp == null || temp.size() == 0) {
            if (homePage == 0) {
                getView().getLoanInfos().clear();
                getView().refreshChange();
                if (getView().getListType() == 2) {
                    getView().showMsg("没有贷款数据!");
                } else {
                    getView().showMsg("暂时还没有任务!");
                }
            } else {
                if (getView().getListType() == 2) {
                    getView().showMsg("没有更多贷款数据了!");
                } else {
                    getView().showMsg("没有更多任务了!");
                }
            }
        } else {
            if (getView().getPull()) {
                getView().getLoanInfos().addAll(temp);
            } else {
                getView().getLoanInfos().clear();
                getView().getLoanInfos().addAll(temp);
            }
            getView().refreshChange();
        }
        getView().completeRefresh();
    }

    //贷款跟进标题列表
    public List<CommonItem> getTitles0(int dayNo) {
        List<CommonItem> titles = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 3; i++) {
            commonItem = new CommonItem();
            commonItem.setClick(dayNo == i);
            switch (i) {
                case 0:
                    commonItem.setName("全部");
                    break;
                case 1:
                    commonItem.setName("最近三天无跟进");
                    break;
                case 2:
                    commonItem.setName("最近七天无跟进");
                    break;
            }
            titles.add(commonItem);
        }
        return titles;
    }

    //贷款跟进标题列表
    public List<CommonItem> getTitles() {
        List<CommonItem> titles = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 6; i++) {
            commonItem = new CommonItem();
            switch (i) {
                case 0:
                    commonItem.setClick(true);
                    commonItem.setName("全部");
                    break;
                case 1:
                    commonItem.setName("抵押贷");
                    break;
                case 2:
                    commonItem.setName("信用贷");
                    break;
                case 3:
                    commonItem.setName("小额");
                    break;
                case 4:
                    commonItem.setName("借贷");
                    break;
                case 5:
                    commonItem.setName("信用卡");
                    break;
            }
            titles.add(commonItem);
        }
        return titles;
    }

    //贷款列表标题
    public List<CommonItem> getLoanTitles(int type) {
        List<CommonItem> titles = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 2; i++) {
            commonItem = new CommonItem();
            commonItem.setClick(type == i);
            switch (i) {
                case 0:
                    commonItem.setName("全部贷款");
                    break;
                case 1:
                    commonItem.setName("失信贷款");
                    break;
            }
            titles.add(commonItem);
        }
        return titles;
    }
}
