package mybaby.models.person;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Post;
import mybaby.models.diary.Post.privacyType;
import mybaby.models.diary.PostRepository;

public class Baby extends Person implements Serializable{

	/**
	 * bj
	 * 返回最小的孩子
	 * @return
	 */
	public static Baby getSmallBaby() {
		if (MyBabyApp.currentUser==null)
			return null;
		Baby[] mBabies = PostRepository.loadBabies(MyBabyApp.currentUser.getUserId());
		if (mBabies==null||mBabies.length==0)
			return null;
		long bir=0;
		Baby sBaby=null;
		for (Baby baby:mBabies){
			if (bir==0) {
				bir = baby.getBirthday();
				sBaby=baby;
			}
			if (bir<baby.getBirthday())
				sBaby=baby;
		}
		bir=0;
		mBabies=null;
		return sBaby;
	}

	public static Baby createByPost(Post post){
		if(post.getTypeNumber()!=Post.type.Baby.ordinal()){
			return null;
		}
		
		Baby baby=new Baby();
		baby.mPost=post;
		return baby;
	}
	
	public static Baby createBaby(String name,long birthday,int babyType){
		Post post=new Post();
		
		if(MyBabyApp.currentUser != null){
			post.setUserid(MyBabyApp.currentUser.getUserId());
		}
		post.setGuid(UUID.randomUUID().toString());
		post.setGenderNumber(babyType);
		post.setTypeNumber(Post.type.Baby.ordinal());
		post.setStatus(Post.Status_Private);
		post.setDateCreated(System.currentTimeMillis());
		
		Baby baby=createByPost(post);
		baby.setName(name);
		baby.setGenderNumber(babyType);
		baby.setBirthday(birthday);
		
		return baby;
	}
	
	
	@Override
	public int getNullAvatar(){
		return R.drawable.avatar;
	}

	//��ʼ�ռ�
	public void initDiary(){
		Calendar calendar=Calendar.getInstance();
		int millisPerDay = 24 * 60 * 60 * 1000;
		
		long today=System.currentTimeMillis();
		long birthday=this.getBirthday();
		
		calendar.setTimeInMillis(birthday);
		while(calendar.getTimeInMillis()<today){
			calendar.add(Calendar.YEAR, 1);
		}
		if(calendar.getTimeInMillis()>birthday){
			calendar.add(Calendar.YEAR, -1);
		}
		long recentBirthday=calendar.getTimeInMillis();
		
		calendar.setTimeInMillis(birthday);
		calendar.add(Calendar.DATE,-280);
		long gestationalDate=calendar.getTimeInMillis();
		
		calendar.setTimeInMillis(gestationalDate);
		calendar.add(Calendar.DATE,-60);
		long fatherAndMontherDate=calendar.getTimeInMillis();
		
		calendar.setTimeInMillis(birthday);
		calendar.add(Calendar.DATE,-180);
		long ultrasoundDate=calendar.getTimeInMillis();
		if(ultrasoundDate>today && today>gestationalDate){
			//���b��ʱ�䳬���˽����ȡ���죬ʹ������׶ε���Ҳ������������¼
			ultrasoundDate=today-millisPerDay;
			if(ultrasoundDate<gestationalDate){
				// ͬ��������  ��һ�� ��������
				ultrasoundDate=gestationalDate+1000;
			}
		}
		
		if(today>fatherAndMontherDate){
			String description=MyBabyApp.getContext().getString(R.string.mom_and_dad);
			Bitmap[] images=new Bitmap[1];
			images[0]=BitmapFactory.decodeResource(MyBabyApp.getContext().getResources(), R.drawable.img_init_post_father_and_mother);
			
			Person selfPerson = PostRepository.loadSelfPerson(this.getData().getUserid());
			Person[] familyPersons = PostRepository.loadFamilyPersons(this.getData().getUserid());
			Person[] tagPersons=new Person[familyPersons.length+1];
			for(int i=0;i<familyPersons.length;i++){
				tagPersons[i]=familyPersons[i];
			}
			tagPersons[tagPersons.length-1]=selfPerson;
			
			PostRepository.createDiary(description, images, fatherAndMontherDate, privacyType.Private.ordinal(), tagPersons);
		}
		
		if(today>gestationalDate && birthday>today){
			String description=MyBabyApp.getContext().getString(R.string.the_budding_of_a_new_life);
			Bitmap[] images=new Bitmap[1];
			images[0]=BitmapFactory.decodeResource(MyBabyApp.getContext().getResources(), R.drawable.img_init_post_gestational);
			
			PostRepository.createDiary(description, images, gestationalDate, privacyType.Private.ordinal(), new Person[]{this});
		}
		
		if(today>ultrasoundDate){
			String description=String.format(MyBabyApp.getContext().getString(R.string.init_post_ultrasound),this.getName());
			Bitmap[] images=new Bitmap[1];
			images[0]=BitmapFactory.decodeResource(MyBabyApp.getContext().getResources(), R.drawable.imginitpost_ultrasoundover);
			//images[1]=BitmapFactory.decodeResource(MyBabyApp.getContext().getResources(), R.drawable.img_init_post_ultrasound_2);
			
			PostRepository.createDiary(description, images, ultrasoundDate, privacyType.Private.ordinal(), new Person[]{this});
		}		

		if(today>birthday){
			String description= String.format(MyBabyApp.getContext().getString(R.string.init_post_born),this.getName());
			Bitmap[] images;
			if(today>recentBirthday && birthday<recentBirthday){
				images=new Bitmap[0];
			}else{
				images=new Bitmap[1];
				images[0]=BitmapFactory.decodeResource(MyBabyApp.getContext().getResources(), R.drawable.img_init_post_birthday);
			}
			PostRepository.createDiary(description, images, birthday, privacyType.Private.ordinal(), new Person[]{this});
		}
		
		if(today>recentBirthday && birthday<recentBirthday){
			String description= String.format(MyBabyApp.getContext().getString(R.string.init_post_birthday),this.getName(),getAgeText(recentBirthday));
			Bitmap[] images=new Bitmap[1];
			images[0]=BitmapFactory.decodeResource(MyBabyApp.getContext().getResources(), R.drawable.img_init_post_birthday);
			
			PostRepository.createDiary(description, images, recentBirthday, privacyType.Private.ordinal(), new Person[]{this});
		}
	}

}
