package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.widgets.CustomDialog;
import com.xxjr.cfs_system.LuDan.adapters.AddLoanAdapter;
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener;
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener;
import com.xxjr.cfs_system.LuDan.presenter.AddLoanPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.AddLoanVInter;
import com.xxjr.cfs_system.ViewsHolder.PopChoose;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import entity.ChooseType;
import entity.ClientInfo;
import entity.CommonItem;
import entity.LoanInfo;

public class AddLoanActivity extends BaseActivity<AddLoanPresenter, AddLoanActivity> implements AddLoanVInter {
    private LoanInfo loanInfo;
    private AddLoanAdapter adapter;
    private PopupWindow popWindow;
    boolean isUpdate = false, isMortgage = false, save = false;
    private int isSubmit = 0;//0-->不提交,1--->提交

    @Bind(R.id.recycler_add_loan)
    RecyclerView recyclerAddLoan;
    @Bind(R.id.water)
    FrameLayout water;

    @Override
    protected AddLoanPresenter getPresenter() {
        return new AddLoanPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_loan;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getToolbarTitle().setText("贷款信息");
        TextView textView = getSubTitle();
        textView.setText("保存");
        loanInfo = (LoanInfo) getIntent().getSerializableExtra("loanInfo");
        if (loanInfo == null) {
            isUpdate = false;
            loanInfo = new LoanInfo();
            List<ClientInfo> infos = (List<ClientInfo>) getIntent().getSerializableExtra("CustomerInfo");
            if (infos.size() == 1) {
                loanInfo.setCustomer(infos.get(0).getName());
                loanInfo.setCustomerId(String.valueOf(infos.get(0).getUserID()));
            }
//            String userType = Hawk.get("UserType", "");
//            if (userType.equals("18") || userType.equals("22") || userType.equals("28") || userType.equals("30")) {
//                isMortgage = true;
//                loanInfo.setMortgage(Integer.valueOf(Hawk.get("UserID", "0")));
//                loanInfo.setMortgageName(Hawk.get("UserRealName", ""));
//            }
        } else {
            save = true;
            isUpdate = true;
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter.check()) {
                    if (isMortgage) {
                        isSubmit = 1;
                        post();
                    } else {
                        if (getScheduleUp()) {
                            CustomDialog.showTwoButtonDialog(AddLoanActivity.this, "是否保存贷款信息?", "保存", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    isSubmit = 0;
                                    post();
                                }
                            });
                        } else {
                            CustomDialog.showTwoButtonDialog(AddLoanActivity.this, "保存贷款信息,是否提交到按揭部?\n注:提交按揭部后，贷款信息将不能修改", "保存不提交", "提交按揭部", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    isSubmit = 0;
                                    post();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    isSubmit = 1;
                                    if (loanInfo.getMortgage() != 0) {
                                        post();
                                    } else {
                                        ToastShow.showShort(AddLoanActivity.this, "请先选择按揭员!");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        presenter.setDefaultValue();
    }

    private void post() {
        int pactID = getIntent().getIntExtra("pactID", 0);
        int managerId = getIntent().getIntExtra("managerId", 0);
        String loanId = getIntent().getStringExtra("loanId");
        String content = getIntent().getStringExtra("content");
        String companyID = getIntent().getStringExtra("CompanyID");
        String pactNumberId = getIntent().getStringExtra("pactCode");
        presenter.addOrUpdateLoan(isUpdate, pactID, managerId, loanId, content, companyID, pactNumberId);
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    public void onBackPressed() {
        isSave(save, AddLoanActivity.this);
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(getApplicationContext(), msg);
    }

    @Override
    public LoanInfo getLoanInfo() {
        return loanInfo;
    }

    @Override
    public boolean getScheduleUp() {
        return getIntent().getBooleanExtra("scheduleUp", false);
    }

    @Override
    public void initRecycler(List<CommonItem> commonItems) {
        recyclerAddLoan.setLayoutManager(new LinearLayoutManager(AddLoanActivity.this));
        adapter = new AddLoanAdapter(AddLoanActivity.this, commonItems);
        adapter.setItemClickListener(itemClickListener);
        adapter.setTextChangeListener(new TextChangeListener() {
            @Override
            public void setTextChage(int position, String text) {
                if (position == 3) {
                    if (!TextUtils.isEmpty(text)) {
                        loanInfo.setAmount(Utils.getBigLong(getDefaultInt(text.replaceAll(",", "").trim())));
                    } else {
                        loanInfo.setAmount(0);
                    }
                } else {
                    loanInfo.setNote(text);
                }
            }
        });
        recyclerAddLoan.setAdapter(adapter);
    }

    @Override
    public void refreshData(int position, String text) {
        CommonItem item = adapter.getDatas().get(position);
        item.setContent(text);
        adapter.notifyItemChanged(position, item);
    }

    @Override
    public void hidePop() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    @Override
    public boolean getIsUpdate() {
        return isUpdate;
    }

    @Override
    public int getSubmit() {
        return isSubmit;
    }

    @Override
    public void completeOver(String loanId) {
        String loanIds = getIntent().getStringExtra("loanId");
        String content = getIntent().getStringExtra("content");
        ChooseType chooseType = new ChooseType();
        if (isUpdate) {//点击编辑修改
            if (getScheduleUp()) {
                setResult(111);
            } else {
                chooseType.setContent(presenter.getDescriptions(loanIds, content));
                Intent postdata = new Intent();
                postdata.putExtra("postdata", chooseType);
                setResult(66, postdata);
            }
        } else {//点击添加
            Intent intent = getIntent();
            String ids;
            if (TextUtils.isEmpty(intent.getStringExtra("loanId"))) {
                ids = loanId;
            } else {
                ids = intent.getStringExtra("loanId") + "," + loanId;
            }
            chooseType.setIds(ids);
            chooseType.setContent(presenter.getDescriptions(loanIds, content));
            Intent postdata = new Intent();
            postdata.putExtra("postdata", chooseType);
            setResult(88, postdata);
        }
        AddLoanActivity.this.finish();
    }

    //点击监听
    RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Intent intent;
            switch (position) {
                case 0:
                    intent = new Intent(AddLoanActivity.this, SearchActivity.class);
                    intent.putExtra("type", Constants.BANK_CODE);
                    intent.putExtra("hintContent", "搜索银行");
                    startActivity(intent);
                    break;
                case 1:
                    if (loanInfo.getBankId() == 0) {
                        showMsg("请先选择申请银行!");
                        return;
                    }
                    intent = new Intent(AddLoanActivity.this, SearchActivity.class);
                    intent.putExtra("type", Constants.BANK_PRODUCT_CODE);
                    intent.putExtra("hintContent", "搜索银行产品");
                    intent.putExtra("bankId", loanInfo.getBankId());
                    startActivity(intent);
                    break;
                case 2:
                    String[] strings = presenter.getLoanTypes().split(",");
                    popWindow = PopChoose.showChooseType(AddLoanActivity.this, recyclerAddLoan, "贷款类型",
                            getTypes(Utils.getTypeDataList("LoansType"), strings), Constants.LOAN_TYPE_CODE, false);
                    break;
                case 4:
                    intent = new Intent(AddLoanActivity.this, CustomerActivity.class);
                    intent.putExtra("CustomerInfo", getIntent().getSerializableExtra("CustomerInfo"));
                    startActivityForResult(intent, 1);
                    break;
                case 5:
                    intent = new Intent(AddLoanActivity.this, SearchActivity.class);
                    intent.putExtra("type", Constants.MORTGAGE_CODE);
                    intent.putExtra("hintContent", "搜索按揭员");
                    startActivity(intent);
                    break;
            }
        }
    };

    private List<ChooseType> getTypes(List<ChooseType> loanTypes, String[] strings) {
        List<ChooseType> list = new ArrayList<>();
        for (String type : strings) {
            if (!TextUtils.isEmpty(type)) {
                for (ChooseType chooseType : loanTypes) {
                    if (chooseType.getId() == Integer.valueOf(type)) {
                        list.add(chooseType);
                        break;
                    }
                }
            }
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 11) {
            ChooseType chooseType = (ChooseType) data.getSerializableExtra("chooseType");
            if (chooseType != null) {
                loanInfo.setCustomer(chooseType.getContent());
                loanInfo.setCustomerId(chooseType.getIds());
                refreshData(4, chooseType.getContent());
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        setWater(water);
    }

    @Override
    protected void onDestroy() {
        presenter.rxDeAttach();
        super.onDestroy();
    }
}
