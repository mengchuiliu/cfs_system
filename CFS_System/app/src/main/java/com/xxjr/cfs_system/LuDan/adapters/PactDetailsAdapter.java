package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.HttpAction;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.view.activitys.PactDetailsActivity;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.ClientInfo;
import entity.CommonItem;
import entity.LoanInfo;
import entity.Schedule;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;
import rvadapter.ItemViewDelegate;
import rvadapter.MultiItemAdapter;
import subscribers.ProgressSubscriber;
import subscribers.SubscriberOnNextListener;

/**
 * Created by Administrator on 2017/8/25.
 * 合同详情适配器
 */

public class PactDetailsAdapter extends MultiItemAdapter<CommonItem> {
    private PactDetailsActivity activity;
    private RecycleItemClickListener pactItemClickListener;//合同编辑
    private RecycleItemClickListener loanItemClickListener;//贷款编辑

    public PactDetailsAdapter(Context context, List datas) {
        super(context, datas);
        this.activity = (PactDetailsActivity) context;
        addItemViewDelegate(new TitleDelegate());
        addItemViewDelegate(new TextDelegate());
        addItemViewDelegate(new RecycleDelegate());
        addItemViewDelegate(new BlankDelegate());
        addItemViewDelegate(new TextPactDelegate());
    }

    public void setPactItemClickListener(RecycleItemClickListener itemClickListener) {
        this.pactItemClickListener = itemClickListener;
    }

    public void setLoanItemClickListener(RecycleItemClickListener itemClickListener) {
        this.loanItemClickListener = itemClickListener;
    }

    private class TitleDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 0;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_title;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setImageResource(R.id.iv_title, item.getIcon());
            holder.setText(R.id.tv_title, item.getName());
            if (position == 1) {
                if (item.getPosition() == 0) {
                    holder.setVisible(R.id.tv_right, true);
                    holder.setTextSize(R.id.tv_right, 15);
                    holder.setTextColorRes(R.id.tv_right, R.color.font_home);
                    holder.setText(R.id.tv_right, "编辑");
                    holder.setOnClickListener(R.id.tv_right, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pactItemClickListener.onItemClick(position);
                        }
                    });
                }
            }
        }
    }

    private class TextDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 1;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_show;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            ViewGroup.LayoutParams params = holder.getConvertView().getLayoutParams();
            if (item.isLineShow()) {
                holder.getConvertView().setVisibility(View.GONE);
                params.height = 0;
                params.width = 0;
            } else {
                holder.getConvertView().setVisibility(View.VISIBLE);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            holder.getConvertView().setLayoutParams(params);
            holder.setText(R.id.tv_content_name, item.getName() + "：");
            holder.setText(R.id.tv_content, item.getContent());
        }
    }

    private class RecycleDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 2;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_recycle;
        }

        @Override
        public void convert(BaseViewHolder holder, CommonItem item, int position) {
            RecyclerView recyclerView = holder.getView(R.id.recycle_item);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            if (position == 12) {
                showCustomers(recyclerView, item.getList());
            } else if (position == 16) {
                showLaonInfo(recyclerView, item.getList());
            }
        }
    }

    private class BlankDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 3;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.blank;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            ViewGroup.LayoutParams params = holder.getConvertView().getLayoutParams();
            if (item.isEnable()) {
                holder.getConvertView().setVisibility(View.VISIBLE);
                params.height = Utils.dip2px(activity, 8f);
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                holder.getConvertView().setVisibility(View.GONE);
                params.height = 0;
                params.width = 0;
            }
            holder.getConvertView().setLayoutParams(params);
            if (position == 0 || position == 14) {
                holder.setBackgroundRes(R.id.v_blank, R.color.blank_bg);
            } else {
                holder.setBackgroundRes(R.id.v_blank, R.color.white);
            }
        }
    }

    private class TextPactDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 4;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_details;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            ViewGroup.LayoutParams params = holder.getConvertView().getLayoutParams();
            if (item.isEnable()) {
                holder.getConvertView().setVisibility(View.VISIBLE);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                holder.getConvertView().setVisibility(View.GONE);
                params.height = 0;
                params.width = 0;
            }
            holder.getConvertView().setLayoutParams(params);
            holder.setVisible(R.id.iv_title, true);
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setTextColorRes(R.id.tv_content_name, R.color.font_c3);
            holder.setTextSize(R.id.tv_content_name, 16);
            holder.setImageResource(R.id.iv_title, item.getIcon());
            holder.setVisible(R.id.iv_right, true);
            if (TextUtils.isEmpty(item.getContent())) {
                holder.setVisible(R.id.tv_yuan, false);
            } else {
                holder.setVisible(R.id.tv_yuan, true);
                holder.setBackgroundRes(R.id.tv_yuan, R.drawable.point_bg);
                holder.setText(R.id.tv_yuan, item.getContent());
                holder.setTextSize(R.id.tv_yuan, 12);
                holder.setTextColorRes(R.id.tv_yuan, R.color.white);
            }
            holder.setVisible(R.id.v_line, false);
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pactItemClickListener.onItemClick(position);
                }
            });
        }
    }

    private void showCustomers(RecyclerView recyclerView, List<ClientInfo> clientInfos) {
        CommonAdapter adapter = new CommonAdapter<ClientInfo>(activity, clientInfos, R.layout.item_custmoers) {
            @Override
            protected void convert(BaseViewHolder holder, ClientInfo clientInfo, int position) {
                holder.setText(R.id.tv_num, "客户" + (position + 1) + "：");
                holder.setText(R.id.tv_customer, clientInfo.getName());
                final String phone = clientInfo.getMobile();
                if (!TextUtils.isEmpty(phone)) {
                    holder.setText(R.id.tv_phone, phone);
                    holder.setOnClickListener(R.id.tv_phone, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            if (PermissionTools.checkPermission(activity, Manifest.permission.CALL_PHONE, R.string.permission_phone, true)) {
//                                String phoneStr = phone;
//                                if (phone.length() == 11) {
//                                    phoneStr = phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7, 11);
//                                }
//                                CustomDialog.showTwoButtonDialog(activity, phoneStr, "呼叫", "取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                        Intent in = new Intent();
//                                        in.setAction(Intent.ACTION_CALL);
//                                        in.setData(Uri.parse("tel:" + phone));
//                                        activity.startActivity(in);
//                                    }
//                                });
//                            }
                        }
                    });
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void showLaonInfo(RecyclerView recyclerView, List<LoanInfo> loanInfos) {
        CommonAdapter adapter = new CommonAdapter<LoanInfo>(activity, loanInfos, R.layout.item_recycle) {
            @Override
            protected void convert(BaseViewHolder holder, final LoanInfo loanInfo, int position) {
                RecyclerView recyclerView = holder.getView(R.id.recycle_item);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                final LoanItemAdapter loanItemAdapter = new LoanItemAdapter(activity, getItems(loanInfo));
                loanItemAdapter.setItemClickListener(loanItemClickListener, position);
                loanItemAdapter.setItemClickListener1(new RecycleItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        getScheduleData(loanInfo.getLoanId(), pos, loanItemAdapter);
                    }
                });
                loanItemAdapter.setItemClickListener2(new RecycleItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        CommonItem commonItem = loanItemAdapter.getDatas().get(pos);
                        commonItem.setClick(false);
                        CommonItem commonItem1 = loanItemAdapter.getDatas().get(pos + 1);
                        commonItem1.setClick(false);
                        loanItemAdapter.notifyDataSetChanged();
                    }
                });
                recyclerView.setAdapter(loanItemAdapter);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private List<CommonItem> getItems(LoanInfo loanInfo) {
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem commonItem;
        for (int i = 0; i < 13; i++) {
            commonItem = new CommonItem();
            switch (i) {
                case 0:
                    commonItem.setType(4);
                    break;
                case 1:
                    commonItem.setType(0);
                    commonItem.setName("贷款信息" + "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() +
                            "(" + (TextUtils.isEmpty(loanInfo.getLoanTypeName()) ? "" : loanInfo.getLoanTypeName().substring(0, 1)) + ")" + "】");
                    commonItem.setIcon(R.mipmap.icon_loan);
                    commonItem.setPosition(loanInfo.getScheduleId());
                    break;
                case 2:
                    commonItem.setType(1);
                    commonItem.setName("贷款编号：");
                    commonItem.setContent(loanInfo.getLoanCode());
                    break;
                case 3:
                    commonItem.setType(1);
                    commonItem.setName("申请金额：");
                    commonItem.setContent(Utils.parseMoney(new BigDecimal(loanInfo.getAmount())));
                    break;
                case 4:
                    commonItem.setType(1);
                    commonItem.setName("收费方式：");
                    commonItem.setContent(loanInfo.getChargeWay() == 1 ? ("点数 " + loanInfo.getPercent() + "%") : "包干");
                    commonItem.setLineShow(true);
                    break;
                case 5:
                    commonItem.setType(1);
                    commonItem.setName(" 手 续 费 ：");
                    commonItem.setContent(Utils.parseMoney(new BigDecimal(loanInfo.getPoundage())));
                    commonItem.setLineShow(true);
                    break;
                case 6:
                    commonItem.setType(1);
                    commonItem.setName("申请客户：");
                    commonItem.setContent(loanInfo.getCustomer());
                    break;
                case 7:
                    commonItem.setType(1);
                    commonItem.setName(" 按 揭 员 ：");
                    commonItem.setContent(CacheProvide.getMortgageName(loanInfo.getMortgage()));
                    break;
                case 8:
                    commonItem.setType(1);
                    commonItem.setName("最新进度：");
                    commonItem.setContent(loanInfo.getSchedule());
                    break;
                case 9:
                    commonItem.setType(2);
                    commonItem.setName("进度跟踪");
                    commonItem.setClick(false);
                    break;
                case 10:
                    commonItem.setType(3);
                    commonItem.setClick(false);
                    break;
                case 11:
                    commonItem.setType(2);
                    commonItem.setName("成本跟踪");
                    commonItem.setClick(false);
                    break;
                case 12:
                    commonItem.setType(3);
                    commonItem.setClick(false);
                    break;
            }
            commonItems.add(commonItem);
        }
        return commonItems;
    }

    private void getScheduleData(String loanId, final int type, final LoanItemAdapter loanItemAdapter) {
        final List<Schedule> scheduleList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        List<Object> list = new ArrayList<>();
        map.put("Action", "GET");
        if (type == 9) {
            list.add("t_loan_follow_record");
            list.add("1=1" + "and LoanID = " + loanId + " and DelMarker = 0");
            list.add("*");
            list.add("InsertTime desc");
        } else {
            list.add("t_loan_cost");
            list.add("1=1" + "and LoanID = " + loanId + " and DelMarker = 0");
            list.add("*");
            list.add("InsertTime desc");
        }
        map.put("DBMarker", "DB_CFS_Loan");
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("ParamString", list);
        map.put("TranName", "GetPermitData");
        String str = JSON.toJSONString(map);
        HttpAction.getInstance().getData(new ProgressSubscriber(new SubscriberOnNextListener<ResponseData>() {
            @Override
            public void onNext(ResponseData data) {
                if (data.getExecuteResult()) {
                    JSONArray jsonArray = JSON.parseArray(data.getData());
                    if (jsonArray != null && jsonArray.size() != 0) {
                        Schedule schedule;
                        for (int i = 0; i < jsonArray.size(); i++) {
                            schedule = new Schedule();
                            JSONObject object = jsonArray.getJSONObject(i);
                            if (type == 9) {
                                schedule.setStatus(object.getString("LF3"));
                                schedule.setName(object.getString("ServiceName"));
                                schedule.setDate(Utils.getTimeFormat(object.getString("UpdateTime")));
                            } else {
                                int AuditStatus = object.getIntValue("AuditStatus");
                                String audit = "";
                                if (AuditStatus == 1) {
                                    audit = "  审核通过";
                                } else if (AuditStatus == 2) {
                                    audit = "  审核拒绝";
                                }
                                String string = object.getString("ClerkName") + "--" + CacheProvide.getCostType(object.getIntValue("CostType"))
                                        + ":" + (new BigDecimal(object.getDoubleValue("Cost")).setScale(2, BigDecimal.ROUND_DOWN).toString()) + "元" +
                                        audit + "  备注:" + object.getString("ReMark");
                                schedule.setStatus(string);
                                schedule.setName(object.getString("ServiceName"));
                                schedule.setDate(Utils.getTimeFormat(object.getString("UpdateTime")));
                            }
                            scheduleList.add(schedule);
                        }
                    }
                    CommonItem commonItem = loanItemAdapter.getDatas().get(type);
                    commonItem.setClick(true);
                    CommonItem commonItem1 = loanItemAdapter.getDatas().get(type + 1);
                    commonItem1.setList(scheduleList);
                    commonItem1.setClick(true);
                    loanItemAdapter.notifyDataSetChanged();
                } else {
                    activity.showMsg(data.getData());
                }
            }

            @Override
            public void onError(String msg) {
                activity.showMsg(msg);
            }
        }, activity), Hawk.get("SessionID", ""), str);
    }
}
