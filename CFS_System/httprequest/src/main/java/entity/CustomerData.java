package entity;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 *
 * @author mengchuiliu
 *         顾客上传的资料信息
 */

public class CustomerData implements Serializable {
    private static final long serialVersionUID = 1213249796015923123L;
    private String customerId;
    private String name;
    private ChooseType data;
    private List<ChooseType> allDatas;//id--->上传类型，paths--->图片路径集合

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChooseType getData() {
        return data;
    }

    public void setData(ChooseType data) {
        this.data = data;
    }

    public List<ChooseType> getAllDatas() {
        return allDatas;
    }

    public void setAllDatas(List<ChooseType> allDatas) {
        this.allDatas = allDatas;
    }

    public boolean checked(Context context) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(customerId)) {
            show(context, "客户不能为空!");
            return false;
        }
        if (data == null || data.getId() == 0) {
            show(context, "资料类型不能为空!");
            return false;
        } else if (data.getPaths() == null || data.getPaths().size() == 0) {
            show(context, "请选择上传的图片!");
            return false;
        }
        return true;
    }

    private void show(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String toString() {
        return "CustomerData{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", data=" + data +
                ", allDatas=" + allDatas +
                '}';
    }
}
