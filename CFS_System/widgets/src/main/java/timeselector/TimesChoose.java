package timeselector;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxiao.widgets.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 双日期选择器
 * Created by mengchuiliu on 2017/3/10.
 *
 * @author mengchuiliu
 */
public class TimesChoose {
    public interface TimeResultHandler {
        void handle(String time, String endtime);
    }

    public enum SCROLLTYPE {
        YEAR(1),
        MONTH(2),
        DAY(4),
        HOUR(8),
        MINUTE(16);

        SCROLLTYPE(int value) {
            this.value = value;
        }

        public int value;

    }

    private int scrollUnits = SCROLLTYPE.YEAR.value + SCROLLTYPE.MONTH.value + SCROLLTYPE.DAY.value + SCROLLTYPE.HOUR.value + SCROLLTYPE.MINUTE.value;
    private TimeResultHandler handler;
    private Context context;
    private final String FORMAT_STR = "yyyy-MM-dd";

    private PopupWindow seletorDialog;
    private PickerView year_pv, year_pv1;
    private PickerView month_pv, month_pv1;
    private PickerView day_pv, day_pv1;

    private final int MAXMONTH = 12;

    private ArrayList<String> year, month, day, year1, month1, day1;
    private int startYear, startMonth, startDay, endYear, endMonth, endDay;
    private boolean spanYear, spanMon, spanDay;
    private Calendar selectedCalender = Calendar.getInstance();
    private Calendar selectedCalender1 = Calendar.getInstance();
    private final long ANIMATORDELAY = 200L;
    private final long CHANGEDELAY = 90L;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private TextView tv_cancle;
    private TextView tv_select;

    /**
     * @param context
     * @param resultHandler 选取时间后回调
     * @param startDate     format："yyyy-MM-dd HH:mm"
     * @param endDate
     */
    public TimesChoose(Context context, TimeResultHandler resultHandler, String startDate, String endDate) {
        this.context = context;
        this.handler = resultHandler;
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(parse(startDate, FORMAT_STR));
        endCalendar.setTime(parse(endDate, FORMAT_STR));
        initView();
    }

    public void show(View parent) {
        if (startCalendar.getTime().getTime() >= endCalendar.getTime().getTime()) {
            Toast.makeText(context, "起始时间应小于结束时间", Toast.LENGTH_LONG).show();
            return;
        }
        initParameter();
        initTimer();
        addListener();
        if (seletorDialog != null)
            seletorDialog.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void dismiss() {
        if (seletorDialog != null && seletorDialog.isShowing())
            seletorDialog.dismiss();
    }

    private void initView() {
        if (seletorDialog == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.pop_selector_time, null);
            year_pv = (PickerView) view.findViewById(R.id.year_pv);
            month_pv = (PickerView) view.findViewById(R.id.month_pv);
            day_pv = (PickerView) view.findViewById(R.id.day_pv);
            year_pv1 = (PickerView) view.findViewById(R.id.end_year_pv);
            month_pv1 = (PickerView) view.findViewById(R.id.end_month_pv);
            day_pv1 = (PickerView) view.findViewById(R.id.end_day_pv);

            tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
            tv_select = (TextView) view.findViewById(R.id.tv_complete);
            tv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            tv_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedCalender.getTime().getTime() > selectedCalender1.getTime().getTime()) {
                        Toast.makeText(context, "开始时间不能大于结束时间!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    handler.handle(format(selectedCalender.getTime(), FORMAT_STR), format(selectedCalender1.getTime(), FORMAT_STR));
                    dismiss();
                }
            });
            seletorDialog = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            seletorDialog.setAnimationStyle(android.R.style.Animation_InputMethod);
            seletorDialog.setFocusable(true);
            seletorDialog.setOutsideTouchable(true);
            seletorDialog.setBackgroundDrawable(new BitmapDrawable());
            seletorDialog.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private void initParameter() {
        startYear = startCalendar.get(Calendar.YEAR);
        startMonth = startCalendar.get(Calendar.MONTH) + 1;
        startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        endYear = endCalendar.get(Calendar.YEAR);
        endMonth = endCalendar.get(Calendar.MONTH) + 1;
        endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        spanYear = true;
//        spanYear = startYear != endYear;
//        spanMon = (!spanYear) && (startMonth != endMonth);
//        spanDay = (!spanMon) && (startDay != endDay);
        selectedCalender.setTime(endCalendar.getTime());
        selectedCalender1.setTime(endCalendar.getTime());
    }

    private void initTimer() {
        initArrayList();
        if (spanYear) {
            for (int i = startYear; i <= 2060; i++) {
                year.add(String.valueOf(i));
                year1.add(String.valueOf(i));
            }
            for (int i = startMonth; i <= MAXMONTH; i++) {
                month.add(fomatTimeUnit(i));
                month1.add(fomatTimeUnit(i));
            }
            for (int i = startDay; i <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(fomatTimeUnit(i));
                day1.add(fomatTimeUnit(i));
            }

        } else if (spanMon) {
            year.add(String.valueOf(startYear));
            year1.add(String.valueOf(startYear));
            for (int i = startMonth; i <= MAXMONTH; i++) {
                month1.add(fomatTimeUnit(i));
                month1.add(fomatTimeUnit(i));
            }
            for (int i = startDay; i <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day1.add(fomatTimeUnit(i));
                day1.add(fomatTimeUnit(i));
            }

        } else if (spanDay) {
            year.add(String.valueOf(startYear));
            month.add(fomatTimeUnit(startMonth));
            year1.add(String.valueOf(startYear));
            month1.add(fomatTimeUnit(startMonth));
            for (int i = startDay; i <= endDay; i++) {
                day.add(fomatTimeUnit(i));
                day1.add(fomatTimeUnit(i));
            }
        }
        loadComponent();
    }

    private String fomatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void initArrayList() {
        if (year == null) year = new ArrayList<>();
        if (month == null) month = new ArrayList<>();
        if (day == null) day = new ArrayList<>();
        year.clear();
        month.clear();
        day.clear();
        if (year1 == null) year1 = new ArrayList<>();
        if (month1 == null) month1 = new ArrayList<>();
        if (day1 == null) day1 = new ArrayList<>();
        year1.clear();
        month1.clear();
        day1.clear();
    }


    private void addListener() {
        year_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.YEAR, Integer.parseInt(text));
                monthChange();
            }
        });
        month_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.MONTH, Integer.parseInt(text) - 1);
                dayChange();
            }
        });
        day_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text));
            }
        });
        year_pv1.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender1.set(Calendar.YEAR, Integer.parseInt(text));
                monthChange1();
            }
        });
        month_pv1.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender1.set(Calendar.MONTH, Integer.parseInt(text) - 1);
                dayChange1();
            }
        });
        day_pv1.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text));
            }
        });

    }

    private void loadComponent() {
        year_pv.setData(year);
        month_pv.setData(month);
        day_pv.setData(day);
        year_pv.setSelected(fomatTimeUnit(endYear));
        month_pv.setSelected(fomatTimeUnit(endMonth));
        day_pv.setSelected(fomatTimeUnit(endDay));

        year_pv1.setData(year1);
        month_pv1.setData(month1);
        day_pv1.setData(day1);
        year_pv1.setSelected(fomatTimeUnit(endYear));
        month_pv1.setSelected(fomatTimeUnit(endMonth));
        day_pv1.setSelected(fomatTimeUnit(endDay));
        excuteScroll();
    }

    private void excuteScroll() {
        year_pv.setCanScroll(year.size() > 1 && (scrollUnits & SCROLLTYPE.YEAR.value) == SCROLLTYPE.YEAR.value);
        month_pv.setCanScroll(month.size() > 1 && (scrollUnits & SCROLLTYPE.MONTH.value) == SCROLLTYPE.MONTH.value);
        day_pv.setCanScroll(day.size() > 1 && (scrollUnits & SCROLLTYPE.DAY.value) == SCROLLTYPE.DAY.value);
        year_pv1.setCanScroll(year1.size() > 1 && (scrollUnits & SCROLLTYPE.YEAR.value) == SCROLLTYPE.YEAR.value);
        month_pv1.setCanScroll(month1.size() > 1 && (scrollUnits & SCROLLTYPE.MONTH.value) == SCROLLTYPE.MONTH.value);
        day_pv1.setCanScroll(day1.size() > 1 && (scrollUnits & SCROLLTYPE.DAY.value) == SCROLLTYPE.DAY.value);
    }

    private void monthChange() {
        month.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        if (selectedYear == startYear) {
            for (int i = startMonth; i <= MAXMONTH; i++) {
                month.add(fomatTimeUnit(i));
            }
        } else if (selectedYear == endYear) {
            for (int i = 1; i <= MAXMONTH; i++) {
                month.add(fomatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= MAXMONTH; i++) {
                month.add(fomatTimeUnit(i));
            }
        }
        selectedCalender.set(Calendar.MONTH, Integer.parseInt(month.get(0)) - 1);
        month_pv.setData(month);
        month_pv.setSelected(0);
        excuteAnimator(ANIMATORDELAY, month_pv);
        month_pv.postDelayed(new Runnable() {
            @Override
            public void run() {
                dayChange();
            }
        }, CHANGEDELAY);
    }

    private void monthChange1() {
        month1.clear();
        int selectedYear = selectedCalender1.get(Calendar.YEAR);
        if (selectedYear == startYear) {
            for (int i = startMonth; i <= MAXMONTH; i++) {
                month1.add(fomatTimeUnit(i));
            }
        } else if (selectedYear == endYear) {
            for (int i = 1; i <= MAXMONTH; i++) {
                month1.add(fomatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= MAXMONTH; i++) {
                month1.add(fomatTimeUnit(i));
            }
        }
        selectedCalender1.set(Calendar.MONTH, Integer.parseInt(month1.get(0)) - 1);
        month_pv1.setData(month1);
        month_pv1.setSelected(0);
        excuteAnimator(ANIMATORDELAY, month_pv1);
        month_pv1.postDelayed(new Runnable() {
            @Override
            public void run() {
                dayChange1();
            }
        }, CHANGEDELAY);
    }

    private void dayChange() {
        day.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (int i = startDay; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(fomatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(fomatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(fomatTimeUnit(i));
            }
        }
        selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(0)));
        day_pv.setData(day);
        day_pv.setSelected(0);
        excuteAnimator(ANIMATORDELAY, day_pv);
        excuteScroll();
    }

    private void dayChange1() {
        day1.clear();
        int selectedYear = selectedCalender1.get(Calendar.YEAR);
        int selectedMonth = selectedCalender1.get(Calendar.MONTH) + 1;
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (int i = startDay; i <= selectedCalender1.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day1.add(fomatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day1.add(fomatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= selectedCalender1.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day1.add(fomatTimeUnit(i));
            }
        }
        selectedCalender1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day1.get(0)));
        day_pv1.setData(day1);
        day_pv1.setSelected(0);
        excuteAnimator(ANIMATORDELAY, day_pv1);
        excuteScroll();
    }

    private void excuteAnimator(long ANIMATORDELAY, View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(ANIMATORDELAY).start();
    }

    /**
     * 设置选取时间文本 默认"选择"
     */
    public void setNextBtTip(String str) {
        tv_select.setText(str);
    }

    public int setScrollUnit(SCROLLTYPE... scrolltypes) {
        scrollUnits = 0;
        for (SCROLLTYPE scrolltype : scrolltypes) {
            scrollUnits ^= scrolltype.value;
        }
        return scrollUnits;
    }

    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */
    public Date parse(String strDate, String pattern) {
        if (TextUtils.isEmpty(strDate)) {
            return null;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用用户格式格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */
    public String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }
}
