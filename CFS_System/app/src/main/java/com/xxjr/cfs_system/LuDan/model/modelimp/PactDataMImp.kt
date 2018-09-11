package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSONArray
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.Utils
import entity.PactData

/**
 * Created by Administrator on 2017/11/24.
 */
class PactDataMImp : ModelImp() {
    fun getPactData(data: String): MutableList<PactData> {
        val list = mutableListOf<PactData>()
        if (!data.isBlank()) {
            val array = JSONArray.parseArray(data)
            if (array.isNotEmpty()) {
                var pact: PactData
                for (i in array.indices) {
                    val jsonObject = array.getJSONObject(i)
                    pact = PactData()
                    pact.ID = jsonObject.getString("ID")
                    pact.fileGuid = jsonObject.getString("FileGuid")
                    pact.customer = jsonObject.getString("CustomerName")
                    pact.fileType = Utils.getTypeValue(Utils.getTypeDataList("ImageDataType"), jsonObject.getIntValue("FileType"))
                    pact.fileName = jsonObject.getString("LocalFileName")
                    pact.fileSize = jsonObject.getString("FileSize")
                    pact.uploader = jsonObject.getString("ServicePeopleName")
                    pact.uploandTime = Utils.FormatTime(jsonObject.getString("UpdateTime"), "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm")
                    pact.viewTime = Utils.FormatTime(jsonObject.getString("ViewTime"), "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm")
                    pact.reason = jsonObject.getString("Reason")
                    pact.remark = jsonObject.getString("Remark")
                    pact.contractID = jsonObject.getIntValue("ContractID")
                    list.add(pact)
                }
            }
        }
        return list
    }
}