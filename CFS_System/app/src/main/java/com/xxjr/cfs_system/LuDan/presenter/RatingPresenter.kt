package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.viewinter.RatingVInter
import com.xxjr.cfs_system.tools.Utils

/**
 * Created by Administrator on 2018/1/9.
 */
class RatingPresenter : BasePresenter<RatingVInter, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(1, model.getParam(getStarListParam(), "GetMortgageEvaluation"), true)
    }

    fun saveData(score: Float, improvementId: String) {
        getData(0, model.getParam(getSavaListParam(score, improvementId), "AddMortgageEvaluation"), true)
    }

    private fun getStarListParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(view.getLoanInfo().loanId)
        list.add(Hawk.get("UserID"))
        return list
    }

    private fun getSavaListParam(score: Float, improvementId: String): MutableList<Any> {
        val list = mutableListOf<Any>()
        val map = hashMapOf<Any, Any>()
        map.put("LoanId", view.getLoanInfo().loanId)
        map.put("MortgageID", view.getLoanInfo().mortgage)
        map.put("Score", score)
        map.put("Improvement", improvementId)//改善标签id
        map.put("Evaluator", Hawk.get("UserID"))//评估人
        map.put("Remark", view.getRemark())
        val str = JSON.toJSONString(map)
        list.add(str)
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> view.complete()
                1 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray = JSON.parseArray(data?.data)
                        if (jsonArray != null && jsonArray.isNotEmpty()) {
                            val jsonObject = jsonArray.getJSONObject(0)
                            val scores = jsonObject.getFloatValue("Score")
                            val improvement = jsonObject.getString("Improvement")
                            val remark = jsonObject.getString("Remark")
                            view.initStar(scores, getImprovementPos(improvement), remark)
                        }
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getLabels(): MutableList<String> {
        val labels = mutableListOf<String>()
        val data = Utils.getTypeDataList("ImprovementType")
        if (data.isNotEmpty() && data.size != 0) {
            for (showData in data) {
                labels.add(showData.id, showData.content)
            }
        }
        return labels
    }

    fun join(labels: MutableList<Int>, splitter: String): String {
        if (labels.isEmpty() || labels.size == 0) {
            return ""
        } else {
            val sb = StringBuffer()
            for (i in labels) {
                sb.append(i).append(splitter)
            }
            return sb.toString().substring(0, sb.toString().length - 1)
        }
    }

    private fun getImprovementPos(improvement: String): MutableList<Int> {
        val list = mutableListOf<Int>()
        if (improvement.isNotBlank()) {
            val positions = improvement.split(",")
            for (pos in positions) {
                if (pos.isNotBlank()) {
                    list.add(pos.toInt())
                }
            }
        }
        return list
    }
}