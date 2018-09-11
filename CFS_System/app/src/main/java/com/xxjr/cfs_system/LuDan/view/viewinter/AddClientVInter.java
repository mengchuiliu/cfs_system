package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.ClientInfo;
import entity.CommonItem;

/**
 * Created by Administrator on 2017/8/24.
 * 添加用户接口
 */

public interface AddClientVInter extends BaseViewInter {
    void initRecycler(List<CommonItem> commonItems);

    void hidePop();

    ClientInfo getClient();

    void refreshData(int position, CommonItem commonItem);
}
