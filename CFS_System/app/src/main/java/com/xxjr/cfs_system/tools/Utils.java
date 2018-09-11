package com.xxjr.cfs_system.tools;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xxjr.cfs_system.services.CacheProvide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import entity.ChooseType;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/3/14 .
 *
 * @author mengchuiliu
 */
@SuppressLint("SimpleDateFormat")
public class Utils {
    //图片缓存
    private static HashMap<String, Bitmap> bitmapCache = null;

    public static Bitmap getBitmapFromCache(String path) {
        if (bitmapCache == null) {
            bitmapCache = new HashMap<>();
        }
        return bitmapCache.get(path);
    }

    public static void setBitmapToCache(String path, Bitmap bitmap) {
        if (bitmapCache == null) {
            bitmapCache = new HashMap<>();
        }
        if (bitmap == null) {
            bitmapCache.remove(path);
        } else {
            bitmapCache.put(path, bitmap);
        }
    }

    /**
     * 获取用户的头像
     *
     * @return 头像
     */
    public static Bitmap getPortraitBitmap() {
        try {
            String locpath = Constants.PortraitPath + Hawk.get("UserID") + ".png";
            File file = new File(locpath);
            if (file.exists()) {
                Bitmap bitmap = getBitmapFromCache(file.getPath());
//                if (bitmap == null) {
//                    byte[] data = readStream(new FileInputStream(file));
//                    bitmap = BitmapManage.compressBitmap(data, 256, 256);
//                    setBitmapToCache(file.getPath(), bitmap);
//                }
                return bitmap;
            }
        } catch (Exception e) {
            Log.e("test", "Util getPortraitBitmap cause an Exception=>:", e);
        }
        return null;
    }

    /**
     * 保存用户头像到内存中
     *
     * @param bitmap 用户的头像
     */
    public static void setPortraitBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("test", "Utils setPortraitBitmap bitmap==null.");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), "/CFS/cache");
        if (file.exists() || file.mkdirs()) {
            String locpath = Constants.PortraitPath + Hawk.get("UserID") + ".png";
            BitmapManage.savePhotoToSDCard(bitmap, locpath);
        }
    }

    public static byte[] readStream(InputStream in) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        in.close();
        return outputStream.toByteArray();
    }

    /*
     * MD5加密
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            System.out.println("MD5 加密异常");
        }
        assert messageDigest != null;
        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        for (byte aByteArray : byteArray) {
            if (Integer.toHexString(0xFF & aByteArray).length() == 1) {
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & aByteArray));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
            }
        }
        // 16位加密，从第9位到25位
        // return md5StrBuff.substring(8, 24).toString().toUpperCase();
        // 32位大写MD5加密
        return md5StrBuff.toString();
    }

    // 过滤字符串中的特殊字符
    public static String StringFilter(String str) throws PatternSyntaxException {
        // 要过滤掉的字符
        String regEx = "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？\"\n\t]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    //验证是否正确手机
    public static boolean isMobileNO(String mobiles) {
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][34578]\\d{9}";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (wm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.getDefaultDisplay().getRealSize(point);
            } else {
                wm.getDefaultDisplay().getSize(point);
            }
        }
        return point.x;
    }

    /**
     * 检查sdcard是否存在
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 从短信字符窜提取验证码
     *
     * @param body      短信内容
     * @param YZMLENGTH 验证码的长度 一般6位或者4位
     * @return 接取出来的验证码
     * @about 有些验证码是纯数字的那么直接用这个就可以了 Pattern p
     * =Pattern.compile("(?<![0-9])([0-9]{" + YZMLENGTH+ "})(?![0-9])");
     */
    public static String getYZM(String body, int YZMLENGTH) {
        // 首先([a-zA-Z0-9]{YZMLENGTH})是得到一个连续的六位数字字母组合
        // (?<![a-zA-Z0-9])负向断言([0-9]{YZMLENGTH})前面不能有数字
        // (?![a-zA-Z0-9])断言([0-9]{YZMLENGTH})后面不能有数字出现
        // Pattern p = Pattern.compile("(?<![a-zA-Z0-9])([a-zA-Z0-9]{" +
        // YZMLENGTH
        // + "})(?![a-zA-Z0-9])");
        Pattern p = Pattern.compile("(?<![0-9])([0-9]{" + YZMLENGTH + "})(?![0-9])");
        Matcher m = p.matcher(body);
        if (m.find()) {
            return m.group(0);
        }
        return null;
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr 身份证号
     * @return 有效：返回"" 无效：返回String信息
     * @throws ParseException
     */
    public static String IDCardValidate(String IDStr) {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
//            errorInfo = "请正确填写身份证号码！";
            return errorInfo;
        }
        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (!isNumeric(Ai)) {
//            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            errorInfo = "请正确填写身份证号码！";
            return errorInfo;
        }
        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (!isDataFormat(strYear + "-" + strMonth + "-" + strDay)) {
//            errorInfo = "身份证生日无效。";
            errorInfo = "请正确填写身份证号码！";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
//                errorInfo = "身份证生日不在有效范围。";
                errorInfo = "请正确填写身份证号码！";
                return errorInfo;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "异常错误";
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
//            errorInfo = "身份证月份无效";
            errorInfo = "请正确填写身份证号码！";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
//            errorInfo = "身份证日期无效";
            errorInfo = "请正确填写身份证号码！";
            return errorInfo;
        }
        // ================ 地区码时候有效 ================
        Hashtable<String, String> h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
//            errorInfo = "身份证地区编码错误。";
            errorInfo = "请正确填写身份证号码！";
            return errorInfo;
        }
        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (!Ai.equals(IDStr.toLowerCase())) {
                errorInfo = "身份证无效，不是合法的身份证号码";
//                errorInfo = "请正确填写身份证号码！";
                return errorInfo;
            }
        } else {
            return "";
        }
        return "";
    }

    //获取身份证出生日期
    public static String getIDCradBirth(String IDStr) {
        String Ai = "";
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        return strYear + "-" + strMonth + "-" + strDay;
    }

    //获取年龄
    public static int getIDCradAge(String IDStr) {
        String Ai = "";
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        String strYear = Ai.substring(6, 10);// 年份
        return DateUtil.getYear() - Integer.valueOf(strYear);
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    private static Hashtable<String, String> GetAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 验证日期字符串是否是YYYY-MM-DD格式
     *
     * @param str
     * @return
     */
    private static boolean isDataFormat(String str) {
        boolean flag = false;
        //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * @param type 数据类型json键
     * @return 返回选择数据类型列表
     */
    public static List<ChooseType> getTypeDataList(String type) {
        List<ChooseType> list = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey(type), ""));
        if (jsonArray != null && jsonArray.size() != 0) {
            ChooseType chooseType;
            for (int i = 0; i < jsonArray.size(); i++) {
                chooseType = new ChooseType();
                JSONObject object = jsonArray.getJSONObject(i);
                chooseType.setId(object.getIntValue("Value"));
                chooseType.setContent(object.getString("Name"));
                list.add(chooseType);
            }
        }
        return list;
    }

    public static String getTypeValue(List<ChooseType> types, int value) {
        for (ChooseType chooseType : types) {
            if (chooseType.getId() == value) {
                return chooseType.getContent();
            }
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(String updateTime) {
        if (TextUtils.isEmpty(updateTime)) {
            return "";
        }
        String time = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(updateTime);
            time = new SimpleDateFormat("yyyy-MM-dd").format(date);
            if (time.equals("1900-01-01")) {
                return "";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getTimeFormat(String updateTime) {
        if (TextUtils.isEmpty(updateTime)) {
            return "";
        }
        String time = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(updateTime);
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * @param updateTime 时间
     * @param pattern    传入的时间格式
     * @param end        最终返回时间格式
     */
    public static String FormatTime(String updateTime, String pattern, String end) {
        if (TextUtils.isEmpty(updateTime)) {
            return "";
        }
        String time = "";
        try {
            Date date = new SimpleDateFormat(pattern).parse(updateTime);
            time = new SimpleDateFormat(end).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * @param s
     * @return
     */
    public static String parseMoney(Object s) {
        DecimalFormat df = new DecimalFormat(",###,###,###.##");
        return df.format(s);
    }

    public static String parseTwoMoney(Object s) {
        DecimalFormat df = new DecimalFormat("###.##");
        return df.format(s);
    }

    //输出非科学计数法数据，除10000保留两位小数
    public static double div(double d1) {
        //  当然在此之前，你要判断分母是否为0，
        //  为0你可以根据实际需求做相应的处理
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(10000.0);
        return bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double getBigLong(String amount) {
        BigDecimal bigDecimal = new BigDecimal(amount);
        return bigDecimal.setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
//        return bigDecimal.setScale(0, BigDecimal.ROUND_UNNECESSARY).longValue();
    }

    /**
     * 大数输出 - 不使用科学技术法
     *
     * @param d1       被除数
     * @param divisor  除数
     * @param newScale 保留小数点后几位 (默认两位)
     * @return
     */
    public static double div(double d1, double divisor, int newScale) {
        if (divisor == 0) return 0.0;
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(divisor);
        return bd1.divide(bd2, newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 判断当前程序是否前台进程
     *
     * @return true->前台
     */
    public static boolean isCurAppTop(Context context) {
        if (context == null) {
            return false;
        }
        String curPackageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ActivityManager.RunningTaskInfo info = list.get(0);
            String topPackageName = info.topActivity.getPackageName();
            String basePackageName = info.baseActivity.getPackageName();
            return topPackageName.equals(curPackageName) && basePackageName.equals(curPackageName);
        }
        return false;
    }

    /**
     * 某段时间内不可多次点击
     *
     * @param time     上一次点击时间
     * @param interval 间隔时间
     * @return true->可以再次点击
     */
    public static boolean isClickAgain(long time, long interval) {
        return System.currentTimeMillis() - time > interval;
    }
}
