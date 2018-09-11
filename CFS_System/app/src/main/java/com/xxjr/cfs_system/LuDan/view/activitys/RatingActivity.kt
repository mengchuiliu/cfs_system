package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import com.donkingliang.labels.LabelsView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.RatingPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.RatingVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.ToastShow
import entity.LoanInfo
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.android.synthetic.main.item_common_note.*

class RatingActivity : BaseActivity<RatingPresenter, RatingVInter>(), RatingVInter {
    private val maxNote = 100 //备注字数限制
    private var loanInfo: LoanInfo? = null
    private var scores = 0f

    override fun getPresenter(): RatingPresenter = RatingPresenter()

    override fun getLayoutId(): Int = R.layout.activity_rating

    override fun getLoanInfo(): LoanInfo = loanInfo ?: LoanInfo()

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "按揭员评分"
        loanInfo = intent.getSerializableExtra("loanInfo") as? LoanInfo
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        tv_rating.text = "对${CacheProvide.getMortgageName(loanInfo?.mortgage ?: 0)}的评分："
        rating_bar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser) {
                scores = rating
                if (scores == 5f) {
                    labels.selectType = LabelsView.SelectType.NONE
                } else {
                    labels.selectType = LabelsView.SelectType.MULTI
                }
            }
        }
        getAndSaveData()
        initLabels()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    private fun getAndSaveData() {
        presenter.setDefaultValue()
        tv_save.setOnClickListener {
            if (scores != 0f) {
                presenter.saveData(scores, presenter.join(labels.selectLabels, ","))
            } else {
                showMsg("请评分后在保存")
            }
        }
    }

    private fun initLabels() {
        labels.setLabels(presenter.getLabels())
        tv_edit_title.text = "备注:"
        tv_hint_nub.text = "0/$maxNote"
        et_content.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxNote))
        et_content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (charSequence?.length ?: 0 <= maxNote) {
                    tv_hint_nub.text = "${charSequence?.length}/$maxNote"
                }
            }
        })
    }

    override fun initStar(score: Float, improvementIds: MutableList<Int>, remark: String) =
            if (score == 0f) {
                tv_save.isEnabled = true
                et_content.isEnabled = true
                rating_bar.isIndicator = false
            } else {
                et_content.isEnabled = false
                tv_save.isEnabled = false
                rating_bar.isIndicator = true
                rating_bar.rating = score
                labels.compulsorys = improvementIds
                et_content.setText(remark)
            }

    override fun getRemark(): String = et_content.text.toString()

    override fun complete() {
        showMsg("评价保存成功!")
        finish()
    }
}
