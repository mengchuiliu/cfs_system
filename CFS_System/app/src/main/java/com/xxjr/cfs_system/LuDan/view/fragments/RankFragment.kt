package com.xxjr.cfs_system.LuDan.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RankAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.presenter.RankPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.RankDetailsActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.RankVInter
import com.xxjr.cfs_system.ViewsHolder.Popup7
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import kotlinx.android.synthetic.main.fragment_rank.*
import kotlinx.android.synthetic.main.item_rank_choose.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.*

/**
 * Created by Administrator on 2017/9/26.
 * 排名
 */
class RankFragment : BaseFragment(), RankVInter, View.OnClickListener {
    private var type = 0
    private var companyType = ""//门店类型
    private var companyZone_id = ""//区域类型
    private var timeType = 4 // 4->月 5->年
    private var rankType = 1 // 1->回款 2->签单
    private var typePos = 3//年月位置
    private var companyID: String? = null
    private var curMonth = DateUtil.getMonth()
    private var curYear = DateUtil.getYear()
    private var presenter = RankPresenter(this)

    override fun getType(): Int = type

    override fun getTimeType(): Int = timeType

    override fun getRankType(): Int = rankType

    override fun getCompanyId(): String = companyID ?: ""

    override fun getCompanyType(): String = companyType

    override fun getFrgContext(): Context = context

    override fun initView(inflater: LayoutInflater, savedInstanceState: Bundle?, arguments: Bundle): View {
        type = arguments.getInt("position")
        companyID = Hawk.get("CompanyID", "")
        return inflater.inflate(R.layout.fragment_rank, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun initData(companyT: String, CompanyZone_id: String) {
        firstUp = false
        companyType = companyT
        companyZone_id = CompanyZone_id
        ll_year.visibility = View.GONE
        rank_choose.visibility = View.GONE
        tv_type.visibility = View.VISIBLE
        line_type.visibility = View.VISIBLE
        setMonth()
        tv_month_1.setOnClickListener(this)
        tv_month_2.setOnClickListener(this)
        tv_month_3.setOnClickListener(this)
        tv_store_name.setOnClickListener(this)
        tv_type.setOnClickListener(this)
        when (type) {
            2 -> {
                if (rankType == 1) tv_store_name.text = "按签单量排名" else tv_store_name.text = "按回款量排名"
                iv_store.setImageResource(R.mipmap.qiehuan)
            }
            3 -> {
                if (rankType == 1) tv_store_name.text = "按放款笔数排名" else tv_store_name.text = "按放款金额排名"
                iv_store.setImageResource(R.mipmap.qiehuan)
            }
            else -> {
                tv_store_name.text = presenter.getStoreName(companyID ?: "")
            }
        }
        presenter.getData(curMonth, curYear, companyID ?: "", typePos, companyType, companyZone_id)
    }

    override fun showMsg(msg: String?) {
        ToastShow.showShort(activity.applicationContext, msg)
    }

    override fun initRecycler(mutableList: MutableList<CommonItem<*>>, doubles: MutableList<Double>, maxN: Int) {
        recycle_rank.layoutManager = LinearLayoutManager(context)
        rank_choose.visibility = View.GONE
        val adapter = RankAdapter(context, mutableList, doubles, maxN)
        adapter.setOnItemClick(RecycleItemClickListener { position ->
            typeClick(position)
        })

        adapter.setOnItemStoreClick(RecycleItemClickListener { position ->
            when (position) {
                -1 -> showChooseType(context, (recycle_rank.layoutManager as LinearLayoutManager).findViewByPosition(1),
                        presenter.getTypeDataList(companyType, companyZone_id))
                1, 2 -> showRankPro(position)
            }
        })

        adapter.setOnItemRankClick(RecycleItemClickListener { position ->
            val intent = Intent(activity, RankDetailsActivity::class.java)
            intent.putExtra("rankItem", mutableList[position])
            intent.putExtra("rankNum", position)
            intent.putExtra("rankType", rankType)
            intent.putExtra("Type", type)
            startActivity(intent)
        })

        recycle_rank.adapter = adapter
        recycle_rank.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val pos = (recycle_rank.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (pos <= 1) {
                    rank_choose.visibility = View.GONE
                } else {
                    rank_choose.visibility = View.VISIBLE
                    if (type == 6) {
                        tv_store_name.visibility = View.INVISIBLE
                        iv_store.visibility = View.INVISIBLE
                    } else {
                        tv_store_name.visibility = View.VISIBLE
                        iv_store.visibility = View.VISIBLE
                    }
                }
            }
        })
        adapter.notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_store_name -> {
                when (type) {
                    2, 3 -> showRankPro(rankType)
                    else -> showChooseType(context, rank_choose, presenter.getTypeDataList(companyType, companyZone_id))
                }
            }
            tv_month_1 -> typeClick(1)
            tv_month_2 -> typeClick(2)
            tv_month_3 -> typeClick(3)
            tv_type -> typeClick(0)
        }
    }

    //年月点击数据切换
    private fun typeClick(position: Int) {
        typePos = position
        when (position) {
            0 -> {
                curYear = DateUtil.getYear()
                when (timeType) {
                    4 -> {
                        timeType = 5
                        curMonth = 0
                    }
                    5 -> {
                        timeType = 4
                        curMonth = DateUtil.getMonth()
                        presenter.setMonthDate(DateUtil.getChooseDate(Date()))
                    }
                }
                setMonth()
                typePos = 3
                showMonth(tv_month_3, tv_month_2, tv_month_1, typePos)
            }
            1 -> {
                if (timeType == 4) {
                    when (DateUtil.getMonth() - 2) {
                        0 -> {
                            curMonth = 12
                            curYear = DateUtil.getYear() - 1
                        }
                        -1 -> {
                            curMonth = 11
                            curYear = DateUtil.getYear() - 1
                        }
                        else -> {
                            curMonth = DateUtil.getMonth() - 2
                        }
                    }
                } else curYear = DateUtil.getYear() - 2
                showMonth(tv_month_1, tv_month_2, tv_month_3, position)
            }
            2 -> {
                if (timeType == 4) {
                    when (DateUtil.getMonth() - 1) {
                        0 -> {
                            curMonth = 12
                            curYear = DateUtil.getYear() - 1
                        }
                        else -> curMonth = DateUtil.getMonth() - 1
                    }
                } else curYear = DateUtil.getYear() - 1
                showMonth(tv_month_2, tv_month_1, tv_month_3, position)
            }
            3 -> {
                if (timeType == 4) {
                    curMonth = DateUtil.getMonth()
                    curYear = DateUtil.getYear()
                } else curYear = DateUtil.getYear()
                showMonth(tv_month_3, tv_month_2, tv_month_1, position)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setMonth() {
        if (timeType == 4) {
            tv_type.text = "年"
            tv_month_3.text = "${DateUtil.getMonth()}月"
            tv_month_2.text = "${when (DateUtil.getMonth() - 1) {
                0 -> 12
                else -> DateUtil.getMonth() - 1
            }}月"
            tv_month_1.text = "${when (DateUtil.getMonth() - 2) {
                0 -> 12
                -1 -> 11
                else -> DateUtil.getMonth() - 2
            }}月"
        } else {
            tv_type.text = "月"
            tv_month_3.text = "${DateUtil.getYear()}"
            tv_month_2.text = "${DateUtil.getYear() - 1}"
            tv_month_1.text = "${DateUtil.getYear() - 2}"
        }
    }

    private fun showRankPro(curRankType: Int) {
        when (curRankType) {
            1 -> {
                rankType = 2
                when (type) {
                    2 -> tv_store_name.text = "按回款量排名"
                    3 -> tv_store_name.text = "按放款金额排名"
                }
            }
            2 -> {
                rankType = 1
                when (type) {
                    2 -> tv_store_name.text = "按签单量排名"
                    3 -> tv_store_name.text = "按放款笔数排名"
                }
            }
        }
        presenter.getData(curMonth, curYear, companyID
                ?: "", presenter.getClickPos(), companyType, companyZone_id)
    }

    private fun showMonth(select: TextView, tv1: TextView, tv2: TextView, pos: Int) {
        select.run {
            setBackgroundResource(R.color.font_home)
            setTextColor(resources.getColor(R.color.white))
        }
        tv1.run {
            setBackgroundResource(R.color.transparent)
            setTextColor(resources.getColor(R.color.font_c3))
        }
        tv2.run {
            setBackgroundResource(R.color.transparent)
            setTextColor(resources.getColor(R.color.font_c3))
        }
        presenter.getData(curMonth, curYear, companyID ?: "", pos, companyType, companyZone_id)
    }

    private fun showChooseType(context: Context, parent: View, stringList: List<ChooseType>) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_store, null)
        val popWindow = Popup7(view, Utils.dip2px(activity, 180f), ViewGroup.LayoutParams.WRAP_CONTENT)
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
                    presenter.getData(curMonth, curYear, companyID
                            ?: "", presenter.getClickPos(), companyType, companyZone_id)
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
}