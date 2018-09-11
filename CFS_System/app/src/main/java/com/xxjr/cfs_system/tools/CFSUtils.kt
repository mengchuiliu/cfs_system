package com.xxjr.cfs_system.tools

import android.text.TextUtils
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.services.CacheProvide
import java.util.ArrayList

/**
 * CFS业务系统业务相关处理类
 */
object CFSUtils {

    /**
     * 特需成本二次审核数值
     * 当某些成本大于缓存中的特定值时需要二次审核处理
     */
    fun getAuditCost(): Int {
        var cost = 0
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("T_Config"), ""))
        if (jsonArray != null && jsonArray.size != 0) {
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("Name")
                if (!TextUtils.isEmpty(name) && name == "Cost_Audit_Value") {
                    cost = jsonObject.getIntValue("Value")
                }
            }
        }
        return cost
    }

    /**
     * 根据缓存数据匹配判断是否拥有审核权限
     * @param auditName 需要判断的审核字段名
     * @param userId 是否拥有权限
     * @return true 有
     */
    fun isAudit(userId: String, auditName: String): Boolean {
        var isAudit = false
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("T_Config"), ""))
        if (jsonArray != null && jsonArray.size != 0) {
            for (i in jsonArray.indices) {
                val `object` = jsonArray.getJSONObject(i)
                val name = `object`.getString("Name")
                if (!TextUtils.isEmpty(name) && name == auditName) {
                    val integers = `object`.getString("Value").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (id in integers) {
                        if (id == userId) {
                            isAudit = true
                        }
                    }
                }
            }
        }
        Logger.e("==二次审核权限==> %b", isAudit)
        return isAudit
    }


    /**
     * 按揭经理是否拥有成本二次审核权限
     * @return true--有
     */
    fun isMortgageAudit(userId: String): Boolean {
        var isAudit = false
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("T_Config"), ""))
        if (jsonArray != null && jsonArray.size != 0) {
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("Name")
                if (!TextUtils.isEmpty(name) && name == "Cost_Mortgage_Audit_Id") {
                    val integers = jsonObject.getString("Value").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (id in integers) {
                        if (id == userId) {
                            isAudit = true
                        }
                    }
                }
            }
        }
        return isAudit
    }

    /**
     *获取相关菜单下的权限
     * @param permissions 权限字符串
     * @param type 菜单码
     */
    @JvmStatic
    fun getPostPermitValue(permissions: String, type: String): List<String>? {
        if (!TextUtils.isEmpty(permissions)) {
            val list = ArrayList<String>()
            val strings = permissions.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (strings.size > 2) {
                val strings1 = strings[2].split("M".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (strings1.isNotEmpty()) {
                    for (s in strings1) {
                        if (s.startsWith("80")) {
                            val strings2 = s.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            if (strings.size > 1) {
                                val s1 = strings2[1]
                                val strings3 = s1.split("F".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                for (s2 in strings3) {
                                    if (s2.startsWith(type)) {
                                        val strings4 = s2.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                        if (strings4.size > 1) {
                                            val s3 = strings4[1]
                                            var i = 0
                                            while (i < s3.length) {
                                                if (i + 2 <= s3.length) {
                                                    list.add(s3.substring(i, i + 2))
                                                }
                                                i += 2
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Logger.e("$type==菜单操作权限==> %s", list)
            return list
        }
        return null
    }

    /**
     * 获取抵押贷或者信用贷下的权限码
     * @param type 0->信用贷 1->抵押贷
     * @param permissions 权限码
     */
    @JvmStatic
    fun getPermitValue(permissions: String, type: Int): List<String>? {
        if (!TextUtils.isEmpty(permissions)) {
            val list = ArrayList<String>()
            val strings = permissions.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (strings.size > 2) {
                val strings1 = strings[2].split("M".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (strings1.isNotEmpty()) {
                    for (s in strings1) {
                        if (s.startsWith("02")) {
                            val strings2 = s.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            if (strings.size > 1) {
                                val s1 = strings2[1]
                                val strings3 = s1.split("F".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                for (s2 in strings3) {
                                    if (type == 1) {
                                        if (s2.startsWith("061")) {
                                            val strings4 = s2.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                            if (strings4.size > 1) {
                                                val s3 = strings4[1]
                                                var i = 0
                                                while (i < s3.length) {
                                                    if (i + 2 <= s3.length) {
                                                        list.add(s3.substring(i, i + 2))
                                                    }
                                                    i += 2
                                                }
                                            }
                                        }
                                    } else {
                                        if (s2.startsWith("060")) {
                                            val strings4 = s2.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                            if (strings4.size > 1) {
                                                val s3 = strings4[1]
                                                var i = 0
                                                while (i < s3.length) {
                                                    if (i + 2 <= s3.length) {
                                                        list.add(s3.substring(i, i + 2))
                                                    }
                                                    i += 2
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Logger.e("==权限==> %s", list)
            return list
        }
        return null
    }


}