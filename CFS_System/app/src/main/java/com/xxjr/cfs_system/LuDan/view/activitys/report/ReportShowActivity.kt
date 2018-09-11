package com.xxjr.cfs_system.LuDan.view.activitys.report

import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.ViewsHolder.BarMarkerView
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_report_show.*

class ReportShowActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>() {
    private lateinit var mv: BarMarkerView
    private var contrastType: Int = 0 //0->回款量 1->签单量 2->放款金额 3->放款笔数
    private lateinit var mOrientationListener: OrientationEventListener // 屏幕方向改变监听器

    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_report_show

    override fun initView(savedInstanceState: Bundle?) {
        val jsonData = intent.getStringExtra("jsonData")
        contrastType = intent.getIntExtra("contrastType", 0)

        tv_title.text = "${DateUtil.getMonth() - 2}/${DateUtil.getMonth() - 1}/${DateUtil.getMonth()}月份同期数据对比图"
        initOrientationListener()
        iv_back.setOnClickListener { onBackPressed() }
        val commonItems = getDatas(jsonData)
        initChart(mutableListOf<MutableList<BarEntry>>().apply {
            if (commonItems.isNotEmpty()) {
                for (commonItem in commonItems) {
                    add(commonItem.list)
                }
            }
        })
        refreshBarData(commonItems)
    }

    //屏幕旋转角度监听
    private fun initOrientationListener() {
        mOrientationListener = object : OrientationEventListener(this@ReportShowActivity) {
            override fun onOrientationChanged(rotation: Int) {
                // 只检测是否有四个角度的改变
                if ((rotation in 353..359) || (rotation in 0..7)) {
                    // 0度：手机默认竖屏状态（home键在正下方）
                    onBackPressed()
                } else if (rotation in 81..99) {
                    // 90度：手机顺时针旋转90度横屏（home建在左侧）
                } else if (rotation in 171..189) {
                    // 手机顺时针旋转180度竖屏（home键在上方）
                } else if (rotation in 261..279) {
                    // 手机顺时针旋转270度横屏，（home键在右侧）
                }
            }
        }
    }

    private fun initChart(entrys: MutableList<MutableList<BarEntry>>) {
        barChart.run {
            description = Description().apply { isEnabled = false }
            setFitBars(true)//在bar开头结尾两边添加一般bar宽的留白
            setNoDataText("暂无数据")
            setNoDataTextColor(Color.parseColor("#54b1fd"))//没有数据时显示文字的颜色
            setDrawGridBackground(false)//chart 绘图区后面的背景矩形将绘制
            isDragEnabled = true// 是否可以拖拽
            setScaleEnabled(false)// 是否可以缩放 x和y轴, 默认是true
//            val matrix = Matrix()
//            matrix.postScale(2f, 1f)
//            viewPortHandler.refresh(matrix, barChart, false)
            isScaleXEnabled = false //是否可以缩放 仅x轴
            isScaleYEnabled = false //是否可以缩放 仅y轴
            setPinchZoom(false)//设置x轴和y轴能否同时缩放。默认是否
            isDoubleTapToZoomEnabled = false//设置是否可以通过双击屏幕放大图表。默认是true
            isHighlightPerDragEnabled = false//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
            isDragDecelerationEnabled = true//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
            extraBottomOffset = 8f
            this.legend.apply {
                isEnabled = true
                form = Legend.LegendForm.LINE
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                textSize = 12f
                xEntrySpace = 20f
                formSize = 15f
                isWordWrapEnabled = true
                formLineWidth = 10f
                textColor = resources.getColor(R.color.font_c6)
            }
            animateY(1000, Easing.EasingOption.Linear)
            animateX(1000, Easing.EasingOption.Linear)
        }

        barChart.xAxis.apply {
            isEnabled = true
            setDrawGridLines(false)
            setDrawAxisLine(true)
            position = XAxis.XAxisPosition.BOTTOM // 设置x轴数据的位置
            setCenterAxisLabels(true)//设置在轴中间
            granularity = 1f//设置最小间隔，防止当放大时，出现重复标签。
            axisMinimum = 0f
            axisMaximum = 31f
            setLabelCount(31, false)//设置标签显示的个数
            valueFormatter = IAxisValueFormatter { value, axis ->
                "${value.toInt() + 1}"
            }

            valueFormatter.getFormattedValue(0f, this)
            axisLineWidth = 1.5f
            textColor = R.color.font_c9
            textSize = 10f
        }

        barChart.axisLeft.apply {
            // 设置y轴数据的位置
            setDrawGridLines(false)
            setDrawAxisLine(true)
            setDrawZeroLine(false)
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            axisLineWidth = 1.5f
            textColor = R.color.font_c9
            textSize = 12f
            // 设置y轴数据偏移量
            axisMinimum = 0f
        }

        barChart.axisRight.apply {
            isEnabled = false
        }

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {}

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                mv = BarMarkerView(this@ReportShowActivity, R.layout.chart_mark)
                mv.setText("第" + barChart.xAxis.valueFormatter.getFormattedValue(e?.x
                        ?: 0f, barChart.xAxis))
                mv.setCallBack(object : BarMarkerView.MakerClickListener {
                    override fun onItemClick(x: Float) {
                        mv.setMakerContent(contrastType, x.toInt(), barChart.legend.labels, entrys)
                    }
                })
                mv.chartView = barChart
                barChart.marker = mv
                barChart.setDrawMarkers(true)
            }
        })
        mv = BarMarkerView(this@ReportShowActivity, R.layout.chart_mark)
    }

    //刷新柱状图数据
    private fun refreshBarData(datas: MutableList<CommonItem<BarEntry>>) = if (datas.isEmpty()) {
        tv_show_unit.visibility = View.GONE
        barChart.data = null
        barChart.notifyDataSetChanged()
        barChart.invalidate()
    } else {
        tv_show_unit.visibility = View.VISIBLE
        tv_show_unit.text = if (contrastType == 1 || contrastType == 3) "(单位/笔)" else "(单位/万)"
        barChart.setDrawMarkers(false)
        val data = BarData()
        for (commonItem in datas) {
            data.addDataSet(BarDataSet(commonItem.list, commonItem.name).apply {
                axisDependency = YAxis.AxisDependency.LEFT
                color = commonItem.icon
            })
        }
        // (barWidth + barSpace) * 3 + groupSpace = 1，即一个间隔为一组，包含两个柱图 -> interval per "group"
//        val groupSpace = 0.28f
//        val barSpace = 0.035f
//        val barWidth = 0.205f
        val groupSpace = 0.28f
        val barSpace = 0.0f
        val barWidth = 0.24f
//        data.isHighlightEnabled = false
        data.setDrawValues(false)
        data.barWidth = barWidth
        data.groupBars(0f, groupSpace, barSpace)//(起始点、柱状图组间距、柱状图之间间距)
        barChart.data = data
        barChart.invalidate()
    }

    //获取同期对比数据
    fun getDatas(jsonData: String) = mutableListOf<CommonItem<BarEntry>>().apply {
        if (jsonData.isNotBlank()) {
            val jsonObject = JSONObject.parseObject(jsonData)
            if (jsonObject != null && jsonObject.isNotEmpty()) {
                for (i in 2 downTo 0) {
                    add(CommonItem<BarEntry>().apply {
                        val jsonArray = jsonObject.getJSONArray(DateUtil.getBeforeMonth(-i))
                        name = "${DateUtil.getBeforeMonth(-i)}月"
                        when (i) {
                            2 -> icon = Color.parseColor("#54b1fd")
                            1 -> icon = Color.parseColor("#9cd632")
                            0 -> icon = Color.parseColor("#fe6caa")
                        }
                        list = mutableListOf<BarEntry>().apply {
                            if (jsonArray != null && jsonArray.isNotEmpty()) {
                                for (j in jsonArray.indices) {
                                    val json = jsonArray.getJSONObject(j)
                                    add(BarEntry(json.getFloat("Day"),
                                            if (contrastType == 0 || contrastType == 2) Utils.div(json.getDouble("TotalValue")).toFloat()
                                            else json.getFloat("TotalValue")))
                                }
                            }
                        }
                    })
                }
            }
        }
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
}
