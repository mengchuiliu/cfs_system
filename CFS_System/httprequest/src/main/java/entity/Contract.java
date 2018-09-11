package entity;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/10.
 *
 * @author mengchuiliu
 * 合同列表
 */

public class Contract implements Serializable {
    private static final long serialVersionUID = -8397024623182205053L;
    private int ID;//合同id
    private String S1;
    private String S2;
    private int S3;
    private Object S4;
    private String S5;
    private String S6;
    private String S7;
    private Object S8;
    private double S9;
    private int S10;
    private Object S11;
    private int S12;
    private String S13;
    private Object S14;
    private Object S15;
    private Object S16;
    private String S17;
    private String CustomerIDs;
    private String CustomerNames;
    private String LoanIDs;
    private String LoanDescription;
    private Object Remark;
    private String CompanyID;
    private boolean DelMarker;
    private String InsertTime;
    private String UpdateTime;
    private String ClerkName;
    private String ZoneId;

    private String CustomerInfo;//客户信息
    private int AccompanyPeople;//谈单员id
    private String AccompanyPeopleName;
    private int WitnessId;//见证人id
    private String WitnessName;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getS1() {
        return S1;
    }

    public void setS1(String S1) {
        this.S1 = S1;
    }

    public String getS2() {
        return S2;
    }

    public void setS2(String S2) {
        this.S2 = S2;
    }

    public int getS3() {
        return S3;
    }

    public void setS3(int S3) {
        this.S3 = S3;
    }

    public Object getS4() {
        return S4;
    }

    public void setS4(Object S4) {
        this.S4 = S4;
    }

    public String getS5() {
        return S5;
    }

    public void setS5(String S5) {
        this.S5 = S5;
    }

    public String getS6() {
        return S6;
    }

    public void setS6(String S6) {
        this.S6 = S6;
    }

    public String getS7() {
        return S7;
    }

    public void setS7(String S7) {
        this.S7 = S7;
    }

    public Object getS8() {
        return S8;
    }

    public void setS8(Object S8) {
        this.S8 = S8;
    }

    public double getS9() {
        return S9;
    }

    public void setS9(double S9) {
        this.S9 = S9;
    }

    public int getS10() {
        return S10;
    }

    public void setS10(int S10) {
        this.S10 = S10;
    }

    public Object getS11() {
        return S11;
    }

    public void setS11(Object S11) {
        this.S11 = S11;
    }

    public int getS12() {
        return S12;
    }

    public void setS12(int S12) {
        this.S12 = S12;
    }

    public String getS13() {
        return S13;
    }

    public void setS13(String S13) {
        this.S13 = S13;
    }

    public Object getS14() {
        return S14;
    }

    public void setS14(Object S14) {
        this.S14 = S14;
    }

    public Object getS15() {
        return S15;
    }

    public void setS15(Object S15) {
        this.S15 = S15;
    }

    public Object getS16() {
        return S16;
    }

    public void setS16(Object S16) {
        this.S16 = S16;
    }

    public String getS17() {
        return S17;
    }

    public void setS17(String S17) {
        this.S17 = S17;
    }

    public String getCustomerIDs() {
        return CustomerIDs;
    }

    public void setCustomerIDs(String CustomerIDs) {
        this.CustomerIDs = CustomerIDs;
    }

    public String getCustomerNames() {
        return CustomerNames;
    }

    public void setCustomerNames(String CustomerNames) {
        this.CustomerNames = CustomerNames;
    }

    public String getLoanIDs() {
        return LoanIDs;
    }

    public void setLoanIDs(String LoanIDs) {
        this.LoanIDs = LoanIDs;
    }

    public String getLoanDescription() {
        return LoanDescription;
    }

    public void setLoanDescription(String LoanDescription) {
        this.LoanDescription = LoanDescription;
    }

    public Object getRemark() {
        return Remark;
    }

    public void setRemark(Object Remark) {
        this.Remark = Remark;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String CompanyID) {
        this.CompanyID = CompanyID;
    }

    public boolean isDelMarker() {
        return DelMarker;
    }

    public void setDelMarker(boolean DelMarker) {
        this.DelMarker = DelMarker;
    }

    public String getInsertTime() {
        return InsertTime;
    }

    public void setInsertTime(String InsertTime) {
        this.InsertTime = InsertTime;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

    public String getCustomerInfo() {
        return CustomerInfo;
    }

    public void setCustomerInfo(String customerInfo) {
        CustomerInfo = customerInfo;
    }

    public String getClerkName() {
        return ClerkName;
    }

    public void setClerkName(String clerkName) {
        ClerkName = clerkName;
    }

    public String getZoneId() {
        return ZoneId;
    }

    public void setZoneId(String zoneId) {
        ZoneId = zoneId;
    }

    public int getAccompanyPeople() {
        return AccompanyPeople;
    }

    public void setAccompanyPeople(int accompanyPeople) {
        AccompanyPeople = accompanyPeople;
    }

    public String getAccompanyPeopleName() {
        return AccompanyPeopleName;
    }

    public void setAccompanyPeopleName(String accompanyPeopleName) {
        AccompanyPeopleName = accompanyPeopleName;
    }

    public int getWitnessId() {
        return WitnessId;
    }

    public void setWitnessId(int witnessId) {
        WitnessId = witnessId;
    }

    public String getWitnessName() {
        return WitnessName;
    }

    public void setWitnessName(String witnessName) {
        WitnessName = witnessName;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "ID=" + ID +
                ", S1='" + S1 + '\'' +
                ", S2='" + S2 + '\'' +
                ", S3=" + S3 +
                ", S4=" + S4 +
                ", S5='" + S5 + '\'' +
                ", S6='" + S6 + '\'' +
                ", S7='" + S7 + '\'' +
                ", S8=" + S8 +
                ", S9=" + S9 +
                ", S10=" + S10 +
                ", S11=" + S11 +
                ", S12=" + S12 +
                ", S13='" + S13 + '\'' +
                ", S14=" + S14 +
                ", S15=" + S15 +
                ", S16=" + S16 +
                ", S17='" + S17 + '\'' +
                ", CustomerIDs='" + CustomerIDs + '\'' +
                ", CustomerNames='" + CustomerNames + '\'' +
                ", LoanIDs='" + LoanIDs + '\'' +
                ", LoanDescription='" + LoanDescription + '\'' +
                ", Remark=" + Remark +
                ", CompanyID='" + CompanyID + '\'' +
                ", DelMarker=" + DelMarker +
                ", InsertTime='" + InsertTime + '\'' +
                ", UpdateTime='" + UpdateTime + '\'' +
                ", ClerkName='" + ClerkName + '\'' +
                ", CustomerInfo='" + CustomerInfo + '\'' +
                '}';
    }


    public boolean checkOk(Context context) {
        if (S3 == 0) {
            show(context, "请选择客户经理!");
            return false;
        } else if (TextUtils.isEmpty(S7)) {
            show(context, "合同费用不能为空!");
            return false;
        } else if (TextUtils.isEmpty(S6)) {
            show(context, "合同项目不能为空!");
            return false;
        }
        return true;
    }

    private void show(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
