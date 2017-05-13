package mybaby.cache;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by niubaobei on 2016/1/19.
 */
@Deprecated
public class GenCache implements Serializable{
    String version;//版本信息
    Date lastTime;//缓存最后添加时间
    String value;//字符串对象

    public GenCache(String version, Date lastTime, String value) {
        this.version = version;
        this.lastTime = lastTime;
        this.value = value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
