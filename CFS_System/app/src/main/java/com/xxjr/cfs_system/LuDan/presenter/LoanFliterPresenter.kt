package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.LoanFliterMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.FliterVInter
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import me.kareluo.ui.OptionMenu
import me.kareluo.ui.OptionMenuView
import me.kareluo.ui.PopupMenuView
import rx.Subscription
import timeselector.TimesChoose

/**
 * Created by Administrator on 2017/10/9.
 * 贷款结案执行
 */
class LoanFliterPresenter : BasePresenter<FliterVInter, LoanFliterMImp>(), OptionMenuView.OnOptionMenuClickListener {
    private var companySubscription: Subscription? = null//门店
    private lateinit var mPopupMenuView: PopupMenuView
    private var timesChoose: TimesChoose? = null
    private var titles: MutableList<String>? = null

    override fun getModel(): LoanFliterMImp = LoanFliterMImp()

    override fun setDefaultValue() {
        initPop()
        initTimeChoose()
        when (view.getContractType()) {
            5 -> getTitlesData("", -1)
        }
    }

    //初始化菜单选项
    private fun initPop() {
        mPopupMenuView = PopupMenuView(view as Context)
        mPopupMenuView.popLayout?.radiusSize = 15
        mPopupMenuView.orientation = LinearLayout.VERTICAL
        mPopupMenuView.setOnMenuClickListener(this@LoanFliterPresenter)
        mPopupMenuView.menuItems = mutableListOf<OptionMenu>().apply {
            for (i in 1..3) {
                add(OptionMenu().apply {
                    id = i
                    when (i) {
                        1 -> title = "客户名"
                        2 -> title = if (view.getContractType() == 6) "申请人" else "贷款号"
                        3 -> title = "门店名"
                    }
                })
            }
        }
    }

    private fun initTimeChoose() {
        if (isViewAttached) {
            companySubscription = RxBus.getInstance().toObservable(Constants.Company_Choose, ChooseType::class.java).subscribe { s ->
                view.setCompanyName(s.ids, s.content)
            }

            timesChoose = TimesChoose(view as Context, TimesChoose.TimeResultHandler { time, endtime ->
                view.timeClick(time, endtime)
            }, "1900-01-01", DateUtil.getCurDate())
            timesChoose?.setScrollUnit(TimesChoose.SCROLLTYPE.YEAR, TimesChoose.SCROLLTYPE.MONTH, TimesChoose.SCROLLTYPE.DAY)
        }
    }

    fun getTitles() = mutableListOf<String>().apply {
        when (view.getContractType()) {
            5 -> addAll(model.getTitles())
            6 -> addAll(model.getBorrowTitles())
        }
    }

    fun getTitlesData(query: String, pos: Int) {
        getData(0, model.getParam(getParamList(query), "GetScreenStatistic"), true)
    }

    private fun getParamList(query: String): MutableList<*> {
        val list = ArrayList<Any>()
        list.add(query)
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        when (resultCode) {
            0 -> {
                titles = model.getTitles(data?.data ?: "", -1, titles ?: ArrayList())
                view.refreshTitle(titles!!)
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.showMsg(msg)
    }

    fun showTime(parent: View) {
        if (isViewAttached) timesChoose?.show(parent)
    }

    fun popChoose(parent: View) {
        if (isViewAttached) mPopupMenuView.show(parent)
    }

    override fun onOptionMenuClick(position: Int, menu: OptionMenu): Boolean {
        when (menu.id) {
            1 -> view.customerClick()
            2 -> view.loanCodeClick()
            3 -> view.companyClick()
        }
        return true
    }

    fun rxDeAttach() {
        if (companySubscription != null && !companySubscription!!.isUnsubscribed) {
            companySubscription!!.unsubscribe()
        }
        if (timesChoose != null) {
            timesChoose = null
        }
    }
}