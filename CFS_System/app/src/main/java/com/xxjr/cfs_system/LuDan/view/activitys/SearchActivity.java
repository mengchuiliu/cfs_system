package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.presenter.SearchPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.SearchVInter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import entity.ChooseType;
import entity.LoanInfo;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;

public class SearchActivity extends BaseActivity<SearchPresenter, SearchActivity> implements SearchVInter {
    private int type;
    CommonAdapter adapter;

    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.recycle_search)
    RecyclerView recycleSearch;
    @Bind(R.id.tv_add_manager)
    TextView addManager;
    @Bind(R.id.water)
    FrameLayout water;

    @Override
    protected SearchPresenter getPresenter() {
        return new SearchPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        type = getIntent().getIntExtra("type", 0);
        String hint = getIntent().getStringExtra("hintContent");
        if (!TextUtils.isEmpty(hint)) {
            etSearch.setHint(hint);
        }
        if (type == Constants.BANK_MANAGER_CODE) {
            addManager.setVisibility(View.VISIBLE);
            addManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchActivity.this, AddBankManagerActivity.class);
                    startActivityForResult(intent, 99);
                }
            });
        }
        presenter.setDefaultValue();
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(SearchActivity.this, msg);
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                SearchActivity.this.finish();
                break;
        }
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getBankID() {
        return getIntent().getExtras().getInt("bankId");
    }

    @Override
    public String getAisleType() {
        return getIntent().getExtras().getString("aisleType");
    }

    @Override
    public String getCompanyId() {
        return getIntent().getExtras().getString("CompanyID");
    }

    @Override
    public boolean getSubmit() {
        return getIntent().getExtras().getBoolean("isSubmit", false);
    }

    @Override
    public void initRecycler(List<ChooseType> chooseTypes) {
        recycleSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        adapter = new CommonAdapter<ChooseType>(SearchActivity.this, chooseTypes, R.layout.item_search) {
            @Override
            protected void convert(BaseViewHolder holder, final ChooseType chooseType, int position) {
                holder.setText(R.id.tv_search, chooseType.getContent());
                holder.setOnClickListener(R.id.tv_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getSubmit()) {
                            LoanInfo loanInfo = (LoanInfo) getIntent().getSerializableExtra("loanInfo");
                            presenter.submitMortgage(loanInfo.getLoanId(), String.valueOf(chooseType.getId()));
                        } else {
                            RxBus.getInstance().post(type, chooseType);
                            SearchActivity.this.finish();
                        }
                    }
                });
            }
        };
        recycleSearch.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshData(List<ChooseType> listData) {
        adapter.setNewData(listData);
    }

    @Override
    public void setEditChageListener(TextWatcher textWatcher) {
        etSearch.addTextChangedListener(textWatcher);
    }

    @Override
    public void complete() {
        setResult(99);
        SearchActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == 99) {
            presenter.setDefaultValue();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWater(water);
    }
}
