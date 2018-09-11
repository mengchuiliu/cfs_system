package com.xxjr.cfs_system.ViewsHolder

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import me.kareluo.ui.PopLayout
import java.util.*

/**
 * Created by Administrator on 2018/1/31.
 */
class MyMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private var myContext: Context = context
    private var content = ""
    private var dateType = 1
    private var dataType = 1
    private var itemClickListener: RecycleItemClickListener? = null

    private val pl: PopLayout = findViewById(R.id.pl_img)
    private val tvDate: TextView = findViewById(R.id.tv_date)
    private val ll: LinearLayout = findViewById(R.id.ll)
    private val ll1: LinearLayout = findViewById(R.id.ll_1)
    private val ll2: LinearLayout = findViewById(R.id.ll_2)
    private val ll3: LinearLayout = findViewById(R.id.ll_3)
    private val ll4: LinearLayout = findViewById(R.id.ll_4)
    private val tvContent: TextView = findViewById(R.id.tvContent)
    private val tvContent1: TextView = findViewById(R.id.tvContent1)
    private val tvContent2: TextView = findViewById(R.id.tvContent2)
    private val tvContent3: TextView = findViewById(R.id.tvContent3)
    private val tvContent4: TextView = findViewById(R.id.tvContent4)

    fun setContent(content: String, dateType: Int, dataType: Int) {
        this.content = content
        this.dateType = dateType
        this.dataType = dataType
    }

    fun setCallBack(itemClickListener: RecycleItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun refreshContent(e: Entry, highlight: Highlight) {
        itemClickListener?.onItemClick(e.x.toInt())
//        tvContent.text = "${if (dateType == 1 && content != "本月") Utils.FormatTime(content, "yyyy-M月", "yyyy年M月") else content}\n总数：${e.y.toInt()}"
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF = MPPointF(0f, (-height / 2).toFloat())
//    override fun getOffset(): MPPointF = MPPointF((-width / 2).toFloat(), -height.toFloat())//中心位置

    @SuppressLint("SetTextI18n")
    fun setMakerView(x: Int, labels: Array<String>, entrys: MutableList<MutableList<Entry>>) {
        if (labels.isNotEmpty() && entrys.isNotEmpty()) {
            if (dateType == 1) {
                if (content == "本月") {
                    tvDate.text = "${Utils.FormatTime(DateUtil.formatChooseDate(Date()), "yyyy-MM", "yyyy年M月")}${x + 1}日"
                } else {
                    tvDate.text = "${Utils.FormatTime(content, "yyyy-M月", "yyyy年M月")}${x + 1}日"
                }
            } else if (dateType == 2) {
                tvDate.text = "$content${x + 1}月"
            }
            when (labels.size) {
                1 -> {
                    pl.offset = Utils.dip2px(myContext, 25f)
                    ll.visibility = View.VISIBLE
                    ll1.visibility = View.GONE
                    ll2.visibility = View.GONE
                    ll3.visibility = View.GONE
                    ll4.visibility = View.GONE
                }
                2 -> {
                    pl.offset = Utils.dip2px(myContext, 35f)
                    ll.visibility = View.VISIBLE
                    ll1.visibility = View.VISIBLE
                    ll2.visibility = View.GONE
                    ll3.visibility = View.GONE
                    ll4.visibility = View.GONE
                }
                3 -> {
                    pl.offset = Utils.dip2px(myContext, 43f)
                    ll.visibility = View.VISIBLE
                    ll1.visibility = View.VISIBLE
                    ll2.visibility = View.VISIBLE
                    ll3.visibility = View.GONE
                    ll4.visibility = View.GONE
                }
                4 -> {
                    pl.offset = Utils.dip2px(myContext, 51f)
                    ll.visibility = View.VISIBLE
                    ll1.visibility = View.VISIBLE
                    ll2.visibility = View.VISIBLE
                    ll3.visibility = View.VISIBLE
                    ll4.visibility = View.GONE
                }
                5 -> {
                    pl.offset = Utils.dip2px(myContext, 59f)
                    ll.visibility = View.VISIBLE
                    ll1.visibility = View.VISIBLE
                    ll2.visibility = View.VISIBLE
                    ll3.visibility = View.VISIBLE
                    ll4.visibility = View.VISIBLE
                }
            }
            for (i in labels.indices) {
                var value = 0f
                for (entry in entrys[i]) {
                    if (entry.x.toInt() == x) {
                        value = entry.y
                        break
                    }
                }
                val myValue = if (dataType == 0) value.toInt().toString() else value.toString()
                val content = "${labels[i]}：$myValue"
                when (i) {
                    0 -> tvContent.text = content
                    1 -> tvContent1.text = content
                    2 -> tvContent2.text = content
                    3 -> tvContent3.text = content
                    4 -> tvContent4.text = content
                }
            }
        } else {
            pl.visibility = View.GONE
            ll.visibility = View.GONE
            ll1.visibility = View.GONE
            ll2.visibility = View.GONE
            ll3.visibility = View.GONE
            ll4.visibility = View.GONE
        }
    }
}