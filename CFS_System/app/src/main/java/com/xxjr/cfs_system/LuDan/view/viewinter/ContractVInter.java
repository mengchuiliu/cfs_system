package com.xxjr.cfs_system.LuDan.view.viewinter;

import java.util.List;

import entity.Contract;

/**
 * Created by Administrator on 2017/9/5.
 * 合同列表接口
 */

public interface ContractVInter extends BaseLsitVInter {

    List<Contract> getContracts();

    void delData();
}
