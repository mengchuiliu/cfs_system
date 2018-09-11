package com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.attachment

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.HttpMethods
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.view.activitys.WebActivity
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 *
 * 回款审核 - 附件布局
 * @author huangdongqiang
 * @date 02/07/2018
 */
class AttachmentAdapter (val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: TextChangeListener? = null
    private var onItemShrink: RecycleItemClickListener? = null
    private var onItemDelete: OnItemDeleteListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(BlankWhiteDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleNDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(UploadBtnLayoutDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(FileDelegate2() as ItemViewDelegate<Any>)
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

    private inner class TitleNDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 5

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setTextColorRes(R.id.tv_content_name, R.color.font_home)

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

    /**
     * 上传按钮布局
     */
    private inner class UploadBtnLayoutDelegate: ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean  = item?.type == 17

        override fun getItemViewLayoutId(): Int = R.layout.item_returned_audit_upload

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            holder.convertView?.setOnClickListener{onItemShrink?.onItemClick(position)}
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
        }

    }

    /**
     * 附件布局
     */
    private inner class FileDelegate2 : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 16

        override fun getItemViewLayoutId(): Int = R.layout.item_returned_audit_file

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
            holder.setText(R.id.tv_protocol_value, item.content)
            holder.setText(R.id.tv_upload_time_value, item.date)
            holder.setOnClickListener(R.id.iv_icon, {
                clickAttachment(item)

            })
            holder.setOnClickListener(R.id.tv_read, {
                clickAttachment(item)
            })
            holder.setOnClickListener(R.id.tv_delete, {
                onItemDelete?.onItemDelete(position)
            })
        }

        private fun clickAttachment(item: CommonItem<*>) {
            if (!item.isClick) {
                when(item.payType) {
                    //PC 端附件只有 png 格式，字段为 1
                    "1" -> {
                        //cotnent 保存 ContractID
                        //用 remark 字段保存文件对于 id
                        //payType 保存 文件类型 LP1
                        //用 name 保存 回款列表Id
                        showFile(item.remark, item.content, WebActivity::class.java)
                    } else ->{
                        ToastShow.showShort(context, "手机暂不支持查看此类文件，请在电脑上查看")
                    }
                }
            }
        }

        //参考文档 App回款审核下载图片.doc
        //id 回款审核查看列表返回的id
        //contractId 所属协议（在回款列表基本信息中）
        //fileID 固定"CollectPayPicUpLoad"
        private fun showFile(fileGuid: String,  contractId: String, clazz: Class<*>) {
            try {
                val intent2 = Intent(context, clazz)
                //App回款审核下载图片
                //http://192.168.31.155:2568/DuiService/GetFileStream?id=1&contractId=1806280306&fileID=CollectPayPicUpLoad&sessionId=16b394ae9cb5&userId=13372
                val filePath = "${HttpMethods.FileUrl}id=${fileGuid}&SessionId=${Hawk.get<String>("SessionID")}" +
                        "&UserId=${Hawk.get<String>("UserID")}&ContractId=${contractId}" +
                        "&fileID=CollectPayPicUpLoad"
                intent2.putExtra("isRead", 0)
                intent2.putExtra("filePath", filePath)
                //intent2.putExtra("fileType", name)
                context.startActivity(intent2)
            } catch (ex: Exception) {
                ToastShow.showShort(context, "打开文件失败，请在电脑上查看")
            }
        }
    }



    fun setOnItemClick(onItemClick: TextChangeListener) {
        this.onItemClick = onItemClick
    }

    fun setOnItemShrink(onItemShrink: RecycleItemClickListener) {
        this.onItemShrink = onItemShrink
    }

    fun setOnItemDelete(onItemDelete: OnItemDeleteListener) {
        this.onItemDelete = onItemDelete
    }

    /**
     * 删除按钮回调
     */
    interface OnItemDeleteListener {
        fun onItemDelete(position: Int)
    }
}