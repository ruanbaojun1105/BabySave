package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.MapUtils;

/**
 * Created by LeJi_BJ on 2016/3/1.
 */
public class Link implements Serializable{
    /*"desc" 描述,
            "image_url" 图片,
            "action" 动作*/
    private String desc;
    private String image_url;
    private String action;

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

    public Link() {
    }
    public static Link[] createByArray(Object[] arr){
        if (arr==null)
            return null;
        Link[] retArr=new Link[arr.length];

        for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
        }

        return retArr;
    }
    public static Link createByMap(Map<?, ?> map){
        if (null==map)
            return null;
        Link obj=new Link();
        obj.setDesc(MapUtils.getMapStr(map, "desc"));
        obj.setAction(MapUtils.getMapStr(map, "action"));
        obj.setImage_url(MapUtils.getMapStr(map, "image_url"));
        return obj;
    }
}
