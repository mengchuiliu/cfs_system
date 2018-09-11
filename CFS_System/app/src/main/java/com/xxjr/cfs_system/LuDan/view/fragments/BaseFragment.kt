package com.xxjr.cfs_system.LuDan.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xiaoxiao.ludan.R

open class BaseFragment : Fragment() {
    internal var view: View? = null
    open var firstUp = true

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val arguments = arguments
        if (view == null) {
            view = initView(inflater!!, savedInstanceState, arguments)
        }
        return view
    }

    /**
     * 加载布局
     *
     * @param inflater           初始化对象
     * @param savedInstanceState 状态
     * @param arguments          传递的参数
     * @return fragment界面
     */
    protected open fun initView(inflater: LayoutInflater, savedInstanceState: Bundle?, arguments: Bundle): View =
            inflater.inflate(R.layout.fragment_base, null)

    open fun changePageData(searchType: Int, query1: String, query2: String, query3: String) {}

    open fun setIsFirst(isFirst: Boolean) {}

    open fun getIsFirst(): Boolean = firstUp

//    open fun getETName(): String = ""
}