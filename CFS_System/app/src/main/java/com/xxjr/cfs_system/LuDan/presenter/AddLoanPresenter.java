package com.xxjr.cfs_system.LuDan.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.model.modelimp.AddLoanMImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.AddLoanVInter;
import com.xxjr.cfs_system.tools.Constants;

import java.util.ArrayList;
import java.util.List;

import entity.ChooseType;
import entity.CommonItem;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/9/1.
 * 执行类
 */

public class AddLoanPresenter extends BasePresenter<AddLoanVInter, AddLoanMImp> {
    private Subscription bankSubscription, productSubscription, mortgageSubscription, loanSubscription;
    private String addLoanId;
    private String loanTypes = "";

    @Override
    protected AddLoanMImp getModel() {
        return new AddLoanMImp();
    }

    @Override
    public void setDefaultValue() {
        initRX();
        getView().initRecycler(getModel().getItems(getView().getLoanInfo(), getView().getScheduleUp()));
    }

    private void initRX() {
        bankSubscription = RxBus.getInstance().toObservable(Constants.BANK_CODE, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                getView().getLoanInfo().setBankId(chooseType.getId());
                getView().getLoanInfo().setBankName(chooseType.getContent());
                getView().refreshData(0, chooseType.getContent());
                //////////
                getView().getLoanInfo().setProductId(0);
                getView().refreshData(1, "");
                loanTypes = "";
                getView().getLoanInfo().setLoanType(0);
                getView().refreshData(2, "");
            }
        });
        productSubscription = RxBus.getInstance().toObservable(Constants.BANK_PRODUCT_CODE, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                getView().getLoanInfo().setProductId(chooseType.getId());
                getView().getLoanInfo().setProductName(chooseType.getContent());
                loanTypes = chooseType.getIds();
                getView().refreshData(1, chooseType.getContent());
                ///////////
                getView().getLoanInfo().setLoanType(0);
                getView().refreshData(2, "");
            }
        });
        mortgageSubscription = RxBus.getInstance().toObservable(Constants.MORTGAGE_CODE, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                getView().getLoanInfo().setMortgage(chooseType.getId());
                getView().getLoanInfo().setMortgageName(chooseType.getContent());
                getView().refreshData(5, chooseType.getContent());
            }
        });
        loanSubscription = RxBus.getInstance().toObservable(Constants.LOAN_TYPE_CODE, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                getView().hidePop();
                getView().getLoanInfo().setLoanType(chooseType.getId());
                getView().getLoanInfo().setLoanTypeName(chooseType.getContent());
                getView().refreshData(2, chooseType.getContent());
            }
        });
    }

    public void addOrUpdateLoan(boolean isUpdate, int pactID, int managerId, String loanId, String content,
                                String companyID, String pactNumberId) {
        getData(0, model.getLoanParam(isUpdate, pactID, managerId, loanId, content, companyID, pactNumberId, getView().getLoanInfo()), true);
    }

    public boolean check() {
        if (getView().getLoanInfo().getBankId() == 0) {
            getView().showMsg("请选择申请银行！");
            return false;
        } else if (getView().getLoanInfo().getProductId() == 0) {
            getView().showMsg("请选择银行产品！");
            return false;
        } else if (getView().getLoanInfo().getLoanType() == 0) {
            getView().showMsg("请选择贷款类型！");
            return false;
        } else if (getView().getLoanInfo().getAmount() == 0) {
            getView().showMsg("请正确的填写申请金额！");
            return false;
        } else if (TextUtils.isEmpty(getView().getLoanInfo().getCustomer()) || TextUtils.isEmpty(getView().getLoanInfo().getCustomerId())) {
            getView().showMsg("请选择申请的客户！");
            return false;
        }
        return true;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        if (resultCode == 0) {
            if (getView().getSubmit() != 0) {
                if (getView().getIsUpdate()) {
                    addLoanId = getView().getLoanInfo().getLoanId();
                    getData(1, model.getParam(getMortgageList(addLoanId), "SubmitMortgager"), true);
                } else {
                    addLoanId = data.getReturnString();
                    getData(1, model.getParam(getMortgageList(addLoanId), "SubmitMortgager"), true);
                }
            } else {
                getView().showMsg("保存成功!");
                getView().completeOver(data.getReturnString());
            }
        } else if (resultCode == 1) {
            getView().showMsg("保存并提交成功!");
            getView().completeOver(addLoanId);
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }

    private List<Object> getMortgageList(String loanId) {
        List<Object> list = new ArrayList<>();
        list.add(loanId);
        list.add(getView().getLoanInfo().getMortgage());
        return list;
    }

    public String getDescriptions(String ids, String content) {
        String descriptions = "";
        if (getView().getIsUpdate()) {
            descriptions = model.getLoanDescriptions(ids, content, getView().getLoanInfo());
        } else {
            if (TextUtils.isEmpty(content)) {
                descriptions = model.getDescriptions(getView().getLoanInfo());
            } else {
                descriptions = content + model.getDescriptions(getView().getLoanInfo());
            }
        }
        return descriptions;
    }

    public String getLoanTypes() {
        return loanTypes;
    }

    public void rxDeAttach() {
        if (bankSubscription != null && !bankSubscription.isUnsubscribed()) {
            bankSubscription.unsubscribe();
        }
        if (productSubscription != null && !productSubscription.isUnsubscribed()) {
            productSubscription.unsubscribe();
        }
        if (mortgageSubscription != null && !mortgageSubscription.isUnsubscribed()) {
            mortgageSubscription.unsubscribe();
        }
        if (loanSubscription != null && !loanSubscription.isUnsubscribed()) {
            loanSubscription.unsubscribe();
        }
    }
}
