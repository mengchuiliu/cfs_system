package com.xxjr.cfs_system.LuDan.view.viewinter;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.CommonItem;

/**
 * Created by Administrator on 2017/8/1.
 * 个人主页
 */

public interface PersonalVInter extends BaseViewInter {
    void setPortrait(Bitmap bitmap);

    void initReacycle(List<CommonItem> commonItems);

    SparseArray<String> getSparseData();

    void hidePop();

    void showSexPop();

    void showPortraitPop();

    void showBirthDate();

    void over();
}
