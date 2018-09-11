package com.xxjr.cfs_system.LuDan.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.view.post_image.ImageDisplayer;

import java.util.List;

import entity.ImageInfo;

/**
 * Created by Administrator on 2016/3/3 0003.
 *
 * @author mengchuiliu
 */
public class ImageChooseAdapter extends BaseAdapter {
    private Context mContext;
    private List<ImageInfo> mDataList;

    public ImageChooseAdapter(Context context, List<ImageInfo> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }


    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder mHolder;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_image_grid, null);
            mHolder = new ViewHolder();
            mHolder.imageIv = convertView.findViewById(R.id.image_icon);
            mHolder.selectedIv = convertView.findViewById(R.id.selected_tag);
            mHolder.selectedBgTv = convertView.findViewById(R.id.image_selected_bg);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        final ImageInfo item = mDataList.get(position);
        ImageDisplayer.getInstance(mContext).displayBmp(mHolder.imageIv, item.thumbnailPath, item.sourcePath);

        if (item.isSelected) {
            mHolder.selectedIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_ok));
            mHolder.selectedIv.setVisibility(View.VISIBLE);
            mHolder.selectedBgTv.setBackgroundResource(R.drawable.image_selected);
        } else {
            mHolder.selectedIv.setImageDrawable(null);
            mHolder.selectedIv.setVisibility(View.GONE);
            mHolder.selectedBgTv.setBackgroundResource(R.color.light_gray);
        }
        return convertView;
    }

    static class ViewHolder {
        private ImageView imageIv;
        private ImageView selectedIv;
        private TextView selectedBgTv;
    }
}
