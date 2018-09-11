package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2018/1/16.
 */
class HomePageAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null
    private var isChange = false

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TaskDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(CommonDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(OtherDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    fun setChange(isChange: Boolean) {
        this.isChange = isChange
    }

    fun getChange(): Boolean = isChange

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 10f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class TaskDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_page_content

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            val param = holder.convertView.layoutParams
            if (item.list != null && item.list.isNotEmpty()) {
                holder.convertView.visibility = View.VISIBLE
                param.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder.convertView.visibility = View.GONE
                param.height = 0
                param.width = 0
            }
            holder.convertView.layoutParams = param
            holder.setImageResource(R.id.iv_icon, item.icon)
            holder.setText(R.id.tv_name, item.name)
            holder.setVisible(R.id.page_line, false)
            holder.setVisible(R.id.menu_editor, false)
            val recyclerView = holder.getView<RecyclerView>(R.id.rv_home)
            recyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            val taskAdapter = object : CommonAdapter<CommonItem<Any>>(context, item.list as MutableList<CommonItem<Any>>,
                    R.layout.item_page_task) {
                override fun convert(holder: BaseViewHolder, task: CommonItem<Any>, position: Int) {
                    holder.setText(R.id.tv_task_num, task.position.toString())
                    holder.setText(R.id.tv_content, task.content)
                    holder.convertView.setOnClickListener({
                        if (!isChange)
                            textChange?.setTextChage(100, task.remark)
                    })
                }
            }
            recyclerView.adapter = taskAdapter
        }
    }

    private inner class CommonDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_page_content

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            val imageView = holder.getView<ImageView>(R.id.iv_icon)
            imageView.layoutParams = LinearLayout.LayoutParams(Utils.dip2px(context, 10f), Utils.dip2px(context, 20f))
            holder.setImageResource(R.id.iv_icon, item.icon)
            holder.setText(R.id.tv_name, item.name)
            holder.setText(R.id.menu_editor, item.content)
            holder.setVisible(R.id.page_line, false)
            holder.setVisible(R.id.page_title_line, true)
            holder.setOnClickListener(R.id.menu_editor, {
                if (isChange) textChange?.setTextChage(position, "编辑") else textChange?.setTextChage(position, "完成")
            })
            val recyclerView = holder.getView<RecyclerView>(R.id.rv_home)
            recyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            val menuAdapter = object : CommonAdapter<CommonItem<Any>>(context, item.list as MutableList<CommonItem<Any>>,
                    R.layout.item_menu) {
                override fun convert(holder: BaseViewHolder, menu: CommonItem<Any>, position: Int) {
                    holder.setImageResource(R.id.iv_menu, menu.icon)
                    holder.setVisible(R.id.iv_change, isChange)
                    holder.setImageResource(R.id.iv_change, R.mipmap.menu_close)
                    holder.setText(R.id.tv_menu, menu.name)
                    holder.convertView.setOnClickListener({
                        if (!isChange)
                            textChange?.setTextChage(101, menu.remark)
                    })
                }
            }
            recyclerView.adapter = menuAdapter
        }
    }

    private inner class OtherDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_page_content

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            val imageView = holder.getView<ImageView>(R.id.iv_icon)
            imageView.layoutParams = LinearLayout.LayoutParams(Utils.dip2px(context, 10f), Utils.dip2px(context, 20f))
            holder.setImageResource(R.id.iv_icon, item.icon)
            holder.setText(R.id.tv_name, item.name)
            holder.setVisible(R.id.page_line, false)
            holder.setVisible(R.id.page_title_line, true)
            holder.setVisible(R.id.menu_editor, false)
            val recyclerView = holder.getView<RecyclerView>(R.id.rv_home)
            recyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            val menuAdapter = object : CommonAdapter<CommonItem<Any>>(context, item.list as MutableList<CommonItem<Any>>,
                    R.layout.item_menu) {
                override fun convert(holder: BaseViewHolder, menu: CommonItem<Any>, position: Int) {
                    holder.setImageResource(R.id.iv_menu, menu.icon)
                    holder.setVisible(R.id.iv_change, isChange)
                    holder.setImageResource(R.id.iv_change, R.mipmap.menu_add)
                    holder.setText(R.id.tv_menu, menu.name)
                    holder.convertView.setOnClickListener({
                        if (!isChange)
                            textChange?.setTextChage(101, menu.remark)
                    })
                }
            }
            recyclerView.adapter = menuAdapter
        }
    }

}