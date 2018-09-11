package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.MortgageScore
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Administrator on 2018/3/7.
 */
class MortgageScoreMImp : ModelImp() {

    fun getScoreParam(list: List<Any>): String {
        val map = HashMap<String, Any>()
        map["Action"] = "Default"
        map["DBMarker"] = "DB_CFS_Loan"
        map["Marker"] = "HQServer"
        map["IsUseZip"] = false
        map["Function"] = "Page"
        map["ParamString"] = list
        map["TranName"] = "GetPermitData"
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getScoresData(data: String, type: Int): MutableList<MortgageScore> {
        val list = mutableListOf<MortgageScore>()
        if (data.isNotBlank()) {
            val jsonArray = JSON.parseArray(data)
            if (jsonArray != null && jsonArray.size != 0) {
                var improvements = mutableListOf<ChooseType>()
                if (type == 1) improvements = Utils.getTypeDataList("ImprovementType")

                var mortgageScoreType: MutableList<ChooseType>? = null
                if (type == 20) {
                    mortgageScoreType = Utils.getTypeDataList("MortgageScoreType")
                }

                var mortgageScore: MortgageScore
                for (i in jsonArray.indices) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    mortgageScore = MortgageScore()
                    when (type) {
                        0 -> {
                            mortgageScore.name = jsonObject.getString("MortgageName")
                            mortgageScore.userID = jsonObject.getString("UserID")
                            mortgageScore.scoreDescribe = (jsonObject.getString("Integral")?:"0.0") +
                                    "    业务分：${jsonObject.getString("BusinessScore")?:"0.0"}    " +
                                    "服务分：${if (jsonObject.getString("ServiceScore") == null) "未评分" else jsonObject.getString("ServiceScore")}"
                            mortgageScore.updateTime = jsonObject.getString("YearMonth")
                        }
                        1 -> {
                            mortgageScore.name = jsonObject.getString("MortgageName")
                            val improvement = jsonObject.getString("Improvement")
                            var describe = getScoreDescribe(improvement, improvements)
                            if (describe.isNotBlank()) {
                                describe = "——待改善（${getScoreDescribe(improvement, improvements)}）"
                            }
                            mortgageScore.scoreDescribe = "${getScore(jsonObject.getIntValue("Score"))}$describe"
                            mortgageScore.updateTime = Utils.getTime(jsonObject.getString("UpdateTime"))
                            mortgageScore.remark = jsonObject.getString("Remark")
                        }
                        2 -> {
                            mortgageScore.name = "<font color='#54b1fd'>${jsonObject.getString("MortgageName")}</font>" + "&ensp;&ensp;&ensp;平均分：<font color='#54b1fd'>${String.format("%.2f", jsonObject.getDoubleValue("AvgScore"))}</font>"
                            mortgageScore.oneStar = jsonObject.getIntValue("Star1")
                            mortgageScore.twoStar = jsonObject.getIntValue("Star2")
                            mortgageScore.threeStar = jsonObject.getIntValue("Star3")
                            mortgageScore.fourStar = jsonObject.getIntValue("Star4")
                            mortgageScore.fiveStar = jsonObject.getIntValue("Star5")
                        }
                        20 -> {//积分详情
                            if (mortgageScoreType != null) {
                                val scoreType = jsonObject.getIntValue("ScoreType")
                                mortgageScore.scoreType = Utils.getTypeValue(mortgageScoreType, scoreType)
                            }
                            mortgageScore.scoreDescribe = jsonObject.getString("Score")
                            mortgageScore.updateTime = jsonObject.getString("YearMonth")
                        }
                    }
                    list.add(mortgageScore)
                }
            }
        }
        return list
    }

    //获取服务评分改善
    private fun getScoreDescribe(improvement: String, improvements: MutableList<ChooseType>): String {
        val describe = StringBuilder()
        if (improvement.isNotBlank()) {
            val positions = improvement.split(",")
            if (improvements.isNotEmpty() && improvements.size != 0) {
                for (pos in positions) {
                    if (pos.isNotBlank()) {
                        for (showData in improvements) {
                            if (showData.id == pos.toInt()) {
                                describe.append(showData.content).append(" ")
                            }
                        }
                    }
                }
            }
        }
        return describe.toString()
    }

    private fun getScore(mortgageScore: Int): String {
        var score = ""
        when (mortgageScore) {
            0 -> score = "未评分"
            1 -> score = "★"
            2 -> score = "★★"
            3 -> score = "★★★"
            4 -> score = "★★★★"
            5 -> score = "★★★★★"
        }
        return score
    }

    fun getTypeDataList(): MutableList<ChooseType> {
        val list = ArrayList<ChooseType>()
        var chooseType: ChooseType
        for (i in 0..2) {
            chooseType = ChooseType()
            chooseType.id = i
            when (i) {
                0 -> chooseType.content = "全部"
                1 -> chooseType.content = "有备注"
                2 -> chooseType.content = "无备注"
            }
            list.add(chooseType)
        }
        return list
    }
}