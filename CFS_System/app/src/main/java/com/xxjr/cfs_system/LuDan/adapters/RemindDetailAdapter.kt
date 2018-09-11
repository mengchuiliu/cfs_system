package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.SwipeMenuLayout
import com.xxjr.cfs_system.LuDan.view.activitys.WebActivity
import com.xxjr.cfs_system.LuDan.view.post_image.FileDisplayActivity
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.LoanInfo
import entity.PactData
import refresh_recyclerview.DividerItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter
import java.math.BigDecimal

/**
 * Created by Administrator on 2017/10/17.
 */
class RemindDetailAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: TextChangeListener? = null
    private var onItemShrink: RecycleItemClickListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(ManyLendDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(BlankWhiteDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleNDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(FileDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(FileDetailDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(LoanDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: TextChangeListener) {
        this.onItemClick = onItemClick
    }

    fun setOnItemShrink(onItemShrink: RecycleItemClickListener) {
        this.onItemShrink = onItemShrink
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 10f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class BlankWhiteDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 8f))
            if (item?.isClick == false) {
                holder?.convertView?.visibility = View.VISIBLE
                lp.height = Utils.dip2px(context, 8f)
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder?.convertView?.visibility = View.GONE
                lp.height = 0
                lp.width = 0
            }
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_common_title

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            holder?.setImageResource(R.id.iv_title, item!!.icon)
            holder?.setText(R.id.tv_title, item?.name)
        }
    }

    private inner class TitleNDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 5

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            holder?.setVisible(R.id.tv_content_name, false)
            holder?.setText(R.id.tv_content, item?.name)
            holder?.setTextColorRes(R.id.tv_content, R.color.font_home)
            holder?.getView<TextView>(R.id.tv_content)?.gravity = Gravity.LEFT

            if ((item?.icon ?: 0) != 0) {
                holder?.setVisible(R.id.iv_title, true)
                holder?.setImageResource(R.id.iv_title, item?.icon ?: 0)
            } else holder?.setVisible(R.id.iv_title, false)

            holder?.setVisible(R.id.tv_yuan, item?.isLineShow == true)
            if (item?.isLineShow == true) {
                holder?.setText(R.id.tv_yuan, "编辑")
            }
            holder?.setOnClickListener(R.id.tv_yuan) {
                if (item?.isLineShow == true) {
                    onItemShrink?.onItemClick(position)
                }
            }

            holder?.setVisible(R.id.iv_right, item?.isEnable == false)
            if (item?.isEnable == false) {
                if (item.isClick) {
                    holder?.setImageResource(R.id.iv_right, R.mipmap.icon_spend_shrink)
                } else {
                    holder?.setImageResource(R.id.iv_right, R.mipmap.icon_spend_unfold)
                }
                holder?.setVisible(R.id.v_line, !item.isClick)
                holder?.convertView?.setOnClickListener { onItemShrink?.onItemClick(position) }
            } else {
                holder?.setVisible(R.id.v_line, true)
            }
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            val param = holder?.convertView?.layoutParams
            if (item?.isClick == false) {
                holder?.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder?.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            }
            holder?.convertView?.layoutParams = param
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content)
            holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(false)
            if (item?.icon == 0) holder?.setTextColorRes(R.id.tv_content, R.color.font_c3)
            else holder?.setTextColorRes(R.id.tv_content, item?.icon ?: 0)
        }
    }

    private inner class ManyLendDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>, position: Int): Boolean = item.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_many_lend

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>?, position: Int) {
            val loanInfo = item?.item as? LoanInfo
            val swipeMenuLayout = (holder.convertView as SwipeMenuLayout).setIos(false).setLeftSwipe(true)
            swipeMenuLayout.isSwipeEnable = false
            holder.setText(R.id.tv_lend_money, Utils.parseMoney(BigDecimal(loanInfo?.lendingAmount
                    ?: 0.0)) + "元")
            holder.setText(R.id.tv_lend_date, loanInfo?.lendingTime ?: "")
            holder.setText(R.id.tv_month_money, Utils.parseMoney(BigDecimal(loanInfo?.monthAmount
                    ?: 0.0)) + "元")
            holder.setTextColorRes(R.id.tv_month_money, R.color.detail1)
            holder.setText(R.id.tv_month_date, loanInfo?.monthDate ?: "")
            holder.setTextColorRes(R.id.tv_month_date, R.color.detail1)
            holder.setText(R.id.tv_other_amount, "${Utils.parseMoney(BigDecimal(loanInfo?.otherAmount
                    ?: 0.0))}元")
            holder.setTextColorRes(R.id.tv_other_amount, R.color.detail1)
            holder.setText(R.id.tv_return_date, loanInfo?.returnTime ?: "")
            holder.setText(R.id.tv_add_name, loanInfo?.clerkName ?: "")//添加者
            holder.setText(R.id.tv_period, "${loanInfo?.lendingPeriod}期")//还款期数
//            holder.setText(R.id.tv_interest, loanInfo?.interestRate.toString() + "%")
//            holder.setText(R.id.tv_payment_type, loanInfo?.paymentName)
            holder.setVisible(R.id.ll_interest, false)
            holder.setVisible(R.id.ll_remark, false)
            holder.setVisible(R.id.ll_other_remark, false)
            holder.getView<LinearLayout>(R.id.ll_home).isEnabled = false
        }
    }

    private inner class FileDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 6

        override fun getItemViewLayoutId(): Int = R.layout.item_spend_file

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            val param = holder.convertView?.layoutParams
            if (!item.isClick) {
                holder.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            }
            holder.convertView?.layoutParams = param
            holder.setText(R.id.tv_file_name, item.name)
            holder.setText(R.id.tv_file_size, item.content)
            holder.setText(R.id.tv_remark, item.remark)
            holder.convertView.setOnClickListener {
                if (!item.isClick) {
                    when (getFileType(item.name ?: "")) {
                        "jpg", "JPG", "png", "PNG", "GIF", "gif", "JPEG", "jpeg" -> {
                            showFile(getFileType(item.name
                                    ?: ""), item.hintContent, WebActivity::class.java)
                        }
                        "doc", "excel", "ppt", "txt", "pdf", "pptx", "docx", "xlsx", "xls", "wps" -> {
                            showFile(getFileType(item.name
                                    ?: ""), item.hintContent, FileDisplayActivity::class.java)
                        }
                        else -> ToastShow.showShort(context, "手机暂不支持查看此类文件，请在电脑上查看")
                    }
                }
            }
        }
    }

    private inner class FileDetailDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>, position: Int): Boolean = item.type == 7

        override fun getItemViewLayoutId(): Int = R.layout.item_file

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            val param = holder.convertView?.layoutParams
            if (!item.isClick) {
                holder.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            }
            holder.convertView?.layoutParams = param
            val file = item.item as PactData
            holder.setText(R.id.tv_customer, file.customer)
            holder.setText(R.id.tv_file_type, file.fileType)
            holder.setText(R.id.tv_file_name, file.fileName)
            holder.setText(R.id.tv_file_size, file.fileSize)
            holder.setText(R.id.tv_service, file.uploader)
            holder.setText(R.id.tv_remark, file.remark)
            holder.convertView.setOnClickListener {
                if (!item.isClick) {
                    when (getFileType(file.fileName ?: "")) {
                        "jpg", "JPG", "png", "PNG", "GIF", "gif", "JPEG", "jpeg" -> {
                            showFile(getFileType(file.fileName
                                    ?: ""), file.fileGuid ?: "", WebActivity::class.java)
                        }
                        "doc", "excel", "ppt", "txt", "pdf", "pptx", "docx", "xlsx", "xls", "wps" -> {
                            showFile(getFileType(file.fileName
                                    ?: ""), file.fileGuid ?: "", FileDisplayActivity::class.java)
                        }
                        else -> ToastShow.showShort(context, "手机暂不支持查看此类文件，请在电脑上查看")
                    }
                }
            }
        }
    }

    private inner class LoanDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 8

        override fun getItemViewLayoutId(): Int = R.layout.item_return_audit_loan_info

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            val param = holder?.convertView?.layoutParams
            if (item?.isClick == false) {
                holder?.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder?.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            }
            holder?.convertView?.layoutParams = param
            holder?.setText(R.id.tv_loan_code, item?.name ?: "")
            holder?.setText(R.id.tv_service, item?.content ?: "")
            holder?.setText(R.id.tv_mortgage, item?.hintContent ?: "")
            holder?.setText(R.id.tv_customer, item?.date ?: "")
            holder?.setText(R.id.tv_loan_product, item?.remark ?: "")
            holder?.setText(R.id.tv_lending, item?.payType ?: "")
            holder?.setINVISIBLE(R.id.line, item?.isLineShow == true)
        }
    }

    private fun showFile(name: String, fileGuid: String, clazz: Class<*>) {
        val intent2 = Intent(context, clazz)
        intent2.putExtra("isRead", 0)
        intent2.putExtra("contractId", "0")
        intent2.putExtra("fileID", "0")
        intent2.putExtra("fileGuid", fileGuid)
        intent2.putExtra("fileType", name)
        context.startActivity(intent2)
    }

    //文件类型
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
}