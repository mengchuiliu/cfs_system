package com.xiaoxiao.rxjavaandretrofit;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2017/3/14.
 *
 * @author mengchuiliu
 *         数据返回
 */

public class ResponseData {
    //    "ExecuteResult":true,"ReturnMsg":null,"ReturnString":null,"ReturnStrings":null,"ReturnBytes":null,"ReturnDataTable":[],
//            "ReturnDataSet":null,"Marker":null,"IsZip":false,"ZipType":null
    private Boolean ExecuteResult;//true返回正确数据，false或null为错误信息
    private String data;//返回数据或者错误信息
    private String ReturnString;
    private String Marker;
    private String ReturnStrings;
    private String ReturnMsg;
    private JSONObject ReturnDataSet;

    public Boolean getExecuteResult() {
        return ExecuteResult;
    }

    public void setExecuteResult(Boolean executeResult) {
        ExecuteResult = executeResult;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getReturnString() {
        return ReturnString;
    }

    public void setReturnString(String returnString) {
        ReturnString = returnString;
    }

    public String getMarker() {
        return Marker;
    }

    public void setMarker(String marker) {
        Marker = marker;
    }

    public String getReturnStrings() {
        return ReturnStrings;
    }

    public void setReturnStrings(String returnStrings) {
        ReturnStrings = returnStrings;
    }

    public String getReturnMsg() {
        return ReturnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        ReturnMsg = returnMsg;
    }

    public JSONObject getReturnDataSet() {
        return ReturnDataSet;
    }

    public void setReturnDataSet(JSONObject returnDataSet) {
        ReturnDataSet = returnDataSet;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "ExecuteResult=" + ExecuteResult +
                ", data='" + data +
                ",ReturnString=" + ReturnString +
                ",Marker=" + Marker +
                ",ReturnDataSet=" + ReturnDataSet +
                '}';
    }
}
