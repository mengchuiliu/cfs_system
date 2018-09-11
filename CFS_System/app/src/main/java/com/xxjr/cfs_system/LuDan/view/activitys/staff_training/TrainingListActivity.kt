package com.xxjr.cfs_system.LuDan.view.activitys.staff_training

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.TrainingListPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.BaseListActivity
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.TrainingList
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class TrainingListActivity : BaseListActivity<TrainingListPresenter, TrainingListActivity>() {
    private var type = 0//0->未结束  1->已结束
    var trainingListInfo = mutableListOf<TrainingList>()

    override fun getListPresenter(): TrainingListPresenter = TrainingListPresenter()

    override fun initAdapter() {
        tvSearchType.visibility = View.GONE
        etPactSearch.hint = "请输入项目名称查询"

        adapter = object : CommonAdapter<TrainingList>(this@TrainingListActivity, arrayListOf(), R.layout.item_training_list) {
            override fun convert(holder: BaseViewHolder, training: TrainingList, position: Int) {
                holder.setText(R.id.tv_project_name, "【${training.Title ?: ""}】")
                holder.setText(R.id.tv_start_time, Utils.FormatTime(training.InsertTime
                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd"))
                holder.setText(R.id.tv_end_time, Utils.FormatTime(training.SignUpDeadline
                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd  HH:mm:ss"))
                holder.setText(R.id.tv_teacher, training.LecturerNames ?: "")
                holder.setText(R.id.tv_apply_amount, "${training.TotalSignUp}人")
                holder.setText(R.id.tv_training_site, training.Site ?: "")
                holder.setText(R.id.tv_personnel, training.Limitations ?: "")
                holder.setVisible(R.id.iv_point, training.IsRed)
                when (training.State) {
                    0 -> {
                        holder.setText(R.id.tv_state, "报名中")
                        holder.setTextColorRes(R.id.tv_state, R.color.detail2)
                    }
                    1 -> {
                        holder.setText(R.id.tv_state, "已截止")
                        holder.setTextColorRes(R.id.tv_state, R.color.detail1)
                    }
                    2 -> {
                        holder.setText(R.id.tv_state, "进行中")
                        holder.setTextColorRes(R.id.tv_state, R.color.detail3)
                    }
                    3 -> {
                        holder.setText(R.id.tv_state, "已结束")
                        holder.setTextColorRes(R.id.tv_state, R.color.font_c9)
                    }
                }
                holder.convertView.setOnClickListener {
                    val intent = Intent(this@TrainingListActivity, TrainingDetailActivity::class.java)
                    intent.putExtra("TrainingId", training.Id)
                    startActivity(intent)
                }
            }
        }

        adapter0 = object : CommonAdapter<CommonItem<*>>(this@TrainingListActivity, presenter.getTrainTitles() as List<CommonItem<*>>, R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                val params = holder.convertView.layoutParams as GridLayoutManager.LayoutParams
                params.width = LinearLayout.LayoutParams.MATCH_PARENT
                holder.convertView.layoutParams = params
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@TrainingListActivity, 15f),
                        Utils.dip2px(this@TrainingListActivity, 10f),
                        Utils.dip2px(this@TrainingListActivity, 15f),
                        Utils.dip2px(this@TrainingListActivity, 10f))
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { refreshTitle0(position) }
            }
        }
    }

    private fun refreshTitle0(position: Int) {
        type = position
        isPull = false
        page = 0
        for (i in 0 until adapter0.datas.size) {
            (adapter0.datas[i] as CommonItem<*>).isClick = i == position
        }
        adapter0.notifyDataSetChanged()
        presenter.getTrainingListData(page, type)
    }

    override fun refreshData(page: Int, searchType: Int) {
        presenter.getTrainingListData(page, type)
    }

    override fun refreshChange() {
        adapter.setNewData(trainingListInfo)
    }

    override fun onResume() {
        super.onResume()
        isPull = false
        page = 0
        presenter.getTrainingListData(page, type)
    }
}
