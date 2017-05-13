package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.MapUtils;

/**
 * Created by bj on 2016/4/15 0015.
 */
public class BigImage implements Serializable {
    private String desc;
    private String image_url;
    private String action;
    private String title;


    public static BigImage[] createByArray(Object[] arr){
        if (arr==null)
            return null;
        BigImage[] retArr=new BigImage[arr.length];

        for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
        }

        return retArr;
    }
    public static BigImage createByMap(Map<?, ?> map){
        if (null==map)
            return null;
        BigImage obj=new BigImage();
        obj.setDesc(MapUtils.getMapStr(map, "desc"));
        obj.setAction(MapUtils.getMapStr(map, "image_url"));
        obj.setImage_url(MapUtils.getMapStr(map, "action"));
        obj.setImage_url(MapUtils.getMapStr(map, "title"));
        return obj;
    }
    public BigImage() {
    }

    public BigImage(String title, String desc, String image_url, String action) {
        this.title = title;
        this.desc = desc;
        this.image_url = image_url;
        this.action = action;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
