package mybaby.models.person;


import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Post;

public abstract class NotBabyPerson extends Person {
	public int getPersonTypeNumber(){
		return mPost.getPersonTypeNumber();
	}
	public void  setPersonTypeNumber(int personTypeNumber){
		mPost.setPersonTypeNumber(personTypeNumber);
	}
	
	public String getAppellation(){
		if(mPost.getPersonTypeNumber()==Post.personType.Mom.ordinal()){
			return MyBabyApp.getContext().getString(R.string.mom);
		}else if(mPost.getPersonTypeNumber()==Post.personType.Dad.ordinal()){
			return MyBabyApp.getContext().getString(R.string.dad);
		}else{
			return null;
		}
	}
	
}
