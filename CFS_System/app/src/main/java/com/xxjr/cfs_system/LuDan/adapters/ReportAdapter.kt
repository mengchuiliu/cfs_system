package com.xxjr.cfs_system.LuDan.adapters

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.xxjr.cfs_system.LuDan.view.fragments.*

class ReportAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var fragments: ArrayList<BaseFragment>? = null
    private var list: MutableList<String>? = null

    constructor(fm: FragmentManager, list: MutableList<String>, flag: Int) : this(fm) {
        this.list = list
        when (flag) {
            0 -> this.fragments = getOverFragments(list.size)//贷款结案
            1 -> this.fragments = getReportFragments(list.size)//报表
            3 -> this.fragments = getBorrowFragments(list.size)//拆借列表
        }
    }

    fun setList(titles: MutableList<String>) {
        this.list = titles
    }

    override fun getItem(position: Int): BaseFragment? = fragments?.get(position)

    override fun getCount(): Int = list?.size ?: 0

    override fun getPageTitle(position: Int): CharSequence? = list?.get(position)

    /**
     * @param num 页面数
     * @return  报表页面集合
     */
    private fun getReportFragments(num: Int): ArrayList<BaseFragment> {
        val arrayList = ArrayList<BaseFragment>()
        var fragment: BaseFragment
        for (i in 0 until num) {
            val bundle = Bundle()
            bundle.putInt("position", i)
            fragment = when (i) {
                0 -> QuantityFragment()
                1 -> QuantityFragment()
                2 -> RankFragment()
                3 -> RankFragment()
                4 -> RankFragment()
                5 -> RankFragment()
                6 -> RankFragment()
                7 -> VisitFragment()
                else -> QuantityFragment()
            }
            fragment.arguments = bundle
            arrayList.add(fragment)
        }
        return arrayList
    }

    /**
     * @param num 页面数
     * @return 贷款结案页面集合
     */
    private fun getOverFragments(num: Int): ArrayList<BaseFragment> {
        val arrayList = ArrayList<BaseFragment>()
        var fragment: BaseFragment
        for (i in 0 until num) {
            val bundle = Bundle()
            bundle.putInt("position", i)
            fragment = LoanOverFragment()
            fragment.arguments = bundle
            arrayList.add(fragment)
        }
        return arrayList
    }

    /**
     * @param num 页面数
     * @return 拆借页面集合
     */
    private fun getBorrowFragments(num: Int): ArrayList<BaseFragment> {
        val arrayList = ArrayList<BaseFragment>()
        var fragment: BaseFragment
        for (i in 0 until num) {
            val bundle = Bundle()
            bundle.putInt("position", i)
            fragment = BorrowListFragment()
            fragment.arguments = bundle
            arrayList.add(fragment)
        }
        return arrayList
    }
}