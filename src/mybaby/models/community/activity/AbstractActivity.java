package mybaby.models.community.activity;

import java.io.Serializable;
import java.util.Date;

import mybaby.models.User;
import mybaby.util.DateUtils;

public class AbstractActivity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id;
	long datetime_gmt;
	User user;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getDatetime_gmt() {
		return datetime_gmt;
	}
	public void setDatetime_gmt(long datetime_gmt) {
		this.datetime_gmt = datetime_gmt;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public long getDatetime(){
	    return DateUtils.gmtTime2LocalTime(getDatetime_gmt());
	}
	public AbstractActivity(int id, long datetime_gmt, User user) {
		super();
		this.id = id;
		this.datetime_gmt = datetime_gmt;
		this.user = user;
	}
	public AbstractActivity() {
		super();
	}

}
