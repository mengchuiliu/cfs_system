package com.xxjr.cfs_system.LuDan.model.modelimp;

import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.main.MyApplication;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import entity.ClientInfo;
import entity.CommonItem;

import static android.R.attr.type;

/**
 * Created by Administrator on 2017/8/24.
 * 数据处理
 */

public class AddClientMImp extends ModelImp {
    //获取客户信息显示的内容
    public List<CommonItem> getClientItems(ClientInfo clientInfo) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 4; i++) {
            commonItem = new CommonItem();
            commonItem.setPosition(i);
            if (clientInfo.getUserID() != 0) {
                commonItem.setClick(false);
            } else {
                commonItem.setClick(true);
            }
            switch (i) {
                case 0:
                    commonItem.setName("姓名");
                    commonItem.setHintContent("请填写");
                    commonItem.setType(2);
                    commonItem.setContent(clientInfo.getName());
                    break;
                case 1:
                    commonItem.setClick(true);
                    commonItem.setName("手机号");
                    commonItem.setHintContent("请填写");
                    commonItem.setType(2);
                    commonItem.setContent(clientInfo.getMobile());
                    break;
                case 2:
                    commonItem.setName("证件类型");
                    commonItem.setType(1);
                    int type = clientInfo.getCardType();
                    commonItem.setContent(Utils.getTypeValue(Utils.getTypeDataList("IDType"), type));
                    break;
                case 3:
                    commonItem.setName("证件号码");
                    commonItem.setHintContent("请填写");
                    commonItem.setType(2);
                    commonItem.setContent(clientInfo.getIdCode());
                    break;
            }
            commonItems.add(commonItem);
        }
        return commonItems;
    }
}
