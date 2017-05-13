package mybaby.models.friend;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.util.StringUtils;
import android.database.Cursor;

public class RecommendFriend {
	public enum sourceType{		
	    PhoneContact_PhoneNo, //电话联系人-电话号码
	    PhoneContact_Email, //电话联系人-邮箱
	    FacebookFriend, //Facebook好友
	    FriendOfFriend, //好友的好友
	    MayKnow, //其他可能认识的人
	    Other
	}


	private int recommendFriendId;
	private int userId;
	private int sourceTypeNumber;
	private String recommendDescription;
	
	public int getRecommendFriendId(){
	    return recommendFriendId;
	}
	public void  setRecommendFriendId(int recommendFriendId){
	    this.recommendFriendId = recommendFriendId;
	}
	public int getUserId(){
	    return userId;
	}
	public void  setUserId(int userId){
	    this.userId = userId;
	}
	public int getSourceTypeNumber(){
	    return sourceTypeNumber;
	}
	public void  setSourceTypeNumber(int sourceTypeNumber){
	    this.sourceTypeNumber = sourceTypeNumber;
	}
	public String getRecommendDescription(){
	    return recommendDescription;
	}
	public void  setRecommendDescription(String recommendDescription){
	    this.recommendDescription = recommendDescription;
	}
	

	public String getMergeDescription(){
		String typeDesc="";
    
	    if(getSourceTypeNumber()==sourceType.PhoneContact_PhoneNo.ordinal() 
	    		|| getSourceTypeNumber()==sourceType.PhoneContact_Email.ordinal()){
	    	typeDesc=MyBabyApp.getContext().getString(R.string.contacts);
	    }else if(getSourceTypeNumber()==sourceType.FacebookFriend.ordinal()){
	    	typeDesc=MyBabyApp.getContext().getString(R.string.facebook_friend);
	    }else if(getSourceTypeNumber()==sourceType.FriendOfFriend.ordinal()){
	    	typeDesc=MyBabyApp.getContext().getString(R.string.friend_of_friend);
	    }
    
	    String resultString=typeDesc;
    
	    if(!StringUtils.isEmpty(resultString) && !StringUtils.isEmpty(getRecommendDescription())){
	        resultString=resultString + ": ";
	    }
	    
	    resultString=resultString + getRecommendDescription();
	    
	    return resultString;
}

	
	
    public RecommendFriend(Cursor c){
    	this.setRecommendFriendId(c.getInt(0));
    	this.setUserId(c.getInt(1));
    	this.setSourceTypeNumber(c.getInt(2));
    	this.setRecommendDescription(c.getString(3));
    }	
	
    public RecommendFriend(	int recommendFriendId,
				    		int userId,
				    		int sourceTypeNumber,
				    		String recommendDescription) {
    	this.setRecommendFriendId(recommendFriendId);
    	this.setUserId(userId);
    	this.setSourceTypeNumber(sourceTypeNumber);
    	this.setRecommendDescription(recommendDescription);
    }
    
	public RecommendFriend() {
		
	}	
}
