package mybaby.models.person;

import java.util.UUID;

import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Post;

public class FamilyPerson extends NotBabyPerson {
	
	public static FamilyPerson createByPost(Post post){
		if(post.getTypeNumber()!=Post.type.Person.ordinal() || post.getIsSelf()){
			return null;
		}
		
		FamilyPerson person=new FamilyPerson();
		person.mPost=post;
		return person;
	}
	
	public static FamilyPerson createFamilyPerson(String name,int personType){
		Post post=new Post();
		
		if(MyBabyApp.currentUser != null){
			post.setUserid(MyBabyApp.currentUser.getUserId());
		}
		post.setGuid(UUID.randomUUID().toString());

		post.setTypeNumber(Post.type.Person.ordinal());
		post.setIsSelf(false);
		post.setStatus(Post.Status_Private);
		post.setDateCreated(System.currentTimeMillis());
		
		FamilyPerson person=createByPost(post);
		person.setName(name);
		person.setPersonTypeNumber(personType);
		
		return person;
	}
	
}
