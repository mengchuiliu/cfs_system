package com.xxjr.cfs_system.LuDan.view.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.widgets.PopWindow;
import com.xxjr.cfs_system.LuDan.adapters.PersonalAdapter;
import com.xxjr.cfs_system.LuDan.adapters.SaveAdapterData;
import com.xxjr.cfs_system.LuDan.adapters.SetText;
import com.xxjr.cfs_system.LuDan.presenter.PersonalPresenter;
import com.xxjr.cfs_system.LuDan.view.viewinter.PersonalVInter;
import com.xxjr.cfs_system.ViewsHolder.PopChoose;
import com.xxjr.cfs_system.clipheadphoto.ClipActivity;
import com.xxjr.cfs_system.main.BaseActivity;
import com.xxjr.cfs_system.tools.BitmapManage;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.DateUtil;
import com.xxjr.cfs_system.tools.ToastShow;
import com.xxjr.cfs_system.tools.Utils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import entity.CommonItem;
import timeselector.TimeSelector;

public class PersonalInfoActivity extends BaseActivity<PersonalPresenter, PersonalInfoActivity> implements PersonalVInter {
    private SparseArray<String> sparse;
    public SetText setText;
    private PopupWindow popWindow, portraitPop;
    private TimeSelector timeSelector;
    public static final int PHOTOZOOM = 0; // 相册
    public static final int PHOTOTAKE = 1; // 拍照
    public static final int IMAGE_COMPLETE = 2; // 结果
    String photoSaveName = "";
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.recycle_person_info)
    RecyclerView recyclePersonInfo;
    @Bind(R.id.fl_water)
    FrameLayout flWater;

    @Override
    protected PersonalPresenter getPresenter() {
        return new PersonalPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_info;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getToolbarTitle().setText("个人信息");
        TextView textView = getSubTitle();
        textView.setText("保存");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getData(0, presenter.getSaveParam(), true);
            }
        });
        AndPermission.with(this)
                .requestCode(Constants.REQUEST_CODE_PERMISSION_SD)
                .permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(presenter.getPermissioner())
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(PersonalInfoActivity.this, rationale).show();
                    }
                })
                .start();
        initTimeSelector();
        presenter.setDefaultValue();
        presenter.getChooseData();
    }

    private void initTimeSelector() {
        timeSelector = new TimeSelector(PersonalInfoActivity.this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                setText.setText(time);
            }
        }, "1900-01-01", DateUtil.getCurDate());
        timeSelector.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR, TimeSelector.SCROLLTYPE.MONTH, TimeSelector.SCROLLTYPE.DAY);
    }

    //更换头像需要的权限申请
    private void getPermissions() {
        AndPermission.with(this)
                .requestCode(Constants.REQUEST_CODE_PERMISSION_MORE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .callback(presenter.getPermissioner())
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(PersonalInfoActivity.this, rationale).show();
                    }
                })
                .start();
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    public void showMsg(String msg) {
        ToastShow.showShort(PersonalInfoActivity.this, msg);
    }

    public void set(SetText setText) {
        this.setText = setText;
    }

    @Override
    public SparseArray<String> getSparseData() {
        return sparse;
    }

    @Override
    public void hidePop() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    @Override
    public void over() {
        PersonalInfoActivity.this.finish();
    }

    @Override
    public void showSexPop() {
        if (popWindow == null) {
            popWindow = PopChoose.showChooseType(PersonalInfoActivity.this, recyclePersonInfo, "性别",
                    Utils.getTypeDataList("SexType"), 1, false);
        } else {
            popWindow.showAtLocation(recyclePersonInfo, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void showPortraitPop() {
        if (portraitPop == null) {
            portraitPop = PopWindow.choosePortrait(PersonalInfoActivity.this, onClickListener, recyclePersonInfo);
        } else {
            portraitPop.showAtLocation(recyclePersonInfo, Gravity.CENTER, 0, 0);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (portraitPop != null && portraitPop.isShowing()) {
                portraitPop.dismiss();
            }
            switch (view.getId()) {
                case R.id.photograph://拍照
                    takePicture();
                    break;
                case R.id.albums://相册
                    Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(openAlbumIntent, PHOTOZOOM);
                    break;
                case R.id.cancel://取消
                    break;
            }
        }
    };

    @Override
    public void showBirthDate() {
        if (timeSelector != null) {
            timeSelector.show();
        } else {
            initTimeSelector();
            timeSelector.show();
        }
    }

    @Override
    public void setPortrait(Bitmap bitmap) {
        if (bitmap != null) {
            ivHead.setImageBitmap(bitmap);
        }
    }

    @Override
    public void initReacycle(List<CommonItem> commonItems) {
        recyclePersonInfo.setLayoutManager(new LinearLayoutManager(PersonalInfoActivity.this));
        PersonalAdapter adapter = new PersonalAdapter(PersonalInfoActivity.this, commonItems);
        adapter.setSave(new SaveAdapterData() {
            @Override
            public void save(SparseArray sparseArray) {
                sparse = sparseArray;
            }
        });
        recyclePersonInfo.setAdapter(adapter);
    }

    @OnClick(R.id.water)
    public void onViewClicked() {
        getPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Uri uri;
        switch (requestCode) {
            case PHOTOZOOM:// 相册
                if (data == null || data.getData() == null || data.getData().getPath() == null) {
                    return;
                }
                String path = data.getData().getPath();
                if (!new File(path).exists()) {
                    uri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    path = cursor.getString(column_index);// 图片在的路径
                    cursor.close();
                }
                Intent intent3 = new Intent(PersonalInfoActivity.this, ClipActivity.class);
                intent3.putExtra("path", path);
                startActivityForResult(intent3, IMAGE_COMPLETE);
                break;
            case PHOTOTAKE:// 拍照
                String photoPath = getPhotoSavePath() + photoSaveName;
                Intent intent2 = new Intent(PersonalInfoActivity.this, ClipActivity.class);
                intent2.putExtra("path", photoPath);
                startActivityForResult(intent2, IMAGE_COMPLETE);
                break;
            case IMAGE_COMPLETE://剪辑返回的头像
                final String tempPath = data.getStringExtra("path");
                if (TextUtils.isEmpty(tempPath)) {
                    String locpath = Constants.PortraitPath + Hawk.get("UserID") + ".png";
                    File file = new File(locpath);
                    if (file.exists()) {
                        ivHead.setImageBitmap(BitmapManage.toRoundBitmap(BitmapFactory.decodeFile(locpath)));
                    } else {
                        presenter.getData(1, presenter.getPortraitParam(), true);
                    }
                } else {
                    ivHead.setImageBitmap(BitmapManage.toRoundBitmap(BitmapFactory.decodeFile(tempPath)));
                }
                break;
            case Constants.RESULT_CODE_SETTING: {
                ToastShow.showShort(PersonalInfoActivity.this, "权限拒绝");
                break;
            }
            default:
                break;
        }
    }

    //拍照
    public void takePicture() {
        photoSaveName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        String photoSavePath = getPhotoSavePath();
        // 下面这句指定调用相机拍照后的照片存储的路径
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(PersonalInfoActivity.this, "com.xiaoxiao.ludan.fileprovider", new File(photoSavePath, photoSaveName));//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
        }
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, PHOTOTAKE);
    }

    private String getPhotoSavePath() {
        File file = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");
        if (file.exists() || file.mkdir()) {
            return file.getPath() + File.separator;
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWater(flWater);
    }

    @Override
    protected void onDestroy() {
        popWindow = null;
        portraitPop = null;
        timeSelector = null;
        presenter.rxDeAttach();
        super.onDestroy();
    }
}
