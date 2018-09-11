package com.xxjr.cfs_system.LuDan.view.post_image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.LuDan.view.activitys.PostActivity;

import java.util.ArrayList;
import java.util.List;

import entity.ImageInfo;

@SuppressLint("SetTextI18n")
public class ImageZoomActivity extends Activity {
    private ViewPager pager;
    private MyPageAdapter adapter;
    private int currentPosition;
    private List<ImageInfo> mDataList = new ArrayList<>();
    private TextView sum;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        initData();

        findViewById(R.id.zoom_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.zoom_del).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mDataList.size() == 1) {
                    removeImgs();
                    finish();
                } else {
                    removeImg(currentPosition);
                    pager.removeAllViews();
                    adapter.removeView(currentPosition);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(22);
        this.finish();
    }

    private void initData() {
        currentPosition = getIntent().getIntExtra("position", 0);
        mDataList = PostActivity.Companion.getMDataList();
        sum = findViewById(R.id.sum);
        sum.setText("" + (currentPosition + 1) + "/" + mDataList.size());
        pager = findViewById(R.id.viewpager);
        pager.addOnPageChangeListener(pageChangeListener);
        adapter = new MyPageAdapter(mDataList);
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentPosition);
    }

    private void removeImgs() {
        mDataList.clear();
    }

    private void removeImg(int location) {
        if (location + 1 <= mDataList.size()) {
            mDataList.remove(location);
        }
        sum.setText("" + (location + 1) + "/" + mDataList.size());
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            currentPosition = arg0;
            sum.setText("" + (currentPosition + 1) + "/" + mDataList.size());
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    class MyPageAdapter extends PagerAdapter {
        private List<ImageInfo> dataList = new ArrayList<>();
        private ArrayList<ImageView> mViews = new ArrayList<>();

        public MyPageAdapter(List<ImageInfo> dataList) {
            this.dataList = dataList;
            int size = dataList.size();
            for (int i = 0; i != size; i++) {
                ImageView iv = new ImageView(ImageZoomActivity.this);
                ImageDisplayer.getInstance(ImageZoomActivity.this).displayBmp(iv, null, dataList.get(i).sourcePath, false);
                iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mViews.add(iv);
            }
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public Object instantiateItem(View arg0, int arg1) {
            ImageView iv = mViews.get(arg1);
            ((ViewPager) arg0).addView(iv);
            return iv;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            if (mViews.size() >= arg1 + 1) {
                ((ViewPager) arg0).removeView(mViews.get(arg1));
            }
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        public void removeView(int position) {
            if (position + 1 <= mViews.size()) {
                mViews.remove(position);
            }
        }
    }
}