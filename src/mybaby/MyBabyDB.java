package mybaby;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class MyBabyDB {

    private static final String DATABASE_NAME = "mybaby";

    public SQLiteDatabase db;
    public static final String BLOG_TABLE = "blog";
    public static final String POSTS_TABLE = "post";
    public static final String MEDIA_TABLE = "media";
    public static final String POST_TAG_TABLE = "postTag";
    public static final String USER_TABLE = "user";
    public static final String ACTIVITY_TABLE = "activity";
    public static final String FRIEND_TABLE = "friend";
    public static final String FRIEND_REQUEST_TABLE = "friendRequest";
    public static final String RECOMMEND_FRIEND_TABLE = "recommendFriend";   
    public static final String PHONE_CONTACT_TABLE = "phoneContact"; 
    public static final String PLACE_TABLE = "place"; 
    
    
    //－－－－－－－－－－－－－－－－－－版本0－－－－－－－－－－－－－－－－－－
    
    private static final String CREATE_TABLE_BLOG = "create table if not exists blog ("
    		+ "id integer primary key autoincrement,"
    		+ "username text,"
    		+ "password text,"
    		+ "runService boolean,"
    		+ "blogTypeNumber integer default 0,"
    		+ "userId integer default 0"
            + ");";
    
    private static final String CREATE_TABLE_MEDIA = "create table if not exists media ("
    		+ "id integer primary key autoincrement,"
    		+ "pid integer,"
    		+ "filePath text default '',"
    		+ "fileName text default '',"
    		+ "title text default '',"
    		+ "description text default '',"
    		+ "caption text default '',"
    		+ "width integer default 0,"
    		+ "height integer default 0,"
    		+ "mimeType text default '',"
    		+ "isVideo boolean default false,"
    		+ "fileURL text default '',"
    		+ "mediaId integer default 0,"
    		+ "date_created_gmt date,"
    		+ "remoteSyncFlag integer default 0,"
    		+ "isLocalDeleted boolean default 0,"
    		+ "imageThumbnailRemoteURL text,"
    		+ "imageMediumRemoteURL text,"
    		+ "imageLargeRemoteURL text,"
    		+ "imageThumbnailWidth integer default 0,"
    		+ "imageMediumWidth integer default 0,"
    		+ "imageLargeWidth integer default 0,"
    		+ "imageThumbnailHeight integer default 0,"
    		+ "imageMediumHeight integer default 0,"
    		+ "imageLargeHeight integer default 0,"
    		+ "mediaOrder integer default 0,"
    		+ "assetURL text default '',"
    		+ "userId integer default 0"
		    + ");"; 

    private static final String CREATE_TABLE_POST = "create table if not exists post ("
    		+ "id integer primary key autoincrement,"
    		+ "postid integer default 0,"
    		+ "dateCreated date,"
    		+ "date_created_gmt date,"
    		+ "description text default '',"
    		+ "userid integer default 0,"
    		+ "remoteSyncFlag integer default 0,"
    		+ "isLocalDeleted boolean default 0,"
    		+ "typeNumber integer default 0,"
    		+ "bookTheme integer default 0,"
    		+ "birthday date,"
    		+ "genderNumber integer default 0,"
    		+ "isSelf boolean default false,"
    		+ "personTypeNumber integer default 0,"
    		+ "timelinePostToBookBeginDate date,"
    		+ "timelinePostToBookEndDate date,"
    		+ "page integer default 0,"
    		+ "orderNumber integer default 0,"
    		+ "privacyTypeNumber integer default 0,"
    		+ "guid text default '',"
    		+ "status text default 'private'"
		    + ");"; 
    
    
    private static final String CREATE_TABLE_POST_TAG = "create table if not exists postTag ("
    		+ "pid integer default 0,"
    		+ "tagPid integer default 0"
		    + ");";       

    private static final String CREATE_TABLE_USER = "create table if not exists user ("
    		+ "userId integer default 0 primary key,"
    		+ "name text,"
    		+ "uniqueName text,"
    		+ "email text,"
    		+ "birthday date,"
    		+ "avatarUrl text,"
    		+ "avatarThumbnailUrl text,"
    		+ "genderNumber integer default 0,"
    		+ "lastActivityTime_gmt date,"
    		+ "bzRegistered boolean default false,"
    		+ "bzInfoModified boolean default false,"
    		+ "bzPhotoSubscriptionEndDate date,"
    		+ "bzCreateDate date,"
    		+ "fb_user_id text,"
    		+ "fb_name text,"
    		+ "frdIsBindingContacts boolean default false,"
    		+ "frdIsOpen boolean default false,"
    		+ "frdPrivacyTypeNumber integer default 0,"
    		+ "frdTelephoneCountryCode text,"
    		+ "frdTelephone text,"
    		+ "frdAllowFindMeByPhone boolean default false,"
    		+ "frdAllowFindMeByEmail boolean default false,"
    		+ "frdAllowFindMeByFacebook boolean default false,"
    		+ "lastPostsSyncTime date,"
    		+ "androidGCMRegId text,"
    		+ "androidAppVersion integer default 0"
		    + ");"; 

    private static final String CREATE_TABLE_ACTIVITY = "create table if not exists activity ("
    		+ "activityId integer default 0 primary key,"
    		+ "content text,"
    		+ "typeNumber integer default 0,"
    		+ "userId integer default 0,"
    		+ "datetime_gmt date,"
    		+ "itemId integer default 0,"
    		+ "secondaryItemId integer default 0,"
    		+ "lastSync date,"
    		+ "isMentionMe boolean default false"
		    + ");"; 
  
    private static final String CREATE_TABLE_FRIEND = "create table if not exists friend ("
    		+ "friendshipId integer default 0 primary key,"
    		+ "friendUserId integer default 0,"
    		+ "isNew boolean default false"
		    + ");"; 

    private static final String CREATE_TABLE_FRIEND_REQUEST = "create table if not exists friendRequest ("
    		+ "friendshipId integer default 0 primary key,"
    		+ "requestDate_gmt date,"
    		+ "requestUserId integer default 0,"
    		+ "sayHello text"
		    + ");"; 

    private static final String CREATE_TABLE_RECOMMEND_FRIEND = "create table if not exists recommendFriend ("
    		+ "recommendFriendId integer default 0 primary key,"
    		+ "userId integer default 0,"
    		+ "sourceTypeNumber integer default 0,"
    		+ "recommendDescription text"
		    + ");"; 
    
    private static final String CREATE_TABLE_PHONE_CONTACT = "create table if not exists phoneContact ("
    		+ "contactId integer default 0 primary key,"
    		+ "rowVersion integer default 0,"
    		+ "remoteSyncFlag integer default 0"
		    + ");";     
    
    
    //－－－－－－－－－－－－－－－－－－版本2－－－－－－－－－－－－－－－－－－
    
    private static final String CREATE_TABLE_PLACE = "create table if not exists place ("
    		+ "objId integer primary key autoincrement,"
    		+ "placeId integer default 0,"
    		+ "latitude double default 0,"
    		+ "longitude double default 0,"
    		+ "country text,"
    		+ "state text,"
    		+ "city text,"
    		+ "district text,"
    		+ "address text,"
    		+ "place_name text,"
    		+ "adcode text"
		    + ");";     
    
    private static final String ADD_FIELD_POST_PLACEOBJID = "alter table post add placeObjId integer default 0;";
    private static final String ADD_FIELD_MEDIA_PARENTID = "alter table media add parentId integer default 0;";
     
    private static final String ADD_FIELD_USER_ACTIVITY_COUNT = "alter table user add activityCount integer default 0";
    private static final String ADD_FIELD_USER_UMENGANDROIDPUSHID = "alter table user add uMengAndroidPushId text";
    
    
    //－－－－－－－－－－－－－－－－－－版本4－－－－－－－－－－－－－－－－－－
    private static final String ADD_FIELD_USER_IMUSERNAME = "alter table user add im_userid text";//IM
    private static final String ADD_FIELD_USER_IMUSERPASSWORD = "alter table user add im_userpass text";

	//－－－－－－－－－－－－－－－－－－版本5－－－－－－－－－－－－－－－－－－
	private static final String ADD_FIELD_USER_ISADMIN = "alter table user add is_admin integer default 0;";//是否是管理员
    
    
    
    //   private Context context;

    public MyBabyDB(Context ctx) {
      //  this.context = ctx;

        try {
            db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        } catch (SQLiteException e) {
            db = null;
            return;
        }

        // Create tables if they don't exist
        db.execSQL(CREATE_TABLE_BLOG);
        db.execSQL(CREATE_TABLE_POST);
        db.execSQL(CREATE_TABLE_MEDIA);
        db.execSQL(CREATE_TABLE_POST_TAG);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_ACTIVITY);
        db.execSQL(CREATE_TABLE_FRIEND);
        db.execSQL(CREATE_TABLE_FRIEND_REQUEST);
        db.execSQL(CREATE_TABLE_RECOMMEND_FRIEND);
        db.execSQL(CREATE_TABLE_PHONE_CONTACT);

        // Update tables for new installs and app updates
        try {
            int currentVersion = db.getVersion();
            //case后面没有break,通过currentVersion增加,表示每个版本递增执行
            switch (currentVersion) {  
                case 0:
                    // New install
                    currentVersion++;
                case 1:
                	db.execSQL(CREATE_TABLE_PLACE);
                	db.execSQL(ADD_FIELD_POST_PLACEOBJID);
                	db.execSQL(ADD_FIELD_MEDIA_PARENTID);
                	db.execSQL(ADD_FIELD_USER_ACTIVITY_COUNT);
                	db.execSQL(ADD_FIELD_USER_UMENGANDROIDPUSHID);
                	
                	currentVersion++;
                case 2:
                	//空的也要写
                	currentVersion++;	
                case 3:
                	db.execSQL(ADD_FIELD_USER_IMUSERNAME);
                	db.execSQL(ADD_FIELD_USER_IMUSERPASSWORD);

                	currentVersion++;
				case 4:
					db.execSQL(ADD_FIELD_USER_ISADMIN);
					currentVersion++;
            }
            db.setVersion(currentVersion);//设置当前版本，注意最后一个case后面currentVersion不要++
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
