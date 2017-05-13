package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.MapUtils;

/**
 * Created by bj on 2016/4/12 0012.
 */
public class Icon implements Serializable{
    private String action;
    private String image_url;
    private String name;

    public static Icon creatByMap(Map<?, ?> mapItem){
        if (null==mapItem)
            return null;
        Icon icon=new Icon();
        icon.setAction(MapUtils.getMapStr(mapItem, "action"));
        icon.setImage_url(MapUtils.getMapStr(mapItem, "image_url"));
        icon.setName(MapUtils.getMapStr(mapItem, "name"));
        return icon;
    }

    public Icon() {
    }

    public Icon(String action, String image_url, String name) {
        this.action = action;
        this.image_url = image_url;
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
