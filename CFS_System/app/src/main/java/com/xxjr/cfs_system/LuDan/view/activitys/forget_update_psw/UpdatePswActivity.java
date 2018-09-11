package com.xxjr.cfs_system.LuDan.view.activitys.forget_update_psw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.presenter.UpdatePswPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.UpdatePswInter;
import com.xxjr.cfs_system.main.LoginActivity;
import com.xxjr.cfs_system.main.MyApplication;
import com.xxjr.cfs_system.tools.ToastShow;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatePswActivity extends AppCompatActivity implements UpdatePswInter {
    private UpdatePswPresenter presenter;

    @Bind(R.id.et_old_psw)
    EditText etOldPsw;
    @Bind(R.id.et_new_psw)
    EditText etNewPsw;
    @Bind(R.id.et_new_psw_2)
    EditText etNewPsw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_psw);
        ButterKnife.bind(this);
        presenter = new UpdatePswPresenter();
        presenter.attach(this);
    }

    @OnClick({R.id.iv_back, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_confirm:
                if (presenter.check(getOldPsw(), getNowPsw(), getConfirmPsw())) {
                    presenter.getData(0, presenter.getUpdatePswParam(), true);
                }
                break;
        }
    }

    @Override
    public String getOldPsw() {
        return etOldPsw.getText().toString().trim();
    }

    @Override
    public String getNowPsw() {
        return etNewPsw.getText().toString().trim();
    }

    @Override
    public String getConfirmPsw() {
        return etNewPsw2.getText().toString().trim();
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(UpdatePswActivity.this, msg);
    }

    @Override
    public void complete() {
        ((MyApplication) getApplicationContext()).exit();
        startActivity(new Intent(UpdatePswActivity.this, LoginActivity.class));
        UpdatePswActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.deAttach();
    }
}
