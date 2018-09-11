package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xiaoxiao.widgets.WaterMarkDrawable
import com.xxjr.cfs_system.LuDan.adapters.HomeMenuAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.presenter.HomeMenuPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View.MessageActivity
import com.xxjr.cfs_system.LuDan.view.activitys.lending_list.LendingListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.loan_calculator.CalculatorActivity
import com.xxjr.cfs_system.LuDan.view.activitys.mortgage_score.MortgageScoreActivity
import com.xxjr.cfs_system.LuDan.view.activitys.reimbursement_remind.RemindListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.report.ReportActivity
import com.xxjr.cfs_system.LuDan.view.activitys.report.ReportChartActivity
import com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.main.ReturnedAuditListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.spending_audit.SpendingAuditListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.TrainingListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.transaction_record.TransactionListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.transfer_receivable.TransferReceivableActivity
import com.xxjr.cfs_system.LuDan.view.activitys.visit_record.VisitRecordActivity
import com.xxjr.cfs_system.LuDan.view.activitys.withdraw_wallet.WageConfirmActivity
import com.xxjr.cfs_system.LuDan.view.activitys.withdraw_wallet.WithdrawListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.withholding_agreement.WithholdingList
import com.xxjr.cfs_system.LuDan.view.fragments.PageFragment
import com.xxjr.cfs_system.LuDan.view.viewinter.PageVInter
import com.xxjr.cfs_system.ViewsHolder.ItemDragHelperCallback
import com.xxjr.cfs_system.ViewsHolder.MyCountDown
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.main.WelWebActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import com.yanzhenjie.permission.AndPermission
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_home_page.*
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import java.io.File
import java.util.*

class HomeMenuActivity : AppCompatActivity(), PageVInter, View.OnClickListener {
    private var presenter: HomeMenuPresenter = HomeMenuPresenter()
    private var adapter: HomeMenuAdapter? = null
    private var pageFrg: PageFragment? = null
    private lateinit var birthCount: CountDownTimer
    private var birthShow = false
    private lateinit var qBadgeView: Badge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        (applicationContext as MyApplication).addActivity(this@HomeMenuActivity)
        val birthDay = Utils.getTime(intent.getStringExtra("UserBirthday") ?: "")
        if (birthDay.isNotBlank()) {
            if (DateUtil.getFormatDate(Date()) == birthDay) {
                birthShow = true
                presenter.cardShow = false
                showBirthView()
            }
        }
        initPresenter()
        pageFrg = supportFragmentManager.findFragmentById(R.id.fragment_left) as PageFragment?
        pageFrg?.setPermissions(userPermission)
        pageFrg?.setVersion(intent.getStringExtra("versionName") ?: "")
        iv_portrait.setOnClickListener(this)
    }

    private fun showBirthView() {
        view_stub.inflate()
        val birthView = findViewById<View>(R.id.birth_view)
        val tvCount = birthView.findViewById<TextView>(R.id.tv_count)
        val tvName = birthView.findViewById<TextView>(R.id.tv_name)
        birthCount = object : CountDownTimer(5 * 1000 + 100, 1000) {
            override fun onFinish() {
                birthView.visibility = View.GONE
                RxBus.getInstance().post(Constants.SHOW_BIRTH, 1)
            }

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished / 1000 > 0)
                    tvCount.text = "${millisUntilFinished / 1000}s 跳过"
            }
        }
        birthCount.start()
        val typeFace = Typeface.createFromAsset(assets, "fonts/Songtypeface.TTF") // 应用字体
        tvName.typeface = typeFace
        tvName.text = "亲爱的，$realName"
        tvCount.setOnClickListener {
            birthCount.cancel()
            birthView.visibility = View.GONE
            RxBus.getInstance().post(Constants.SHOW_BIRTH, 1)
        }
    }

    private fun initPresenter() {
        iv_message.setOnClickListener {
            startActivity(Intent(this@HomeMenuActivity, MessageActivity::class.java))
        }
        qBadgeView = QBadgeView(this@HomeMenuActivity).bindTarget(iv_message)
        //消息圈
        qBadgeView.setBadgeTextSize(10f, true)
        qBadgeView.badgeGravity = Gravity.END or Gravity.TOP
        qBadgeView.setBadgePadding(2.5f, true)
//        qBadgeView.setOnDragStateChangedListener { i: Int, badge: Badge, view: View -> }

        presenter.attach(this)
        presenter.checkHasBank()//检测银行卡
        presenter.upLoadLogFile()//上传错误日志
        presenter.initMenu()
        presenter.refreshMyTask(!birthShow)

        when ((application as MyApplication).MsgType) {
            3 -> userAction("818")//还款提醒
            4 -> userAction("804")//失信贷款
            5 -> userAction("821") //提成钱包
            6 -> startActivity(Intent(this@HomeMenuActivity, MessageActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.setDefaultValue()
        if (pageFrg != null) pageFrg?.initView(typeName, company, realName)
        AndPermission.with(this)
                .requestCode(Constants.REQUEST_CODE_PERMISSION_SD)
                .permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(presenter.permissioner)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale { requestCode, rationale ->
                    // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                    AndPermission.rationaleDialog(this@HomeMenuActivity, rationale).show()
                }
                .start()
    }

    override fun onStart() {
        super.onStart()
        val drawable = WaterMarkDrawable(realName, resources.getColor(R.color.font_dd), 45, resources.getColor(R.color.transparent))
        water.setBackgroundDrawable(drawable)
        initADView()
        if ((application as MyApplication).canShowAd() && (application as MyApplication).showAdvertising) {
            showADView()
        }
    }

    private var advertisingCount: MyCountDown? = null
    private lateinit var mAdView: View
    private lateinit var tvCount: TextView//倒计时
    private lateinit var adImage: ImageView//倒计时

    //创建广告页
    private fun initADView() {
        mAdView = View.inflate(this, R.layout.activity_welcome, null)
        adImage = mAdView.findViewById(R.id.imageView)
        tvCount = mAdView.findViewById(R.id.tv_count)
        tvCount.setOnClickListener {
            if (advertisingCount != null) advertisingCount!!.cancel()
            mAdView.visibility = View.GONE
        }
        adImage.setOnClickListener {
            if (!TextUtils.isEmpty((application as MyApplication).advertisingUrl)) {
                if (advertisingCount != null) advertisingCount?.cancel()
                val intent = Intent(this@HomeMenuActivity, WelWebActivity::class.java)
                intent.putExtra("Type", 0)
                intent.putExtra("AdvertisingUrl", (application as MyApplication).advertisingUrl)
                startActivity(intent)
                mAdView.visibility = View.GONE
            }
        }
        val params = WindowManager.LayoutParams()
        params.x = 0
        params.y = 0
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        windowManager.addView(mAdView, params)
        mAdView.visibility = View.GONE
    }

    //显示广告页
    private fun showADView() {
        val file = File(Constants.AdvertisingPath)
        if (file.exists()) {
            Glide.with(this).load(file)
                    .apply(RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.launchlmage).timeout(60 * 1000))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            mAdView.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            mAdView.visibility = View.VISIBLE
                            tvCount.visibility = View.VISIBLE
                            advertisingCount = MyCountDown((application as MyApplication).advertisingTime + 100, 1000, tvCount)
                            advertisingCount?.setTimeFinishListener(RecycleItemClickListener {
                                if (advertisingCount != null) advertisingCount?.cancel()
                                mAdView.visibility = View.GONE
                            })
                            advertisingCount?.start()
                            return false
                        }
                    })
                    .into(adImage)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!(application as MyApplication).canShowAd()) {
            if (advertisingCount != null) advertisingCount?.cancel()
            mAdView.visibility = View.GONE
            windowManager.removeViewImmediate(mAdView)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun setBadgeNumber(number: Int) {
        if (number in 0..99) {
            qBadgeView.badgeNumber = number//消息数
        } else if (number > 99) {
            qBadgeView.badgeText = "99+"
        }
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun setPortrait(bitmap: Bitmap?) {
        if (bitmap != null) {
            iv_portrait.setImageBitmap(bitmap)
            if (pageFrg != null) pageFrg?.setFrgPortrait(bitmap)
        }
    }

    override fun setUserName(userName: String?) {
        tv_userName.text = userName ?: ""
    }

    fun initRecycle(mMyChannelItems: ArrayList<CommonItem<*>>, mOtherChannelItems: ArrayList<CommonItem<*>>,
                    mTaskItems: CommonItem<*>?) {
        val manager = GridLayoutManager(this@HomeMenuActivity, 4)
        recycle_page.layoutManager = manager
        val helper = ItemTouchHelper(ItemDragHelperCallback())
        helper.attachToRecyclerView(recycle_page)

        adapter = HomeMenuAdapter(this@HomeMenuActivity, helper, mMyChannelItems, mOtherChannelItems, mTaskItems)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter?.getItemViewType(position)
                return if (viewType == HomeMenuAdapter.TYPE_MY || viewType == HomeMenuAdapter.TYPE_OTHER) 1 else 4
            }
        }
        adapter?.setTaskClickListener { position, remark ->
            when (position) {
                0 -> userAction(changeTask(remark)) //我的任务
                1, 2 -> userAction(remark)//1->常用 2->其他
            }
        }
        recycle_page.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    override fun getData(): MutableList<CommonItem<Any>>? = null

    override fun refreshData(commonItems: MutableList<CommonItem<Any>>?) {}

    override fun getUserPermission(): String = intent.getStringExtra("permissions") ?: ""

    override fun getRealName(): String = intent.getStringExtra("RealName") ?: ""

    override fun getCompany(): String = intent.getStringExtra("CompanyName") ?: ""

    override fun getTypeName(): String = intent.getStringExtra("TypeName") ?: ""

    override fun onClick(p0: View?) {
        when (p0) {
            iv_portrait -> drawer_layout.openDrawer(Gravity.LEFT)
        }
    }

    fun hideLeft() {
        drawer_layout.closeDrawers()
    }

    override fun addBank() {
        val intent = Intent(this, AddBankActivity::class.java)
        intent.putExtra("isCheckAdd", true)
        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
                drawer_layout.closeDrawers()
            } else {
//                moveTaskToBack(false)
                //方式二：返回手机的主屏幕
                val intent = Intent(Intent.ACTION_MAIN)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }

    //根据不同菜单进行跳转
    private fun userAction(type: String) {
        val intent: Intent
        when (type) {
            "8041"//未评分贷款
            -> {
                intent = Intent(this@HomeMenuActivity, LoanListActivity::class.java)
                intent.putExtra("contractType", 2)
                intent.putExtra("isEvaluation", true)
                startActivity(intent)
            }
            "8051"//未提交
            -> {
                intent = Intent(this@HomeMenuActivity, TaskListActivity::class.java)
                intent.putExtra("contractType", 3)
                intent.putExtra("Schedule", 1)
                startActivity(intent)
            }
            "8053"//三天未跟进贷款
            -> {
                intent = Intent(this@HomeMenuActivity, TaskListActivity::class.java)
                intent.putExtra("contractType", 3)
                intent.putExtra("DayNo", 1)
                startActivity(intent)
            }
            "8057"//七天未跟进贷款
            -> {
                intent = Intent(this@HomeMenuActivity, TaskListActivity::class.java)
                intent.putExtra("contractType", 3)
                intent.putExtra("DayNo", 2)
                startActivity(intent)
            }
            "8061"//门店经理未审核的成本任务
            -> {
                intent = Intent(this@HomeMenuActivity, CostListActivity::class.java)
                intent.putExtra("contractType", 4)
                intent.putExtra("AuditPos", 1)
                startActivity(intent)
            }
            "8062"//按揭经理未审核的成本任务
            -> {
                intent = Intent(this@HomeMenuActivity, CostListActivity::class.java)
                intent.putExtra("contractType", 4)
                intent.putExtra("AuditPos", 3)
                startActivity(intent)
            }
            "8063"//待报销成本数
            -> {
                intent = Intent(this@HomeMenuActivity, CostListActivity::class.java)
                intent.putExtra("contractType", 4)
                intent.putExtra("schedulePos", 3)
                startActivity(intent)
            }
            "8251"//有异议放款
            -> {
                intent = Intent(this@HomeMenuActivity, LendingListActivity::class.java)
                intent.putExtra("contractType", 25)
                intent.putExtra("Type", 1)
                startActivity(intent)
            }
            "801"//报单列表
            -> {
                intent = Intent(this@HomeMenuActivity, ContractListActivity::class.java)
                intent.putExtra("contractType", 0)
                startActivity(intent)
            }
            "802"//新增报单
            -> startActivity(Intent(this@HomeMenuActivity, AddPactActivity::class.java))
            "803"//合同列表
            -> {
                intent = Intent(this@HomeMenuActivity, ContractListActivity::class.java)
                intent.putExtra("contractType", 1)
                startActivity(intent)
            }
            "804"//贷款列表
            -> {
                intent = Intent(this@HomeMenuActivity, LoanListActivity::class.java)
                if ((application as MyApplication).MsgType == 2) {
                    intent.putExtra("Type", 1)
                }
                intent.putExtra("contractType", 2)
                startActivity(intent)
            }
            "805"//贷款更进
            -> {
                intent = Intent(this@HomeMenuActivity, TaskListActivity::class.java)
                intent.putExtra("contractType", 3)
                startActivity(intent)
            }
            "806"//成本列表
            -> {
                intent = Intent(this@HomeMenuActivity, CostListActivity::class.java)
                intent.putExtra("contractType", 4)
                startActivity(intent)
            }
            "807"//贷款结案
            -> {
                intent = Intent(this@HomeMenuActivity, LoanFliterActivity::class.java)
                intent.putExtra("contractType", 5)
                startActivity(intent)
            }
            "808"//经营报表
            -> {
                intent = Intent(this@HomeMenuActivity, ReportActivity::class.java)
                startActivity(intent)
            }
            "809"//拆借列表
            -> {
                intent = Intent(this@HomeMenuActivity, LoanFliterActivity::class.java)
                intent.putExtra("contractType", 6)
                startActivity(intent)
            }
            "810"//来访记录
            -> {
                intent = Intent(this@HomeMenuActivity, VisitRecordActivity::class.java)
                intent.putExtra("contractType", 7)
                startActivity(intent)
            }
            "811"//提成确认
            -> {
                intent = Intent(this@HomeMenuActivity, WageConfirmActivity::class.java)
                startActivity(intent)
            }
            "812"//合同资料查看
            -> {
                intent = Intent(this@HomeMenuActivity, PactDataActivity::class.java)
                intent.putExtra("isAudit", true)
                startActivity(intent)
            }
            "813"//按揭报表
            -> {
                intent = Intent(this@HomeMenuActivity, MortgageReportActivity::class.java)
                startActivity(intent)
            }
            "814"//按揭积分
            -> {
                intent = Intent(this@HomeMenuActivity, MortgageScoreActivity::class.java)
                startActivity(intent)
            }
            "815"//历史报表
            -> {
                intent = Intent(this@HomeMenuActivity, ReportChartActivity::class.java)
                startActivity(intent)
            }
            "816"//金账户
            -> presenter.hasGoldAccount()
            "817"//待转账回款
            -> {
                intent = Intent(this@HomeMenuActivity, TransferReceivableActivity::class.java)
                startActivity(intent)
            }
            "818"//还款提醒
            -> {
                intent = Intent(this@HomeMenuActivity, RemindListActivity::class.java)
                startActivity(intent)
            }
            "819"//贷款计算器
            -> {
                intent = Intent(this@HomeMenuActivity, CalculatorActivity::class.java)
                startActivity(intent)
            }
            "820"//代扣协议
            -> {
                intent = Intent(this@HomeMenuActivity, WithholdingList::class.java)
                startActivity(intent)
            }
            "821"//提成钱包
            -> {
                intent = Intent(this@HomeMenuActivity, WithdrawListActivity::class.java)
                startActivity(intent)
            }
            "822"//支出审核
            -> {
                intent = Intent(this@HomeMenuActivity, SpendingAuditListActivity::class.java)
                intent.putExtra("contractType", 22)
                startActivity(intent)
            }
            "823"//员工培训
            -> {
                intent = Intent(this@HomeMenuActivity, TrainingListActivity::class.java)
                intent.putExtra("contractType", 23)
                startActivity(intent)
            }
            "824"//回款审核
            -> {
                intent = Intent(this, ReturnedAuditListActivity::class.java)
                intent.putExtra("contractType", 44)
                startActivity(intent)
            }
            "825"//放款列表
            -> {
                intent = Intent(this@HomeMenuActivity, LendingListActivity::class.java)
                intent.putExtra("contractType", 25)
                startActivity(intent)
            }
            "826"//交易中心
            -> {
                intent = Intent(this@HomeMenuActivity, TransactionListActivity::class.java)
                intent.putExtra("contractType", 26)
                startActivity(intent)
            }
        }
    }

    private fun changeTask(taskType: String): String {
        var type = ""
        when (taskType) {
            "UserNoSubmitLoan" -> type = "8051"//提交按揭部
            "UserFollowLoan" -> type = "805"
            "UserPreReimburseCost" -> type = "8063"//待报销成本数
            "UserPreAuditCost" -> type = "8061" //门店经理未审核成本
            "MortgagePreAuditCost" -> type = "8062" //按揭经理未审核成本
            "UserPreLaunchLoan" -> type = "807"//门店财务发起申请
            "UserPreAuditLoan" -> type = "807"//贷款审核
            "Mortgager3DayNoSubmit" -> type = "8053"//三天未跟进贷款
            "Mortgager7DayNoSubmit" -> type = "8057"//七天未跟进贷款
            "SalesmanEvaluation" -> type = "8041"//未评分贷款
            "BankAdvanceForProblem" -> type = "8251"//有异议放款
        }
        return type
    }
}
