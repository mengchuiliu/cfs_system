package entity;

/**
 * Created by Administrator on 2017/3/8.
 *
 * @author mengchuiliu
 */

public class Message {
    private int code;
    private Object object;

    public Message() {
    }

    public Message(int code, Object o) {
        this.code = code;
        this.object = o;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Message{" +
                "code=" + code +
                ", object=" + object +
                '}';
    }
}
