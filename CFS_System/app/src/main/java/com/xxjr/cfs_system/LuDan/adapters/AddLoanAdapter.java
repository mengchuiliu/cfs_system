package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.view.activitys.AddLoanActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.SearchActivity;
import com.xxjr.cfs_system.tools.Constants;

import java.math.BigDecimal;
import java.util.List;

import entity.CommonItem;
import rvadapter.BaseViewHolder;
import rvadapter.ItemViewDelegate;
import rvadapter.MultiItemAdapter;

/**
 * Created by Administrator on 2017/9/1.
 * 添加贷款适配器
 */
public class AddLoanAdapter extends MultiItemAdapter<CommonItem> {
    private AddLoanActivity activity;
    private TextChangeListener textChangeListener;
    private RecycleItemClickListener itemClickListener;
    private boolean shouldStopChange = false;

    public AddLoanAdapter(Context context, List datas) {
        super(context, datas);
        activity = (AddLoanActivity) context;
        addItemViewDelegate(new ChooseDelegate());
        addItemViewDelegate(new EditDelegate());
        addItemViewDelegate(new EditNoteDelegate());
    }

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    public void setItemClickListener(RecycleItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private class ChooseDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 0;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_choose;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            if (TextUtils.isEmpty(item.getContent())) {
                holder.setTextColorRes(R.id.tv_content, R.color.font_c9);
                holder.setText(R.id.tv_content, "请选择");
            } else {
                holder.setTextColorRes(R.id.tv_content, R.color.font_c3);
                holder.setText(R.id.tv_content, item.getContent());
            }
            LinearLayout layout = holder.getView(R.id.ll_content);
            layout.setEnabled(!item.isClick());
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(position);
                }
            });
        }
    }

    private class EditDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 1;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_edit;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            shouldStopChange = false;
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setVisible(R.id.tv_yuan, true);
            final EditText editText = holder.getView(R.id.et_content);
            editText.removeTextChangedListener((TextWatcher) holder.getConvertView().getTag(R.id.tag_first));
            editText.setText(item.getContent());
            editText.setHint(item.getHintContent());
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
//            final double[] temp = {0};
//            if (holder.getConvertView().getTag(R.id.tag_second) != null && item.getContent().length() != 0) {
//                editText.setSelection((int) holder.getConvertView().getTag(R.id.tag_second));
//            }
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (shouldStopChange) {
                        shouldStopChange = false;
                        return;
                    }
                    shouldStopChange = true;
                    String str = editable.toString().trim().replaceAll(",", "");
                    int len = str.length();
                    if (len > 9) {
                        activity.showMsg("拆借金额不能大于1亿!");
                        editText.setText("");
                        textChangeListener.setTextChage(position, "");
                        return;
                    }
                    textChangeListener.setTextChage(position, editable.toString());
                    int courPos;
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < len; i++) {
                        builder.append(str.charAt(i));
                        if (i == 2 || i == 5 || i == 8) {
                            if (i != len - 1)
                                builder.append(",");
                        }
                    }
                    courPos = builder.length();
                    editText.setText(builder.toString());
                    editText.setSelection(courPos);
                }
            };
            editText.addTextChangedListener(textWatcher);
            holder.getConvertView().setTag(R.id.tag_first, textWatcher);
        }
    }

    private class EditNoteDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 2;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_note;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_edit_title, item.getName());
            holder.setText(R.id.tv_hint_nub, "0/" + item.getPosition());
            final EditText editText = holder.getView(R.id.et_content);
            editText.removeTextChangedListener((TextWatcher) holder.getConvertView().getTag(R.id.tag_first));
            editText.setText(item.getContent());
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(item.getPosition())});
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() <= item.getPosition()) {
                        holder.setText(R.id.tv_hint_nub, charSequence.length() + "/" + item.getPosition());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    textChangeListener.setTextChage(position, editable.toString().trim());
                }
            };
            editText.addTextChangedListener(textWatcher);
            holder.getConvertView().setTag(R.id.tag_first, textWatcher);
        }
    }
}
