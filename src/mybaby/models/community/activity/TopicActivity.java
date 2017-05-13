package mybaby.models.community.activity;

import java.io.Serializable;

public class TopicActivity extends AbstractMainActivity  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String actionDesc(){
		return "发布了帖子";
	}
}
