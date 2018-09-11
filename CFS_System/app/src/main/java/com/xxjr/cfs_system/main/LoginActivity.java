package com.xxjr.cfs_system.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.widgets.CustomDialog;
import com.xxjr.cfs_system.LuDan.presenter.LoginPresenter;
import com.xxjr.cfs_system.LuDan.presenter.SignUpPresenter;
import com.xxjr.cfs_system.LuDan.view.activitys.HomeMenuActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.SignActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.forget_update_psw.ForgetPswActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.forget_update_psw.UpdatePswActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.SignUpActivity;
import com.xxjr.cfs_system.LuDan.view.viewinter.LoginVInter;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yinglan.keyboard.HideUtil;
import com.zyyoona7.lock.GestureLockLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginVInter {
    LoginPresenter presenter;
    Dialog dialog;//数据加载框
    boolean isSuccess = false;
    String versionName = "";
    String versionCode = "";
    private Handler mHandler = new Handler();

    @Bind(R.id.et_account)
    EditText etAccount;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.iv_account_clean)
    ImageView ivAccountClean;
    @Bind(R.id.iv_psw_clean)
    ImageView ivPswClean;
    @Bind(R.id.tv_version)
    TextView tvVersion;
    @Bind(R.id.ll_common_login)
    LinearLayout llLogin;
    @Bind(R.id.gesture_view_login)
    View gestureView;
    @Bind(R.id.gesture_lock_view)
    GestureLockLayout mGestureLockLayout;
    @Bind(R.id.gesture_num)
    TextView gestureNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        HideUtil.init(this);
//        initGesture();//手势密码
        initPresenter();
        etAccount.setOnFocusChangeListener(onFocusChangeListener);
        etPassword.setOnFocusChangeListener(onFocusChangeListener);
    }

    //设置手势密码
    private void initGesture() {
        boolean isUnlock = Hawk.get(Hawk.get("Account", "") + "_isUnlock", false);
        if (isUnlock) {
            gestureView.setVisibility(View.VISIBLE);
            llLogin.setVisibility(View.GONE);
            mGestureLockLayout.setDotCount(3);
            mGestureLockLayout.setMode(GestureLockLayout.VERIFY_MODE);
            mGestureLockLayout.setTryTimes(5);
            mGestureLockLayout.setAnswer(Hawk.get(Hawk.get("Account", "") + "_answer", "123"));
            mGestureLockLayout.setOnLockVerifyListener(new GestureLockLayout.OnLockVerifyListener() {
                @Override
                public void onGestureSelected(int id) {
                    //每选中一个点时调用
                }

                @Override
                public void onGestureFinished(boolean isMatched) {
                    //绘制手势解锁完成时调用
                    if (isMatched) {
                        login();
                    } else {
                        //不匹配
                        gestureNum.setText("您还有" + mGestureLockLayout.getTryTimes() + "次机会");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mGestureLockLayout.resetGesture();
                            }
                        }, 200);
                    }
                }

                @Override
                public void onGestureTryTimesBoundary() {
                    //超出最大尝试次数时调用
                    showMsg("超出密码绘制次数，忘记密码请登录后重置");
                    llLogin.setVisibility(View.VISIBLE);
                    gestureView.setVisibility(View.GONE);
                }
            });
        } else {
            llLogin.setVisibility(View.VISIBLE);
            gestureView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setDefaultValue();
    }

    private void initPresenter() {
        presenter = new LoginPresenter();
        presenter.attach(this);
        getVersionName();
        AndPermission.with(this)
                .requestCode(Constants.REQUEST_CODE_PERMISSION_SD)
                .permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(presenter.getPermissioner())
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(LoginActivity.this, rationale).show();
                    }
                })
                .start();
    }

    private void getVersionName() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo;
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = String.valueOf(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        presenter.setVersionName(versionName, versionCode);
    }

    @Override
    public void showLoading() {
        dialog = CustomDialog.createLoadingDialog(LoginActivity.this, "加载中...");
        if (dialog != null) {
            dialog.show();
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                    dialogInterface.dismiss();
                    dialog = null;
                    return false;
                } else {
                    return true;
                }
            }
        });
    }

    @Override
    public void hideLoading() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void showMsg(String msg) {
        isSuccess = false;
        ToastShow.showShort(getApplicationContext(), msg);
    }

    @Override
    public void complete(String permissions, String typeName, String company, String realName, String UserBirthday, String returnStrings) {
        isSuccess = false;
        JSONArray jsonArray = JSONArray.parseArray(returnStrings);
        Intent intent;
        String sign = "";
        String phone = "";
        if (jsonArray != null && jsonArray.size() >= 2) {
            sign = jsonArray.getString(1);
            phone = jsonArray.getString(0);
            if ("False".equals(sign)) {
//                intent = new Intent(LoginActivity.this, HomePageActivity.class);
                intent = new Intent(LoginActivity.this, HomeMenuActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, SignActivity.class);
            }
        } else {
//            intent = new Intent(LoginActivity.this, HomePageActivity.class);
            intent = new Intent(LoginActivity.this, HomeMenuActivity.class);
        }
        intent.putExtra("permissions", permissions);
        intent.putExtra("RealName", realName);
        intent.putExtra("UserBirthday", UserBirthday);
        intent.putExtra("CompanyName", company);
        intent.putExtra("TypeName", typeName);
        intent.putExtra("versionName", versionName);
        if (TextUtils.isEmpty(sign) || sign.equals("False")) {
            showMsg("登录成功，数据加载完成");
            startActivity(intent);
            LoginActivity.this.finish();
        } else {
            if (TextUtils.isEmpty(phone)) {
                showMsg("手机号不正确，请联系管理员修改手机号!");
            } else {
                intent.putExtra("returnStrings", returnStrings);
                intent.putExtra("isLogin", true);
                startActivityForResult(intent, 66);
            }
        }
    }

    @Override
    public void showUpdatePswDialog() {
        isSuccess = false;
        CustomDialog.showTwoButtonDialog(LoginActivity.this, "为了您的账户安全，请修改初始密码后再行操作?", "确定", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivity(new Intent(LoginActivity.this, UpdatePswActivity.class));
            }
        });
    }

    @Override
    public void setUserName(String userName) {
        etAccount.setText(userName);
    }

    @Override
    public void setPassword(String password) {
        etPassword.setText(password);
    }

    @Override
    public void setVersionName(String name) {
        tvVersion.setText(name);
    }

    @Override
    public String getUserName() {
        return etAccount.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return etPassword.getText().toString().trim();
    }

    @OnClick({R.id.iv_account_clean, R.id.iv_psw_clean, R.id.bt_login, R.id.tv_other_login, R.id.tv_forget_psw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_account_clean:
                etAccount.setText("");
                break;
            case R.id.iv_psw_clean:
                etPassword.setText("");
                break;
            case R.id.tv_forget_psw:
                startActivity(new Intent(this, ForgetPswActivity.class));
                break;
            case R.id.bt_login:
                login();
                break;
            case R.id.tv_other_login:
                llLogin.setVisibility(View.VISIBLE);
                gestureView.setVisibility(View.GONE);
                break;
        }
    }

    //登录
    public void login() {
        if (((MyApplication) getApplication()).isCheckUpgradeApp) {
            AndPermission.with(this)
                    .requestCode(Constants.REQUEST_CODE_PERMISSION_SD)
                    .permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .callback(presenter.getPermissioner())
                    // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                    // 这样避免用户勾选不再提示，导致以后无法申请权限。
                    // 你也可以不设置。
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                            AndPermission.rationaleDialog(LoginActivity.this, rationale).show();
                        }
                    })
                    .start();
        } else {
            if (!isSuccess) {
                isSuccess = true;
                if (TextUtils.isEmpty(getUserName())) {
                    showMsg("用户名不能为空！");
                    return;
                } else if (TextUtils.isEmpty(getPassword())) {
                    showMsg("密码不能为空!");
                    return;
                }
                showLoading();
                presenter.getData();
            }
        }
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                if (etAccount.hasFocus()) {
                    ivAccountClean.setVisibility(View.VISIBLE);
                } else {
                    ivAccountClean.setVisibility(View.GONE);
                }
                if (etPassword.hasFocus()) {
                    ivPswClean.setVisibility(View.VISIBLE);
                } else {
                    ivPswClean.setVisibility(View.GONE);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, 0);
            } else {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66 && resultCode == 66) {
            LoginActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        presenter.deAttach();
        super.onDestroy();
    }
}
