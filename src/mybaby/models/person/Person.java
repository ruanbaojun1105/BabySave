package mybaby.models.person;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import me.hibb.mybaby.android.R;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.models.diary.PostTagRepository;
import mybaby.ui.MyBabyApp;
import mybaby.util.DateUtils;
import mybaby.util.ImageViewUtil;

public abstract class Person implements Serializable{
	public static final int Background_Media_Order=500; 
	
	
	protected Post mPost;
	
	public Post getData(){
		return mPost;
	}
	
	public int getId(){
		return mPost.getId();
	}
	public void setId(int id){
		mPost.setId(id);
	}
	public String getName(){
		return mPost.getDescription();
	}
	public void  setName(String name){
		mPost.setDescription(name);
	}
	public long getBirthday(){
		return mPost.getBirthday();
	}
	public void  setBirthday(long birthday){
		mPost.setBirthday(birthday);
	}
	public int getGenderNumber(){
		return mPost.getGenderNumber();
	}
	public void  setGenderNumber(int genderNumber){
		mPost.setGenderNumber(genderNumber);
	}
	
	public Post[] getPosts(){
		return PostRepository.loadPostsByPerson(mPost.getId());
	}
	
	public Media getAvatar(){
		Media[] medias=MediaRepository.getForPId(mPost.getId());
		for(int i=0;i<medias.length;i++){
			if(medias[i].getMediaOrder() != Background_Media_Order){
				return medias[i];
			}
		}

		return null;
	}

	/**
	 *2016/3/5 bj
	 */
	public String getAvatarUrl(){
		Media[] medias=MediaRepository.getForPId(mPost.getId());
		Media media=null;
		if (medias!=null) {
			for (Media m:medias) {
				if (m.getMediaOrder() != Background_Media_Order) {
					media = m;
					break;
				}
			}
		}
		if (media==null){
			return getNullAvatarUrl();
		}else return ImageViewUtil.getFileUrl(media,media.getWidth(),media.getHeight());
	}

	public void setAvatar(String fileUrl){
		if(fileUrl==null || fileUrl.isEmpty() || getId()<=0){
			return;
		}
		
		Media[] medias=MediaRepository.getForPId(mPost.getId());
		for(int i=0;i<medias.length;i++){
			if(medias[i].getMediaOrder() != Background_Media_Order){
				MediaRepository.delete(medias[i]);
			}
		}

		MediaRepository.createMedia(getId(), fileUrl);
	}
	public String getNullAvatarUrl(){
		if (this instanceof Baby)
			return "assets://avatar.png";
		if(getGenderNumber()==Post.gender.Male.ordinal()){
			return "assets://avatar_male.png";
		}else if(getGenderNumber()==Post.gender.Female.ordinal()){
			return "assets://avatar_female.png";
		}else{
			return "assets://avatar.png";
		}
	}
	public int getNullAvatar(){
		if(getGenderNumber()==Post.gender.Male.ordinal()){
			return R.drawable.avatar_male;
		}else if(getGenderNumber()==Post.gender.Female.ordinal()){
			return R.drawable.avatar_female;
		}else{
			return R.drawable.avatar;
		}
	}
	
	public Media getBackground(){
		Media[] medias=MediaRepository.getForPId(mPost.getId());
		for(int i=0;i<medias.length;i++){
			if(medias[i].getMediaOrder() == Background_Media_Order){
				return medias[i];
			}
		}

		return null;
	}
	public void setBackground(String fileUrl){
		if(fileUrl==null || fileUrl.isEmpty() || getId()<=0){
			return;
		}
		
		Media[] medias=MediaRepository.getForPId(mPost.getId());
		for(int i=0;i<medias.length;i++){
			if(medias[i].getMediaOrder() == Background_Media_Order){
				MediaRepository.delete(medias[i]);
			}
		}

		MediaRepository.createMedia(getId(), fileUrl, Background_Media_Order);
	}
	
	
	public void delete(){
		Post[] posts=PostRepository.loadPostsByPerson(getId()); 
		for(int i=0;i<posts.length;i++){
			PostTagRepository.delete(posts[i].getId(),getId());
			
			posts[i].setRemoteSyncFlag(Post.remoteSync.LocalModified.ordinal());
			PostRepository.save(posts[i]);
		}
		
		PostRepository.deletePost(mPost);
	}
	

	public String getAgeText() {
		long lngDate= DateUtils.lngDatetime2Date(System.currentTimeMillis());//去除时间部分
		return getAgeText(lngDate);
	}

	/**
	 * 支持不显示后面的多少天
	 * @param visiDay
	 * @return
	 */
	public String getAgeText(boolean visiDay) {
		long lngDate= DateUtils.lngDatetime2Date(System.currentTimeMillis());//去除时间部分
		return getAgeText(lngDate,visiDay);
	}
	
	public String getAgeText(long currDate) {
		return getAgeText(currDate,true);
	}
	public String getAgeText(long currDate,boolean visiDay) {
		return calAgeText(getBirthday(), currDate,visiDay);
	}
	
	public static String calAgeText(long BirthDate) {
		long lngDate= DateUtils.lngDatetime2Date(System.currentTimeMillis());//去除时间部分
		return calAgeText(BirthDate,lngDate);
	}

	public static String calAgeText(long BirthDate,boolean visiDay) {
		long lngDate= DateUtils.lngDatetime2Date(System.currentTimeMillis());//去除时间部分
		return calAgeText(BirthDate,lngDate,visiDay);
	}

	public static String calAgeText(long BirthDate,long currDate) {
		return calAgeText(BirthDate,currDate,true);
	}
	/*java中计算年龄
	* 计算年龄 */
	public  String getAge(Date birthDay) throws Exception {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH)+1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthDay);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				//monthNow==monthBirth
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				//monthNow>monthBirth
				age--;
			}
		}

		return age +"";
	}

	public static String calAgeText(long BirthDate,long currDate,boolean visiDay) {
		int millisPerDay = 24 * 60 * 60 * 1000;
		
		long lngBirthday=DateUtils.lngDatetime2Date(BirthDate);//格式化时间

		Calendar currentCalendar=Calendar.getInstance();
		currentCalendar.setTimeInMillis(currDate);
		if(currDate>=lngBirthday){//�ѳ���
			
			Calendar birthdayCalendar=Calendar.getInstance();
			birthdayCalendar.setTimeInMillis(lngBirthday);
			
			int yearDiff=currentCalendar.get(Calendar.YEAR)-birthdayCalendar.get(Calendar.YEAR);
			int monthDiff=currentCalendar.get(Calendar.MONTH)-birthdayCalendar.get(Calendar.MONTH);
			int dayDiff=currentCalendar.get(Calendar.DAY_OF_MONTH)-birthdayCalendar.get(Calendar.DAY_OF_MONTH);
			
			int ageYear=yearDiff;
			int ageMonth=monthDiff;
			int ageDay=dayDiff;

			if(monthDiff<0){
				ageYear=yearDiff-1;
				ageMonth=monthDiff+12;
			}
			if(dayDiff<0){
				if (monthDiff>0){
					ageMonth=monthDiff-1;
				}else {
					ageYear=yearDiff-1;
					ageMonth=monthDiff+12-1;
				}
				birthdayCalendar.add(Calendar.YEAR, ageYear);
				birthdayCalendar.add(Calendar.MONTH, ageMonth);
				ageDay=(int)Math.floor((currDate - birthdayCalendar.getTimeInMillis())/millisPerDay);
			}
			
			String yearWord=getAgeLocalizedString_word(MyBabyApp.getContext().getString(R.string.age_year_words),ageYear);
			String monthWord=getAgeLocalizedString_word(MyBabyApp.getContext().getString(R.string.age_month_words),ageMonth);
			String dayWord=getAgeLocalizedString_word(MyBabyApp.getContext().getString(R.string.age_day_words),ageDay+1);
			
			if(ageYear==0 ){
				if(ageMonth<1){
					if ((!visiDay)) {
						dayWord = "未满月";
					}
					return dayWord;
				}else{
					if (visiDay) {
						dayWord=String.format(MyBabyApp.getContext().getString(ageDay>0?(visiDay?R.string.age_text_2:R.string.age_text_1):R.string.age_text_1),monthWord,dayWord);
					}else dayWord=String.format(MyBabyApp.getContext().getString(R.string.age_text_1), monthWord);

					return dayWord;
				}
			}else{
				if(ageMonth>0){
					if (visiDay) {
						if (ageDay > 0) {
							return String.format(MyBabyApp.getContext().getString(R.string.age_text_3 ), yearWord, monthWord, dayWord);
						}else {
							return String.format(MyBabyApp.getContext().getString(R.string.age_text_2),yearWord,monthWord);
						}
					}else {
						return String.format(MyBabyApp.getContext().getString( R.string.age_text_2), yearWord, monthWord);
					}
				}else{
					if (visiDay) {
						if (ageDay > 0) {
							return String.format(MyBabyApp.getContext().getString(R.string.age_text_2 ), yearWord, dayWord);
						}else {
							return String.format(MyBabyApp.getContext().getString(R.string.age_text_1),yearWord);
						}
					}else {
						return String.format(MyBabyApp.getContext().getString( R.string.age_text_1), yearWord);
					}
				}
			}
			
		}else{
			long curCut=DateUtils.lngDatetime2Date(currDate);//去除时间部分
			int weeks=(int)Math.floor((280-(lngBirthday-curCut)/millisPerDay-1)/7);
			int day=((int)Math.floor((280-(lngBirthday-curCut)/millisPerDay-1))) % 7;
		
			String weekWord=getAgeLocalizedString_word(MyBabyApp.getContext().getString(R.string.age_week_words) ,weeks);
			String dayWord=getAgeLocalizedString_word(MyBabyApp.getContext().getString(R.string.age_day_words) ,day);
			
			if(weeks>0 && day>0){
	            if(weekWord != null && dayWord != null){
					if(visiDay){
						return String.format(MyBabyApp.getContext().getString(R.string.age_text_pregnant_2),weekWord,dayWord);
					}else {
						return String.format(MyBabyApp.getContext().getString(R.string.age_text_pregnant_1),weekWord);
					}
	            }else{
	                return "";
	            }
			}else{
	            if(weekWord != null){
	                return String.format(MyBabyApp.getContext().getString(R.string.age_text_pregnant_1),weekWord);
	            }else if(dayWord != null){
	                return String.format(MyBabyApp.getContext().getString(R.string.age_text_pregnant_1),dayWord);
	            }else{
	                return "";
	            }
			}			
		}
	}
	
	private static String getAgeLocalizedString_word(String agesWordString,int ages){
		if(ages>0){
			String[] words=agesWordString.split("\\|");
			if(words.length>ages){
				return words[ages-1];
			}else{
				return  String.format(words[words.length-1], ages);
			}
		}
		return null;
	}
	
}
