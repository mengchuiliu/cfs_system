package com.xxjr.cfs_system.LuDan.presenter;

import android.text.TextUtils;

import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.modelimp.UpdatePswMImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.UpdatePswInter;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 * 修改密码执行类
 */

public class UpdatePswPresenter extends BasePresenter<UpdatePswInter, UpdatePswMImp> {
    @Override
    protected UpdatePswMImp getModel() {
        return new UpdatePswMImp();
    }

    @Override
    public void setDefaultValue() {
    }

    private List<Object> getListParam() {
        List<Object> list = new ArrayList<>();
        list.add(Hawk.get("UserID"));
        list.add(Utils.getMD5Str(getView().getOldPsw()));
        list.add(Utils.getMD5Str(getView().getNowPsw()));
        return list;
    }

    public String getUpdatePswParam() {
        return model.getUpdatePswParam(getListParam(), "UserInfoManage");
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        Hawk.put("Psw", "");
        getView().showMsg("密码修改成功!");
        getView().complete();
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }


    public boolean check(String oldPsw, String newPsw, String newPsw2) {
        if (TextUtils.isEmpty(oldPsw)) {
            getView().showMsg("旧密码不能为空!");
            return false;
        } else if (TextUtils.isEmpty(newPsw)) {
            getView().showMsg("新密码不能为空!");
            return false;
        } else if (newPsw.length() < 8) {
            getView().showMsg("新密码不能少于8位!");
            return false;
        } else if (!isLetter(newPsw)) {
            getView().showMsg("密码必须包含数字字母!");
            return false;
        } else if (!newPsw.equals(newPsw2)) {
            getView().showMsg("两次输入的新密码不一致!");
            return false;
        }
        return true;
    }

    /**
     * 规则2：至少包含大小写字母及数字中的两种
     * 是否包含
     *
     * @param str
     * @return
     */
    private boolean isLetter(String str) {
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        return isLetter && str.matches(regex);
    }
}
