package com.xxjr.cfs_system.LuDan.view.activitys.report

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.ReportAdapter

import com.xxjr.cfs_system.LuDan.presenter.ReportPresenter
import com.xxjr.cfs_system.LuDan.view.fragments.QuantityFragment
import com.xxjr.cfs_system.LuDan.view.fragments.RankFragment
import com.xxjr.cfs_system.LuDan.view.fragments.VisitFragment
import com.xxjr.cfs_system.LuDan.view.viewinter.ReportVInter
import com.xxjr.cfs_system.ViewsHolder.BarMarkerView
import com.xxjr.cfs_system.ViewsHolder.Popup7
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.item_rank_choose.*
import listener.OnTabSelectListener
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class ReportActivity : BaseActivity<ReportPresenter, ReportActivity>(), ReportVInter {
    private lateinit var mv: BarMarkerView
    var adapter: ReportAdapter? = null
    private var titleAdapter: CommonAdapter<CommonItem<*>>? = null
    private var contrastAdapter: CommonAdapter<CommonItem<*>>? = null
    private var companyType: Int = 0 //分类标题  1->同期对比
    private var contrastType: Int = 0 //0->回款量 1->签单量 2->放款金额 3->放款笔数
    private var companyID = Hawk.get<String>("CompanyID")
    private var entrys: MutableList<MutableList<BarEntry>> = mutableListOf()
    private var jsonData: String = ""

    private lateinit var mOrientationListener: OrientationEventListener // 屏幕方向改变监听器
    private var isRotate = true

    override fun getPresenter(): ReportPresenter = ReportPresenter()

    override fun getLayoutId(): Int = R.layout.activity_report

    override fun initView(savedInstanceState: Bundle?) {
        iv_back.setOnClickListener { finish() }
        initOrientationListener()
        initTitle()
        initChart()
        presenter.setDefaultValue()
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    //屏幕旋转角度监听
    private fun initOrientationListener() {
        mOrientationListener = object : OrientationEventListener(this@ReportActivity) {
            override fun onOrientationChanged(rotation: Int) {
                if (((rotation > 45) && (rotation < 135)) || ((rotation > 225) && (rotation < 315))) {
                    if (companyType == 1 && isRotate) {
                        isRotate = false
                        val intent = Intent(this@ReportActivity, ReportShowActivity::class.java)
                        intent.putExtra("jsonData", jsonData)
                        intent.putExtra("contrastType", contrastType)
                        startActivityForResult(intent, 99)
                    }
                }
            }
        }

        tv_any.setOnClickListener {
            isRotate = false
            val intent = Intent(this@ReportActivity, ReportShowActivity::class.java)
            intent.putExtra("jsonData", jsonData)
            intent.putExtra("contrastType", contrastType)
            startActivityForResult(intent, 99)
        }
    }

    //初始化柱状图
    private fun initChart() {
        rv_contrast_title.visibility = View.GONE
        ll_show_chart.visibility = View.GONE
        rl_date.visibility = View.GONE
        tv_store_name.text = presenter.getStoreName(companyID)
        tv_store_name.setOnClickListener {
            showChooseType(this@ReportActivity, tv_store_name, presenter.getTypeDataList())
        }

        barChart.run {
            description = Description().apply { isEnabled = false }
            setFitBars(true)//在bar开头结尾两边添加一般bar宽的留白
            setNoDataText("暂无数据")
            setNoDataTextColor(Color.parseColor("#54b1fd"))//没有数据时显示文字的颜色
            setDrawGridBackground(false)//chart 绘图区后面的背景矩形将绘制
            isDragEnabled = true// 是否可以拖拽
            setScaleEnabled(false)// 是否可以缩放 x和y轴, 默认是true
            val matrix = Matrix()
            matrix.postScale(4.2f, 1f)
            viewPortHandler.refresh(matrix, barChart, false)
            isScaleXEnabled = false //是否可以缩放 仅x轴
            isScaleYEnabled = false //是否可以缩放 仅y轴
            setPinchZoom(false)//设置x轴和y轴能否同时缩放。默认是否
            isDoubleTapToZoomEnabled = false//设置是否可以通过双击屏幕放大图表。默认是true
            isHighlightPerDragEnabled = false//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
            isDragDecelerationEnabled = true//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
            extraBottomOffset = 10f
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
//            valueFormatter = IndexAxisValueFormatter(mutableListOf<String>().apply {
//                for (i in 1..31) {
//                    add("${i}日")
//                }
//            })
            valueFormatter = IAxisValueFormatter { value, axis ->
                "${value.toInt() + 1}日"
            }

            valueFormatter.getFormattedValue(0f, this)
            axisLineWidth = 1.5f
            textColor = R.color.font_c9
            textSize = 13f
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
                mv = BarMarkerView(this@ReportActivity, R.layout.chart_mark)
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
        mv = BarMarkerView(this@ReportActivity, R.layout.chart_mark)
    }

    private fun initTitle() {
        rv_company_title.layoutManager = LinearLayoutManager(this@ReportActivity, LinearLayoutManager.HORIZONTAL, false)
        titleAdapter = object : CommonAdapter<CommonItem<*>>(this@ReportActivity, presenter.getCompanyTitles(), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 14f)
                holder.setBackgroundRes(R.id.tv_line, R.color.white)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@ReportActivity, 0f),
                        Utils.dip2px(this@ReportActivity, 5f),
                        Utils.dip2px(this@ReportActivity, 0f),
                        Utils.dip2px(this@ReportActivity, 5f))
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(Utils.dip2px(this@ReportActivity, 20f), 0, Utils.dip2px(this@ReportActivity, 5f), 0)
                holder.convertView.layoutParams = lp
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.white)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.report_title)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { refreshTitle(position) }
            }
        }
        rv_company_title.adapter = titleAdapter

        rv_contrast_title.layoutManager = LinearLayoutManager(this@ReportActivity, LinearLayoutManager.HORIZONTAL, false)
        contrastAdapter = object : CommonAdapter<CommonItem<*>>(this@ReportActivity, presenter.getContrastTitles(), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 14f)
                holder.setBackgroundRes(R.id.tv_line, R.color.white)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@ReportActivity, 0f),
                        Utils.dip2px(this@ReportActivity, 5f),
                        Utils.dip2px(this@ReportActivity, 0f),
                        Utils.dip2px(this@ReportActivity, 5f))
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(Utils.dip2px(this@ReportActivity, 20f), 0, Utils.dip2px(this@ReportActivity, 8f), 0)
                holder.convertView.layoutParams = lp
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.white)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.report_title)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { freshContrastTitle(position) }
            }
        }
        rv_contrast_title.adapter = contrastAdapter
    }

    override fun initPagerView(titles: MutableList<String>) {
        adapter = ReportAdapter(supportFragmentManager, titles, 1)
        vp_report.adapter = adapter
        vp_report.offscreenPageLimit = titles.size
        tabs.setViewPager(vp_report)
        vp_report.currentItem = 0

        tabs.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                vp_report.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })
        tabs.setOnPagerSlidingListener { position ->
            if (position == 0 || position == 1) {
                if ((adapter?.getItem(position) as QuantityFragment).firstUp) {
                    (adapter?.getItem(position) as QuantityFragment).initData(getCompanyT(companyType), getCompanyZone(companyType))
                }
            } else if (position == (titles.size - 1)) {
                if ((adapter?.getItem(position) as VisitFragment).firstUp) {
                    (adapter?.getItem(position) as VisitFragment).initData(getCompanyT(companyType), getCompanyZone(companyType))
                }
            } else {
                if ((adapter?.getItem(position) as RankFragment).firstUp) {
                    (adapter?.getItem(position) as RankFragment).initData(getCompanyT(companyType), getCompanyZone(companyType))
                }
            }
        }
    }

    private fun refreshTitle(position: Int) {
        companyType = position
        for (i in 0 until titleAdapter?.datas?.size!!) {
            (titleAdapter?.datas!![i] as CommonItem<*>).isClick = i == position
        }
        titleAdapter?.notifyDataSetChanged()

        if (companyType == 1) {
            presenter.getContrastData(contrastType, companyID)
            rv_contrast_title.visibility = View.VISIBLE
            ll_show_chart.visibility = View.VISIBLE
            tv_contrast.text = "${DateUtil.getMonth() - 2}/${DateUtil.getMonth() - 1}/${DateUtil.getMonth()}月份同期数据对比图"
            tabs.visibility = View.GONE
            vp_report.visibility = View.GONE
        } else {
            rv_contrast_title.visibility = View.GONE
            ll_show_chart.visibility = View.GONE
            tabs.visibility = View.VISIBLE
            vp_report.visibility = View.VISIBLE
            when (vp_report.currentItem) {
                0, 1 -> {
                    (adapter?.getItem(vp_report.currentItem) as QuantityFragment).initData(getCompanyT(companyType), getCompanyZone(companyType))
                }
                (adapter?.count ?: 0) - 1 -> {
                    (adapter?.getItem(vp_report.currentItem) as VisitFragment).initData(getCompanyT(companyType), getCompanyZone(companyType))
                }
                else -> {
                    (adapter?.getItem(vp_report.currentItem) as RankFragment).initData(getCompanyT(companyType), getCompanyZone(companyType))
                }
            }
        }
    }

    private fun getCompanyT(companyP: Int): String = when (companyP) {
        2 -> "4"
        3 -> "5"
        4 -> "6"
        5 -> "7"
        else -> ""
    }

    private fun getCompanyZone(companyP: Int): String = when (companyP) {
        6 -> "110000"//北京
        7 -> "310000"//上海
        8 -> "440300"//深圳
        9 -> "440100"//广州
        10 -> "other"//第五区
        else -> ""
    }

    private fun freshContrastTitle(position: Int) {
        contrastType = position
        if (contrastType == 1 || contrastType == 3) tv_unit.text = "(单位/笔)" else tv_unit.text = "(单位/万)"
        for (i in 0 until contrastAdapter?.datas?.size!!) {
            (contrastAdapter?.datas!![i] as CommonItem<*>).isClick = i == position
        }
        contrastAdapter?.notifyDataSetChanged()
        presenter.getContrastData(contrastType, companyID)
    }

    //刷新柱状图数据
    override fun refreshBarData(datas: MutableList<CommonItem<BarEntry>>, jsonData: String) = if (datas.isEmpty()) {
        tv_unit.visibility = View.GONE
        barChart.data = null
        barChart.notifyDataSetChanged()
        barChart.invalidate()
        entrys.clear()
        this.jsonData = ""
    } else {
        tv_unit.visibility = View.VISIBLE
        barChart.setDrawMarkers(false)
        entrys.clear()
        this.jsonData = jsonData
        val data = BarData()
        for (commonItem in datas) {
            entrys.add(commonItem.list)
            data.addDataSet(BarDataSet(commonItem.list, commonItem.name).apply {
                axisDependency = YAxis.AxisDependency.LEFT
                color = commonItem.icon
            })
        }
        // (barWidth + barSpace) * 3 + groupSpace = 1，即一个间隔为一组，包含两个柱图 -> interval per "group"
        val groupSpace = 0.28f
        val barSpace = 0.035f
        val barWidth = 0.205f
//        data.isHighlightEnabled = false
        data.setDrawValues(false)
        data.barWidth = barWidth
        data.groupBars(0f, groupSpace, barSpace)//(起始点、柱状图组间距、柱状图之间间距)
        barChart.data = data
        barChart.invalidate()
    }

    private fun showChooseType(context: Context, parent: View, stringList: List<ChooseType>) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_store, null)
        val popWindow = Popup7(view, Utils.dip2px(this@ReportActivity, 180f), ViewGroup.LayoutParams.WRAP_CONTENT)
        val adapter = object : CommonAdapter<ChooseType>(context, stringList, R.layout.item_store_list) {
            override fun convert(holder: BaseViewHolder, chooseType: ChooseType, position: Int) {
                holder.setText(R.id.tv_type, chooseType.content)
                holder.setBackgroundRes(R.id.tv_type, R.drawable.store_clicked)
                holder.setOnClickListener(R.id.tv_type) {
                    if (popWindow.isShowing) {
                        popWindow.dismiss()
                    }
                    tv_store_name.text = chooseType.content
                    companyID = chooseType.ids
                    presenter.getContrastData(contrastType, companyID)
                }
            }
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.recycle_store)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        popWindow.isFocusable = true
        popWindow.isOutsideTouchable = true
        popWindow.setBackgroundDrawable(BitmapDrawable())
        popWindow.showAsDropDown(parent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {
            Handler().postDelayed({
                isRotate = true
            }, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        mOrientationListener.enable()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        mOrientationListener.disable()
        super.onDestroy()
    }
}
