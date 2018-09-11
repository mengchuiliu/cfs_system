package entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/25.
 * 通用实体类
 */

public class CommonItem<T> implements Serializable {
    private static final long serialVersionUID = 1818888360006359321L;
    private String name = "";
    private String content = "";
    private String hintContent = "";
    private int type;//显示类型
    private int position;//item的位置
    private int icon;//图标
    private boolean isClick;
    private boolean isLineShow;
    private boolean enable = true;
    private String date = "";
    private String remark;
    private String payType;

    private double percent;

    private List<T> list;
    private T item;

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHintContent() {
        return hintContent;
    }

    public void setHintContent(String hintContent) {
        this.hintContent = hintContent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public boolean isLineShow() {
        return isLineShow;
    }

    public void setLineShow(boolean lineShow) {
        isLineShow = lineShow;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "CommonItem{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", hintContent='" + hintContent + '\'' +
                ", type=" + type +
                ", position=" + position +
                ", icon=" + icon +
                ", isClick=" + isClick +
                ", isLineShow=" + isLineShow +
                ", enable=" + enable +
                ", date='" + date + '\'' +
                ", remark='" + remark + '\'' +
                ", percent=" + percent +
                ", list=" + list +
                '}';
    }
}
