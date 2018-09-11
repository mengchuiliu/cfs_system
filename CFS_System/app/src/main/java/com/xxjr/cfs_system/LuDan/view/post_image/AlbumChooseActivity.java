package com.xxjr.cfs_system.LuDan.view.post_image;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.adapters.AlbumAdapter;
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import entity.ImageBucket;

public class AlbumChooseActivity extends BaseActivity {
    private List<ImageBucket> mDataList = new ArrayList<>();
    AlbumAdapter mAdapter;
    private int availableSize;

    @Bind(R.id.albums_list)
    ListView mListView;

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album_choose;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getToolbarTitle().setText("相册");

        initData();
        mAdapter = new AlbumAdapter(this, mDataList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AlbumChooseActivity.this, ImageChooseActivity.class);
                intent.putExtra("iamge_list", (Serializable) mDataList.get(position).imageList);
                intent.putExtra("bucketName", mDataList.get(position).bucketName);
                intent.putExtra("AvailableSize", availableSize);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initData() {
        ImageFetcher mHelper = ImageFetcher.getInstance(getApplicationContext());
        mDataList = mHelper.getImagesBucketList(false);
        availableSize = getIntent().getIntExtra("AvailableSize", Constants.MAX_IMAGE_SIZE);
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 8) {
            if (data != null) {
                setResult(6, data);
                this.finish();
            }
        }
    }
}
