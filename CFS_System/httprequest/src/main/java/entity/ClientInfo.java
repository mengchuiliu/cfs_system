package entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/15.
 *
 * @author mengchuiliu
 *         客户信息录入对象
 */

public class ClientInfo implements Serializable {
    private static final long serialVersionUID = -6461221645723292044L;

    private int UserID;
    private String Name;
    private String Mobile;
    private String IdCode;
    private int CardType = -1;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getIdCode() {
        return IdCode;
    }

    public void setIdCode(String IdCode) {
        this.IdCode = IdCode;
    }

    public int getCardType() {
        return CardType;
    }

    public void setCardType(int CardType) {
        this.CardType = CardType;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "UserID='" + UserID + '\'' +
                ", Name='" + Name + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", IdCode='" + IdCode + '\'' +
                ", CardType=" + CardType +
                '}';
    }
}
