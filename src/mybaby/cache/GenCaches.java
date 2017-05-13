package mybaby.cache;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by niubaobei on 2016/1/19.
 */
public class GenCaches implements Serializable{
    String version;//版本信息
    Date lastTime;//缓存最后添加时间
    Object serializableObj;//字符串对象

    public GenCaches(Object serializableObj, String version, Date lastTime) {
        this.serializableObj = serializableObj;
        this.version = version;
        this.lastTime = lastTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public Object getSerializableObj() {
        return serializableObj;
    }

    public void setSerializableObj(Object serializableObj) {
        this.serializableObj = serializableObj;
    }
}
