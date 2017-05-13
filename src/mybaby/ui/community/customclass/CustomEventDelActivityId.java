package mybaby.ui.community.customclass;

import android.app.Activity;

import java.io.Serializable;

public class CustomEventDelActivityId implements Serializable{
	private int id;
	private boolean isKill;
	private Activity activity;


	public CustomEventDelActivityId(int id, boolean isKill, Activity activity) {
		this.id = id;
		this.isKill = isKill;
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isKill() {
		return isKill;
	}

	public void setIsKill(boolean isKill) {
		this.isKill = isKill;
	}


}
