package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.SignUpActivity
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.StaffInfo
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.HashMap


class SignUpPresenter : BasePresenter<SignUpActivity, ModelImp>() {
    private lateinit var lp: WindowManager.LayoutParams
    private var popWindow: PopupWindow? = null
    private lateinit var adapter: CommonAdapter<ChooseType>
    var chooseTypes = mutableListOf<ChooseType>()//岗位筛选
    private var isFirst = true
    var postIds = ""//岗位id

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        if (isViewAttached) {
            adapter = object : CommonAdapter<ChooseType>(view as Context, arrayListOf(), R.layout.item_label) {
                override fun convert(holder: BaseViewHolder, chooseType: ChooseType, position: Int) {
                    val lp = holder.convertView.layoutParams as ViewGroup.MarginLayoutParams
                    lp.setMargins(Utils.dip2px(view, 8f),
                            Utils.dip2px(view, 15f),
                            Utils.dip2px(view, 6f), 0)
                    holder.convertView.layoutParams = lp

                    holder.setText(R.id.tv_customer_choose, chooseType.content.trim { it <= ' ' })
                    holder.setTextSize(R.id.tv_customer_choose, 12f)
                    holder.getView<TextView>(R.id.tv_customer_choose).isSelected = chooseType.isChoose
                    holder.convertView.setOnClickListener {
                        chooseType.isChoose = !chooseType.isChoose
                        adapter.notifyItemChanged(position, chooseType)
                    }
                }
            }
            getFilterData()
            getStaffData()

        }
    }

    //显示岗位筛选
    fun showFilter(parent: View) {
        if (isViewAttached) {
            if (popWindow == null) {
                createPop()
                showPop(parent)
            } else {
                if (popWindow?.isShowing == false) {
                    showPop(parent)
                }
            }
        }
    }

    //获取岗位数据
    private fun getFilterData() {
        getData(0, model.getMoreParam(mutableListOf(), "TrainingSignUp", "GetTrainingPostList"), false)
    }

    //获取所有培训人员数据
    fun getStaffData() {
        getData(1, model.getMoreParam(mutableListOf<Any>().apply {
            add(view.page)//页码
            add("20")//条数
            add(view.getETContent())//搜索条件员工姓名和编号
            add(view.getTrainingId())//培训记录id
            add("")//额外搜索条件，区域Id，多个英文逗号间隔
            add("")//额外搜索条件，门店Id，多个英文逗号间隔
            add(postIds)//额外搜索条件，岗位Id，多个英文逗号间隔
        }, "TrainingSignUp", "GetEmployeeList"), true)
    }

    //获取已经培训人员
    private fun getIsTrainingStaffData() {
        getData(2, model.getMoreParam(mutableListOf<Any>().apply {
            add(view.getTrainingId())//培训记录id
        }, "TrainingSignUp", "Get"), true)
    }

    //完成报名
    fun completeApply() {
        getData(3, getApplyParam(mutableListOf<Any>().apply {
            add(view.getTrainingId())//培训记录id
        }, "TrainingSignUp", "SignUp", JSON.toJSONString(mutableListOf<Any>().apply {
            for (choose in view.projects) {
                add(hashMapOf<Any, Any>().apply {
                    put("ProjectId", choose.id)
                    put("Users", view.sparseArray.get(choose.id))
                })
            }
        })), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray = JSONArray.parseArray(data?.data ?: "")
                        for (i in jsonArray.indices) {
                            chooseTypes.add(ChooseType().apply {
                                val jsonObject = jsonArray.getJSONObject(i)
                                id = jsonObject.getIntValue("Id")
                                content = jsonObject.getString("Name")
                            })
                        }
                        adapter.setNewData(chooseTypes)
                    }
                }
                1 -> {
                    val temp = getItemData(data?.data ?: "")
                    if (temp.size == 0) {
                        if (view.page == 0) {
                            view.staffs.clear()
                            view.refreshData()
                        } else {
                            view.showMsg("没有更多数据!")
                        }
                    } else {
                        if (view.pull) {
                            view.staffs.addAll(temp)
                        } else {
                            view.staffs.clear()
                            view.staffs.addAll(temp)
                        }
                        view.refreshData()
                    }
                    if (isFirst) {
                        isFirst = false
                        getIsTrainingStaffData()
                    } else {
                        refreshIsTrainingItem(view.projectId)
                    }
                }
                2 -> {
                    if ((data?.returnString ?: "").isNotBlank()) {
                        getIsTrainingItemData(data?.returnString ?: "")
                        refreshIsTrainingItem(view.projects[0].id)
                    }
                }
                3 -> {
                    view.complete()
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    //全部人员解析
    fun getItemData(data: String) = mutableListOf<StaffInfo>().apply {
        if (data.isNotBlank()) {
            val jsonArray = JSONArray.parseArray(data)
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                add(StaffInfo().apply {
                    UserId = jsonObject.getIntValue("UserID")
                    UserName = "[${jsonObject.getString("EmployeeNo")}]${jsonObject.getString("UserRealName")}" +
                            "(${jsonObject.getString("PostName")})-${jsonObject.getString("Company")}"
                    IsTimeOverlap = jsonObject.getBooleanValue("IsTimeOverlap") // true ->时间冲突不可选
                })
            }
        }
    }

    //获取已报名人员，并将对象保存在sparseArray中
    private fun getIsTrainingItemData(data: String) {
        val jsonArray = JSONArray.parseArray(data)
        for (project in view.projects) {
            view.sparseArray.get(project.id).apply {
                for (i in jsonArray.indices) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    if (project.id == jsonObject.getIntValue("ProjectId")) {
                        addAll(JSONArray.parseArray(jsonObject.getString("Users"), StaffInfo::class.java))
                    }
                }
            }
        }
    }

    //刷新报名人员状态
    private fun refreshIsTrainingItem(projectId: Int) {
        val list = view.sparseArray.get(projectId)
        if (list != null) {
            for (staff in list) {
                for (i in view.staffs.indices) {
                    if (staff.UserId == view.staffs[i].UserId) {
                        view.staffs[i].DelMarker = true
                        view.staffs[i].IsGreatOperation = staff.IsGreatOperation
                        view.staffs[i].IsNeedCheckInBefore = staff.IsNeedCheckInBefore
                        view.staffs[i].IsNeedUnifiedRiding = staff.IsNeedUnifiedRiding
                        break
                    }
                }
            }
        }
        view.refreshData()
    }

    //通用参数组装
    fun getApplyParam(list: List<Any>, tranName: String, Action: String, json: String): String {
        val map = HashMap<String, Any>()
        map["Action"] = Action
        map["Function"] = ""
        map["IsUseZip"] = false
        map["ParamString"] = list
        map["Json"] = json
        map["TranName"] = tranName
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    //创建显示筛选职位pop
    private fun createPop() {
        lp = view.window.attributes
        val popView = LayoutInflater.from(view as Context).inflate(R.layout.pop_filter, null)
        popWindow = PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        popView.findViewById<View>(R.id.v_blank).setOnClickListener { popWindow?.dismiss() }
        popView.findViewById<TextView>(R.id.tv_reset).setOnClickListener {
            adapter.setNewData(chooseTypes.apply {
                for (choose in this) {
                    choose.isChoose = false
                }
            })
        }
        popView.findViewById<TextView>(R.id.tv_complete).setOnClickListener {
            popWindow?.dismiss()
            val stringBuilder = StringBuilder()
            for (i in adapter.datas.indices) {
                if (adapter.datas[i].isChoose) {
                    stringBuilder.append(adapter.datas[i].id).append(",")
                }
            }
            postIds = stringBuilder.toString()
            if (postIds.isNotBlank()) {
                postIds = postIds.substring(0, postIds.length - 1)
            }
            view.pull = false
            view.page = 0
            getStaffData()
        }
        val recyclerView = popView.findViewById<RecyclerView>(R.id.recycler_filter)
        recyclerView.layoutManager = GridLayoutManager(view as Context, 3)
        recyclerView.adapter = adapter
        popWindow?.animationStyle = R.style.my_popwindow
        popWindow?.isFocusable = true
        popWindow?.isOutsideTouchable = true
        popWindow?.setBackgroundDrawable(BitmapDrawable())
        popWindow?.setOnDismissListener {
            lp.alpha = 1f
            view.window.attributes = lp
        }
    }

    //显示筛选
    private fun showPop(parent: View) {
        popWindow?.showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0)
        lp.alpha = 0.4f
        view.window.attributes = lp
    }
}