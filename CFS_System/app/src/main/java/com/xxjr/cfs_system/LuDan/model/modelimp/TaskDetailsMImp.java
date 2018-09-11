package com.xxjr.cfs_system.LuDan.model.modelimp;

import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.model.ModelImp;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import entity.ChooseType;
import entity.CommonItem;
import entity.LoanInfo;
import entity.ScheduleNote;

public class TaskDetailsMImp extends ModelImp {

    public List<CommonItem> getItemList(JSONArray jsonArray, LoanInfo loanInfo) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 3; i++) {
            commonItem = new CommonItem();
            switch (i) {
                case 0:
                    commonItem.setType(0);
                    List<LoanInfo> loanInfos = new ArrayList<>();
                    loanInfo.setRecorder(getRecorder(jsonArray));
                    loanInfos.add(loanInfo);
                    commonItem.setList(loanInfos);
                    break;
                case 1:
                    commonItem.setType(1);
                    break;
                case 2:
                    commonItem.setType(0);
                    commonItem.setList(getLoanDetailsItem(jsonArray, loanInfo));
                    commonItem.setPosition(loanInfo.getScheduleId());
                    break;
            }
            commonItems.add(commonItem);
        }
        return commonItems;
    }

    //根据权限显示按钮数据源
    public List<CommonItem> getScheduleBtData(int pos, LoanInfo loanInfo, List<String> permits) {
        List<CommonItem> commonItems = new ArrayList<>();
        if (permits != null && permits.size() != 0) {
            if ((pos > 0 && pos <= 4) || (pos >= 102 && pos < 104) || pos == 108) {
                if (permits.contains("CD")) {
                    commonItems.addAll(getScheduleBt(loanInfo));
                }
                if (loanInfo.getLoanType() == 1) {
                    if (pos <= 103 && permits.contains("CT")) {
                        CommonItem commonItem = new CommonItem();
                        commonItem.setContent("赎楼");
                        commonItem.setPosition(5);
                        commonItem.setIcon(R.drawable.schedule_click_3);
                        commonItems.add(commonItem);
                    }
                    if (loanInfo.getIsForeclosureFloor()) {
                        if (!loanInfo.isPrepayment() && permits.contains("CR")) {
                            CommonItem commonItem = new CommonItem();
                            commonItem.setContent("还款");
                            commonItem.setPosition(3);
                            commonItem.setIcon(R.drawable.schedule_click_4);
                            commonItems.add(commonItem);
                        }
                    }
                }
            } else if (pos >= 104 && pos < 108) {
                if (permits.contains("CI")) {
                    commonItems.addAll(getScheduleBt(loanInfo));
                }
                if (loanInfo.getIsForeclosureFloor() && pos <= 105) {
                    if (!loanInfo.isPrepayment() && permits.contains("CR")) {
                        CommonItem commonItem = new CommonItem();
                        commonItem.setContent("还款");
                        commonItem.setPosition(3);
                        commonItem.setIcon(R.drawable.schedule_click_4);
                        commonItems.add(commonItem);
                    }
                }
            } else if ((pos > 4 && pos < 6) || (pos > 108 && pos < 110) || pos == -3 || pos == -4 || pos == -5) {
                if (permits.contains("CE")) {
                    commonItems.addAll(getScheduleBt(loanInfo));
                }
            }
            if ((pos <= 3 && pos > 1) || (pos <= 103 && pos > 1) && permits.contains("E0")) {
                CommonItem commonItem1 = new CommonItem();
                commonItem1.setPosition(8);
                commonItem1.setContent("修改");
                commonItem1.setIcon(R.drawable.schedule_click_5);
                commonItems.add(commonItem1);
            }
            if (loanInfo.getLoanType() == 1 && permits.contains("CN")) {
                if (loanInfo.getIsNotary().equals("0")) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setContent("公证");
                    commonItem.setPosition(4);
                    commonItem.setIcon(R.drawable.schedule_click_5);
                    commonItems.add(commonItem);
                }
            }
            if ((pos > 0 && pos < 5) || (pos > 100 && pos < 109) || pos == -3 || pos == -4 || pos == -5) {
                if (permits.contains("CS")) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setContent("成本");
                    commonItem.setPosition(2);
                    commonItem.setIcon(R.drawable.schedule_click_2);
                    commonItems.add(commonItem);
                }
            }
            //CQ入账，CU出账
            if (pos == 109 || pos == 5 || pos == -3 || pos == -4 || pos == -5) {
                if (permits.contains("CQ")) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setContent("入账");
                    commonItem.setPosition(6);
                    commonItem.setIcon(R.drawable.schedule_click_6);
                    commonItems.add(commonItem);
                }
                if (permits.contains("CU")) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setContent("出账");
                    commonItem.setPosition(7);
                    commonItem.setIcon(R.drawable.schedule_click_7);
                    commonItems.add(commonItem);
                }
            }

            CommonItem commonItem2 = new CommonItem();
            commonItem2.setContent("备注");
            commonItem2.setPosition(1);
            commonItem2.setIcon(R.drawable.schedule_click_1);
            commonItems.add(commonItem2);
        }
        return commonItems;
    }

    private List<CommonItem> getScheduleBt(LoanInfo loanInfo) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem = new CommonItem();
        if (loanInfo.getScheduleId() == 109 || loanInfo.getScheduleId() == 5 || loanInfo.getScheduleId() == -3 || loanInfo.getScheduleId() == -4 || loanInfo.getScheduleId() == -5) {
            commonItem.setContent("发起");
        } else {
            commonItem.setContent("跟进");
        }
        commonItem.setPosition(0);
        commonItem.setIcon(R.drawable.schedule_click);
        commonItems.add(commonItem);
        return commonItems;
    }

    //获取数据源
    private List<CommonItem> getLoanDetailsItem(JSONArray jsonArray, LoanInfo loanInfo) {
        List<CommonItem> commonItems = new ArrayList<>();
        List<ScheduleNote> list;
        CommonItem commonItem;
        List<ChooseType> chooseTypes = getLoanStatusDetails(loanInfo);
        int ScheduleId = loanInfo.getScheduleId();
        for (int i = 0; i < chooseTypes.size(); i++) {
            commonItem = new CommonItem();
            commonItem.setName(chooseTypes.get(i).getContent());
            int pos = chooseTypes.get(i).getId();
            commonItem.setPosition(pos);
            if (ScheduleId <= -1) {
                list = getItemData(jsonArray, pos);
                if (list != null) {
                    commonItem.setList(list);
                }
                if (pos == 0) {
                    commonItem.setType(1);
                } else if (pos == -3 || pos == -4 || pos == -5) {
                    commonItem.setType(3);
                } else {
                    commonItem.setType(4);
                }
            } else {
                if (pos <= ScheduleId) {
                    list = getItemData(jsonArray, pos);
                    if (list != null) {
                        commonItem.setList(list);
                    }
                } else {
                    commonItem.setContent("");
                    commonItem.setHintContent("");
                }
                if (pos < ScheduleId - 1) {
                    commonItem.setType(1);
                } else if (pos == ScheduleId - 1) {
                    commonItem.setType(2);
                } else if (pos == ScheduleId) {
                    commonItem.setType(3);
                } else if (pos > ScheduleId) {
                    commonItem.setType(4);
                }
            }
            commonItems.add(commonItem);
        }
        return commonItems;
    }

    //获取子对象备注显示
    private List<ScheduleNote> getItemData(JSONArray jsonArray, int pos) {
        List<ScheduleNote> list = null;
        for (int j = 0; j < jsonArray.size(); j++) {
            JSONObject jsonObject = jsonArray.getJSONObject(j);
            int Status = jsonObject.getIntValue("Status");
            if (pos == Status) {
                if (pos != 0) {
                    list = new ArrayList<>();
                    ScheduleNote scheduleNote = new ScheduleNote();
                    scheduleNote.setNote(jsonObject.getString("Remark"));
                    scheduleNote.setProvider("记录者：" + getContent(jsonObject));
                    list.add(scheduleNote);
                    JSONArray array = jsonObject.getJSONArray("RemarkList");
                    ScheduleNote scheduleNote1;
                    if (array != null && array.size() != 0) {
                        for (int k = 0; k < array.size(); k++) {
                            JSONObject object = array.getJSONObject(k);
                            scheduleNote1 = new ScheduleNote();
                            scheduleNote1.setNote(object.getString("Remark"));
                            scheduleNote1.setProvider("记录者：" + getContent(object));
                            list.add(scheduleNote1);
                        }
                    }
                }
                break;
            }
        }
        return list;
    }

    private String getRecorder(JSONArray jsonArray) {
        String recorder = "";
        if (jsonArray != null && jsonArray.size() > 0) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            recorder = getContent(jsonObject);
        }
        return recorder;
    }

    public String getClerkName(JSONArray jsonArray) {
        String clerkName = "";
        if (jsonArray != null && jsonArray.size() > 0) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            clerkName = TextUtils.isEmpty(jsonObject.getString("ServicePeopleName")) ? "" : jsonObject.getString("ServicePeopleName");
        }
        return clerkName;
    }

    private String getContent(JSONObject jsonObject) {
        return jsonObject.getString("ServicePeopleName") + " " +
                (TextUtils.isEmpty(jsonObject.getString("InsertTime")) ? "" : Utils.getTimeFormat(jsonObject.getString("InsertTime")));
    }

    //获取信用贷或者抵押贷的各种状态类型和值
    private List<ChooseType> getLoanStatusDetails(LoanInfo loanInfo) {
        List<ChooseType> chooseTypes = new ArrayList<>();
        ChooseType chooseType;
        if (loanInfo.getScheduleId() <= -1) {
            for (int i = 0; i < 3; i++) {
                chooseType = new ChooseType();
                switch (i) {
                    case 0:
                        chooseType.setId(0);
                        if (loanInfo.getLoanType() == 1) {
                            chooseType.setContent("抵押贷");
                        } else {
                            chooseType.setContent("信用贷");
                        }
                        break;
                    case 1:
                        chooseType.setId(loanInfo.getScheduleId());
                        chooseType.setContent("黄单状态");
                        break;
                    case 2:
                        chooseType.setId(-2);
                        chooseType.setContent("黄单结案");
                        break;
                }
                chooseTypes.add(chooseType);
            }
        } else {
            if (loanInfo.getLoanType() != 1) {
                for (int i = 0; i < 7; i++) {
                    chooseType = new ChooseType();
                    chooseType.setId(i);
                    switch (i) {
                        case 0:
                            chooseType.setContent("信用贷");
                            break;
                        case 1:
                            chooseType.setContent("已签约\n待面签");
                            break;
                        case 2:
                            chooseType.setContent("面签待\n补资料");
                            break;
                        case 3:
                            chooseType.setContent("资料全\n待审批");
                            break;
                        case 4:
                            chooseType.setContent("已批复\n待放款");
                            break;
                        case 5:
                            chooseType.setContent("已放款\n待结算");
                            break;
                        case 6:
                            chooseType.setId(8);
                            chooseType.setContent("正常结案");
                            break;
                    }
                    chooseTypes.add(chooseType);
                }
            } else {
                if (!loanInfo.getIsForeclosureFloor()) {
                    for (int i = 0; i < 8; i++) {
                        chooseType = new ChooseType();
                        switch (i) {
                            case 0:
                                chooseType.setId(0);
                                chooseType.setContent("抵押贷");
                                break;
                            case 1:
                                chooseType.setId(1);
                                chooseType.setContent("已签约\n待面签");
                                break;
                            case 2:
                                chooseType.setId(102);
                                chooseType.setContent("面签待\n补资料");
                                break;
                            case 3:
                                chooseType.setId(103);
                                chooseType.setContent("资料全\n待审批");
                                break;
                            case 4:
                                chooseType.setId(104);
                                chooseType.setContent("已批复\n待抵押");
                                break;
                            case 5:
                                chooseType.setId(108);
                                chooseType.setContent("已抵押\n待放款");
                                break;
                            case 6:
                                chooseType.setId(109);
                                chooseType.setContent("已放款\n待结算");
                                break;
                            case 7:
                                chooseType.setId(112);
                                chooseType.setContent("正常结案");
                                break;
                        }
                        chooseTypes.add(chooseType);
                    }
                } else {
                    for (int i = 0; i < 10; i++) {
                        chooseType = new ChooseType();
                        switch (i) {
                            case 0:
                                chooseType.setId(0);
                                chooseType.setContent("抵押贷");
                                break;
                            case 1:
                                chooseType.setId(1);
                                chooseType.setContent("已签约\n待面签");
                                break;
                            case 2:
                                chooseType.setId(102);
                                chooseType.setContent("面签待\n补资料");
                                break;
                            case 3:
                                chooseType.setId(103);
                                chooseType.setContent("资料全\n待审批");
                                break;
                            case 4:
                                chooseType.setId(105);
                                chooseType.setContent("已批复\n待赎楼");
                                break;
                            case 5:
                                chooseType.setId(106);
                                chooseType.setContent("已还款\n待取证");
                                break;
                            case 6:
                                chooseType.setId(107);
                                chooseType.setContent("已取证\n待抵押");
                                break;
                            case 7:
                                chooseType.setId(108);
                                chooseType.setContent("已抵押\n待放款");
                                break;
                            case 8:
                                chooseType.setId(109);
                                chooseType.setContent("已放款\n待结算");
                                break;
                            case 9:
                                chooseType.setId(112);
                                chooseType.setContent("正常结案");
                                break;
                        }
                        chooseTypes.add(chooseType);
                    }
                }
            }
        }
        return chooseTypes;
    }
}
