package com.xxjr.cfs_system.LuDan.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.model.modelimp.AddClientMImp;
import com.xxjr.cfs_system.LuDan.view.activitys.AddClientActivity;
import com.xxjr.cfs_system.LuDan.view.viewinter.AddClientVInter;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.Utils;

import entity.ChooseType;
import entity.ClientInfo;
import entity.CommonItem;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/8/24.
 * 添加客户执行类
 */

public class AddClientPresenter extends BasePresenter<AddClientVInter, AddClientMImp> {
    private Subscription subscription;

    @Override
    protected AddClientMImp getModel() {
        return new AddClientMImp();
    }

    @Override
    public void setDefaultValue() {
        initRX();
        getView().initRecycler(model.getClientItems(getView().getClient()));
    }

    private void initRX() {
        subscription = RxBus.getInstance().toObservable(Constants.CARD_CODE, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType chooseType) {
                getView().hidePop();
                getView().getClient().setCardType(chooseType.getId());
                CommonItem commonItem = new CommonItem();
                commonItem.setContent(chooseType.getContent());
                getView().refreshData(2, commonItem);
            }
        });
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {

    }

    @Override
    protected void onFailed(int resultCode, String msg) {

    }

    public boolean check(ClientInfo clientInfo) {
        if (TextUtils.isEmpty(clientInfo.getName())) {
            getView().showMsg("请正确的填写客户姓名!");
            return false;
        } else if (TextUtils.isEmpty(clientInfo.getMobile()) || clientInfo.getMobile().length() != 11) {
            getView().showMsg("请正确的填写手机号码!");
            return false;
        } else if (clientInfo.getCardType() == -1) {
            getView().showMsg("请选择证件类型!");
            return false;
        } else if (TextUtils.isEmpty(clientInfo.getIdCode())) {
            getView().showMsg("请正确的填写身份证号码!");
            return false;
        } else if (clientInfo.getCardType() == 1) {
            String s = Utils.IDCardValidate(clientInfo.getIdCode());
            if (!TextUtils.isEmpty(s)) {
                getView().showMsg(s);
                return false;
            }
        }
        return true;
    }

    public void rxDeAttach() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
