package mybaby.models.discovery;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.notification.NotificationCategory;
import mybaby.util.DateUtils;
import mybaby.util.MapUtils;

/**
 * Created by LeJi_BJ on 2016/2/29.
 * ’发现‘ 模型
 */
public class Discovery implements Serializable{
    private String noti_cat_key;
    private String imageUrl;
    private String title;
    private String action;
    private NotificationCategory category;
    private int image_drawable=0;

    public Discovery() {
    }

    public Discovery( String imageUrl,String title) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public Discovery( int image_drawable,String title) {
        this.title = title;
        this.image_drawable = image_drawable;
    }

    public Discovery(String noti_cat_key, String imageUrl, String title, String action, NotificationCategory category) {
        this.noti_cat_key = noti_cat_key;
        this.imageUrl = imageUrl;
        this.title = title;
        this.action = action;
        this.category = category;
    }

    public String getNoti_cat_key() {
        return noti_cat_key;
    }

    public void setNoti_cat_key(String key) {
        this.noti_cat_key = key;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public NotificationCategory getCategory() {
        return category;
    }

    public void setCategory(NotificationCategory category) {
        this.category = category;
    }

    public int getImage_drawable() {
        return image_drawable;
    }

    public void setImage_drawable(int image_drawable) {
        this.image_drawable = image_drawable;
    }

    public static Discovery[] createByArray(Object[] arr){
        if (arr==null)
            return null;
        Discovery[] retArr=new Discovery[arr.length];
        for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
        }
        return retArr;
    }

    public static Discovery createByMap(Map<?, ?> map){
        Discovery obj=new Discovery();

        obj.setNoti_cat_key(MapUtils.getMapStr(map, "noti_cat_key"));
        obj.setImageUrl(MapUtils.getMapStr(map, "icon"));
        obj.setTitle(MapUtils.getMapStr(map, "title"));
        obj.setAction(MapUtils.getMapStr(map, "action"));
        return obj;
    }
}
