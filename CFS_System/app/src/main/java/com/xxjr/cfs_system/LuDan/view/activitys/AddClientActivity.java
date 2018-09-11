package com.xxjr.cfs_system.LuDan.view.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.adapters.ClientAdapter;
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener;
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener;
import com.xxjr.cfs_system.LuDan.presenter.AddClientPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.AddClientVInter;
import com.xxjr.cfs_system.ViewsHolder.PopChoose;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;
import com.xxjr.cfs_system.tools.Utils;

import java.util.List;

import butterknife.Bind;
import entity.ClientInfo;
import entity.CommonItem;

public class AddClientActivity extends BaseActivity<AddClientPresenter, AddClientActivity> implements AddClientVInter {
    private PopupWindow popWindow;
    ClientInfo clientInfo;
    ClientAdapter adapter;

    @Bind(R.id.recycler_add_loan)
    RecyclerView recycleAddClient;
    @Bind(R.id.water)
    FrameLayout water;


    @Override
    protected AddClientPresenter getPresenter() {
        return new AddClientPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_loan;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getToolbarTitle().setText("新增客户");
        TextView textView = getSubTitle();
        textView.setText("保存");
        final List<ClientInfo> infos = (List<ClientInfo>) getIntent().getSerializableExtra("infos");
        final int position = getIntent().getIntExtra("position", -1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter.check(clientInfo)) {
                    for (int i = 0; i < infos.size(); i++) {
                        ClientInfo info = infos.get(i);
                        if (clientInfo.getIdCode().equals(info.getIdCode())) {
                            if (position == -1) {
                                showMsg("贷款客户已存在，禁止重复添加");
                                return;
                            } else {
                                if (position != i) {
                                    showMsg("贷款客户已存在，禁止重复添加");
                                    return;
                                }
                            }
                        }
                    }
                    Intent intent = new Intent();
                    if (position != -1) {
                        intent.putExtra("position", position);
                    }
                    intent.putExtra("addClient", clientInfo);
                    setResult(6, intent);
                    AddClientActivity.this.finish();
                }
            }
        });

        if (position == -1) {
            clientInfo = new ClientInfo();
        } else {
            clientInfo = infos.get(position);
        }
        presenter.setDefaultValue();
    }

    @Override
    public ClientInfo getClient() {
        return clientInfo;
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(this, msg);
    }

    @Override
    public void initRecycler(List<CommonItem> commonItems) {
        recycleAddClient.setLayoutManager(new LinearLayoutManager(AddClientActivity.this));
        adapter = new ClientAdapter(AddClientActivity.this, commonItems);
        adapter.setItemClickListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showCardType();
            }
        });
        adapter.setTextChangeListener(new TextChangeListener() {
            @Override
            public void setTextChage(int position, String text) {
                switch (position) {
                    case 0:
                        clientInfo.setName(text);
                        break;
                    case 1:
                        clientInfo.setMobile(text);
                        break;
                    case 3:
                        clientInfo.setIdCode(text);
                        break;
                }
            }
        });
        recycleAddClient.setAdapter(adapter);
    }

    @Override
    public void refreshData(int position, CommonItem commonItem) {
        CommonItem item = adapter.getDatas().get(position);
        item.setContent(commonItem.getContent());
        adapter.notifyItemChanged(position, item);
    }

    private void showCardType() {
        if (popWindow == null) {
            popWindow = PopChoose.showChooseType(AddClientActivity.this, recycleAddClient, "证件类型",
                    Utils.getTypeDataList("IDType"), Constants.CARD_CODE, false);
        } else {
            popWindow.showAtLocation(recycleAddClient, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void hidePop() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWater(water);
    }

    @Override
    protected void onDestroy() {
        popWindow = null;
        presenter.rxDeAttach();
        super.onDestroy();
    }
}
