package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.widget.GridView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.activitys.PostActivity
import entity.CommonItem
import entity.ImageInfo
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2017/11/27.
 */
class PostAdapter(context: Context, list: List<CommonItem<*>>) : MultiItemAdapter<Any>(context, list) {
    private val activity: PostActivity = context as PostActivity
    private var onItemClick: RecycleItemClickListener? = null
    private var onGvItemClick: RecycleItemClickListener? = null
    private var onDelItemClick: RecycleItemClickListener? = null

    init {
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(RecyclerDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setOnGvItemClick(onItemClick: RecycleItemClickListener) {
        this.onGvItemClick = onItemClick
    }

    fun setOnDelItemClick(onItemClick: RecycleItemClickListener) {
        this.onDelItemClick = onItemClick
    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
            holder?.setVisible(R.id.iv_right, true)
            holder?.convertView?.setOnClickListener { onItemClick?.onItemClick(item.position) }
        }
    }

    private inner class RecyclerDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_post_img

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val gvIcon = holder?.getView<GridView>(R.id.gv_icon)
            val mAdapter = ImageAdapter(activity)
            mAdapter.setData(item?.list as MutableList<ImageInfo>?)
            gvIcon?.adapter = mAdapter
            mAdapter.setOnItemClickListener { position ->
                onGvItemClick?.onItemClick(position)
            }
            mAdapter.setOnDelItemClickListener { position ->
                onDelItemClick?.onItemClick(position)
            }
        }
    }
}