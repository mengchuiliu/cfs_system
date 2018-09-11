package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.AddBankMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.AddBankVInter
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils

class AddBankPresenter : BasePresenter<AddBankVInter, AddBankMImp>() {
    override fun getModel(): AddBankMImp = AddBankMImp()

    override fun setDefaultValue() {
        view.initRecycler(model.getListData())
    }

    fun postBankInfo(owner: String, bankCode: String, branch: String, idCard: String, isDefault: Boolean) {
        when {
            bankCode.isBlank() -> {
                view.showMsg("银行卡号不能为空！")
                return
            }
            idCard.isBlank() -> {
                view.showMsg("请正确定的填写身份证号！")
                return
            }
            !Utils.IDCardValidate(idCard).isNullOrBlank() -> {
                view.showMsg(Utils.IDCardValidate(idCard))
                return
            }
        }
        getData(0, model.getParam(getListPostParam(owner, bankCode, branch, idCard, isDefault), "ManageUserBankCard"), true)
    }

    private fun getListPostParam(owner: String, bankCode: String, branch: String, idCard: String, isDefault: Boolean): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        val map1 = HashMap<String, Any>()
        map1.put("AccountName", owner)
        map1.put("Account", bankCode)
        map1.put("Branch", branch)
        map1.put("CardNum", idCard)
        map1.put("IsDefault", isDefault)
        map1.put("UserID", Hawk.get("UserID"))
        val str = JSON.toJSONString(map1)
        list.add(str)
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            view.addComplete()
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    override fun permissionSuccess(code: Int) {
        when (code) {
            Constants.REQUEST_CODE_PERMISSION_Camera -> {
                view.scanCard()
            }
        }
    }
}