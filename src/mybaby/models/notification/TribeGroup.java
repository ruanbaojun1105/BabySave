package mybaby.models.notification;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.DateUtils;
import mybaby.util.MapUtils;

/**
 * Created by niubaobei on 2016/1/13.
 * 群组模型
 */
public class TribeGroup implements Serializable{
    /*array(
            *              'post_id'             群id
            *              'group_name'          群名
                    *              'type'  0--默认 1--小区群 2--同区群 3--同城群 4--同月群 5--同龄群 6--医院群
                    *              'create_time'          创建时间
                    *              'default_push_switch'  默认是否推送 0-是 1-否
                    *              'max_members_count'    群成员最大数
                    *              'im_tribe_id'          im的群id
                    *    ）*/
    int post_id;
    String group_name;
    int type;
    Long create_time;
    Boolean default_push_switch;
    int max_members_count;
    long im_tribe_id;

    public TribeGroup(int post_id, String group_name, int type, long create_time, Boolean default_push_switch, int max_members_count, long im_tribe_id) {
        this.post_id = post_id;
        this.group_name = group_name;
        this.type = type;
        this.create_time = create_time;
        this.default_push_switch = default_push_switch;
        this.max_members_count = max_members_count;
        this.im_tribe_id = im_tribe_id;
    }

    public TribeGroup() {
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public long getCreate_time_gmt(){
        return DateUtils.gmtTime2LocalTime(getCreate_time());
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public Boolean getDefault_push_switch() {
        return default_push_switch;
    }

    public void setDefault_push_switch(Boolean default_push_switch) {
        this.default_push_switch = default_push_switch;
    }

    public int getMax_members_count() {
        return max_members_count;
    }

    public void setMax_members_count(int max_members_count) {
        this.max_members_count = max_members_count;
    }

    public long getIm_tribe_id() {
        return im_tribe_id;
    }

    public void setIm_tribe_id(long im_tribe_id) {
        this.im_tribe_id = im_tribe_id;
    }

    public static TribeGroup createByMap(Map<?, ?> map){
        TribeGroup obj=new TribeGroup();
        if (map.containsKey("post_id"))
            obj.setPost_id(MapUtils.getMapInt(map, "post_id"));
        if (map.containsKey("group_name"))
            obj.setGroup_name(MapUtils.getMapStr(map, "group_name"));
        if (map.containsKey("type"))
            obj.setType(MapUtils.getMapInt(map, "type"));
        if (map.containsKey("create_time"))
            obj.setCreate_time(MapUtils.getMapLong(map, "create_time"));
        if (map.containsKey("default_push_switch"))
            obj.setDefault_push_switch(MapUtils.getMapBool(map, "default_push_switch"));
        if (map.containsKey("max_members_count"))
            obj.setMax_members_count(MapUtils.getMapInt(map, "max_members_count"));
        if (map.containsKey("im_tribe_id"))
            obj.setIm_tribe_id(MapUtils.getMapInt(map, "im_tribe_id"));
        return obj;
    }

    public static TribeGroup[] createByArray(Object[] objects){
        if (objects!=null) {
            TribeGroup[] retArr=new TribeGroup[objects.length];
            for (int i = 0; i < objects.length; i++) {
                Map<?, ?> map = (Map<?, ?>) objects[i];
                retArr[i] = createByMap(map);
            }
            return retArr;
        }else {
            return null;
        }
    }
}
