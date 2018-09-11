package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.CitySearchMImp
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.CitySearchActivity
import entity.CityInfo

/**
 * Created by Administrator on 2018/3/13.
 */
class CitySearchPresenter : BasePresenter<CitySearchActivity, CitySearchMImp>() {
    private var provinceData = mutableListOf<CityInfo>()
    private var cityData = mutableListOf<CityInfo.ChildrenBean>()
    private val listTemp = mutableListOf<Any>()

    override fun getModel(): CitySearchMImp = CitySearchMImp()

    override fun setDefaultValue() {
//        view.setEditChangeListener(textWatcher)
        if (isViewAttached) {
            provinceData = model.parseData(model.getJson(view as Context))
            if (provinceData.isEmpty()) {
                view.showMsg("数据为空!")
            } else {
                view.initRecycler(provinceData)
            }
        }
    }

//    fun addCityData(mutableList: MutableList<CityInfo.ChildrenBean>) {
//        cityData.clear()
//        cityData.addAll(mutableList)
//    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (TextUtils.isEmpty(charSequence.toString())) {
                listTemp.clear()
                if (view.getType() == 1) listTemp.addAll(provinceData) else listTemp.addAll(cityData)
            } else {
                listTemp.clear()
                when (view.getType()) {
                    1 -> {
                        for (type in provinceData) {
                            if ((type.Names ?: "").contains(charSequence.toString())) {
                                listTemp.add(type)
                            }
                        }
                    }
                    2 -> {
                        for (type in cityData) {
                            if ((type.Names ?: "").contains(charSequence.toString())) {
                                listTemp.add(type)
                            }
                        }
                    }
                }
            }
            if (view.getType() == 1) view.refreshData(listTemp as MutableList<CityInfo>)
            else view.refreshCityData(listTemp as MutableList<CityInfo.ChildrenBean>)
        }

        override fun afterTextChanged(editable: Editable) {}
    }


    override fun onSuccess(resultCode: Int, data: ResponseData?) {}

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}