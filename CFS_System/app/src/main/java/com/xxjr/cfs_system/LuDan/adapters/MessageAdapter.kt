package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.xiaoxiao.ludan.R
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import entity.CommonItem


class MessageAdapter(val context: Context, var list: MutableList<CommonItem<Any>>) : RecyclerView.Adapter<MessageAdapter.Holder>() {
    private var recycleItemClickListener: RecycleItemClickListener? = null

    fun setRecyclerItemClickListener(recycleItemClickListener: RecycleItemClickListener) {
        this.recycleItemClickListener = recycleItemClickListener
    }

    fun setNewData(commonItems: MutableList<CommonItem<Any>>) {
        list = commonItems
        notifyDataSetChanged()
    }

    fun getData() = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.Holder =
            Holder(LayoutInflater.from(context).inflate(R.layout.item_message, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MessageAdapter.Holder, position: Int) {
        val item = list.get(position)
        if (item.icon != 0) holder.iv_portrait?.setImageResource(item.icon)
        holder.tv_title?.text = item.name ?: ""
        holder.tv_date?.text = item.date ?: ""
        holder.tv_content?.text = item.content ?: ""
        holder.badge?.badgeNumber = item.position
        holder.itemView.setOnClickListener { recycleItemClickListener?.onItemClick(position) }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @Bind(R.id.iv_portrait)
        var iv_portrait: ImageView? = null

        @JvmField
        @Bind(R.id.tv_title)
        var tv_title: TextView? = null

        @JvmField
        @Bind(R.id.tv_date)
        var tv_date: TextView? = null

        @JvmField
        @Bind(R.id.tv_content)
        var tv_content: TextView? = null

        var badge: Badge? = null

        init {
            ButterKnife.bind(this, itemView)
            //消息
            badge = QBadgeView(context).bindTarget(iv_portrait)
            badge?.setBadgeTextSize(11f, true)
            badge?.badgeGravity = Gravity.END or Gravity.TOP
            badge?.setBadgePadding(3f, true)
            badge?.isExactMode = true
//                badge.setOnDragStateChangedListener { i: Int, badge: Badge, view: View -> }
        }
    }
}