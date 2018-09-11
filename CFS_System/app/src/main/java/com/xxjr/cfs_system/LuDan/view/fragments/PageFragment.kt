package com.xxjr.cfs_system.LuDan.view.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.PageMenuPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.*
import com.xxjr.cfs_system.LuDan.view.activitys.forget_update_psw.ForgetPswActivity
import com.xxjr.cfs_system.LuDan.view.activitys.gesture_lock.LockSetActivity
import com.xxjr.cfs_system.LuDan.view.activitys.visit_record.QRCodeActivity
import com.xxjr.cfs_system.LuDan.view.activitys.withholding_agreement.AgreementActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.PageMenuVInter
import com.xxjr.cfs_system.main.LoginActivity
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.ToastShow
import entity.SetMenu
import kotlinx.android.synthetic.main.fragment_page.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

/**
 * Created by Administrator on 2017/11/7.
 */
class PageFragment : Fragment(), PageMenuVInter {
    private var presenter = PageMenuPresenter(this)
    private var permissions = ""

    private lateinit var adapter: CommonAdapter<SetMenu>

    override fun getPageContext(): Context = context

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_page, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        iv_portrait.setOnClickListener {
            startActivity(Intent(context, PersonalInfoActivity::class.java))
        }
        presenter.initView(permissions)
    }

    fun initView(typeName: String, company: String, realName: String) {
        tv_name.text = if (realName.isNotBlank()) realName else Hawk.get("UserRealName", "")
        tv_manager.text = if (typeName.isNotBlank()) typeName else Hawk.get("UserTypeName", "")
        tv_store_name.text = if (company.isNotBlank()) company else Hawk.get("CompanyName", "")
    }

    fun setFrgPortrait(bitmap: Bitmap) {
        iv_portrait.setImageBitmap(bitmap)
    }

    fun setPermissions(permissions: String) {
        this.permissions = permissions
    }

    fun setVersion(version: String) {
        tv_version.text = "版本 ${version}"
    }

    override fun setMortgageScore(score: String) = tv_mortgage_score.let {
        it.visibility = View.VISIBLE
        it.text = "服务总分：$score"
    }

    override fun initRecycler(menus: MutableList<SetMenu>) {
        rv_menu.layoutManager = LinearLayoutManager(context)
        adapter = object : CommonAdapter<SetMenu>(context, menus, R.layout.item_recycle_set) {
            override fun convert(holder: BaseViewHolder, menu: SetMenu, position: Int) {
                val param = holder.convertView.layoutParams
                if (menu.isShow) {
                    holder.convertView.visibility = View.VISIBLE
                    param.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    param.width = ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    holder.convertView.visibility = View.GONE
                    param.height = 0
                    param.width = 0
                }
                holder.convertView.layoutParams = param
                holder.setText(R.id.tv_set_content, menu.contentName)
                holder.setImageResource(R.id.iv_set_icon, menu.icon)
                holder.convertView.setOnClickListener {
                    menuClick(menu.type)
                }
            }
        }
        rv_menu.adapter = adapter
    }

    override fun loginOut() {
        Hawk.put("Psw", "")
        (activity.applicationContext as MyApplication).goldRegister = null
        (activity.applicationContext as MyApplication).MsgType = -1
        (activity.applicationContext as MyApplication).exit()
        startActivity(Intent(context, LoginActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        presenter.getMortgageScore()
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(activity.applicationContext, msg)

    //各项菜单点击事件
    private fun menuClick(type: Int) {
//        (activity as HomePageActivity).hideLeft()
        (activity as HomeMenuActivity).hideLeft()
        when (type) {
            0 //来访登记二维码
            -> startActivity(Intent(context, QRCodeActivity::class.java))
            1 -> startActivity(Intent(context, PersonalInfoActivity::class.java))
            2//添加银行卡
            -> startActivity(Intent(context, BindBankActivity::class.java))
            3//更新缓存
            -> presenter.updateCacheData()
            4 //消息设置
            -> startActivity(Intent(context, MessageSetActivity::class.java))
            5//手势密码
            -> startActivity(Intent(context, LockSetActivity::class.java))
            6//修改密码
//            -> startActivity(Intent(context, UpdatePswActivity::class.java))
            -> startActivity(Intent(context, ForgetPswActivity::class.java))
            7//检测更新
            -> presenter.checkAppUpdate()
            8//退出登录
            -> presenter.loginOut()
        }
    }
}