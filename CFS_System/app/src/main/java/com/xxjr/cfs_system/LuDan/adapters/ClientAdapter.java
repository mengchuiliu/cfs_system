package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xiaoxiao.ludan.R;

import java.util.List;

import entity.CommonItem;
import rvadapter.BaseViewHolder;
import rvadapter.ItemViewDelegate;
import rvadapter.MultiItemAdapter;

/**
 * Created by Administrator on 2017/8/24.
 */

public class ClientAdapter extends MultiItemAdapter<CommonItem> {
    private RecycleItemClickListener itemClickListener;
    private TextChangeListener textChangeListener;

    public ClientAdapter(Context context, List datas) {
        super(context, datas);
        addItemViewDelegate(new ChooseDelegate());
        addItemViewDelegate(new EditDelegate());
    }

    public void setItemClickListener(RecycleItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    private class ChooseDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 1;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_choose;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setText(R.id.tv_content, item.getContent());
            LinearLayout layout = holder.getView(R.id.ll_content);
            layout.setEnabled(item.isClick());
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
            return item.getType() == 2;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_edit;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            final EditText editText = holder.getView(R.id.et_content);
            editText.removeTextChangedListener((TextWatcher) holder.getConvertView().getTag(R.id.tag_first));
            editText.setText(item.getContent());
            editText.setHint(item.getHintContent());
            editText.setEnabled(item.isClick());
            if (item.getPosition() == 1) {
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            } else {
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                if (item.getPosition() == 3) {
                    editText.setKeyListener(DigitsKeyListener.getInstance(" 0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`¬!\"£$%^*()~=#{}[];':,.?*-_+;@&<>"));
                }
            }
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    textChangeListener.setTextChage(position, editable.toString());
                }
            };
            editText.addTextChangedListener(textWatcher);
            holder.getConvertView().setTag(R.id.tag_first, textWatcher);
        }
    }
}
