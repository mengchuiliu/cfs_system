package entity;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 *
 * @author mengchuiliu
 *         用户对象
 */

public class Users {
    private int pactId;//合同id
    private String content = "";//贷款信息内容
    private List<CustomerData> customerDatas;//客户资料列表

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPactId() {
        return pactId;
    }

    public void setPactId(int pactId) {
        this.pactId = pactId;
    }

    public List<CustomerData> getCustomerDatas() {
        return customerDatas;
    }

    public void setCustomerDatas(List<CustomerData> customerDatas) {
        this.customerDatas = customerDatas;
    }

    @Override
    public String toString() {
        return "Users{" +
                ",pactId=" + pactId +
                ", content='" + content + '\'' +
                ", customerDatas=" + customerDatas +
                '}';
    }
}
