package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.SwipeMenuLayout
import com.xxjr.cfs_system.LuDan.view.activitys.CostDetailsActivity
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.Cost
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

class CostRecordAdapter(context: Context?, datas: List<CommonItem<*>>?, val permits: List<String>?) : MultiItemAdapter<Any>(context, datas) {
    val activity: CostDetailsActivity = context as CostDetailsActivity
    private var itemClick: RecycleItemClickListener? = null
    private var deleteClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(RecyclerDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditNoteDelegate() as ItemViewDelegate<Any>)
    }

    fun setItemClick(itemClick: RecycleItemClickListener) {
        this.itemClick = itemClick
    }

    fun setDeleteClick(itemClick: RecycleItemClickListener) {
        this.deleteClick = itemClick
    }

    fun setTextChange(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            if (position != 8) {
                holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            } else {
                holder?.setBackgroundRes(R.id.v_blank, R.color.white)
            }
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_common_title

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setImageResource(R.id.iv_title, item!!.icon)
            holder?.setText(R.id.tv_title, item?.name)
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val param = holder?.convertView?.layoutParams
            if (item?.isClick == true) {
                holder?.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            } else {
                holder?.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            holder?.convertView?.layoutParams = param
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content)
        }
    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content)
            holder?.setVisible(R.id.iv_right, item?.isClick ?: false)
            holder?.setVisible(R.id.v_line, !(item?.isLineShow ?: false))
            if (item?.isClick!!) {
                holder?.convertView?.setOnClickListener({
                    itemClick?.onItemClick(position)
                })
            }
        }
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit_small

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val edittext: EditText = holder?.getView(R.id.et_content)!!
            edittext.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_content_name, item?.name)
            holder.setText(R.id.et_content, item?.content)
            holder.setHint(R.id.et_content, item?.hintContent)
            holder.setVisible(R.id.tv_yuan, item?.isClick ?: false)
            if (item?.isClick!!) {
                edittext.keyListener = DigitsKeyListener.getInstance("0123456789")
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
            } else {
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
            }
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textChange?.setTextChage(position, p0?.toString())
                }
            }
            edittext.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

    private inner class RecyclerDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 5

        override fun getItemViewLayoutId(): Int = R.layout.item_recycle

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val recyclerView = holder?.getView<RecyclerView>(R.id.recycle_item)
            recyclerView?.layoutManager = LinearLayoutManager(activity)
            if (item?.list?.isNotEmpty()!!) {
                val adapter = object : CommonAdapter<Cost>(activity, item.list as MutableList<Cost>?, R.layout.item_cost_check) {
                    override fun convert(holder: BaseViewHolder?, cost: Cost?, position: Int) {
                        val swipeMenuLayout = (holder?.convertView as SwipeMenuLayout).setIos(false).setLeftSwipe(true)
                        swipeMenuLayout.isSwipeEnable = false
                        holder.setText(R.id.tv_cost_type, "${Utils.getTypeValue(Utils.getTypeDataList("LoanCostType"), cost?.costType ?: 0)}：")
                        holder.setText(R.id.tv_nub, "${cost?.money}元")
                        holder.setText(R.id.tv_cost_data, Utils.getTime(cost?.happenDate ?: ""))
                        if ((cost?.remark ?: "").isBlank()) {
                            holder.setVisible(R.id.ll_remark, false)
                        } else {
                            holder.setVisible(R.id.ll_remark, true)
                            holder.setText(R.id.tv_note, cost?.remark)
                        }
                        holder.setText(R.id.tv_people, cost?.serviceName)
                        holder.setText(R.id.tv_time, Utils.getTime(cost?.operateTime))

                        //界面显示
                        holder.setVisible(R.id.ll_audit_people, false)
                        holder.setVisible(R.id.ll_audit_people_manager, false)
                        holder.setVisible(R.id.ll_cost_paid, false)
                        holder.setVisible(R.id.ll_audit_note, false)
                        if (cost?.auditStatus == 0) {
                            swipeMenuLayout.isSwipeEnable = (permits != null && permits.contains("CY"))
                        }
                        if (cost?.auditStatus ?: 0 > 0 && cost?.auditStatus != 5) {
                            holder.setVisible(R.id.ll_audit_people, true)
                            holder.setText(R.id.tv_audit_people, cost?.auditorName)
                            holder.setText(R.id.tv_audit_time, Utils.getTime(cost?.auditTime ?: ""))
                        }
                        if (cost?.auditStatus ?: 0 > 2 && cost?.auditStatus != 5) {
                            holder.setVisible(R.id.ll_audit_people_manager, true)
                            holder.setText(R.id.tv_audit_manager, cost?.auditorManager)
                            holder.setText(R.id.tv_audit_manager_time, Utils.getTime(cost?.managerAuditDateTime))
                        }
                        if ((cost?.auditStatus ?: 0 == 3 || cost?.auditStatus ?: 0 == 7) && cost?.auditStatus != 5) {
                            holder.setVisible(R.id.ll_audit_accounter, true)
                            holder.setText(R.id.tv_audit_accounter, cost?.accounterName)
                            holder.setText(R.id.tv_audit_accounter_time, Utils.getTime(cost?.accounterTime ?: ""))
                        }
                        if (cost?.paidTime.isNullOrBlank()) {
                            holder.setText(R.id.tv_paid, "未付款")
                            holder.setText(R.id.tv_paid_time, "")
                        } else {
                            holder.setText(R.id.tv_paid, "已付款-${cost?.paidName}")
                            holder.setText(R.id.tv_paid_time, Utils.getTime(cost?.paidTime))
                        }

                        when (cost?.auditStatus) {
                            2, 4 -> {
                                holder.setVisible(R.id.ll_audit_note, true)
                                holder.setText(R.id.tv_audit_note, cost.auditRemark)
                            }
                            7 -> {
                                holder.setVisible(R.id.ll_audit_note, true)
                                holder.setText(R.id.tv_audit_note, cost.accounterAuditRemark)
                            }
                        }

                        holder.setOnClickListener(R.id.tv_del, View.OnClickListener {
                            deleteClick?.onItemClick(position)
                        })
                    }
                }
                recyclerView?.adapter = adapter
            }
        }
    }

    private inner class EditNoteDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 6

        override fun getItemViewLayoutId(): Int = R.layout.item_common_note

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            val param = holder.convertView?.layoutParams
            if (item.isClick) {
                holder.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            }
            holder.convertView?.layoutParams = param
            holder.setText(R.id.tv_edit_title, item.name)
            holder.setText(R.id.tv_hint_nub, "0/" + item.position)
            val editText = holder.getView<EditText>(R.id.et_content)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            editText.setText(item.content)
            editText.hint = item.hintContent
            editText.setOnTouchListener { view, motionEvent ->
                view.parent.requestDisallowInterceptTouchEvent(true)
                false
            }
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(item.position))
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (charSequence.length <= item.position) {
                        holder.setText(R.id.tv_hint_nub, charSequence.length.toString() + "/" + item.position)
                    }
                }

                override fun afterTextChanged(editable: Editable) {
                    textChange?.setTextChage(position, editable.toString().trim())
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

}