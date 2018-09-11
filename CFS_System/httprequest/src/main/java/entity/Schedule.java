package entity;

/**
 * Created by Administrator on 2017/3/7.
 *
 * @author mengchuiliu
 *         进度实体类
 */

public class Schedule {
    private String status;
    private String name;
    private String date;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "Schedule{" +
                "status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
