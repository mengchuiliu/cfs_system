package entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/2 0002.
 *
 * @author mengchuiliu
 *         图片信息
 */
public class ImageInfo implements Serializable {
    private static final long serialVersionUID = 1352044332149780131L;
    public String imageId;
    public String thumbnailPath;//缩略图路径
    public String sourcePath;//图片路径
    public boolean isSelected = false;//是否已被选中

}
