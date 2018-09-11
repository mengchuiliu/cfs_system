package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.widget.EditText
import bankcardformat.BandCardEditText
import com.suke.widget.SwitchButton
import com.xiaoxiao.ludan.R
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2017/10/17.
 */
class AddBankAdapter(context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemCheck: RecyclerItemCheck? = null
    private var onItemScanClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null

    init {
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(ButtomDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(BankDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemCheck(onItemCheck: RecyclerItemCheck) {
        this.onItemCheck = onItemCheck
    }

    fun setOnItemScanClick(onItemScanClick: RecycleItemClickListener) {
        this.onItemScanClick = onItemScanClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit_small

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val edittext: EditText = holder?.getView(R.id.et_content)!!
            edittext.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_content_name, item?.name)
            holder.setHint(R.id.et_content, item?.hintContent)
            holder.setText(R.id.et_content, item?.content)
            edittext.isEnabled = item?.isEnable ?: true
            edittext.gravity = Gravity.LEFT
            when (position) {
                3 -> edittext.keyListener = DigitsKeyListener.getInstance("0123456789zZ")
                else -> edittext.inputType = InputType.TYPE_CLASS_TEXT
            }
            edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(35))
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textChange?.setTextChage(position, p0?.toString()?.trim())
                }
            }
            edittext.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

    private inner class ButtomDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.switch_button

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setText(R.id.tv_name, item?.name)
            holder?.setVisible(R.id.v_line, item?.isLineShow ?: false)
            val bt = holder?.getView<SwitchButton>(R.id.bt_switch)
            bt?.isChecked = item?.isClick ?: false
            bt?.isEnabled = item?.isEnable ?: true
            bt?.setOnCheckedChangeListener { _, isChecked ->
                onItemCheck?.onItemCheck(isChecked)
            }
        }
    }

    private inner class BankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_bank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val edittext: BandCardEditText = holder?.getView(R.id.et_content)!!
            holder.setText(R.id.tv_content_name, item?.name)
            holder.setHint(R.id.et_content, item?.hintContent)
            holder.setText(R.id.et_content, item?.content)
            holder.setVisible(R.id.iv_scan, true)
            holder.setImageResource(R.id.iv_scan, R.mipmap.icon_scanning)
            edittext.isEnabled = item?.isEnable ?: true
            edittext.gravity = Gravity.LEFT
            edittext.keyListener = DigitsKeyListener.getInstance("0123456789")
            edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
            edittext.setBankCardListener { textChange?.setTextChage(position, edittext.bankCardText) }

            holder.setOnClickListener(R.id.iv_scan, {
                onItemScanClick?.onItemClick(position)
            })
        }
    }
}