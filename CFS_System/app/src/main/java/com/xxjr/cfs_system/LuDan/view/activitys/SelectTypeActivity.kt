package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.SelectTypePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.ChooseType
import kotlinx.android.synthetic.main.activity_select_type.*
import refresh_recyclerview.DividerItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class SelectTypeActivity : BaseActivity<SelectTypePresenter, SelectTypeActivity>(), BaseViewInter {
    private var list: MutableList<ChooseType> = mutableListOf()
    private var adapter: CommonAdapter<ChooseType>? = null

    override fun getPresenter(): SelectTypePresenter = SelectTypePresenter()

    override fun getLayoutId(): Int = R.layout.activity_select_type

    fun getListData(): MutableList<ChooseType> = list

    fun getType(): Int = intent.getIntExtra("selectType", 0)

    fun getIds(): String = intent.getStringExtra("selectIds") ?: ""

    override fun initView(savedInstanceState: Bundle?) {
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        confirmSelect()
        presenter.setDefaultValue()
    }

    private fun confirmSelect() {
        subTitle.text = "确认"
        subTitle.setOnClickListener {
            val customerIds = StringBuilder()
            val customerNames = StringBuilder()
            for (choose in list) {
                if (choose.isChoose) {
                    customerIds.append(choose.ids).append(",")
                    customerNames.append(choose.content).append("，")
                }
            }
            var ids = ""
            var names = ""
            if (customerIds.length > 1 && customerNames.length > 1) {
                ids = customerIds.substring(0, customerIds.length - 1)
                names = customerNames.substring(0, customerNames.length - 1)
            }
            val intent = Intent()
            intent.putExtra("chooseType", ChooseType(ids, names))
            setResult(66, intent)
            this@SelectTypeActivity.finish()
        }
    }

    fun setTitle(title: String) {
        toolbarTitle.text = title
    }

    fun initRV() {
        val layoutID: Int
        when (getType()) {
            0 -> {
                layoutID = R.layout.item_label
                rv_type.layoutManager = GridLayoutManager(this@SelectTypeActivity, 4)
            }
            1 -> {
                layoutID = R.layout.item_label
                rv_type.layoutManager = GridLayoutManager(this@SelectTypeActivity, 2)
            }
            else -> {
                layoutID = R.layout.item_recycler_customer
                rv_type.layoutManager = LinearLayoutManager(this@SelectTypeActivity)
                rv_type.addItemDecoration(DividerItemDecoration(this@SelectTypeActivity, DividerItemDecoration.VERTICAL_LIST))
            }
        }
        adapter = object : CommonAdapter<ChooseType>(this@SelectTypeActivity, list, layoutID) {
            override fun convert(holder: BaseViewHolder, chooseType: ChooseType, position: Int) {
                holder.setText(R.id.tv_customer_choose, chooseType.content.trim { it <= ' ' })
                when (getType()) {
                    0, 1 -> holder.getView<TextView>(R.id.tv_customer_choose).isSelected = chooseType.isChoose
                    else -> {
                        if (chooseType.isChoose) holder.setVisible(R.id.iv_ok, true)
                        else holder.setVisible(R.id.iv_ok, false)
                    }
                }
                holder.convertView.setOnClickListener {
                    if (chooseType.isChoose) {
                        list[position].isChoose = false
                        chooseType.isChoose = false
                        adapter?.notifyItemChanged(position, chooseType)
                    } else {
                        if (canChoose()) {
                            list[position].isChoose = true
                            chooseType.isChoose = true
                            adapter?.notifyItemChanged(position, chooseType)
                        } else {
                            showMsg("最多选5项")
                        }
                    }
                }
            }
        }
        rv_type.adapter = adapter
    }

    private fun canChoose(): Boolean {
        var can = true
        var chooseCount = 0
        for (choose in list) {
            if (choose.isChoose) {
                chooseCount++
                if (chooseCount >= 5) {
                    can = false
                    break
                }
            }
        }
        return can
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)
}
