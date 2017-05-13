package mybaby.models.person;

import java.util.UUID;

import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Post;

public class SelfPerson extends NotBabyPerson {

	public static SelfPerson createByPost(Post post){
		if(post.getTypeNumber()!=Post.type.Person.ordinal() || !post.getIsSelf()){
			return null;
		}
		
		SelfPerson person=new SelfPerson();
		person.mPost=post;
		return person;
	}
	
	public static SelfPerson createSelfPerson(String name,int personType){
		Post post=new Post();
		
		if(MyBabyApp.currentUser != null){
			post.setUserid(MyBabyApp.currentUser.getUserId());
		}
		post.setGuid(UUID.randomUUID().toString());
		
		post.setTypeNumber(Post.type.Person.ordinal());
		post.setIsSelf(true);
		post.setStatus(Post.Status_Private);
		post.setDateCreated(System.currentTimeMillis());
		
		SelfPerson person=createByPost(post);
		person.setName(name);
		person.setPersonTypeNumber(personType);
		
		return person;
	}
}
