package mybaby.models.diary;

import android.database.Cursor;

public class PostTag {
	private int pid;
	private int tagPid;
	
	public int getPid(){
		return pid;
	}
	public void  setPid(int pid){
		this.pid = pid;
	}
	public int getTagPid(){
		return tagPid;
	}
	public void  setTagPid(int tagPid){
		this.tagPid = tagPid;
	}

    public PostTag(Cursor c){
    	this.setPid(c.getInt(0));
    	this.setTagPid(c.getInt(1));
    }
    
    public PostTag(int pid,
    				int tagPid){
    	this.setPid(pid);
    	this.setTagPid(tagPid);
    }
    
    

}
