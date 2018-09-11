package com.xxjr.cfs_system.LuDan.presenter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.LuDan.model.modelimp.SearchMImp;
import com.xxjr.cfs_system.LuDan.view.activitys.SearchActivity;
import com.xxjr.cfs_system.main.MyApplication;
import com.xxjr.cfs_system.tools.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entity.ChooseType;

/**
 * Created by Administrator on 2017/8/24.
 * 搜索类
 */

public class SearchPresenter extends BasePresenter<SearchActivity, SearchMImp> {
    private List<ChooseType> listData = null;
    private List<ChooseType> listTemp = new ArrayList<>();

    @Override
    protected SearchMImp getModel() {
        return new SearchMImp();
    }

    @Override
    public void setDefaultValue() {
        getView().setEditChageListener(textWatcher);
        int type = getView().getType();
        switch (type) {
            case Constants.MANAGER_CODE:
                getCusManager();
                break;
            case Constants.Sign_Member:
            case Constants.Witness:
                getMemberOrWitness();
                break;
            case Constants.BANK_CODE:
                listData = model.getBankData();
                if (listData == null) {
                    getView().showMsg("银行数据获取失败!");
                } else {
                    listTemp.addAll(listData);
                }
                break;
            case Constants.BANK_PRODUCT_CODE:
                listData = model.getProductsData(getView().getBankID());
                if (listData == null) {
                    getView().showMsg("银行产品获取失败!");
                } else {
                    listTemp.addAll(listData);
                }
                break;
            case Constants.MORTGAGE_CODE:
                listData = model.getMortgageData();
                if (listData == null) {
                    getView().showMsg("按揭员数据获取失败!");
                } else {
                    listTemp.addAll(listData);
                }
                break;
            case Constants.REDEEM_CODE:
                listData = model.getMortgageData();
                if (listData == null) {
                    getView().showMsg("赎楼员数据获取失败!");
                } else {
                    listTemp.addAll(listData);
                }
                break;
            case Constants.BANK_MANAGER_CODE:
                listTemp.clear();
                getBankManager();
                break;
            case Constants.Sign_Bank:
                listData = model.getSignBankData(getView().getAisleType());
                if (listData == null) {
                    getView().showMsg("签约银行获取失败!");
                } else {
                    listTemp.addAll(listData);
                }
                break;
            case Constants.Company_Choose:
                listData = model.getCompanyData();
                if (listData == null) {
                    getView().showMsg("门店数据获取失败!");
                } else {
                    listTemp.addAll(listData);
                }
                break;
        }
        if (type != Constants.BANK_MANAGER_CODE && type != Constants.MANAGER_CODE && type != Constants.Sign_Member && type != Constants.Witness)
            getView().initRecycler(listTemp);
    }

    private void getCusManager() {
        getData(2, model.getSearchParam(getManagerListParam(), "GetPermitData", "Page", "DB_CFS"), true);
    }

    private void getMemberOrWitness() {
        getData(3, model.getSearchParam(getMemberOrWitnessParam(), "HR_EMP_GETEMPINFOBYWHERE", "", "DB_CFS_HR"), true);
    }

    private void getBankManager() {
        getData(1, model.getParam(getBankListParam(getView().getBankID()), "GET_LOANBANKMANAGERLIST"), true);
    }

    public void submitMortgage(String loanId, String id) {
        getData(0, getModel().getParam(getListParam(loanId, id), "SubmitMortgager"), true);
    }

    //客户经理
    private List<Object> getManagerListParam() {
        List<Object> list = new ArrayList<>();
        list.add("");
        list.add("V_UP_User");//表名
        list.add("");
        list.add("UserId");//主键字段
        list.add("UserID,UserRealName,CompanyID");//UserID  UserRealName
        list.add("DelMarker = 0");
        list.add("UserId");//排序字段
        list.add("0");//排序 0->asc 1->desc
        list.add("10");//条数
        list.add("0");//页数
        list.add("");
        list.add("1");//0->代表分页 1->代表不分页
        return list;
    }

    //签单员和见证人
    private List<Object> getMemberOrWitnessParam() {
        List<Object> list = new ArrayList<>();
        list.add("ID as value,(CompanyName+'-'+CAST(EmpCode as nvarchar(10))+'-'+Name) as name");
        list.add("CompanyID = '" + getView().getCompanyId() + "'");
        return list;
    }

    //银行列表参数
    private List<Object> getBankListParam(int bankId) {
        List<Object> list = new ArrayList<>();
        HashMap<Object, Object> map = new HashMap<>();
        if (bankId != -1) {
            map.put("BankId", getView().getBankID());
        }
        list.add(JSON.toJSONString(map));
        list.add("0");
        list.add("0");
        list.add("0");
        return list;
    }

    //提交按揭员
    private List<Object> getListParam(String loanId, String id) {
        List<Object> list = new ArrayList<>();
        list.add(loanId);
        list.add(id);
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        if (isViewAttached())
            switch (resultCode) {
                case 0:
                    getView().complete();
                    break;
                case 1:
                    listData = model.getBankManagerData(data.getData());
                    if (listData == null) {
                        getView().showMsg("该机构无银行客户经理，请添加!");
                    } else {
                        listTemp.addAll(listData);
                    }
                    getView().initRecycler(listTemp);
                    break;
                case 2:
                    listData = model.getManagerData(data.getData());
                    if (listData == null) {
                        getView().showMsg("客户经理数据获取失败!");
                    } else {
                        listTemp.addAll(listData);
                    }
                    getView().initRecycler(listTemp);
                    break;
                case 3:
                    listData = model.getMemberData(data.getData());
                    if (listData == null) {
                        getView().showMsg("数据获取失败!");
                    } else {
                        listTemp.addAll(listData);
                    }
                    getView().initRecycler(listTemp);
                    break;
            }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (TextUtils.isEmpty(charSequence.toString())) {
                listTemp.clear();
                listTemp.addAll(listData);
            } else {
                listTemp.clear();
                for (ChooseType type : listData) {
                    if (type.getContent().contains(charSequence.toString())) {
                        listTemp.add(type);
                    }
                }
            }
            getView().refreshData(listTemp);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
