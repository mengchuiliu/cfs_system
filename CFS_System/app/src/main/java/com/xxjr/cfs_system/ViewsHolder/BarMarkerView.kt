package com.xxjr.cfs_system.ViewsHolder

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.BarEntry
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
class BarMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private var myContext: Context = context
    private var makerClickListener: MakerClickListener? = null

    private val pl: PopLayout = findViewById(R.id.pl_img)
    private val tvDate: TextView = findViewById(R.id.tv_date)

    private val ll1: LinearLayout = findViewById(R.id.ll_1)
    private val ll2: LinearLayout = findViewById(R.id.ll_2)

    private val iv: ImageView = findViewById(R.id.iv)
    private val iv1: ImageView = findViewById(R.id.iv1)
    private val iv2: ImageView = findViewById(R.id.iv2)

    private val tvContent: TextView = findViewById(R.id.tvContent)
    private val tvContent1: TextView = findViewById(R.id.tvContent1)
    private val tvContent2: TextView = findViewById(R.id.tvContent2)

    init {
        ll1.visibility = View.VISIBLE
        ll2.visibility = View.VISIBLE
        iv.setImageResource(R.mipmap.bar_1)
        iv1.setImageResource(R.mipmap.bar_2)
        iv2.setImageResource(R.mipmap.bar_3)
    }

    fun setCallBack(itemClickListener: MakerClickListener) {
        this.makerClickListener = itemClickListener
    }

    override fun refreshContent(e: Entry, highlight: Highlight) {
        makerClickListener?.onItemClick(e.x)
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF = MPPointF(0f, -(height / 2).toFloat())
//    override fun getOffset(): MPPointF = MPPointF((-width / 2).toFloat(), -height.toFloat())//中心位置

    fun setText(text: String) {
        tvDate.text = text
    }

    fun setMakerContent(contrastType: Int, x: Int, labels: Array<String>, entrys: MutableList<MutableList<BarEntry>>) {
        if (labels.isNotEmpty() && entrys.isNotEmpty()) {
            for (i in labels.indices) {
                var value = 0f
                for (entry in entrys[i]) {
                    if (entry.x.toInt() == x) {
                        value = entry.y
                        break
                    }
                }
                val myValue = if (contrastType == 1 || contrastType == 3) "${value.toInt()}笔" else "${value}万"
                val content = "${labels[i]}：$myValue"
                when (i) {
                    0 -> tvContent.text = content
                    1 -> tvContent1.text = content
                    2 -> tvContent2.text = content
                }
            }
        }
    }

    interface MakerClickListener {
        fun onItemClick(x: Float)
    }
}