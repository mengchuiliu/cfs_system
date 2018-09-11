package com.xxjr.cfs_system.LuDan.view.activitys;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.presenter.RemarkPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.RemarkVInter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;

import butterknife.Bind;
import entity.LoanInfo;

public class RemarkActivity extends BaseActivity<RemarkPresenter, RemarkVInter> implements RemarkVInter {
    private LoanInfo loanInfo;
    private int remarkType;

    @Bind(R.id.ll_content)
    LinearLayout llContent;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.v_line)
    View vLine;
    @Bind(R.id.et_yellow_pact)
    EditText etYellowPact;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.water)
    FrameLayout water;

    @Override
    protected RemarkPresenter getPresenter() {
        return new RemarkPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_remark;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        loanInfo = (LoanInfo) getIntent().getSerializableExtra("loanInfo");
        remarkType = getIntent().getIntExtra("remarkType", 0);
        switch (remarkType) {
            case 0:
                getToolbarTitle().setText("黄单");
                break;
            case 1:
                getToolbarTitle().setText("备注");
                etYellowPact.setHint("跟踪进度备注");
                break;
            case 2:
                getToolbarTitle().setText("提前还款");
                etYellowPact.setHint("还款备注");
                break;
            case 3://放款后回退
                getToolbarTitle().setText("贷款进度回退");
                etYellowPact.setHint("回退备注");
                llContent.setVisibility(View.VISIBLE);
                vLine.setVisibility(View.VISIBLE);
                presenter.initSub();
                break;
        }
        setEdit();
        submit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWater(water);
    }

    //提交数据
    private void submit() {
        TextView textView = getSubTitle();
        textView.setText("提交");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.setDefaultValue();
            }
        });
    }

    private void setEdit() {
        etYellowPact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    tvNum.setText(charSequence.length() + "/100");
                } else {
                    tvNum.setText("0/100");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showPop(view);
            }
        });
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(getApplicationContext(), msg);
    }

    @Override
    public LoanInfo getLoanInfo() {
        return loanInfo == null ? new LoanInfo() : loanInfo;
    }

    @Override
    public int getRemarkType() {
        return remarkType;
    }

    @Override
    public String getRemark() {
        return etYellowPact.getText().toString().trim();
    }

    @Override
    public int getYellowType() {
        return getIntent().getIntExtra("yellowSchedule", -3);
    }

    @Override
    public void setTextContent(String text) {
        tvContent.setText(text);
    }

    @Override
    public void complete() {
        showMsg("提交成功!");
        switch (remarkType) {
            case 0:
                setResult(Constants.REQUEST_CODE_YELLOW);
                break;
            case 1:
                setResult(Constants.REQUEST_CODE_REMARK);
                break;
            case 2:
                setResult(Constants.REQUEST_CODE_PREPAYMENT);
                break;
            case 3:
                setResult(Constants.REQUEST_LENDING_BACK);//放款回退
                break;
        }
        RemarkActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        presenter.rxDeAttach();
        super.onDestroy();
    }
}
