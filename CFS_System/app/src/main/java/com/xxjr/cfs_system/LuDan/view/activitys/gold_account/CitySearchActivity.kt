package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextWatcher
import android.view.View
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.presenter.CitySearchPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.ChooseType
import entity.CityInfo
import kotlinx.android.synthetic.main.activity_search.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class CitySearchActivity : BaseActivity<CitySearchPresenter, BaseViewInter>(), BaseViewInter {
    private var type = 1 //1->省级 2->市县级
    private var adapter: CommonAdapter<CityInfo>? = null
    private var adapterCity: CommonAdapter<CityInfo.ChildrenBean>? = null
    private var temp = mutableListOf<CityInfo.ChildrenBean>()
    private var province = ""
    private var Codes = ""

    override fun getPresenter(): CitySearchPresenter = CitySearchPresenter()

    override fun getLayoutId(): Int = R.layout.activity_search

    override fun initView(savedInstanceState: Bundle?) {
        iv_back.setOnClickListener { finish() }
        et_search.setCompoundDrawables(null, null, null, null)
        et_search.isEnabled = false
        tv_ok.visibility = View.VISIBLE
        tv_ok.setOnClickListener {
            val chooseType = ChooseType(Codes, et_search.text.toString())
            RxBus.getInstance().post(33, chooseType)
            this@CitySearchActivity.finish()
        }
        presenter.setDefaultValue()
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    fun getType(): Int = type

    fun setEditChangeListener(textWatcher: TextWatcher) {
        et_search.addTextChangedListener(textWatcher)
    }

    fun initRecycler(cityInfos: MutableList<CityInfo>) {
        recycle_search.layoutManager = LinearLayoutManager(this@CitySearchActivity)
        adapter = object : CommonAdapter<CityInfo>(this@CitySearchActivity, cityInfos, R.layout.item_search) {
            override fun convert(holder: BaseViewHolder, cityInfo: CityInfo, position: Int) {
                holder.setText(R.id.tv_search, cityInfo.Names)
                holder.setOnClickListener(R.id.tv_search) {
                    temp.clear()
                    type = 2
                    province = cityInfo.Names ?: ""
                    et_search.setText(province)
                    recycle_search.visibility = View.GONE
                    recycle_city.visibility = View.VISIBLE
                    val city = CityInfo.ChildrenBean()
                    city.Names = "返回上一级"
                    temp.add(city)
                    temp.addAll(cityInfo.Children ?: mutableListOf())
//                    presenter.addCityData(cityInfo.Children ?: mutableListOf())
                    refreshCityData(temp)
                }
            }
        }
        recycle_search.adapter = adapter

        recycle_city.layoutManager = LinearLayoutManager(this@CitySearchActivity)
        adapterCity = object : CommonAdapter<CityInfo.ChildrenBean>(this@CitySearchActivity, ArrayList(), R.layout.item_search) {
            override fun convert(holder: BaseViewHolder, cityInfo: CityInfo.ChildrenBean, position: Int) {
                holder.setText(R.id.tv_search, cityInfo.Names)
                holder.setOnClickListener(R.id.tv_search) {
                    when (position) {
                        0 -> {
                            type = 1
                            et_search.setText("")
                            recycle_search.visibility = View.VISIBLE
                            recycle_city.visibility = View.GONE
                        }
                        else -> {
                            et_search.setText(province + "—" + cityInfo.Names)
                            Codes = cityInfo.Codes ?: ""
                        }
                    }
                }
            }
        }
        recycle_city.adapter = adapterCity
    }

    fun refreshData(listData: MutableList<CityInfo>) {
        adapter?.setNewData(listData)
    }

    fun refreshCityData(listData: MutableList<CityInfo.ChildrenBean>?) {
        adapterCity?.setNewData(listData)
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
