package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.view.activitys.PersonalInfoActivity;

import java.util.List;

import entity.CommonItem;
import rvadapter.BaseViewHolder;
import rvadapter.ItemViewDelegate;
import rvadapter.MultiItemAdapter;

/**
 * Created by Administrator on 2017/8/1.
 * 个人主页适配器
 */

public class PersonalAdapter extends MultiItemAdapter<CommonItem> {
    private SaveAdapterData saveAdapterData;
    private SparseArray<String> array;
    private PersonalInfoActivity activity;

    public PersonalAdapter(Context context, List datas) {
        super(context, datas);
        activity = (PersonalInfoActivity) context;
        array = new SparseArray<>();
        addItemViewDelegate(new TextDelegate());
        addItemViewDelegate(new ChooseDelegate());
        addItemViewDelegate(new EditDelegate());
    }

    public void setSave(SaveAdapterData saveAdapterData) {
        this.saveAdapterData = saveAdapterData;
    }

    private class TextDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 1;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_text;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setText(R.id.tv_content, item.getContent());
            array.append(item.getPosition(), item.getContent());
            saveAdapterData.save(array);
        }
    }

    private class ChooseDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 2;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_choose;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setText(R.id.tv_content, item.getContent());
            array.append(item.getPosition(), item.getContent());
            saveAdapterData.save(array);
            holder.setOnClickListener(R.id.ll_content, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RxBus.getInstance().post(item.getPosition());
                    activity.set(new SetText() {
                        @Override
                        public void setText(String text) {
                            holder.setText(R.id.tv_content, text);
                            array.put(item.getPosition(), text);
                        }
                    });
                }
            });
        }
    }

    private class EditDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 3;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_edit;
        }

        @Override
        public void convert(BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            EditText editText = holder.getView(R.id.et_content);
            editText.setText(item.getContent());
            if (item.getPosition() == 7 || item.getPosition() == 8) {
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            }
            array.append(item.getPosition(), item.getContent());
            saveAdapterData.save(array);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    array.put(item.getPosition(), editable.toString());
                    saveAdapterData.save(array);
                }
            });
        }
    }
}
