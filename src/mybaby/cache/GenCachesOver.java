package mybaby.cache;

import java.io.Serializable;

/**
 * Created by niubaobei on 2016/1/19.
 */
public class GenCachesOver implements Serializable{
    String version;//版本信息
    long lastTime;//缓存最后添加时间
    Object serializableObj;//字符串对象
    Object[] params;//参数
    Boolean hasMore;
    int lastId;

    public GenCachesOver(String version, long lastTime, Object serializableObj, Object[] params, Boolean hasMore, int lastId) {
        this.version = version;
        this.lastTime = lastTime;
        this.serializableObj = serializableObj;
        this.params = params;
        this.hasMore = hasMore;
        this.lastId = lastId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public Object getSerializableObj() {
        return serializableObj;
    }

    public void setSerializableObj(Object serializableObj) {
        this.serializableObj = serializableObj;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }
}
