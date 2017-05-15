package mybaby.rpc;

import android.util.Log;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import mybaby.ui.MyBabyApp;
import mybaby.models.User;
import mybaby.models.UserRepository;
import mybaby.models.community.Place;
import mybaby.models.community.PlaceRepository;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.models.diary.PostTag;
import mybaby.models.diary.PostTagRepository;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

public class PostRPC extends BaseRPC {
	 
		public static void getPosts(final User user,final XMLRPCCallback callback){
		    Object[] params;
		    String xmlrpcMethod;
		    if(user.isSelf()){
		    	params=extParams(new Object[]{-1,1});
		    	xmlrpcMethod="bz.xmlrpc.getMyPosts";
		    }else{
		    	params=extParams(new Object[]{user.getUserId()});
		    	xmlrpcMethod="bz.xmlrpc.getFriendUserBlogPosts";
		    }

		    XMLRPCCallback nCallback = new XMLRPCCallback() {
	            @Override
	            public void onSuccess(long id, Object result){
	            	Object[] rpcPosts=(Object[])result;
	  
	            	PostRepository.deleteUploadedPosts(user.getUserId());
	            	
	            	for (int i = 0; i < rpcPosts.length; i++) {
                        Map<?, ?> postMap = (Map<?, ?>) rpcPosts[i];
                        createOrUpdatePostByMap(postMap,user);
                    }
	                
	            	if(user.isSelf()){
		            	MyBabyApp.currentUser.setLastPostsSyncTime(System.currentTimeMillis());
		            	UserRepository.save(MyBabyApp.currentUser);
	            	}else{
		            	user.setLastPostsSyncTime(System.currentTimeMillis());
		            	UserRepository.save(user);
	            	}
	            	
	            	if(callback != null){
	            		callback.onSuccess(id, result);
	            	}
	            }
		    
	            @Override
	            public void onFailure(long id, XMLRPCException error){
	            	Utils.LogV("MyBaby", "XMLRPCException-PostRPC: " + error.getMessage());
	            	if(callback != null){
	            		callback.onFailure(id, error);
	            	}
	            }
		    };
		    
		    try {
		        getClient().callAsync(nCallback,xmlrpcMethod, params);
		    } catch ( Exception e) {
		    	Utils.LogV("MyBaby", "XMLRPCException-PostRPC-getPosts: " + e.getMessage());
		    }
		}
		
		
		public static Post createOrUpdatePostByMap(Map<?,?> postMap,User user){
			Post post=PostRepository.loadByPostId(MapUtils.getMapInt(postMap, "postid"));
			if(post==null){
				post = new Post();
				post.setPostid(MapUtils.getMapInt(postMap, "postid"));
			}
			post.setDateCreated(MapUtils.getMapDateLong(postMap, "dateCreated"));
			post.setDate_created_gmt(MapUtils.getMapDateLong(postMap, "date_created_gmt"));
			post.setDescription(MapUtils.getMapStr(postMap, "description"));
			post.setUserid(user.getUserId());
			post.setRemoteSyncFlag(Post.remoteSync.SyncSuccess.ordinal());
			post.setIsLocalDeleted(false);
			post.setTypeNumber(MapUtils.getMapInt(postMap, "type"));
			post.setBookTheme(MapUtils.getMapInt(postMap, "bookTheme"));
			post.setBirthday(MapUtils.getMapDateLongFrom_yyyyMMdd(postMap, "birthday"));
			post.setGenderNumber(MapUtils.getMapInt(postMap, "gender"));
			post.setIsSelf(MapUtils.getMapBool(postMap, "isSelf"));
			post.setPersonTypeNumber(MapUtils.getMapInt(postMap, "personType"));
			post.setTimelinePostToBookBeginDate(0);
			post.setTimelinePostToBookEndDate(0);
			post.setPage(MapUtils.getMapInt(postMap, "page"));
			post.setOrderNumber(MapUtils.getMapInt(postMap, "order"));
			post.setPrivacyTypeNumber(MapUtils.getMapInt(postMap, "privacyType"));
			post.setGuid(MapUtils.getMapStr(postMap, "title"));
			post.setStatus(MapUtils.getMapStr(postMap, "post_status"));
	    	
			//增加place处理   2015-07-17
			Map<?, ?> mapPlace = MapUtils.getMap(postMap,"place");
			if(mapPlace != null){
				Place place=Place.createByMap(mapPlace);
				post.setPlaceObjId(place.getObjId());
			}
			
			//前面已经是load过的,因此这里不需要了
//		   if(MapUtils.getMapInt(postMap, "type")>0){ //tag�п�����֮ǰ�Ѿ����������������Ҫ����id��ʹ�ñ���ʱȥupdateԭ��������¼
//			   	int tagPid=PostRepository.getIdByPostId(MapUtils.getMapInt(postMap, "postid"));
//			   	if(tagPid>0){
//			   		post.setId(tagPid);
//			   	}
//		   }
			
		   PostRepository.save(post);
		   
		   Object[] images = (Object[]) postMap.get("images");
		   for(int j=0;j<images.length;j++){
			   	Map<?, ?> imageMap = (Map<?, ?>) images[j];
			   	Media media=MediaRepository.getForMediaId(MapUtils.getMapStr(imageMap, "id"));
			   	if(media==null){
			   		media=new Media();
			   	}
			   	media.setPid(post.getId());
			   	media.setWidth(MapUtils.getMapInt(imageMap, "fullWidth"));
			   	media.setHeight(MapUtils.getMapInt(imageMap, "fullHeight"));
			   	media.setMediaId(MapUtils.getMapInt(imageMap, "id"));
			   	media.setRemoteSyncFlag(Media.remoteSync.SyncSuccess.ordinal());
			   	media.setFileURL(MapUtils.getMapStr(imageMap, "fullUrl"));
			   	media.setImageThumbnailRemoteURL(MapUtils.getMapStr(imageMap, "thumbnailUrl"));
			   	media.setImageMediumRemoteURL(MapUtils.getMapStr(imageMap, "mediumUrl"));
			   	media.setImageLargeRemoteURL(MapUtils.getMapStr(imageMap, "largeUrl"));
			   	media.setImageThumbnailWidth(MapUtils.getMapInt(imageMap, "thumbnailWidth"));
			   	media.setImageMediumWidth(MapUtils.getMapInt(imageMap, "mediumWidth"));
			   	media.setImageLargeWidth(MapUtils.getMapInt(imageMap, "largeWidth"));
			   	media.setImageThumbnailHeight(MapUtils.getMapInt(imageMap, "thumbnailHeight"));
			   	media.setImageMediumHeight(MapUtils.getMapInt(imageMap, "mediumHeight"));
			   	media.setImageLargeHeight(MapUtils.getMapInt(imageMap, "largeHeight"));
			   	media.setMediaOrder(MapUtils.getMapInt(imageMap, "order"));
			   	media.setAssetURL(MapUtils.getMapStr(imageMap, "assetURL"));
			   	media.setUserId(user.getUserId());
			   	
			   	MediaRepository.save(media);
			}
			   
		   Object[] tags = (Object[]) postMap.get("postTags");
		   for(int j=0;j<tags.length;j++){
			   	//ת��postId��pid,������ػ�û���򴴽�һ��
			   	int postid=Integer.parseInt(String.valueOf(tags[j]));
			   	int tagPid=PostRepository.getIdByPostId(postid);
			   	if(tagPid<=0){
			   		Post tagPost=new Post(postid);
			   		tagPid=PostRepository.save(tagPost);
			   	}
		   	
			   	PostTag tag=new PostTag(post.getId(),tagPid);
			   	PostTagRepository.save(tag);
		   }
		   
		   return post;
		}
		
		
		//增加placeId处理   2015-07-17
		public static boolean uploadPost(final Post post){
			if(post.getRemoteSyncFlag()==Post.remoteSync.SyncSuccess.ordinal()
					|| post.getIsLocalDeleted()){
				return false;
			}
			
		    Object[] params;
		    String xmlrpcMethod;

		    //构件参数
		    Map<String, Object> contentStruct = new HashMap<String, Object>();
		    contentStruct.put("title", post.getGuid());
		    contentStruct.put("description", post.getDescription());
//		    contentStruct.put("mt_excerpt", "");//webApi中说必须
//		    contentStruct.put("mt_text_more", "");//webApi中说必须
//		    contentStruct.put("mt_keywords", "");//webApi中说必须
//		    contentStruct.put("mt_tb_ping_urls", "");//webApi中说必须
//		    contentStruct.put("categories", "");//webApi中说必须

		    contentStruct.put("post_status", post.getStatus());
		    //contentStruct.put("dateCreated", new Date(post.getDateCreated()));
		    contentStruct.put("date_created_gmt", new Date(post.getDate_created_gmt()));
		    
		    //自定义新增
		    contentStruct.put("type", post.getTypeNumber());
		    
		    if(post.getTypeNumber() == Post.type.Baby.ordinal() || post.getTypeNumber() == Post.type.Person.ordinal()){
		        if(post.getBirthday()>0){
		        	contentStruct.put("birthday", (new SimpleDateFormat("yyyy-MM-dd")).format(post.getBirthday()));
		        }else{
		        	contentStruct.put("birthday", "");
		        }
		        contentStruct.put("gender", post.getGenderNumber());
		        contentStruct.put("bookTheme", post.getBookTheme());
		        
		        if(post.getTypeNumber() == Post.type.Person.ordinal()){
		        	contentStruct.put("isStar", 1);//目前已作废，保持兼容旧版api,且一定要设置为1
		        	contentStruct.put("isSelf", post.getIsSelf());
		        	contentStruct.put("personType", post.getPersonTypeNumber());
		        }
			}
		    
		    contentStruct.put("accessScope", 0);//目前已作废，保持兼容旧版api
		    contentStruct.put("order", post.getOrderNumber());
		    contentStruct.put("privacyType", post.getPrivacyTypeNumber()); 

		    PostTag[] postTags=PostTagRepository.load(post.getId());
			for(int i=0;i<postTags.length;i++){
				Post tagPost=PostRepository.load(postTags[i].getTagPid());
				if(tagPost != null){
					if(tagPost.getPostid()>0){
						contentStruct.put(String.format("postTags_%1$d", i+1), tagPost.getPostid()); 
					}else{
		            	post.setRemoteSyncFlag(Post.remoteSync.SyncError.ordinal());
		            	PostRepository.save(post);
						return false;//对应的tag未上传，post也不能上传
					}
				}
			}
		    
			//增加place处理   2015-07-17
			if(post.getPlaceObjId()>0){
				Place place=PlaceRepository.load(post.getPlaceObjId());
				contentStruct.put("place", place.getMap());
			}
			
			//新增或编辑
	    	if(post.getPostid()>0){
	    		params=new Object[]{ post.getPostid(), 
				    				 MyBabyApp.currentBlog.getUsername(), 
				    				 MyBabyApp.currentBlog.getPassword(),
				    				 contentStruct
				    	    		};

	    		xmlrpcMethod="bz.xmlrpc.post.update";
	    	}else{
	    		params=extParams(new Object[]{contentStruct});
	    		xmlrpcMethod="bz.xmlrpc.post.add";
	    	}

	    	//执行及结果反馈	    
		    try {
		    	Object result=getClient().call(xmlrpcMethod, params);
		    	
		    	Map<?, ?> map=(Map<?, ?>)result;
		    	int postId=MapUtils.getMapInt(map, "postId");
		        int placeId=MapUtils.getMapInt(map, "placeId");
		        
            	if(post.getPostid()<=0){
            		post.setPostid(postId);
            	}
            	if(post.getPlaceObjId()>0){
            		Place place=PlaceRepository.load(post.getPlaceObjId());
            		place.setPlaceId(placeId);
            		PlaceRepository.save(place);
            	}
            	
            	post.setRemoteSyncFlag(Post.remoteSync.SyncSuccess.ordinal());
            	PostRepository.save(post);
            	
            	UserRepository.save(MyBabyApp.currentUser);
            	
            	return true;
		    } catch ( Exception e) {
            	post.setRemoteSyncFlag(Post.remoteSync.SyncError.ordinal());
            	PostRepository.save(post);
            	Utils.LogV("MyBaby", "XMLRPCException-PostRPC-uploadPost: " + e.getMessage());
		        
            	return false;
		    }
		}
		
		
		
		
		public static boolean deletePost(final Post post){
			if(post.getPostid()<=0 
					|| post.getRemoteSyncFlag() == Post.remoteSync.SyncSuccess.ordinal()
					|| !post.getIsLocalDeleted()){
				return false;
			}
			
		    Object[] params={ "unused", 
		    		 post.getPostid(),
	   				 MyBabyApp.currentBlog.getUsername(), 
	   				 MyBabyApp.currentBlog.getPassword()
	   	    		};
		    String xmlrpcMethod="metaWeblog.deletePost";
		    
		    try {
		        getClient().call(xmlrpcMethod, params);
		        
            	post.setRemoteSyncFlag(Post.remoteSync.SyncSuccess.ordinal());
            	PostRepository.save(post);
            	
            	UserRepository.save(MyBabyApp.currentUser);
            	
            	return true;
		    } catch ( Exception e) {
            	post.setRemoteSyncFlag(Post.remoteSync.SyncError.ordinal());
            	PostRepository.save(post);
		        Log.e("MyBaby", "XMLRPCException-PostRPC-deletePost: " + e.getMessage());
		        return false;
		    }
		}
}
