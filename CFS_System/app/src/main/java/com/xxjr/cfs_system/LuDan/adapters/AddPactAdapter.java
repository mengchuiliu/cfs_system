package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.view.activitys.AddPactActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.SearchActivity;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;

import java.util.List;

import entity.ClientInfo;
import entity.CommonItem;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;
import rvadapter.ItemViewDelegate;
import rvadapter.MultiItemAdapter;

/**
 * Created by Administrator on 2017/8/9.
 * 添加报单
 */

public class AddPactAdapter extends MultiItemAdapter<CommonItem> {
    private AddPactActivity activity;
    private RecycleItemClickListener onItemClickListener, chooseItemClick;
    private RecycleItemClickListener addClientListener;
    private TextChangeListener textChangeListener;

    public AddPactAdapter(Context context, List datas) {
        super(context, datas);
        this.activity = (AddPactActivity) context;
        addItemViewDelegate(new TitleDelegate());
        addItemViewDelegate(new TextDelegate());
        addItemViewDelegate(new ChooseDelegate());
        addItemViewDelegate(new EditDelegate());
        addItemViewDelegate(new EditNoteDelegate());
        addItemViewDelegate(new RecycleDelegate());
    }

    public void setRecycleItemClickListener(RecycleItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setChooseItemClickListener(RecycleItemClickListener onItemClickListener) {
        this.chooseItemClick = onItemClickListener;
    }

    public void setAddClientListener(RecycleItemClickListener addClientListener) {
        this.addClientListener = addClientListener;
    }

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    private class TitleDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 1;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_title;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setImageResource(R.id.iv_title, item.getIcon());
            holder.setText(R.id.tv_title, item.getName());
            if (position == 9) {
                holder.setVisible(R.id.tv_right, true);
                holder.setBackgroundRes(R.id.tv_right, R.drawable.theme_click);
                holder.setText(R.id.tv_right, "新增客户");
                holder.setOnClickListener(R.id.tv_right, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addClientListener.onItemClick(position);
                    }
                });
            }
        }
    }

    private class TextDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 2;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_text;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setText(R.id.tv_content, item.getContent());
        }
    }

    private class ChooseDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 3;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_choose;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            ViewGroup.LayoutParams layoutParams = holder.getConvertView().getLayoutParams();
            if (item.isEnable()) {
                holder.getConvertView().setVisibility(View.VISIBLE);
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                holder.getConvertView().setVisibility(View.GONE);
                layoutParams.height = 0;
                layoutParams.width = 0;
            }
            holder.getConvertView().setLayoutParams(layoutParams);
            holder.setText(R.id.tv_content_name, item.getName());
            holder.setText(R.id.tv_content, item.getContent());
            LinearLayout layout = holder.getView(R.id.ll_content);
            layout.setEnabled(item.isClick());
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseItemClick.onItemClick(position);
                }
            });
        }
    }

    private class EditDelegate implements ItemViewDelegate<CommonItem> {

        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 4;
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
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
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

    private class EditNoteDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 5;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_common_note;
        }

        @Override
        public void convert(final BaseViewHolder holder, final CommonItem item, final int position) {
            holder.setText(R.id.tv_edit_title, item.getName());
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
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() <= 500) {
                        holder.setText(R.id.tv_hint_nub, charSequence.length() + "/500");
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

    private class RecycleDelegate implements ItemViewDelegate<CommonItem> {
        @Override
        public boolean isForViewType(CommonItem item, int position) {
            return item.getType() == 6;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_recycle;
        }

        @Override
        public void convert(BaseViewHolder holder, CommonItem item, int position) {
            RecyclerView recyclerView = holder.getView(R.id.recycle_item);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            List<ClientInfo> clientInfos = item.getList();
            if (clientInfos != null) {
                CommonAdapter adapter = new CommonAdapter<ClientInfo>(activity, clientInfos, R.layout.item_common_choose) {
                    @Override
                    protected void convert(BaseViewHolder holder, ClientInfo info, final int position) {
                        holder.setText(R.id.tv_content_name, info.getName());
                        holder.setOnClickListener(R.id.ll_content, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onItemClickListener.onItemClick(position);
                            }
                        });
                    }
                };
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
