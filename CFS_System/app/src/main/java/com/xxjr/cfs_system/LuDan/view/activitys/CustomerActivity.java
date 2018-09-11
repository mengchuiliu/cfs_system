package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.presenter.CustomerPresenter;
import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import refresh_recyclerview.DividerItemDecoration;

import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.ToastShow;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import entity.ChooseType;
import entity.ClientInfo;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;

public class CustomerActivity extends BaseActivity<CustomerPresenter, BaseViewInter> implements BaseViewInter {
    List<ChooseType> list = new ArrayList<>();
    CommonAdapter adapter;

    @Bind(R.id.recycle_customer)
    RecyclerView recycleCustomer;

    @Override
    protected CustomerPresenter getPresenter() {
        return new CustomerPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_customer;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getToolbarTitle().setText("申请客户");
        TextView textView = getSubTitle();
        textView.setText("完成");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder customerIds = new StringBuilder();
                StringBuilder customerNames = new StringBuilder();
                ChooseType type;
                for (int i = 0; i < list.size(); i++) {
                    type = list.get(i);
                    if (type.isChoose()) {
                        customerIds.append(type.getId()).append(",");
                        customerNames.append(type.getContent()).append(",");
                    }
                }
                String ids = "";
                String names = "";
                if (customerIds.length() > 1 && customerNames.length() > 1) {
                    ids = customerIds.substring(0, customerIds.length() - 1);
                    names = customerNames.substring(0, customerNames.length() - 1);
                }
                Intent intent = new Intent();
                intent.putExtra("chooseType", new ChooseType(ids, names));
                setResult(11, intent);
                CustomerActivity.this.finish();
            }
        });
        presenter.setDefaultValue();
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    public void initRecycler() {
        recycleCustomer.setLayoutManager(new LinearLayoutManager(CustomerActivity.this));
        recycleCustomer.addItemDecoration(new DividerItemDecoration(CustomerActivity.this, DividerItemDecoration.VERTICAL_LIST));
        adapter = new CommonAdapter<ChooseType>(CustomerActivity.this, list, R.layout.item_recycler_customer) {
            @Override
            protected void convert(BaseViewHolder holder, final ChooseType chooseType, final int position) {
                holder.setText(R.id.tv_customer_choose, chooseType.getContent().trim());
                if (chooseType.isChoose()) {
                    holder.setVisible(R.id.iv_ok, true);
                } else {
                    holder.setVisible(R.id.iv_ok, false);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getIntent().getBooleanExtra("isOne", false)) {
                            Intent intent = new Intent();
                            intent.putExtra("chooseType", new ChooseType(String.valueOf(chooseType.getId()), chooseType.getContent()));
                            setResult(11, intent);
                            CustomerActivity.this.finish();
                        } else {
                            if (chooseType.isChoose()) {
                                list.get(position).setChoose(false);
                                chooseType.setChoose(false);
                            } else {
                                list.get(position).setChoose(true);
                                chooseType.setChoose(true);
                            }
                            adapter.notifyItemChanged(position, chooseType);
                        }
                    }
                });
            }
        };
        recycleCustomer.setAdapter(adapter);
    }

    public List<ClientInfo> getInfos() {
        return (List<ClientInfo>) (getIntent().getSerializableExtra("CustomerInfo"));
    }

    public List<ChooseType> getList() {
        return list;
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(getApplicationContext(), msg);
    }
}
