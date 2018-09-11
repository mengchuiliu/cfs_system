package com.xxjr.cfs_system.LuDan.view.activitys.report

import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.ViewsHolder.MyMarkerView
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_report_line.*
import java.text.SimpleDateFormat
import java.util.*

class ReportLineActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>() {
    private lateinit var mOrientationListener: OrientationEventListener // 屏幕方向改变监听器
    private lateinit var mv: MyMarkerView
    private lateinit var chooseTime: String
    private var dataType: Int = 0
    private var dateType: Int = 1

    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_report_line

    override fun initView(savedInstanceState: Bundle?) {
        chooseTime = intent.getStringExtra("chooseTime")
        val showTime = if (dateType == 1 && chooseTime != "本月") Utils.FormatTime(chooseTime, "yyyy-M月", "yyyy年M月") else chooseTime
        tv_title.text = "${showTime}数据折线图"
        initOrientationListener()
        iv_back.setOnClickListener { onBackPressed() }

        val jsonData = intent.getStringExtra("jsonData")
        dataType = intent.getIntExtra("dataType", 0)
        dateType = intent.getIntExtra("dateType", 1)
        val selectIds = intent.getStringExtra("selectIds")
        val jsonObject = JSONObject.parseObject(jsonData)

        val commonItems = getChartData(dataType, dateType, selectIds, jsonObject ?: JSONObject())
        initChart(mutableListOf<MutableList<Entry>>().apply {
            if (commonItems.isNotEmpty()) {
                for (commonItem in commonItems) {
                    add(commonItem.list)
                }
            }
        })
        refreshLineData(commonItems)
    }

    //屏幕旋转角度监听
    private fun initOrientationListener() {
        mOrientationListener = object : OrientationEventListener(this@ReportLineActivity) {
            override fun onOrientationChanged(rotation: Int) {
                // 只检测是否有四个角度的改变
                if ((rotation in 353..359) || (rotation in 0..7)) {
                    // 0度：手机默认竖屏状态（home键在正下方）
                    onBackPressed()
                } else if (rotation > 80 && rotation < 100) {
                    // 90度：手机顺时针旋转90度横屏（home建在左侧）
                } else if (rotation > 170 && rotation < 190) {
                    // 手机顺时针旋转180度竖屏（home键在上方）
                } else if (rotation > 260 && rotation < 280) {
                    // 手机顺时针旋转270度横屏，（home键在右侧）
                }
            }
        }
    }

    //初始化折线图属性
    private fun initChart(entrys: MutableList<MutableList<Entry>>) {
        val desc = Description()
        desc.isEnabled = false
        lineChart.run {
            description = desc
            setNoDataText("没有显示数据")//没有数据时显示的文字
            setNoDataTextColor(Color.parseColor("#54b1fd"))//没有数据时显示文字的颜色
            setDrawGridBackground(false)//chart 绘图区后面的背景矩形将绘制
            setTouchEnabled(true) // 设置是否可以触摸
            isDragEnabled = true// 是否可以拖拽
            setScaleEnabled(false)// 是否可以缩放 x和y轴, 默认是true
            isScaleXEnabled = false //是否可以缩放 仅x轴
            isScaleYEnabled = false //是否可以缩放 仅y轴
            setPinchZoom(false)//设置x轴和y轴能否同时缩放。默认是否
            isDoubleTapToZoomEnabled = false//设置是否可以通过双击屏幕放大图表。默认是true
            isHighlightPerDragEnabled = false//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
            isDragDecelerationEnabled = true//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
//            dragDecelerationFrictionCoef = 0.99f//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。
            extraTopOffset = 10f
        }

        lineChart.legend.apply {
            isEnabled = true
            form = Legend.LegendForm.LINE
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            textSize = 12f
            xEntrySpace = 20f
            formSize = 12f
            isWordWrapEnabled = true // 当图例超出时是否换行适配，这个配置会降低性能，且只有图例在底部时才可以适配。默认false
            textColor = resources.getColor(R.color.font_c6)
        }

        lineChart.xAxis.apply {
            isEnabled = true
            setDrawGridLines(true)
            setDrawAxisLine(true)
            position = XAxis.XAxisPosition.BOTTOM // 设置x轴数据的位置
            gridColor = resources.getColor(R.color.font_c9)//网格线颜色
            axisLineWidth = 1.5f
            textColor = R.color.font_c9
            textSize = 10f
            axisMinimum = 0f
            granularity = 1f
            enableGridDashedLine(10f, 10f, 0f)
            setAvoidFirstLastClipping(true)//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        }

        lineChart.axisLeft.apply {
            // 设置y轴数据的位置
            setDrawGridLines(true)
            setDrawAxisLine(true)
            setDrawZeroLine(false)
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            gridColor = resources.getColor(R.color.font_c9)//网格线颜色
            axisLineWidth = 1.5f
            textColor = R.color.font_c9
            textSize = 10f
            // 设置y轴数据偏移量
            axisMinimum = 0f
//            setLabelCount(7, false)
            labelCount = 7
            setDrawLabels(true)
            enableGridDashedLine(10f, 10f, 0f)
        }

        lineChart.axisRight.apply {
            isEnabled = false
        }

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {}

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                mv = MyMarkerView(this@ReportLineActivity, R.layout.chart_mark)
                mv.setContent(chooseTime, dateType, dataType)
                mv.setCallBack(RecycleItemClickListener { x ->
                    mv.setMakerView(x, lineChart.legend.labels, entrys)
                })
                mv.chartView = lineChart
                lineChart.marker = mv
                lineChart.setDrawMarkers(true)
                lineChart.isHighlightPerDragEnabled = true
            }
        })
        //自定义的MarkerView对象
        mv = MyMarkerView(this@ReportLineActivity, R.layout.chart_mark)
    }


    //获取折线图数据
    fun getChartData(dataType: Int, dateType: Int, selectIds: String, jsonObject: JSONObject): MutableList<CommonItem<Entry>> {
        val commonItems = mutableListOf<CommonItem<Entry>>()
        if (jsonObject.isNotEmpty()) {
            val ids = selectIds.split(",")
            for (j in ids.indices) {
                val id = ids[j]
                val jsonArray = jsonObject.getJSONArray(id)
                val commonItem = CommonItem<Entry>()
                if (id == "all") commonItem.name = "全国" else commonItem.name = jsonArray.getJSONObject(0).getString("Name")
                commonItem.icon = getLineColor(j)
                val entrys = mutableListOf<Entry>()
                for (i in jsonArray.indices) {
                    val x = when (dateType) {
                        1 -> if (jsonArray.getJSONObject(i).getFloatValue("Day") - 1 < 0) 0f else (jsonArray.getJSONObject(i).getFloatValue("Day") - 1)
                        2 -> if (jsonArray.getJSONObject(i).getFloatValue("Months") - 1 < 0) 0f else (jsonArray.getJSONObject(i).getFloatValue("Months") - 1)
                        else -> 0f
                    }
                    val y = when (dataType) {
                        0 -> jsonArray.getJSONObject(i).getFloatValue("LoanCount")
                        1 -> Utils.div(jsonArray.getJSONObject(i).getDoubleValue("ReplyAmount")).toFloat()
                        2 -> Utils.div(jsonArray.getJSONObject(i).getDoubleValue("LendAmount")).toFloat()
                        3 -> Utils.div(jsonArray.getJSONObject(i).getDoubleValue("BackPaymentAmount")).toFloat()
                        else -> 0f
                    }
                    entrys.add(Entry(x, y))
                }
                commonItem.list = entrys
                commonItems.add(commonItem)
            }
        }
        return commonItems
    }

    //每条线的颜色
    private fun getLineColor(pos: Int): Int = when (pos) {
        0 -> Color.parseColor("#f4af37")
        1 -> Color.parseColor("#54b1fd")
        2 -> Color.parseColor("#f67f7f")
        3 -> Color.parseColor("#57c081")
        4 -> Color.parseColor("#7e65d6")
        else -> Color.parseColor("#00000000")
    }

    //设置折线图数据
    private fun refreshLineData(datas: MutableList<CommonItem<Entry>>) = if (datas.isEmpty()) {
        tv_show_unit.visibility = View.GONE
        lineChart.data = null
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    } else {
        tv_show_unit.visibility = View.VISIBLE
        tv_show_unit.text = when (dataType) {
            0 -> "（单位/笔）"
            1 -> "（单位/万）"
            2 -> "（单位/万）"
            3 -> "（单位/万）"
            else -> ""
        }
        lineChart.setDrawMarkers(false)
        lineChart.isHighlightPerDragEnabled = false
        //判断图表中原来是否有数据
        refreshXAxis()
        val dataSets = mutableListOf<ILineDataSet>()
        for (commonItem in datas) {
            // 保存LineDataSet集合,多条折线图
            //LineDataSet参数1：数据源 参数2：图例名称
            dataSets.add(LineDataSet(commonItem.list, commonItem.name).apply {
                color = commonItem.icon//线的颜色
                setCircleColor(commonItem.icon)
                lineWidth = 1f//设置线宽
                circleRadius = 2f//设置焦点圆心的大小
                setDrawCircleHole(false)//是否空心
                highlightLineWidth = 1.5f//设置点击交点后显示高亮线宽
                highLightColor = resources.getColor(R.color.font_cc)//设置点击交点后显示交高亮线的颜色
                setDrawHorizontalHighlightIndicator(false)//设置不画x高亮线
                valueTextSize = 9f//设置显示值的文字大小
                setDrawValues(false)
                setDrawFilled(false)//设置禁用范围背景填充s
            })
        }
        lineChart.data = LineData(dataSets)
        //绘制图表
        lineChart.invalidate()
    }

    //刷新X轴数据
    private fun refreshXAxis() {
        when (dateType) {
            0 -> lineChart.xAxis.apply {
                labelCount = 7
                valueFormatter = IAxisValueFormatter({ value, _ ->
                    getWeekXText(value)
                })
            }
            1 -> {
                lineChart.xAxis.apply {
                    valueFormatter = IAxisValueFormatter({ value, _ ->
                        getMonthXText(value)
                    })
                }
                val matrix = Matrix()
                matrix.postScale(3.5f, 1f)
                lineChart.viewPortHandler.refresh(matrix, lineChart, false)
            }
            2 -> {
                lineChart.xAxis.apply {
                    labelCount = 12
                    valueFormatter = IAxisValueFormatter({ value, _ ->
                        getYearXText(value)
                    })
                }
                val matrix = Matrix()
                matrix.postScale(1f, 1f)
                lineChart.viewPortHandler.refresh(matrix, lineChart, false)
            }
        }

        when (dataType) {
            0 -> lineChart.axisLeft.apply {
                valueFormatter = IAxisValueFormatter { value, axis -> (Math.ceil(value.toDouble())).toInt().toString() + "笔" }
            }
            1, 2, 3 -> lineChart.axisLeft.apply {
                valueFormatter = IAxisValueFormatter { value, axis -> (Math.ceil(value.toDouble())).toInt().toString() + "万" }
            }
        }
        lineChart.animateX(1500)
    }


    override fun onBackPressed() {
        setResult(99)
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        mOrientationListener.enable()
    }

    override fun onDestroy() {
        mOrientationListener.disable()
        super.onDestroy()
    }

    fun getYearXText(value: Float): String {
        return (0..11)
                .firstOrNull { it.toFloat() == value }
                ?.let { "${it + 1}月" }
                ?: ""
    }

    fun getMonthXText(value: Float): String {
        if (chooseTime == "本月") {
            chooseTime = getTabText(0)
        }
        return (0 until getDayOfMonth(chooseTime))
                .firstOrNull { it.toFloat() == value }
                ?.let { "${it + 1}日" }
                ?: ""
    }

    fun getWeekXText(value: Float): String {
        return (0..6)
                .firstOrNull { it.toFloat() == value }
                ?.let { "${it + 1}周" }
                ?: ""
    }

    fun getTabText(position: Int): String {
        val sdf = SimpleDateFormat("yyyy-M月") //设置时间格式
        val calendar = Calendar.getInstance() //得到日历
        calendar.time = Date()//把当前时间赋给日历
        calendar.add(Calendar.MONTH, -position)  //设置为前3月
        return sdf.format(calendar.time)
    }

    //获取选择月份的最大天数
    fun getDayOfMonth(chooseMonth: String): Int {
        val sdf = SimpleDateFormat("yyyy-M月") //设置时间格式
        val calendar = Calendar.getInstance() //得到日历
        calendar.time = sdf.parse(chooseMonth)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}
