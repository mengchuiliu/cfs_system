package com.xxjr.cfs_system.tools

import java.math.BigDecimal

/**
 *
 * 业务相关类
 * @author huangdongqiang
 * @date 28/06/2018
 */
object BusinessUtils {

    /**
     * 获取 交易状态
     * 交易状态（0已发起 1交易成功 2交易失败 3未发送 4发送中 5 发送超时）
     * 注意：当支付方式为代扣3时，交易状态-1时，AisleType为1、2、5时显示为“未发起交易”其他交易状态为-1的情况统一留白
     *
     * 最新修改：只有代扣情况下有交易状态，其他都为 ""
     */
    fun getBusinessState(payType: Int, aisleType: String, businessState: Int): String {

//        return when (businessState) {
//            -1 -> when (payType) {
//                3 -> when (aisleType) {
//                    "1" -> "未发起交易"
//                    "2" -> "未发起交易"
//                    "5" -> "未发起交易"
//                    else -> ""
//                }
//                else -> ""
//            }
//            0 -> "已发起"
//            1 -> "交易成功"
//            2 -> "交易失败"
//            3 -> "未发送"
//            4 -> "发送中"
//            5 -> "发送超时"
//            else -> ""
//        }

        //最新修改：只有代扣情况下有交易状态，其他都为 ""
        return when (payType) {
            3 -> {
                when (businessState) {
                    -1 -> "未发起交易"
                    0 -> "已发起"
                    1 -> "交易成功"
                    2 -> "交易失败"
                    3 -> "未发送"
                    4 -> "发送中"
                    5 -> "发送超时"
                    else -> ""
                }
            }
            else -> ""
        }

    }

    /**
     * 获取 审核状态
     * 金账户：待充值 6,已入账 1, 交易中5
     * 代扣： 待审核 0,审核拒绝2,已审核3,已发起代扣4,已入账1
     * 现金转账：待审核0,审核拒绝2,已入账1
     */
    fun getState(payType: Int?, state: Int?): String {
        return when (payType) {
            1, 2, 3 -> {
                when (state) {
                    0 -> "待审核"
                    1 -> "已入账"
                    2 -> "审核拒绝"
                    3 -> "已审核"
                    4 -> "已发起代扣"
                    else -> ""
                }
            }
            4 -> {
                when (state) {
                    1 -> "已入账"
                    5 -> "交易中"
                    6 -> "待充值"
                    else -> ""
                }
            }
            else -> ""
        }
    }

    /**
     * 代扣平台
     */
    fun getAisleType(aisletype: String): String {
        return when (aisletype) {
            "1" -> "通联支付"
            "2" -> "上海富友"
            "3" -> "宝付"
            "4" -> "通菀"
            "5" -> "通联协议"
            "6" -> "宝付代扣"
            else -> ""
        }
    }

    /**
     * 支付方式（现金1、转账2、代扣3、金账户4）
     */
    fun getPayType(payType: Int): String {
        return when (payType) {
            1 -> "现金"
            2 -> "转账"
            3 -> "代扣"
            4 -> "金账户"
            else -> ""
        }
    }


    /**
     * 大数输出 - 不使用科学技术法
     * @param money
     * @param newScale  保留小数点后几位 (默认两位)
     * @return
     */
    fun getBigDecimal(money: Double, newScale: Int = 2): String {
        try {

            val bigDecimal = BigDecimal(money)
            return bigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
        } catch (e: Exception) {
        }

        return ""
    }

    /**
     * 大数输出 - 不使用科学技术法
     * @param money
     * @param newScale  保留小数点后几位 (默认两位)
     * @return
     */
    fun getBigDecimalToDobule(money: Double, newScale: Int = 2): Double {
        try {
            val bigDecimal = BigDecimal(money)
            return bigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_UP).toDouble()
        } catch (e: Exception) {
        }

        return 0.0
    }
}