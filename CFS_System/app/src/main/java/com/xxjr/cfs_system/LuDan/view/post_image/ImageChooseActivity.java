package com.xxjr.cfs_system.LuDan.view.post_image;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.adapters.ImageChooseAdapter;
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import entity.ImageInfo;

public class ImageChooseActivity extends BaseActivity {
    List<ImageInfo> mDataList = new ArrayList<>();
    String mBucketName;
    int availableSize;
    @Bind(R.id.iamge_gv)
    GridView mGridView;
    private ImageChooseAdapter mAdapter;
    private HashMap<String, ImageInfo> selectedImgs = new HashMap<>();
    TextView textView;

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_choose;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getdata();
        getToolbarTitle().setText(mBucketName);
        textView = getSubTitle();
        textView.setText("完成" + "(" + selectedImgs.size() + "/" + availableSize + ")");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("image_list", new ArrayList<>(selectedImgs.values()));
                setResult(8, intent);
                finish();
            }
        });

        mAdapter = new ImageChooseAdapter(ImageChooseActivity.this, mDataList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageInfo item = mDataList.get(position);
                if (item.isSelected) {
                    item.isSelected = false;
                    selectedImgs.remove(item.imageId);
                } else {
                    if (selectedImgs.size() >= availableSize) {
                        ToastShow.showShort(ImageChooseActivity.this, "最多选择" + availableSize + "张图片");
                        return;
                    }
                    item.isSelected = true;
                    selectedImgs.put(item.imageId, item);
                }
                textView.setText("完成" + "(" + selectedImgs.size() + "/" + availableSize + ")");
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getdata() {
        mDataList = (List<ImageInfo>) getIntent().getSerializableExtra("iamge_list");
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mBucketName = getIntent().getStringExtra("bucketName");
        if (TextUtils.isEmpty(mBucketName)) {
            mBucketName = "请选择";
        }
        availableSize = getIntent().getIntExtra("AvailableSize", Constants.MAX_IMAGE_SIZE);
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }
}
