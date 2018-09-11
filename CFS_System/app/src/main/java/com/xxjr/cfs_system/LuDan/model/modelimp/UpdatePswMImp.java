package com.xxjr.cfs_system.LuDan.model.modelimp;

import com.alibaba.fastjson.JSON;
import com.xxjr.cfs_system.LuDan.model.ModelImp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */

public class UpdatePswMImp extends ModelImp {
    public String getUpdatePswParam(List<Object> list, String tranName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("DBMarker", "CFS_Loan");
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("Function", "Password");
        map.put("ParamString", list);
        map.put("TranName", tranName);
        return JSON.toJSONString(map);
    }
}
