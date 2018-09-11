package com.xxjr.cfs_system.LuDan.view.activitys.report

import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.OrientationEventListener
import android.view.View
import android.widget.TextView
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.ReportChartP
import com.xxjr.cfs_system.LuDan.view.viewinter.ReportChartVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_report_chart.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.orhanobut.hawk.Hawk
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.view.activitys.SelectTypeActivity
import com.xxjr.cfs_system.ViewsHolder.MyMarkerView
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import refresh_recyclerview.SimpleItemDecoration
import java.util.*


class ReportChartActivity : BaseActivity<ReportChartP, ReportChartVInter>(), ReportChartVInter, View.OnClickListener {
    private var dataType = 0 // 0:签单量统计；1：批复金额统计;2：放款金额统计；3：回款量统计
    private var selectType = 0//0->区域，1->门店，2->梯队，3->销售来源
    private var selectIds = ""
    private var dateType = 1 //0->周  1->月  2->年
    private var chooseTime = "本月"
    private var entrys: MutableList<MutableList<Entry>> = mutableListOf()

    private var jsonData: String = ""
    private lateinit var mOrientationListener: OrientationEventListener // 屏幕方向改变监听器
    private var isRotate = true

    private var adapter: CommonAdapter<CommonItem<*>>? = null
    private lateinit var adapterData: CommonAdapter<CommonItem<*>>
    private lateinit var mv: MyMarkerView

    override fun getPresenter(): ReportChartP = ReportChartP()

    override fun getLayoutId(): Int = R.layout.activity_report_chart

    override fun initView(savedInstanceState: Bundle?) {
        iv_back.setOnClickListener(this)
        tv_data_type.setOnClickListener(this)
        rl_zone.setOnClickListener(this)
        tv_week.setOnClickListener(this)
        tv_month.setOnClickListener(this)
        tv_year.setOnClickListener(this)
        initOrientationListener()
        presenter.setDefaultValue()
        setDefaultSelect()
        initRV()
        presenter.initDateType(dateType)
        initChart()
        //获取折线图数据
        presenter.getChartData(dataType, chooseTime, selectType, selectIds, dateType)
    }

    //屏幕旋转角度监听
    private fun initOrientationListener() {
        mOrientationListener = object : OrientationEventListener(this@ReportChartActivity) {
            override fun onOrientationChanged(rotation: Int) {
                if (((rotation > 45) && (rotation < 135)) || ((rotation > 225) && (rotation < 315))) {
                    if (isRotate) {
                        isRotate = false
                        val intent = Intent(this@ReportChartActivity, ReportLineActivity::class.java)
                        intent.putExtra("jsonData", jsonData)
                        intent.putExtra("chooseTime", chooseTime)
                        intent.putExtra("dateType", dateType)
                        intent.putExtra("dataType", dataType)
                        intent.putExtra("selectIds", selectIds)
                        startActivityForResult(intent, 99)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onResume() {
        super.onResume()
        mOrientationListener.enable()
    }

    override fun setDataTypeRefresh(dataType: Int) {
        this.dataType = dataType
        mv.setContent(chooseTime, dateType, dataType)
        presenter.getChartData(dataType, chooseTime, selectType, selectIds, dateType)
    }

    override fun initTabType(tabTypes: MutableList<CustomTabEntity>) {
        tab_sliding.setTabData(tabTypes as ArrayList<CustomTabEntity>)
        tab_sliding.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                selectType = position
                setDefaultSelect()
                presenter.getChartData(dataType, chooseTime, selectType, selectIds, dateType)
            }

            override fun onTabReselect(position: Int) {
            }
        })
    }

    private fun setDefaultSelect() {
        tv_zone.text = presenter.getDefaultData(selectType)
        tv_data.text = "签单量数据(单位/笔)"
        selectIds = when (selectType) {
            0 -> presenter.defaultZone
            1 -> Hawk.get<String>("CompanyID")
            2 -> "4"
            3 -> "4"
            else -> ""
        }
    }

    private fun initRV() {
        rv_date.layoutManager = LinearLayoutManager(this@ReportChartActivity, LinearLayoutManager.HORIZONTAL, false)
        adapter = object : CommonAdapter<CommonItem<*>>(this@ReportChartActivity, ArrayList(), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { refreshTitle(position) }
            }
        }
        rv_date.adapter = adapter

        rv_data_show.layoutManager = LinearLayoutManager(this)
        rv_data_show.addItemDecoration(SimpleItemDecoration(this@ReportChartActivity, 1))
        adapterData = object : CommonAdapter<CommonItem<*>>(this@ReportChartActivity, ArrayList(), R.layout.item_progress_chart) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                if (position == 0) {
                    holder.setTextColorRes(R.id.tv_1, R.color.font_c5)
                    holder.setTextColorRes(R.id.tv_2, R.color.font_c5)
                    holder.setTextColorRes(R.id.tv_3, R.color.font_c5)
                    holder.setTextColorRes(R.id.tv_4, R.color.font_c5)
                    holder.setTextColorRes(R.id.tv_5, R.color.font_c5)
                    holder.setTextSize(R.id.tv_1, 13f)
                    holder.setTextSize(R.id.tv_2, 13f)
                    holder.setTextSize(R.id.tv_3, 13f)
                    holder.setTextSize(R.id.tv_4, 13f)
                    holder.setTextSize(R.id.tv_5, 13f)
                    setTextLine(holder.getView(R.id.tv_1), false)
                    setTextLine(holder.getView(R.id.tv_2), false)
                    setTextLine(holder.getView(R.id.tv_3), false)
                    setTextLine(holder.getView(R.id.tv_4), false)
                    setTextLine(holder.getView(R.id.tv_5), false)
                } else {
                    setTextLine(holder.getView(R.id.tv_1), true)
                    setTextLine(holder.getView(R.id.tv_2), true)
                    setTextLine(holder.getView(R.id.tv_3), true)
                    setTextLine(holder.getView(R.id.tv_4), true)
                    setTextLine(holder.getView(R.id.tv_5), true)
                    holder.setTextColorRes(R.id.tv_date, R.color.font_c6)
                    holder.setTextColorRes(R.id.tv_1, R.color.font_c6)
                    holder.setTextColorRes(R.id.tv_2, R.color.font_c6)
                    holder.setTextColorRes(R.id.tv_3, R.color.font_c6)
                    holder.setTextColorRes(R.id.tv_4, R.color.font_c6)
                    holder.setTextColorRes(R.id.tv_5, R.color.font_c6)
                    holder.setTextSize(R.id.tv_date, 12f)
                    holder.setTextSize(R.id.tv_1, 12f)
                    holder.setTextSize(R.id.tv_2, 12f)
                    holder.setTextSize(R.id.tv_3, 12f)
                    holder.setTextSize(R.id.tv_4, 12f)
                    holder.setTextSize(R.id.tv_5, 12f)
                }
                holder.setText(R.id.tv_date, String.format("%2s", item.date))
                holder.setText(R.id.tv_1, item.name)
                holder.setText(R.id.tv_2, item.content)
                holder.setText(R.id.tv_3, item.hintContent)
                holder.setText(R.id.tv_4, item.remark)
                holder.setText(R.id.tv_5, item.payType)
//                val v = max<Double>(doubles)
            }
        }
        rv_data_show.adapter = adapterData
    }

    private fun setTextLine(textView: TextView, flag: Boolean) {
        textView.setSingleLine(flag)
        textView.ellipsize = TextUtils.TruncateAt.END
        if (flag) textView.maxLines = 1 else textView.maxLines = 2
    }

    override fun refreshDateType(dateTypes: MutableList<CommonItem<*>>) {
        adapter?.setNewData(dateTypes)
        if (dateType == 1) {
            (rv_date.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(11, 0)
        }
    }

    override fun refreshChartDate(dataTypes: MutableList<CommonItem<*>>) {
        val showTime = if (dateType == 1 && chooseTime != "本月") Utils.FormatTime(chooseTime, "yyyy-M月", "yyyy年M月") else chooseTime
        when (dataType) {
            0 -> tv_data.text = "签单量（${showTime}，单位/笔）"
            1 -> tv_data.text = "批复金额（${showTime}，单位/万）"
            2 -> tv_data.text = "放款金额（${showTime}，单位/万）"
            3 -> tv_data.text = "回款量（${showTime}，单位/万）"
        }
//        doubles = mutableList
        adapterData.setNewData(dataTypes)
    }

    private fun refreshTitle(position: Int) {
        chooseTime = (adapter?.datas?.get(position) as CommonItem<*>).name
        for (i in 0 until adapter?.datas!!.size) {
            (adapter?.datas?.get(i) as CommonItem<*>).isClick = i == position
        }
        adapter?.notifyDataSetChanged()
        mv.setContent(chooseTime, dateType, dataType)
        presenter.getChartData(dataType, chooseTime, selectType, selectIds, dateType)
    }

    //初始化折线图属性
    private fun initChart() {
        val desc = Description()
        desc.isEnabled = false
        lineChart.run {
            description = desc
            setNoDataText("没有显示数据")//没有数据时显示的文字
            setNoDataTextColor(Color.parseColor("#54b1fd"))//没有数据时显示文字的颜色
            setDrawGridBackground(false)//chart 绘图区后面的背景矩形将绘制
//            setDrawBorders(false)//禁止绘制图表边框的线
            setTouchEnabled(true) // 设置是否可以触摸
            isDragEnabled = true// 是否可以拖拽
            setScaleEnabled(false)// 是否可以缩放 x和y轴, 默认是true
            isScaleXEnabled = false //是否可以缩放 仅x轴
            isScaleYEnabled = false //是否可以缩放 仅y轴
            setPinchZoom(false)//设置x轴和y轴能否同时缩放。默认是否
            isDoubleTapToZoomEnabled = false//设置是否可以通过双击屏幕放大图表。默认是true
            isHighlightPerDragEnabled = false//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
            isDragDecelerationEnabled = true//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
            dragDecelerationFrictionCoef = 0.5f//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。
//            extraBottomOffset = 8f
            extraTopOffset = 10f
//            animateXY(1500, 1500)
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
//            gridColor = Color.parseColor("#A8D7FD")//网格线颜色
//            axisLineColor = Color.parseColor("#A8D7FD")//xy轴线颜色
//            axisLineColor = resources.getColor(R.color.black)//xy轴线颜色
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
//            gridColor = Color.parseColor("#A8D7FD")
//            axisLineColor = Color.parseColor("#A8D7FD")
//            axisLineColor = resources.getColor(R.color.font_c3)//xy轴线颜色
            gridColor = resources.getColor(R.color.font_c9)//网格线颜色
            axisLineWidth = 1.5f
            textColor = R.color.font_c9
            textSize = 10f
            // 设置y轴数据偏移量
            axisMinimum = 0f
            setLabelCount(7, false)
//            labelCount = 7
            setDrawLabels(true)
            enableGridDashedLine(10f, 10f, 0f)
        }

        lineChart.axisRight.apply {
            isEnabled = false
        }

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {}

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                mv = MyMarkerView(this@ReportChartActivity, R.layout.chart_mark)
                mv.setContent(chooseTime, dateType, dataType)
                mv.setCallBack(RecycleItemClickListener { x ->
                    mv.setMakerView(x, lineChart.legend.labels, entrys)
                })
                mv.chartView = lineChart
                lineChart.marker = mv
                lineChart.setDrawMarkers(true)
                lineChart.isHighlightPerDragEnabled = true
//                lineChart.isHighlightPerTapEnabled = true
            }
        })
        //自定义的MarkerView对象
        mv = MyMarkerView(this@ReportChartActivity, R.layout.chart_mark)
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> finish()
            tv_data_type -> presenter.showPop(rl_title, tv_data_type)
            rl_zone -> {
                isRotate = false
                val intentSelect = Intent(this@ReportChartActivity, SelectTypeActivity::class.java)
                intentSelect.putExtra("selectType", selectType)
                intentSelect.putExtra("selectIds", selectIds)
                startActivityForResult(intentSelect, 66)
            }
            tv_week -> {
                dateType = 0
                lineChart.fitScreen()
                changeDateView(tv_week, tv_month, tv_year)
            }
            tv_month -> {
                dateType = 1
                //缩放第一种方式
//                val matrix = Matrix()
//                matrix.postScale(3.5f, 1f)
//                lineChart.viewPortHandler.refresh(matrix, lineChart, false)
                changeDateView(tv_month, tv_week, tv_year)
                chooseTime = "本月"
                presenter.getChartData(dataType, chooseTime, selectType, selectIds, dateType)
            }
            tv_year -> {
                dateType = 2
                changeDateView(tv_year, tv_month, tv_week)
                chooseTime = "${DateUtil.getYear()}年"
                presenter.getChartData(dataType, chooseTime, selectType, selectIds, dateType)
            }
        }
    }

    private fun changeDateView(select: TextView, tv: TextView, tv1: TextView) {
        select.run {
            setTextColor(resources.getColor(R.color.white))
            setBackgroundResource(R.drawable.chart_date_bg_select)
        }
        tv.run {
            setTextColor(resources.getColor(R.color.font_home))
            setBackgroundResource(R.drawable.chart_date_bg_normal)
        }
        tv1.run {
            setTextColor(resources.getColor(R.color.font_home))
            setBackgroundResource(R.drawable.chart_date_bg_normal)
        }
        presenter.initDateType(dateType)
    }

    //设置折线图数据
    override fun setData(datas: MutableList<CommonItem<Entry>>, jsonData: String) = if (datas.isEmpty()) {
        lineChart.data = null
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
        entrys.clear()
        this.jsonData = ""
    } else {
        lineChart.setDrawMarkers(false)
        lineChart.isHighlightPerDragEnabled = false
        //判断图表中原来是否有数据
        refreshXAxis()
        entrys.clear()
        this.jsonData = jsonData
        val dataSets = mutableListOf<ILineDataSet>()
        for (commonItem in datas) {
            // 保存LineDataSet集合,多条折线图
            //LineDataSet参数1：数据源 参数2：图例名称
            entrys.add(commonItem.list)
            dataSets.add(LineDataSet(commonItem.list, commonItem.name).apply {
                color = commonItem.icon//线的颜色
                setCircleColor(commonItem.icon)
                lineWidth = 1f//设置线宽
                circleRadius = 2f//设置焦点圆心的大小
                setDrawCircleHole(false)//是否空心
//                enableDashedHighlightLine(10f, 5f, 0f)//点击后的高亮线的显示样式
                highlightLineWidth = 1.5f//设置点击交点后显示高亮线宽
                highLightColor = resources.getColor(R.color.font_cc)//设置点击交点后显示交高亮线的颜色
                setDrawHorizontalHighlightIndicator(false)//设置不画x高亮线
//                isHighlightEnabled = false // 不显示定位线
                valueTextSize = 9f//设置显示值的文字大小
                setDrawValues(false)
                setDrawFilled(false)//设置禁用范围背景填充s
//                    fillDrawable = resources.getDrawable(R.drawable.chart_color)
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
                valueFormatter = IAxisValueFormatter(function = { value, _ ->
                    presenter.getWeekXText(value)
                })
            }
            1 -> {
                lineChart.xAxis.apply {
                    valueFormatter = IAxisValueFormatter { value, _ ->
                        presenter.getMonthXText(value)
                    }
                }
                val matrix = Matrix()
                matrix.postScale(3.5f, 1f)
                lineChart.viewPortHandler.refresh(matrix, lineChart, false)
            }
            2 -> {
                lineChart.xAxis.apply {
                    labelCount = 12
                    valueFormatter = IAxisValueFormatter { value, _ ->
                        presenter.getYearXText(value)
                    }
                }
//                val matrix = Matrix()
//                matrix.postScale(1f, 1f)
//                lineChart.viewPortHandler.refresh(matrix, lineChart, true)
                lineChart.fitScreen()
                lineChart.viewPortHandler.matrixTouch.postScale(1f, 1f)
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
//        lineChart.animateXY(1500, 1500)
        lineChart.animateX(1500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isRotate = true
        if (resultCode == 66 && requestCode == 66) {
            val chooseType = data?.getSerializableExtra("chooseType") as? ChooseType
            tv_zone.text = chooseType?.content
            selectIds = chooseType?.ids ?: ""
            presenter.getChartData(dataType, chooseTime, selectType, selectIds, dateType)
        } else if (resultCode == 99 && requestCode == 99) {
            Handler().postDelayed({
            }, 1000)
        }
    }

    override fun onDestroy() {
        mOrientationListener.disable()
        presenter.rxDeAttach()
        entrys.clear()
        super.onDestroy()
    }
}
