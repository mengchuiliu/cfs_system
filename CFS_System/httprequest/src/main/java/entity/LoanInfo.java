package entity;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/29.
 *
 * @author mengchuiliu
 * 贷款信息
 */

public class LoanInfo implements Serializable {
    private static final long serialVersionUID = 8804359205455350437L;
    private String LoanIDs;//合同下所有贷款id
    private String LoanDescription;//合同下所有贷款id
    private String loanCode;
    private String pactCode;
    private int ClerkID;//所属业务员id
    private String ClerkName;//所属业务员name
    private String CompanyID;
    private int bankId = 0;
    private String bankName;
    private int productId = 0;
    private String productName;
    private int loanType = 0;//贷款类型id
    private String loanTypeName;//贷款类型
    private double amount = 0;
    private int chargeWay = 2;
    private float percent = 0f;
    private double poundage = 0;
    private String customer;
    private String customerId;
    private int managerId;//银行经理
    private int mortgage = 0;
    private String mortgageName;
    private String note;
    private String schedule;//进度
    private int scheduleId;
    private String loanId;
    private String lendingId;//放款id
    private String YellowTime;
    private String RedTime;

    private boolean IsForeclosureFloor;//是否赎楼
    private String ForeclosureTime;
    private boolean IsPrepayment;//是否提前还款
    private int member;//赎楼员
    private String isNotary = "0";//是否已公证，是-->1，否-->0
    private String notaryTime;
    private String notaryNote;
    private double replyAmount;//批复金额
    private String account;//收款账号
    private String accountName;//收款人
    private String offer;
    private String approvalTime;
    private double monthAmount;//月供金额
    private double otherAmount;//其他月供
    private String otherPayRemark;//其他月供说明
    private String monthDate;
    private String returnTime;
    private double lendingAmount;
    private int lendingPeriod;//放款期数
    private String lendingTime;
    private String overTime;
    private String overNote;
    private int contractId;
    private String updateTime;
    private int NoAuditCost;
    private double PassAuditCostMoney;
    private double PassPayCostMoney;
    private double NoAuditCostMoney;
    private double CurrentRevenue;//转总部入账

    private int RefuseAuditCost = 0;
    private int PassAuditCost = 0;
    private int StoreNoAuditCnt = 0;
    private int MortNoAuditCnt = 0;

    private int IsCase = 0;//1->可以发起结案，0->不可以发起结案
    private float MortgageScore = 0f;
    private double InterestRate = 0.0;//利率
    private int PaymentMethod = -1;//还款类型
    private String PaymentName = "";//还款类型
    private String DisagreeReason = "";//异议原因

    public int getIsCase() {
        return IsCase;
    }

    public void setIsCase(int isCase) {
        IsCase = isCase;
    }

    public double getPassPayCostMoney() {
        return PassPayCostMoney;
    }

    public void setPassPayCostMoney(double passPayCostMoney) {
        PassPayCostMoney = passPayCostMoney;
    }

    public String getClerkName() {
        return ClerkName;
    }

    public void setClerkName(String clerkName) {
        ClerkName = clerkName;
    }

    public int getRefuseAuditCost() {
        return RefuseAuditCost;
    }

    public void setRefuseAuditCost(int refuseAuditCost) {
        RefuseAuditCost = refuseAuditCost;
    }

    public int getPassAuditCost() {
        return PassAuditCost;
    }

    public void setPassAuditCost(int passAuditCost) {
        PassAuditCost = passAuditCost;
    }

    public int getStoreNoAuditCnt() {
        return StoreNoAuditCnt;
    }

    public void setStoreNoAuditCnt(int storeNoAuditCnt) {
        StoreNoAuditCnt = storeNoAuditCnt;
    }

    public int getMortNoAuditCnt() {
        return MortNoAuditCnt;
    }

    public void setMortNoAuditCnt(int mortNoAuditCnt) {
        MortNoAuditCnt = mortNoAuditCnt;
    }

    public String getLoanIDs() {
        return LoanIDs;
    }

    public void setLoanIDs(String loanIDs) {
        LoanIDs = loanIDs;
    }

    public String getLoanDescription() {
        return LoanDescription;
    }

    public void setLoanDescription(String loanDescription) {
        LoanDescription = loanDescription;
    }

    private String recorder;

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public double getCurrentRevenue() {
        return CurrentRevenue;
    }

    public void setCurrentRevenue(double currentRevenue) {
        CurrentRevenue = currentRevenue;
    }

    public String getForeclosureTime() {
        return ForeclosureTime;
    }

    public void setForeclosureTime(String foreclosureTime) {
        ForeclosureTime = foreclosureTime;
    }

    public int getNoAuditCost() {
        return NoAuditCost;
    }

    public void setNoAuditCost(int noAuditCost) {
        NoAuditCost = noAuditCost;
    }

    public double getPassAuditCostMoney() {
        return PassAuditCostMoney;
    }

    public void setPassAuditCostMoney(double passAuditCostMoney) {
        PassAuditCostMoney = passAuditCostMoney;
    }

    public double getNoAuditCostMoney() {
        return NoAuditCostMoney;
    }

    public void setNoAuditCostMoney(double noAuditCostMoney) {
        NoAuditCostMoney = noAuditCostMoney;
    }

    public String getYellowTime() {
        return YellowTime;
    }

    public void setYellowTime(String yellowTime) {
        YellowTime = yellowTime;
    }

    public String getRedTime() {
        return RedTime;
    }

    public void setRedTime(String redTime) {
        RedTime = redTime;
    }

    public boolean getIsForeclosureFloor() {
        return IsForeclosureFloor;
    }

    public void setIsForeclosureFloor(boolean isForeclosureFloor) {
        IsForeclosureFloor = isForeclosureFloor;
    }

    public boolean isPrepayment() {
        return IsPrepayment;
    }

    public void setPrepayment(boolean prepayment) {
        IsPrepayment = prepayment;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLoanCode() {
        return loanCode;
    }

    public void setLoanCode(String loanCode) {
        this.loanCode = loanCode;
    }

    public String getPactCode() {
        return pactCode;
    }

    public void setPactCode(String pactCode) {
        this.pactCode = pactCode;
    }

    public int getClerkID() {
        return ClerkID;
    }

    public void setClerkID(int clerkID) {
        ClerkID = clerkID;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }

    public String getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(String approvalTime) {
        this.approvalTime = approvalTime;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getLoanType() {
        return loanType;
    }

    public void setLoanType(int loanType) {
        this.loanType = loanType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getChargeWay() {
        return chargeWay;
    }

    public void setChargeWay(int chargeWay) {
        this.chargeWay = chargeWay;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public double getPoundage() {
        return poundage;
    }

    public void setPoundage(double poundage) {
        this.poundage = poundage;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String id) {
        this.customerId = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getMortgage() {
        return mortgage;
    }

    public void setMortgage(int mortgage) {
        this.mortgage = mortgage;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLoanTypeName() {
        return loanTypeName;
    }

    public void setLoanTypeName(String loanTypeName) {
        this.loanTypeName = loanTypeName;
    }

    public String getMortgageName() {
        return mortgageName;
    }

    public void setMortgageName(String mortgageName) {
        this.mortgageName = mortgageName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getLendingId() {
        return lendingId;
    }

    public void setLendingId(String lendingId) {
        this.lendingId = lendingId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public String getIsNotary() {
        return isNotary;
    }

    public void setIsNotary(String isNotary) {
        this.isNotary = isNotary;
    }

    public String getNotaryTime() {
        return notaryTime;
    }

    public void setNotaryTime(String notaryTime) {
        this.notaryTime = notaryTime;
    }

    public String getNotaryNote() {
        return notaryNote;
    }

    public void setNotaryNote(String notaryNote) {
        this.notaryNote = notaryNote;
    }

    public double getReplyAmount() {
        return replyAmount;
    }

    public void setReplyAmount(double replyAmount) {
        this.replyAmount = replyAmount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public double getMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(double monthAmount) {
        this.monthAmount = monthAmount;
    }

    public double getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(double otherAmount) {
        this.otherAmount = otherAmount;
    }

    public String getOtherPayRemark() {
        return otherPayRemark;
    }

    public void setOtherPayRemark(String otherPayRemark) {
        this.otherPayRemark = otherPayRemark;
    }

    public String getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(String monthDate) {
        this.monthDate = monthDate;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public double getLendingAmount() {
        return lendingAmount;
    }

    public void setLendingAmount(double lendingAmount) {
        this.lendingAmount = lendingAmount;
    }

    public int getLendingPeriod() {
        return lendingPeriod;
    }

    public void setLendingPeriod(int lendingPeriod) {
        this.lendingPeriod = lendingPeriod;
    }

    public String getLendingTime() {
        return lendingTime;
    }

    public void setLendingTime(String lendingTime) {
        this.lendingTime = lendingTime;
    }

    public String getOverTime() {
        return overTime;
    }

    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }

    public String getOverNote() {
        return overNote;
    }

    public void setOverNote(String overNote) {
        this.overNote = overNote;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public float getMortgageScore() {
        return MortgageScore;
    }

    public void setMortgageScore(float mortgageScore) {
        MortgageScore = mortgageScore;
    }

    public double getInterestRate() {
        return InterestRate;
    }

    public void setInterestRate(double interestRate) {
        InterestRate = interestRate;
    }

    public int getPaymentMethod() {
        return PaymentMethod;
    }

    public String getPaymentName() {
        return PaymentName;
    }

    public void setPaymentName(String paymentName) {
        PaymentName = paymentName;
    }

    public void setPaymentMethod(int paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getDisagreeReason() {
        return DisagreeReason;
    }

    public void setDisagreeReason(String disagreeReason) {
        DisagreeReason = disagreeReason;
    }

    @Override
    public String toString() {
        return "LoanInfo{" +
                "loanCode='" + loanCode + '\'' +
                ", pactCode='" + pactCode + '\'' +
                ", ClerkID=" + ClerkID +
                ", CompanyID='" + CompanyID + '\'' +
                ", bankId=" + bankId +
                ", bankName='" + bankName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", loanType=" + loanType +
                ", loanTypeName='" + loanTypeName + '\'' +
                ", amount=" + amount +
                ", chargeWay=" + chargeWay +
                ", percent=" + percent +
                ", poundage=" + poundage +
                ", customer='" + customer + '\'' +
                ", customerId='" + customerId + '\'' +
                ", managerId=" + managerId +
                ", mortgage=" + mortgage +
                ", mortgageName='" + mortgageName + '\'' +
                ", note='" + note + '\'' +
                ", schedule='" + schedule + '\'' +
                ", scheduleId=" + scheduleId +
                ", loanId='" + loanId + '\'' +
                ", YellowTime='" + YellowTime + '\'' +
                ", RedTime='" + RedTime + '\'' +
                ", IsForeclosureFloor=" + IsForeclosureFloor +
                ", ForeclosureTime='" + ForeclosureTime + '\'' +
                ", IsPrepayment=" + IsPrepayment +
                ", member=" + member +
                ", isNotary='" + isNotary + '\'' +
                ", notaryTime='" + notaryTime + '\'' +
                ", notaryNote='" + notaryNote + '\'' +
                ", replyAmount=" + replyAmount +
                ", account='" + account + '\'' +
                ", accountName='" + accountName + '\'' +
                ", offer='" + offer + '\'' +
                ", approvalTime='" + approvalTime + '\'' +
                ", monthAmount=" + monthAmount +
                ", monthDate='" + monthDate + '\'' +
                ", returnTime='" + returnTime + '\'' +
                ", lendingAmount=" + lendingAmount +
                ", lendingTime='" + lendingTime + '\'' +
                ", overTime='" + overTime + '\'' +
                ", overNote='" + overNote + '\'' +
                ", contractId=" + contractId +
                ", updateTime='" + updateTime + '\'' +
                ", NoAuditCost=" + NoAuditCost +
                ", PassAuditCostMoney=" + PassAuditCostMoney +
                ", NoAuditCostMoney=" + NoAuditCostMoney +
                '}';
    }
}
