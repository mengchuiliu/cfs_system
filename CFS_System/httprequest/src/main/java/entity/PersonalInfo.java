package entity;

/**
 * Created by Administrator on 2017/4/27.
 *
 * @author mengchuiliu
 */

public class PersonalInfo {
    private String UserSurname;
    private String UserName;
    private int UserSex = 1;
    private String UserBirthday;
    private String UserCreateTime;
    private String SIP;
    private String Seat;
    private String Phone;
    private String Phone1;

    public String getUserSurname() {
        return UserSurname;
    }

    public void setUserSurname(String userSurname) {
        UserSurname = userSurname;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getUserSex() {
        return UserSex;
    }

    public void setUserSex(int userSex) {
        UserSex = userSex;
    }

    public String getUserBirthday() {
        return UserBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        UserBirthday = userBirthday;
    }

    public String getUserCreateTime() {
        return UserCreateTime;
    }

    public void setUserCreateTime(String userCreateTime) {
        UserCreateTime = userCreateTime;
    }

    public String getSIP() {
        return SIP;
    }

    public void setSIP(String SIP) {
        this.SIP = SIP;
    }

    public String getSeat() {
        return Seat;
    }

    public void setSeat(String seat) {
        Seat = seat;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPhone1() {
        return Phone1;
    }

    public void setPhone1(String phone1) {
        Phone1 = phone1;
    }

    @Override
    public String toString() {
        return "PersonalInfo{" +
                "UserSurname='" + UserSurname + '\'' +
                ", UserName='" + UserName + '\'' +
                ", UserSex=" + UserSex +
                ", UserBirthday='" + UserBirthday + '\'' +
                ", UserCreateTime='" + UserCreateTime + '\'' +
                ", SIP='" + SIP + '\'' +
                ", Seat='" + Seat + '\'' +
                ", Phone='" + Phone + '\'' +
                ", Phone1='" + Phone1 + '\'' +
                '}';
    }
}
