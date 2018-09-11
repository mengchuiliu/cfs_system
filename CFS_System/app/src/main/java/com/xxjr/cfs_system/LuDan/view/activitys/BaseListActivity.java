package com.xxjr.cfs_system.LuDan.view.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.BaseLsitVInter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.CFSUtils;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.DateUtil;
import com.xxjr.cfs_system.tools.ToastShow;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import refresh_recyclerview.PullToRefreshRecyclerView;
import rvadapter.CommonAdapter;
import timeselector.TimesChoose;

/**
 * Created by Administrator on 2017/9/6.
 * 列表基类
 */

public abstract class BaseListActivity<P extends BasePresenter, V extends BaseLsitVInter> extends BaseActivity<P, V> implements BaseLsitVInter {
    protected int contractType = 0;
    protected TimesChoose timesChoose;
    protected PopupMenuView mPopupMenuView;
    protected int searchType = 0;//查询条件类型 0->所有,1->姓名，2->编号
    protected String searchCompanyId = "";//门店id
    protected int page = 0;//查询页数
    protected String chooseTime1, chooseTime2;
    protected boolean isPull = false;//true->上拉加载，false->下拉刷新
    protected CommonAdapter adapter;
    protected CommonAdapter adapter0;
    protected CommonAdapter adapter1;
    protected CommonAdapter adapter2;

    @Bind(R.id.tv_search_type)
    protected TextView tvSearchType;
    @Bind(R.id.et_pact_search)
    protected EditText etPactSearch;
    @Bind(R.id.iv_date)
    protected ImageView ivDate;
    @Bind(R.id.rv_title_0)
    RecyclerView rvTitle0;
    @Bind(R.id.rv_title_1)
    RecyclerView rvTitle1;
    @Bind(R.id.rv_title_2)
    RecyclerView rvTitle2;
    @Bind(R.id.line_0)
    View line0;
    @Bind(R.id.line1)
    View line1;
    @Bind(R.id.line2)
    View line2;
    @Bind(R.id.recycle_home)
    PullToRefreshRecyclerView recycleHome;
    @Bind(R.id.water)
    FrameLayout water;
    @Bind(R.id.iv_add)
    protected ImageView ivAdd;

    LinearLayout llVisitTime;
    protected TextView visiTime;

    @Override
    protected P getPresenter() {
        return getListPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        contractType = getIntent().getIntExtra("contractType", 0);
        if (contractType == 7) {
            initVisit();
        }
        initPopMenu();//初始化菜单选择
        initAdapter();//初始化adapter
        initRecycler();//初始化列表
        initEdit();//初始化搜索监听
        initTimeChoose();//初始化时间选择器
        presenter.setDefaultValue();
    }

    protected abstract P getListPresenter();

    protected abstract void initAdapter();

    protected abstract void refreshData(int page, int searchType);

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(getApplicationContext(), msg);
    }

    @Override
    public int getListType() {
        return contractType;
    }

    @Override
    public String getSearchContent() {
        return etPactSearch.getText().toString().trim();
    }

    @Override
    public String getSearchCompany() {
        return searchCompanyId;
    }

    public void cleanET() {
        etPactSearch.setText("");
    }

    @Override
    public String getChooseTime1() {
        return chooseTime1;
    }

    @Override
    public String getChooseTime2() {
        return chooseTime2;
    }

    @Override
    public boolean getPull() {
        return isPull;
    }

    @Override
    public void refreshChange() {
    }

    @Override
    public void completeRefresh() {
        if (isPull) {
            recycleHome.completeLoadMore();
        } else {
            recycleHome.completeRefresh();
        }
    }

    //初始化菜单选择器
    private void initPopMenu() {
        mPopupMenuView = new PopupMenuView(this);
        mPopupMenuView.getPopLayout().setRadiusSize(15);
        mPopupMenuView.setOrientation(LinearLayout.VERTICAL);
        mPopupMenuView.setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
            @Override
            public boolean onOptionMenuClick(int position, OptionMenu menu) {
                optionMenusClick(menu);
                return true;
            }
        });
        mPopupMenuView.setMenuItems(getPopMenuList());
    }

    //获取选择菜单
    protected List<OptionMenu> getPopMenuList() {
        List<OptionMenu> optionMenus = new ArrayList<>();
        OptionMenu menu;
        for (int i = 1; i < 3; i++) {
            menu = new OptionMenu();
            menu.setId(i);
            switch (i) {
                case 1:
                    menu.setTitle("客户名");
                    break;
                case 2:
                    if (contractType == 0 || contractType == 1) {
                        menu.setTitle("合同号");
                    } else if (contractType == 7) {
                        menu.setTitle("门店名");
                    } else {
                        menu.setTitle("贷款号");
                    }
                    break;
            }
            optionMenus.add(menu);
        }
        if (contractType != 7) {
            OptionMenu menu1 = new OptionMenu();
            menu1.setId(3);
            menu1.setTitle("门店名");
            optionMenus.add(menu1);
        }
        return optionMenus;
    }

    //菜单点击事件
    protected void optionMenusClick(OptionMenu menu) {
        searchType = menu.getId();
        etPactSearch.setText("");
        switch (menu.getId()) {
            case 1:
                tvSearchType.setText("客户名");
                break;
            case 2:
                if (contractType == 0 || contractType == 1) {
                    tvSearchType.setText("合同号");
                } else if (contractType == 7) {
                    tvSearchType.setText("门店名");
                } else {
                    tvSearchType.setText("贷款号");
                }
                break;
            case 3:
                tvSearchType.setText("门店名");
                break;
        }
    }

    //来访时间选择
    private void initVisit() {
        ViewStub stub = findViewById(R.id.visit_view_stub);
        stub.inflate();
        View visitView = findViewById(R.id.visit_view);
        llVisitTime = visitView.findViewById(R.id.ll_visit_time);
        visiTime = visitView.findViewById(R.id.tv_visit_time);
        ImageView iv_visit_time = visitView.findViewById(R.id.iv_visit_time);

        iv_visit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timesChoose != null) {
                    timesChoose.show(etPactSearch);
                } else {
                    initTimeChoose();
                    timesChoose.show(etPactSearch);
                }
            }
        });
        ivAdd.setVisibility(View.VISIBLE);
        ivDate.setVisibility(View.GONE);
        llVisitTime.setVisibility(View.VISIBLE);
    }

    //搜索监听
    @SuppressLint("ClickableViewAccessibility")
    private void initEdit() {
        etPactSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (contractType == 22 || contractType == 44) searchType = 3;
                if (searchType == 3 && motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(BaseListActivity.this, SearchActivity.class);
                    intent.putExtra("type", Constants.Company_Choose);
                    intent.putExtra("hintContent", "搜索门店");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        etPactSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    page = 0;
                    isPull = false;
                    if (searchType == 0) {
                        searchType = 1;
                    }
                    refreshData(page, searchType);
                    return true;
                }
                return false;
            }
        });
    }

    //时间选择器
    private void initTimeChoose() {
        timesChoose = new TimesChoose(getApplicationContext(), new TimesChoose.TimeResultHandler() {
            @Override
            public void handle(String time, String endtime) {
                page = 0;
                isPull = false;
                chooseTime1 = time;
                chooseTime2 = endtime;
                if (contractType == 7) {
                    String visitT = Utils.FormatTime(time, "yyyy-MM-dd", "yyyy.MM.dd") + "~" +
                            Utils.FormatTime(endtime, "yyyy-MM-dd", "yyyy.MM.dd");
                    visiTime.setText(visitT);
                }
                refreshData(page, searchType);
            }
        }, "1900-01-01", DateUtil.getCurDate());
        timesChoose.setScrollUnit(TimesChoose.SCROLLTYPE.YEAR, TimesChoose.SCROLLTYPE.MONTH, TimesChoose.SCROLLTYPE.DAY);
    }

    protected void initRecycler() {
        switch (contractType) {//设置间距
            case 230://培训员工列表TrainingDetailActivity
                ivDate.setVisibility(View.GONE);
                break;
            case 23:
            case 25:
            case 26:
                recycleHome.addItemDecoration(8);
                break;
            default:
                recycleHome.addItemDecoration(1);
                break;
        }
        recycleHome.setAdapter(adapter);
        recycleHome.setOnRefreshListener(new PullToRefreshRecyclerView.OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isPull = false;
                        if (contractType == 26) tvSearchType.setText("流水号");
                        else tvSearchType.setText("客户名");
                        etPactSearch.setText("");
                        page = 0;
                        searchType = 0;
                        searchCompanyId = "";
                        chooseTime1 = "";
                        chooseTime2 = "";
                        if (contractType == 7) {
                            visiTime.setText("");
                        }
                        refreshData(page, searchType);
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isPull = true;
                        page++;
                        refreshData(page, searchType);
                    }
                }, 1000);
            }
        });

        //不同界面显示不同标题
        switch (contractType) {
            case 2:
            case 7:
            case 22:
            case 25:
                showTitle0();
                break;
            case 23:
                List<String> permits23 = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "823");
                if (permits23 != null && permits23.contains("EH")) {
                    showTitle0();
                }
                break;
            case 3:
                showTitle0();
                showTitle1();
                showTitle2();
                break;
            case 4:
            case 44:
                showTitle0();
                showTitle1();
                break;
        }
    }

    private void showTitle0() {
        rvTitle0.setVisibility(View.VISIBLE);
        line0.setVisibility(View.VISIBLE);
        if (contractType == 2 || contractType == 23) {
            rvTitle0.setLayoutManager(new GridLayoutManager(BaseListActivity.this, 2));
        } else {
            rvTitle0.setLayoutManager(new LinearLayoutManager(BaseListActivity.this, LinearLayoutManager.HORIZONTAL, false));
        }
        rvTitle0.setAdapter(adapter0);
    }

    private void showTitle1() {
        rvTitle1.setVisibility(View.VISIBLE);
        line1.setVisibility(View.VISIBLE);
        rvTitle1.setLayoutManager(new LinearLayoutManager(BaseListActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvTitle1.setAdapter(adapter1);
    }

    private void showTitle2() {
        rvTitle2.setVisibility(View.VISIBLE);
        line2.setVisibility(View.VISIBLE);
        rvTitle2.setLayoutManager(new LinearLayoutManager(BaseListActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvTitle2.setAdapter(adapter2);
    }

    public void scrollTop() {
        rvTitle2.smoothScrollToPosition(0);
    }

    @OnClick({R.id.iv_back, R.id.tv_search_type, R.id.iv_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.tv_search_type:
                mPopupMenuView.show(tvSearchType);
                break;
            case R.id.iv_date:
                if (timesChoose != null) {
                    timesChoose.show(etPactSearch);
                } else {
                    initTimeChoose();
                    timesChoose.show(etPactSearch);
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWater(water);
    }
}
