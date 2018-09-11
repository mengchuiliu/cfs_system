package com.xxjr.cfs_system.LuDan.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.view.post_image.ImageDisplayer;
import com.xxjr.cfs_system.tools.Constants;

import java.util.ArrayList;
import java.util.List;

import entity.ImageInfo;

/**
 * Created by Administrator on 2016/3/2 0002.
 *
 * @author mengchuiliu
 */
public class ImageAdapter extends BaseAdapter {
    private List<ImageInfo> mDataList = new ArrayList<>();
    private Context mContext;
    private RecycleItemClickListener onItemClickListener;
    private RecycleItemClickListener onDelItemClickListener;

    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnItemClickListener(RecycleItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnDelItemClickListener(RecycleItemClickListener onItemClickListener) {
        this.onDelItemClickListener = onItemClickListener;
    }

    public void setData(List<ImageInfo> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        // 多返回一个用于展示添加图标
        if (mDataList == null) {
            return 1;
        } else if (mDataList.size() >= Constants.MAX_IMAGE_SIZE) {
            return Constants.MAX_IMAGE_SIZE;
        } else {
            return mDataList.size() + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mDataList != null && mDataList.size() == Constants.MAX_IMAGE_SIZE) {
            return mDataList.get(position);
        } else if (mDataList == null || position - 1 < 0 || position > mDataList.size()) {
            return null;
        } else {
            return mDataList.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 所有Item展示不满一页，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问题
        convertView = View.inflate(mContext, R.layout.gv_item_image, null);
        ImageView imageIv = convertView.findViewById(R.id.item_grid_image);
        ImageView imagedel = convertView.findViewById(R.id.iv_del);

        if (isShowAddItem(position)) {
            imageIv.setImageResource(R.drawable.btn_add_pic);
            imagedel.setVisibility(View.GONE);
        } else {
            final ImageInfo item = mDataList.get(position);
            ImageDisplayer.getInstance(mContext).displayBmp(imageIv, item.thumbnailPath, item.sourcePath);
            imagedel.setVisibility(View.VISIBLE);
        }
        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });

        imagedel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDelItemClickListener.onItemClick(position);
            }
        });
        return convertView;
    }

    private boolean isShowAddItem(int position) {
        int size = mDataList == null ? 0 : mDataList.size();
        return position == size;
    }
}
