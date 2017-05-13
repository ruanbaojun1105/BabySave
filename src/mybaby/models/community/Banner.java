package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.MapUtils;

/**
 * Created by LeJi_BJ on 2016/3/16.
 */
public class Banner implements Serializable{
    String post_id;
    String background_url;
    String title;
    String action;


    public static Banner[] createByArray(Object[] arr){
        if (arr==null)
            return null;
        Banner[] retArr=new Banner[arr.length];

        for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) (arr[i]);
            retArr[i] = createByMap(map);
        }

        return retArr;
    }

    public static Banner createByMap(Map<?, ?> map){
        Banner obj=new Banner();
        obj.setTitle(MapUtils.getMapStr(map, "title"));
        obj.setAction(MapUtils.getMapStr(map, "action"));
        obj.setBackground_url(MapUtils.getMapStr(map, "image_url"));
        obj.setPost_id(MapUtils.getMapStr(map, "post_id"));
        return obj;
    }




    public Banner() {
    }

    public Banner(String post_id, String background_url, String title, String action) {
        this.post_id = post_id;
        this.background_url = background_url;
        this.title = title;
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getBackground_url() {
        return background_url;
    }

    public void setBackground_url(String background_url) {
        this.background_url = background_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
