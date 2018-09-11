package com.xxjr.cfs_system.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

/**
 * Created by Administrator on 2017/11/29.
 */

public class OpenFileUtils {
    /**
     * 私有的构造函数
     */
    private OpenFileUtils() {

    }

    private static class SingletonHolder {
        private static OpenFileUtils instance = new OpenFileUtils();
    }

    public static OpenFileUtils getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 声明各种类型文件的dataType
     **/
    private static final String DATA_TYPE_ALL = "*/*";//未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
    private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
    private static final String DATA_TYPE_VIDEO = "video/*";
    private static final String DATA_TYPE_AUDIO = "audio/*";
    private static final String DATA_TYPE_HTML = "text/html";
    private static final String DATA_TYPE_IMAGE = "image/*";
    private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
    private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
    private static final String DATA_TYPE_WORD = "application/msword";
    private static final String DATA_TYPE_CHM = "application/x-chm";
    private static final String DATA_TYPE_TXT = "text/plain";
    private static final String DATA_TYPE_PDF = "application/pdf";

    /**
     * 打开文件
     *
     * @param filePath 文件的全路径，包括到文件名
     */
    public void openFile(Context mContext, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            //如果文件不存在
            Toast.makeText(mContext, "打开失败!文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
      /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
      /* 依扩展名的类型决定MimeType */
        Intent intent;
        switch (end) {
            case "m4a":
            case "mp3":
            case "mid":
            case "xmf":
            case "ogg":
            case "wav":
                intent = generateVideoAudioIntent(mContext, filePath, DATA_TYPE_AUDIO);
                break;
            case "3gp":
            case "mp4":
                intent = generateVideoAudioIntent(mContext, filePath, DATA_TYPE_VIDEO);
                break;
            case "jpg":
            case "gif":
            case "png":
            case "jpeg":
            case "bmp":
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_IMAGE);
                break;
            case "apk":
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_APK);
                break;
            case "html":
            case "htm":
                intent = generateHtmlFileIntent(filePath);
                break;
            case "ppt":
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_PPT);
                break;
            case "xls":
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_EXCEL);
                break;
            case "doc":
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_WORD);
                break;
            case "pdf":
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_PDF);
                break;
            case "chm":
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_CHM);
                break;
            case "txt":
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_TXT);
                break;
            default:
                intent = generateCommonIntent(mContext, filePath, DATA_TYPE_ALL);
                break;
        }
        mContext.startActivity(intent);
    }

    /**
     * 产生打开视频或音频的Intent
     *
     * @param filePath 文件路径
     * @param dataType 文件类型
     * @return
     */
    private Intent generateVideoAudioIntent(Context mContext, String filePath, String dataType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        File file = new File(filePath);
        intent.setDataAndType(getUri(mContext, intent, file), dataType);
        return intent;
    }

    /**
     * 产生打开网页文件的Intent
     *
     * @param filePath 文件路径
     * @return
     */
    private Intent generateHtmlFileIntent(String filePath) {
        Uri uri = Uri.parse(filePath)
                .buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content")
                .encodedPath(filePath)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, DATA_TYPE_HTML);
        return intent;
    }

    /**
     * 产生除了视频、音频、网页文件外，打开其他类型文件的Intent
     *
     * @param filePath 文件路径
     * @param dataType 文件类型
     * @return
     */
    private Intent generateCommonIntent(Context mContext, String filePath, String dataType) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(filePath);
        Uri uri = getUri(mContext, intent, file);
        intent.setDataAndType(uri, dataType);
        return intent;
    }

    /**
     * 获取对应文件的Uri
     *
     * @param intent 相应的Intent
     * @param file   文件对象
     * @return
     */
    private Uri getUri(Context mContext, Intent intent, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判断版本是否在7.0以上
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


    //7。0以上权限
    public static Uri getUri(Context mContext, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判断版本是否在7.0以上
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
