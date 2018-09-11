package entity;

/**
 * Created by Administrator on 2017/9/7.
 * 贷款进度记录描述
 */

public class ScheduleNote {
    private String note;
    private String provider;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "ScheduleNote{" +
                "note='" + note + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
}
