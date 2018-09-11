package entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 *
 * @author mengchuiliu
 * 选择类型
 */

public class ChooseType implements Serializable {
    private static final long serialVersionUID = 3929502359296674311L;
    private int id;//选择类型id
    private String content;//选择的类型内容
    private boolean isChoose = false;
    private String ids;
    private String account;
    private List<String> paths;//图片路径集合
    private String type;
    private String protocolNo;

    public ChooseType() {
    }

    public ChooseType(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public ChooseType(String ids, String content) {
        this.ids = ids;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    @Override
    public String toString() {
        return "ChooseType{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isChoose=" + isChoose +
                ", ids='" + ids + '\'' +
                ", account='" + account + '\'' +
                ", paths=" + paths +
                ", type='" + type + '\'' +
                ", protocolNo='" + protocolNo + '\'' +
                '}';
    }
}
