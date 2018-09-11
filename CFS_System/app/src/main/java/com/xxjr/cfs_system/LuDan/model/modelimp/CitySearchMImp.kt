package com.xxjr.cfs_system.LuDan.model.modelimp

import android.content.Context
import com.google.gson.Gson
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.ToastShow
import entity.CityInfo
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

/**
 * Created by Administrator on 2018/3/13.
 */
class CitySearchMImp : ModelImp() {

    fun getJson(context: Context): String {
        val stringBuilder = StringBuilder()
        try {
            val assetManager = context.assets
            val bf = BufferedReader(InputStreamReader(assetManager.open("paycity.json")))
            var line = bf.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = bf.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ToastShow.showShort(context, "数据解析错误")
        }
        return stringBuilder.toString()
    }

    fun parseData(result: String): MutableList<CityInfo> {//Gson 解析
        val detail = mutableListOf<CityInfo>()
        try {
            val data = JSONArray(result)
            val gson = Gson()
            for (i in 0 until data.length()) {
                val entity = gson.fromJson<CityInfo>(data.optJSONObject(i).toString(), CityInfo::class.java)
                detail.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return detail
    }
}