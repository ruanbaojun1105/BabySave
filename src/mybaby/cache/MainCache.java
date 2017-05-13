package mybaby.cache;

import java.io.Serializable;
import java.util.Date;

@Deprecated
public class MainCache implements Serializable{

	Object items;//缓存对象
	int lastId;//
	boolean hasMore;//
	String version;//版本信息
	Date lastTime;//缓存最后添加时间
	long maxTime;

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}

	public Object getItems() {
		return items;
	}

	public void setItems(Object items) {
		this.items = items;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public int getLastId() {
		return lastId;
	}
	public void setLastId(int lastId) {
		this.lastId = lastId;
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

	public MainCache(Object items, int lastId,long maxTime, boolean hasMore, String version, Date lastTime) {
		this.items = items;
		this.lastId = lastId;
		this.maxTime = maxTime;
		this.hasMore = hasMore;
		this.version = version;
		this.lastTime = lastTime;
	}


}
