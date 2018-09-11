package com.xxjr.cfs_system.tools

import android.annotation.SuppressLint
import entity.Reimburse
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object ReimburseFormula {
    /**
     * @param amount 还款金额
     * @param rate 年利率
     * @param startDate 还款开始日期 yyyy-MM-dd
     * @param endDate 还款结束日期 yyyy-MM-dd
     * @param returnDate 还款日
     * @param reimburseType 还款类型 1->先息后本(按月) 2->先息后本(按季) 3->先息后本(到期) 4->等额本息 5->等额本金
     */
    fun getReimburseData(amount: Double, rate: Double, startDate: String, endDate: String, returnDate: Int, reimburseType: Int): MutableList<Reimburse> =
            mutableListOf<Reimburse>().apply {
                val months = getMonthSpace(startDate, endDate)
                addAll(when (reimburseType) {
                    1 -> getMonthPriorityInterest(amount, rate, startDate, endDate, returnDate)
                    2 -> getQuarterPriorityInterest(amount, rate, startDate, endDate, returnDate)
                    3 -> getDuePriorityInterest(amount, rate, startDate, endDate)
                    4 -> getEqualInterest(amount, rate, months, endDate, returnDate)
                    5 -> getEqualAmountData(amount, rate, months, endDate, returnDate)
                    else -> mutableListOf()
                })
            }


    /**
     * 等额本金
     * 每月还款金额=（本金/还款月数）+（本金-累计已还本金）×月利率
     * 每月利息=（本金-累计已还本金）×月利率
     * 每月应还本金=贷款本金÷还款月数
     * 还款总利息=（还款月数+1）×贷款额×月利率/2
     * 还款总额=（还款月数+1）×贷款额×月利率/2+ 贷款额
     */
    private fun getEqualAmountData(amount: Double, rate: Double, months: Int, endDate: String, returnDate: Int): MutableList<Reimburse> {
        var sum = 0.0 //累计已还本金
        val reimburses = mutableListOf<Reimburse>()
        val monthRate = rate / 100 / 12 //月利率
        for (i in 1..months) {
            val reimburseA = (amount / months) + (amount - sum) * monthRate
            reimburses.add(Reimburse().apply {
                reimburseAmount = keepTwoDecimal(reimburseA)
                interests = keepTwoDecimal((amount - sum) * monthRate)
                reimburseDate = getBeforeMonth(endDate, -(months - i), returnDate)
            })
            sum += amount / months
        }
        return reimburses
    }

    /**
     * 等额本金
     * @param amount 本金
     * @param rate 年利率
     * @param months 月数
     */
    fun getEqualAmountData(amount: Double, rate: Double, months: Int): MutableList<Reimburse> {
        var sum = 0.0 //累计已还本金
        val reimburses = mutableListOf<Reimburse>()
        val monthRate = rate / 100 / 12 //月利率
        val cumulativeInterests = (months + 1) * amount * monthRate / 2 //还款总利息
        val cumulativeAmount = amount + cumulativeInterests  //还款总额
        for (i in 1..months) {
            val reimburseA = (amount / months) + (amount - sum) * monthRate
            val remainAmount = amount - i * (amount / months)
            reimburses.add(Reimburse().apply {
                reimburseAmount = keepTwoDecimal(reimburseA)
                interests = keepTwoDecimal((amount - sum) * monthRate)
                this.cumulativeInterests = keepTwoDecimal(cumulativeInterests)
                this.cumulativeAmount = keepTwoDecimal(cumulativeAmount)
                this.remainAmount = keepTwoDecimal(remainAmount)
                countDate = "第${i}期"
            })
            sum += amount / months
        }
        return reimburses
    }

    /**
     * 等额本息
     *  每月还款金额= [贷款本金×月利率×（1+月利率）^还款月数]÷[（1+月利率）^还款月数－1]
     *  X=BX-B= a*i(1+i)^N/[(1+i)^N-1]- a*i(1+i)^(n-1)/[(1+i)^N-1] = a*i*[(1+i)^N - (1+i)^(n-1)]/[(1+i)^N-1]
     *  （注：BX=等额本息还贷每月所还本金和利息总额，B=等额本息还贷每月所还本金，
     *  a=贷款总金额
     *  i=贷款月利率，
     *  N=还贷总月数，
     *  n=第n个月
     *  X=等额本息还贷每月所还的利息）
     */
    private fun getEqualInterest(amount: Double, rate: Double, months: Int, endDate: String, returnDate: Int): MutableList<Reimburse> {
        val reimburses = mutableListOf<Reimburse>()

        val monthRate = rate / 100 / 12
        val flag = Math.pow((1 + monthRate), months.toDouble())
        val reimburseA = (amount * monthRate * flag) / (flag - 1)

        for (i in 1..months) {
            reimburses.add(Reimburse().apply {
                reimburseAmount = keepTwoDecimal(reimburseA)
                interests = keepTwoDecimal(amount * monthRate *
                        (flag - (Math.pow((1 + monthRate), (i - 1).toDouble()))) / (flag - 1))
                reimburseDate = getBeforeMonth(endDate, -(months - i), returnDate)
            })
        }

        return reimburses
    }

    /**
     * 等额本息
     */
    fun getEqualInterest(amount: Double, rate: Double, months: Int): MutableList<Reimburse> {
        val reimburses = mutableListOf<Reimburse>()

        val monthRate = rate / 100 / 12
        val flag = Math.pow((1 + monthRate), months.toDouble())
        val reimburseA = (amount * monthRate * flag) / (flag - 1)//月供金额

        val cumulativeAmount = reimburseA * months  //还款总额
        val cumulativeInterests = cumulativeAmount - amount //还款总利息
        var sum = 0.0//累计还款本金
        for (i in 1..months) {
            reimburses.add(Reimburse().apply {
                reimburseAmount = keepTwoDecimal(reimburseA)
                interests = keepTwoDecimal(amount * monthRate *
                        (flag - (Math.pow((1 + monthRate), (i - 1).toDouble()))) / (flag - 1))
                sum += reimburseA - interests
                this.cumulativeAmount = keepTwoDecimal(cumulativeAmount)
                this.cumulativeInterests = keepTwoDecimal(cumulativeInterests)
                this.remainAmount = if ((amount - sum) < 0.0) 0.0 else keepTwoDecimal(amount - sum)
                countDate = "第${i}期"
            })
        }

        return reimburses
    }

    /**
     * 到期还本(按月)
     */
    fun getMonthPriorityInterest(amount: Double, rate: Double, startDate: String, endDate: String, returnDate: Int): MutableList<Reimburse> {
        val reimburses = mutableListOf<Reimburse>()
        val months = getMonthSpace(startDate, endDate)
        val dayRate = rate / 100 / 360//日利率
        val startD = getStartReturnDay(startDate, returnDate)

        reimburses.add(Reimburse().apply {
            reimburseAmount = if (months <= 1) amount + amount * dayRate * getDaySpace(startDate, startD)
            else amount * dayRate * getDaySpace(startDate, startD)
            interests = amount * dayRate * getDaySpace(startDate, startD)
            countDate = getLateMonth(startD, 0, "yyyy-MM-dd", "yyyy/MM/dd", returnDate)
            reimburseDate = countDate
            remainAmount = amount
            cumulativeInterests = amount * dayRate * getDaySpace(startDate, endDate)
            cumulativeAmount = amount + cumulativeInterests
        })

        if (months > 1) {
            var eachQuarter = getLateMonth(startD, 1, "yyyy-MM-dd", "yyyy-MM-dd", returnDate)
            var lastQuarter = startD
            while (getTimeLong(eachQuarter) < getTimeLong(endDate)) {
                reimburses.add(Reimburse().apply {
                    reimburseAmount = amount * dayRate * getDaySpace(lastQuarter, eachQuarter)
                    interests = reimburseAmount
                    countDate = getLateMonth(eachQuarter, 0, "yyyy-MM-dd", "yyyy/MM/dd", returnDate)
                    reimburseDate = countDate
                    remainAmount = amount
                    cumulativeInterests = amount * dayRate * getDaySpace(startDate, endDate)
                    cumulativeAmount = amount + cumulativeInterests
                })
                lastQuarter = eachQuarter
                val temp = getLateMonth(eachQuarter, 1, "yyyy-MM-dd", "yyyy-MM-dd", returnDate)
                eachQuarter = temp
            }
            reimburses.add(Reimburse().apply {
                reimburseAmount = amount + amount * dayRate * getDaySpace(lastQuarter, endDate)
                interests = amount * dayRate * getDaySpace(lastQuarter, endDate)
                countDate = getLateMonth(endDate, 0, "yyyy-MM-dd", "yyyy/MM/dd", 0)
                reimburseDate = countDate
                remainAmount = amount
                cumulativeInterests = amount * dayRate * getDaySpace(startDate, endDate)
                cumulativeAmount = amount + cumulativeInterests
            })
        }
        return reimburses
    }

    /**
     * 到期还本（按季）
     * @param returnDate 月还款日，手动填写，不填还款的最后期限来算
     */
    fun getQuarterPriorityInterest(amount: Double, rate: Double, startDate: String, endDate: String, returnDate: Int): MutableList<Reimburse> {
        val reimburses = mutableListOf<Reimburse>()
        val dayRate = rate / 100 / 360//日利率
        val startD = getStartQuarter(startDate, returnDate)
        if (getTimeLong(startDate) < getTimeLong(startD)) {//刚好在3,6,9,12月份，并且天数小于还款日
            reimburses.add(Reimburse().apply {
                reimburseAmount = amount * dayRate * getDaySpace(startDate, startD)
                interests = reimburseAmount
                countDate = getLateMonth(startD, 0, "yyyy-MM-dd", "yyyy/MM/dd", returnDate)
                reimburseDate = countDate
                remainAmount = amount
                cumulativeInterests = amount * dayRate * getDaySpace(startDate, endDate)
                cumulativeAmount = amount + cumulativeInterests
            })
        }
        var eachQuarter = getLateMonth(startD, 3, "yyyy-MM-dd", "yyyy-MM-dd", returnDate)
        var lastQuarter = startD
        while (getTimeLong(eachQuarter) < getTimeLong(endDate)) {
            reimburses.add(Reimburse().apply {
                reimburseAmount = amount * dayRate * getDaySpace(lastQuarter, eachQuarter)
                interests = reimburseAmount
                countDate = getLateMonth(eachQuarter, 0, "yyyy-MM-dd", "yyyy/MM/dd", returnDate)
                reimburseDate = countDate
                remainAmount = amount
                cumulativeInterests = amount * dayRate * getDaySpace(startDate, endDate)
                cumulativeAmount = amount + cumulativeInterests
            })
            lastQuarter = eachQuarter
            val temp = getLateMonth(eachQuarter, 3, "yyyy-MM-dd", "yyyy-MM-dd", returnDate)
            eachQuarter = temp
        }
        reimburses.add(Reimburse().apply {
            reimburseAmount = amount + amount * dayRate * getDaySpace(lastQuarter, endDate)
            interests = amount * dayRate * getDaySpace(lastQuarter, endDate)
            countDate = getLateMonth(endDate, 0, "yyyy-MM-dd", "yyyy/MM/dd", 0)
            reimburseDate = countDate
            remainAmount = 0.0
            cumulativeInterests = amount * dayRate * getDaySpace(startDate, endDate)
            cumulativeAmount = amount + cumulativeInterests
        })
        return reimburses
    }

    /**
     * 到期还本（到期还本）
     */
    fun getDuePriorityInterest(amount: Double, rate: Double, startDate: String, endDate: String): MutableList<Reimburse> {
        val reimburses = mutableListOf<Reimburse>()
        val dayRate = rate / 100 / 360//日利率
        reimburses.add(Reimburse().apply {
            reimburseAmount = amount + amount * dayRate * getDaySpace(startDate, endDate)
            interests = amount * dayRate * getDaySpace(startDate, endDate)
            countDate = getLateMonth(endDate, 0, "yyyy-MM-dd", "yyyy/MM/dd", 0)
            reimburseDate = countDate
            remainAmount = 0.0
            cumulativeInterests = amount * dayRate * getDaySpace(startDate, endDate)
            cumulativeAmount = amount + cumulativeInterests
        })
        return reimburses
    }

    //获取季度起始月份
    private fun getStartQuarter(startDate: String, returnDate: Int): String {
        var startQuarterDate = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val day: String
        if (returnDate < 1) {
            day = "21"
        } else {
            day = if (returnDate > 9) returnDate.toString() else "0$returnDate"
        }

        val cur = Calendar.getInstance()
        cur.time = sdf.parse(startDate)
        val month = cur.get(Calendar.MONTH) + 1

        when {
            month < 3 -> startQuarterDate = "${cur.get(Calendar.YEAR)}-03-$day"
            month < 6 -> {
                startQuarterDate = if (returnDate > getDayOfMonth("${cur.get(Calendar.YEAR)}-06-01")) {
                    "${cur.get(Calendar.YEAR)}-06-30"
                } else {
                    "${cur.get(Calendar.YEAR)}-06-$day"
                }
            }
            month < 9 -> {
                startQuarterDate = if (returnDate > getDayOfMonth("${cur.get(Calendar.YEAR)}-09-01")) {
                    "${cur.get(Calendar.YEAR)}-09-30"
                } else {
                    "${cur.get(Calendar.YEAR)}-09-$day"
                }
            }
            month < 12 -> startQuarterDate = "${cur.get(Calendar.YEAR)}-12-$day"
        }
        return startQuarterDate
    }

    /**
     * 获取两个时间天数差
     *@param startDate 开始时间
     *@param endDate 结束时间
     */
    private fun getDaySpace(startDate: String, endDate: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val cur = Calendar.getInstance()
        cur.time = sdf.parse(startDate)
        val curLong = cur.time.time

        val cur1 = Calendar.getInstance()
        cur1.time = sdf.parse(endDate)
        val lateLong = cur1.time.time
        return (((lateLong - curLong) / 1000).toInt()) / 3600 / 24
    }

    //获取正常起始月
    private fun getStartReturnDay(startDate: String, returnDate: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val cur = Calendar.getInstance()
        cur.time = sdf.parse(startDate)

        var day = "21"
        if (returnDate < 1) {
            val temp = SimpleDateFormat("yyyy-MM").format(sdf.parse(startDate)) + "-$day"
            if (cur.time.time > getTimeLong(temp)) {
                cur.add(Calendar.MONTH, 1)
            }
        } else {
            val days = cur.get(Calendar.DAY_OF_MONTH)
            val maxD = cur.getActualMaximum(Calendar.DAY_OF_MONTH)
            if (days < returnDate) {
                if (maxD >= returnDate) {
                    day = if (returnDate > 9) returnDate.toString() else "0$returnDate"
                } else {
                    day = maxD.toString()
                }
            } else {
                day = if (returnDate > 9) returnDate.toString() else "0$returnDate"
                cur.add(Calendar.MONTH, 1)
            }
        }
        return SimpleDateFormat("yyyy-MM").format(cur.time) + "-" + day
    }

    /**
     * 计算日期相差月数
     * @param date1 开始时间
     * @param date2 结束时间
     * @return 返回时间相差月数
     */
    fun getMonthSpace(date1: String, date2: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()
        c1.time = sdf.parse(date1)
        c2.time = sdf.parse(date2)
        val month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH)
        val year = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12
        return if (Math.abs(year + month) > 0) Math.abs(year + month) else 1
    }

    /**
     * @param date 某个日期
     * @param space 时间间隔月份
     * @param returnDate 特定的日
     * @return 返回日期前几个月时间
     */
    private fun getBeforeMonth(date: String, space: Int, returnDate: Int): String {
        val sdf = SimpleDateFormat("yyyy年MM月dd日")
        val sdf1 = SimpleDateFormat("yyyy-MM-dd")
        val sdf2 = SimpleDateFormat("yyyy-MM")
        val cur = Calendar.getInstance()
        cur.time = sdf1.parse(date)
        cur.add(Calendar.MONTH, space)
        val beforeM = cur.getActualMaximum(Calendar.DAY_OF_MONTH)
        val temp: String
        if (beforeM < returnDate) {
            temp = sdf2.format(cur.time) + "-" + beforeM
        } else {
            temp = sdf2.format(cur.time) + "-" + if (returnDate > 9) returnDate else "0$returnDate"
        }
        return sdf.format(sdf1.parse(temp))
    }

    /**
     *间隔多少个月的日期
     * @param date 初始时间
     * @param space 间隔月份数
     * @param pattern 传入时间的格式
     * @param backPattern 返回的时间格式
     */
    fun getLateMonth(date: String, space: Int, pattern: String, backPattern: String, returnDate: Int): String {
        if (date.isBlank() || pattern.isBlank() || backPattern.isBlank()) return ""
        val cur = Calendar.getInstance()
        cur.time = SimpleDateFormat(pattern).parse(date)
        cur.add(Calendar.MONTH, space)
        val result: String
        if (returnDate > 0) {
            val temp: String
            val lateM = cur.getActualMaximum(Calendar.DAY_OF_MONTH)
            if (lateM < returnDate) {
                temp = SimpleDateFormat("yyyy-MM").format(cur.time) + "-" + lateM
            } else {
                temp = SimpleDateFormat("yyyy-MM").format(cur.time) + "-" + if (returnDate > 9) returnDate else "0$returnDate"
            }
            result = SimpleDateFormat(backPattern).format(SimpleDateFormat("yyyy-MM-dd").parse(temp))
        } else {
            result = SimpleDateFormat(backPattern).format(cur.time)
        }
        return result
    }

    /**
     * @param amount 需要格式化的金额
     * 格式化金额保留两位小数
     */
    private fun keepTwoDecimal(amount: Double): Double {
        val b = BigDecimal(amount)
        return (b.setScale(2, BigDecimal.ROUND_HALF_EVEN)).toDouble()
    }

    /**
     * 将 yyyy-MM-dd类型数据转换为long时间戳
     */
    private fun getTimeLong(str: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.parse(str).time
    }

    //获取选择月份的最大天数
    private fun getDayOfMonth(chooseMonth: String): Int {
        val dft = SimpleDateFormat("yyyy-MM-dd") //设置时间格式
        val calendar = Calendar.getInstance()//得到日历
        calendar.time = dft.parse(chooseMonth)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}