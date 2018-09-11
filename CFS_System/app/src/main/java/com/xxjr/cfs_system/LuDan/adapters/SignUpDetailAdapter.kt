package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

class SignUpDetailAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var isClose = false //报名截止
    private var onItemClick: RecycleItemClickListener? = null
    private var onItemPassClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null
    private var textHonorChange: TextChangeListener? = null
    private var onItemChoose: RecyclerItemShrink? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(PassDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(HonorEditDelegate() as ItemViewDelegate<Any>)
    }

    fun setIsClose(isClose: Boolean) {
        this.isClose = isClose
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setOnItemPassClick(onItemPassClick: RecycleItemClickListener) {
        this.onItemPassClick = onItemPassClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    fun setTextHonorChangeListener(textHonorChange: TextChangeListener) {
        this.textHonorChange = textHonorChange
    }

    fun setOnItemChooseListener(onItemChoose: RecyclerItemShrink) {
        this.onItemChoose = onItemChoose
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            if (item?.isLineShow == false) holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            else holder?.setBackgroundRes(R.id.v_blank, R.color.white)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 8f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            holder.setText(R.id.tv_content_name, item.name)
            holder.setTextColorRes(R.id.tv_content_name, R.color.font_home)
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
            holder?.setVisible(R.id.iv_content_icon, true)
            if (item?.isLineShow == true) holder?.setImageResource(R.id.iv_content_icon, R.mipmap.choose_blue)
            else holder?.setImageResource(R.id.iv_content_icon, R.mipmap.choose_gray)
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content)
            holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(false)
            holder?.convertView?.setOnClickListener {
                onItemChoose?.onItemShrink(item?.position ?: 0, !item?.isLineShow!!)
                if (!isClose) {
                    notifyItemChanged(position, item.apply { this!!.isLineShow = !isLineShow })
                }
            }
        }
    }

    private inner class PassDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_sign_up_pass

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            val pass = context.resources.getDrawable(R.mipmap.choose_blue)
            val not = context.resources.getDrawable(R.mipmap.choose_gray)
            holder.getView<TextView>(R.id.tv_ok).setCompoundDrawablesWithIntrinsicBounds(if (item.position == 1) pass else not, null, null, null)
            holder.getView<TextView>(R.id.tv_no).setCompoundDrawablesWithIntrinsicBounds(if (item.position == 2) pass else not, null, null, null)
            holder.getView<TextView>(R.id.tv_unknown).setCompoundDrawablesWithIntrinsicBounds(if (item.position == 0) pass else not, null, null, null)
            holder.setOnClickListener(R.id.tv_ok) {
                onItemPassClick?.onItemClick(1)
                if (!isClose) {
                    notifyItemChanged(position, item.apply { this.position = 1 })
                }
            }
            holder.setOnClickListener(R.id.tv_no) {
                onItemPassClick?.onItemClick(2)
                if (!isClose) {
                    notifyItemChanged(position, item.apply { this.position = 2 })
                }
            }
            holder.setOnClickListener(R.id.tv_unknown) {
                onItemPassClick?.onItemClick(0)
                if (!isClose) {
                    notifyItemChanged(position, item.apply { this.position = 0 })
                }
            }
        }
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_sign_up_input

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            val editText: EditText = holder.getView(R.id.et_input)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_title, item.name)
            editText.setText(item.content)
            editText.isEnabled = item.isEnable
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textChange?.setTextChage(position, p0?.toString()?.trim())
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

    private inner class HonorEditDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 5

        override fun getItemViewLayoutId(): Int = R.layout.item_sign_up_input

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            val editText: EditText = holder.getView(R.id.et_input)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_title, item.name)
            editText.setText(item.content)
            holder.setVisible(R.id.iv_add_honor, item.isClick)
            holder.setOnClickListener(R.id.iv_add_honor) {
                onItemClick?.onItemClick(position)
            }
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textHonorChange?.setTextChage(item.position, p0?.toString()?.trim())
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

}