package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.presenter.PactDataPresenter
import com.xxjr.cfs_system.LuDan.view.post_image.FileDisplayActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.PactDataVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.PactData
import kotlinx.android.synthetic.main.activity_post.*
import refresh_recyclerview.PullToRefreshRecyclerView
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import kotlin.collections.ArrayList

class PactDataActivity : BaseActivity<PactDataPresenter, PactDataVInter>(), PactDataVInter {
    private var pactDatas = mutableListOf<PactData>()
    var adapter: CommonAdapter<PactData>? = null
    var adapterTitle: CommonAdapter<CommonItem<Any>>? = null
    private var pull = false
    private var page = 0
    private var state = 0 //0->全部 1-> 未审核  2->已审核  3->不合规
    private var isRead = 0//1->审核查看标识
    private var isAudit = false//是否是审核列表

    //E3->上传 E4->删除
    val permits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "803")
    val permitsAudit = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "812")

    override fun getPresenter(): PactDataPresenter = PactDataPresenter()

    override fun getPull(): Boolean = pull

    override fun setPull(isPull: Boolean) {
        pull = isPull
    }

    override fun getPactDatas(): MutableList<PactData> = pactDatas

    override fun getContractId(): Int = intent.getIntExtra("contractId", 0)

    override fun getLayoutId(): Int = R.layout.activity_post

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "合同资料"
        isAudit = intent.getBooleanExtra("isAudit", false)
        if (!isAudit) {
            if (permits != null && permits.contains("E3")) {
                subTitle.text = "上传"
                subTitle.setOnClickListener {
                    val intentTo = Intent(this@PactDataActivity, PostActivity::class.java)
                    intentTo.putExtra("CustomerInfo", intent.getSerializableExtra("CustomerInfo"))
                    intentTo.putExtra("contractId", getContractId())
                    intentTo.putExtra("CompanyID", intent.getStringExtra("CompanyID"))
                    startActivityForResult(intentTo, 99)
                }
            }
        } else {
            ivRight.setImageResource(R.mipmap.icon_date)
            ivRight.setOnClickListener { presenter.showTime(ivRight) }
        }
        if (permitsAudit != null && permitsAudit.contains("E8")) {
            isRead = 1
        }
        initTitle()
        initRVPact()
        presenter.setDefaultValue()
    }

    override fun onResume() {
        super.onResume()
        pull = false
    }

    fun initTitle() {
        rv_title.layoutManager = LinearLayoutManager(this@PactDataActivity, LinearLayoutManager.HORIZONTAL, false)
        adapterTitle = object : CommonAdapter<CommonItem<Any>>(this@PactDataActivity, presenter.getTitles(state), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@PactDataActivity, 15f),
                        Utils.dip2px(this@PactDataActivity, 10f),
                        Utils.dip2px(this@PactDataActivity, 15f),
                        Utils.dip2px(this@PactDataActivity, 10f))
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { refreshTitle(position) }
            }
        }
        rv_title.adapter = adapterTitle
    }

    override fun initRVPact() {
        adapter = object : CommonAdapter<PactData>(this@PactDataActivity, ArrayList(), R.layout.item_pact_data) {
            override fun convert(holder: BaseViewHolder?, pact: PactData?, position: Int) {
                if (!isAudit && permits != null && permits.contains("E4")) {
                    holder?.setVisible(R.id.pact_data_del, true)
                    holder?.setOnClickListener(R.id.pact_data_del) {
                        CustomDialog.showTwoButtonDialog(this@PactDataActivity, "确定删除此文件?",
                                "确定", "取消") { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            presenter.delPactData(pact?.ID ?: "0", position)
                        }
                    }
                } else {
                    holder?.setVisible(R.id.pact_data_del, false)
                }
                if (pact?.viewTime.isNullOrBlank()) {
                    if (pact?.isCurAudit == true) {
                        holder?.setText(R.id.tv_read, "已查看")
                        holder?.setTextColorRes(R.id.tv_read, R.color.font_name)
                    } else {
                        holder?.setText(R.id.tv_read, "点击查看")
                        holder?.setTextColorRes(R.id.tv_read, R.color.font_home)
                    }
                } else {
                    holder?.setText(R.id.tv_read, "已查看")
                    holder?.setTextColorRes(R.id.tv_read, R.color.font_name)
                }
                holder?.setText(R.id.tv_time, pact?.uploandTime)
                when (getFileType(pact?.fileName ?: "")) {
                    "jpg", "JPG", "png", "PNG", "GIF", "gif", "JPEG", "jpeg" -> {
                        holder?.setImageResource(R.id.iv_type, R.mipmap.photo)
                    }
                    else -> holder?.setImageResource(R.id.iv_type, R.mipmap.word)
                }
                holder?.getView<LinearLayout>(R.id.ll_all)?.setOnClickListener {
                    //查看
                    when (getFileType(pact?.fileName ?: "")) {
                        "jpg", "JPG", "png", "PNG", "GIF", "gif", "JPEG", "jpeg" -> {
                            showFile(pact, position, WebActivity::class.java)
                        }
                        "doc", "excel", "ppt", "txt", "pdf", "pptx", "docx", "xlsx", "xls", "wps" -> {
                            showFile(pact, position, FileDisplayActivity::class.java)
                        }
                        else -> showMsg("手机暂不支持查看此类文件，请在电脑上查看")
                    }
                }
                holder?.setText(R.id.tv_remark, if (pact?.reason.isNullOrBlank())
                    " 备  注 ：${pact?.remark ?: ""}" else "不合规原因：${pact?.reason ?: ""}")

                val itemData = holder?.getView<RecyclerView>(R.id.rv_item_data)
                itemData?.layoutManager = LinearLayoutManager(this@PactDataActivity)
                itemData?.adapter = object : CommonAdapter<CommonItem<Any>>(this@PactDataActivity, presenter.getItemData(pact!!), R.layout.item_common_show) {
                    override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, itemPosition: Int) {
                        val param = holder?.convertView?.layoutParams
                        if (item?.isEnable == true) {
                            holder?.convertView?.visibility = View.VISIBLE
                            param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            param?.width = ViewGroup.LayoutParams.MATCH_PARENT
                        } else {
                            holder?.convertView?.visibility = View.GONE
                            param?.height = 0
                            param?.width = 0
                        }
                        holder?.convertView?.layoutParams = param
                        if (item?.isLineShow == true) {
                            holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(false)
                            holder?.getView<TextView>(R.id.tv_content)?.maxLines = 3
                        } else {
                            holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(true)
                            holder?.getView<TextView>(R.id.tv_content)?.maxLines = 1
                            holder?.getView<TextView>(R.id.tv_content)?.ellipsize = TextUtils.TruncateAt.END
                        }
                        if (itemPosition == 1) {
                            holder?.setTextColorRes(R.id.tv_content, R.color.toolbar_bg)
                        } else {
                            holder?.setTextColorRes(R.id.tv_content, R.color.font_c3)
                        }
                        holder?.setText(R.id.tv_content_name, item?.name)
                        holder?.setText(R.id.tv_content, item?.content)
                        holder?.convertView?.setOnClickListener {
                            when (getFileType(pact?.fileName ?: "")) {
                                "jpg", "JPG", "png", "PNG", "GIF", "gif", "JPEG", "jpeg" -> showFile(pact, position, WebActivity::class.java)
                                "doc", "excel", "ppt", "txt", "pdf", "pptx", "docx", "xlsx", "xls", "wps" -> showFile(pact, position, FileDisplayActivity::class.java)
                                else -> showMsg("手机暂不支持查看此类文件，请在电脑上查看")
                            }
                        }
                    }
                }
            }
        }

        rv_pact_data.setAdapter(adapter)

        rv_pact_data.setOnRefreshListener(object : PullToRefreshRecyclerView.OnRefreshListener {
            override fun onPullDownRefresh() {
                Handler().postDelayed({
                    pull = false
                    page = 0
                    presenter.cleanTime()
                    presenter.getPactData(page, state, false)
                }, 1000)
            }

            override fun onLoadMore() {
                Handler().postDelayed({
                    page++
                    pull = true
                    presenter.getPactData(page, state, false)
                }, 1000)
            }
        })
    }

    private fun showFile(pact: PactData?, position: Int, clazz: Class<*>) {
        val intent2 = Intent(this@PactDataActivity, clazz)
        intent2.putExtra("isRead", isRead)
        intent2.putExtra("contractId", pact?.contractID)
        intent2.putExtra("fileID", pact?.ID)
        intent2.putExtra("fileGuid", pact?.fileGuid)
        intent2.putExtra("fileType", getFileType(pact?.fileName ?: ""))
        intent2.putExtra("position", position)
        startActivityForResult(intent2, 88)
    }

    private fun refreshTitle(position: Int) {
        state = position
        pull = false
        page = 0
        for (i in 0 until adapterTitle?.datas?.size!!) {
            (adapterTitle?.datas?.get(i) as CommonItem<*>).isClick = i == position
        }
        adapterTitle?.notifyDataSetChanged()
        presenter.getPactData(page, state, true)
    }

    override fun removeItem(position: Int) {
        adapter?.datas?.removeAt(position)
        adapter?.notifyItemRemoved(position)
        if (position != adapter?.datas?.size) {
            adapter?.notifyItemRangeChanged(position, adapter?.datas?.size!! - position)
        }
    }

    override fun refreshItem(position: Int) {
        val pact = adapter?.datas?.get(position)
        pact?.isCurAudit = true
        adapter?.notifyItemChanged(position, pact)
    }

    override fun completeRefresh() {
        if (pull) {
            rv_pact_data.completeLoadMore()
        } else {
            rv_pact_data.completeRefresh()
        }
    }

    override fun refreshData() {
        if (pactDatas.isEmpty()) {
            rl_empty.visibility = View.VISIBLE
//            rv_pact_data.visibility = View.GONE
        } else {
            rl_empty.visibility = View.GONE
            rv_pact_data.visibility = View.VISIBLE
        }
        adapter?.setNewData(pactDatas)
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun isShowBacking(): Boolean = true

    private fun getFileType(fileName: String): String {
        var str = ""
        if (TextUtils.isEmpty(fileName)) {
            return str
        }
        val i = fileName.lastIndexOf('.')
        if (i <= -1) {
            return str
        }
        str = fileName.substring(i + 1)
        return str
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99 && resultCode == 99) {
            pull = false
            presenter.getPactData(0, 0, true)
        } else if (requestCode == 88 && resultCode == 88) {
            val pos = data?.getIntExtra("position", -1)
            val audit = data?.getBooleanExtra("audit", false)
            if (pos != -1 && audit == true) {
                if (state == 0 || state == 3) {
                    refreshItem(pos!!)
                } else if (state == 1) {
                    removeItem(pos!!)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
