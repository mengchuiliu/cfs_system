package entity;

import java.io.Serializable;
import java.util.List;

/**
 * 相册对象
 */
public class ImageBucket implements Serializable{
    private static final long serialVersionUID = 6810465379482772858L;
    public int count = 0;
    public String bucketName;
    public List<ImageInfo> imageList;
    //public boolean selected = false;
}
