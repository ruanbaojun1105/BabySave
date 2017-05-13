package mybaby.ui.posts;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.hibb.mybaby.android.R;
import mybaby.models.User;
import mybaby.models.UserRepository;
import mybaby.models.diary.Media;
import mybaby.models.diary.Post;
import mybaby.models.friend.FriendRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.Person;
import mybaby.rpc.PostRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.ImageViewUtil;
import mybaby.util.StringUtils;

public abstract class TimelineListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
	   protected User mUser;  
	
	   protected Post[] mPosts;
	   protected BaseQuickAdapter mListAdapter;

	   int mFriendsCount;
	   boolean mIsFriend;

	   public static boolean refushOk=true;
	   public RecyclerView mRecyclerView;
	   public SwipeRefreshLayout mSwipeRefreshWidget;

		@Override
	    public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);

	        MyBabyApp.initScreenParams(this.getActivity());
	        
	        Bundle extras = getActivity().getIntent().getExtras();
	        if (extras != null) {
	            int userId = extras.getInt("userId");
	            if(userId>0){
	            	mUser=UserRepository.load(userId);
	            }else{
	            	mUser=MyBabyApp.currentUser;
	            }
	        }else{
	        	mUser=MyBabyApp.currentUser;
	        }

//	        Utils.LogI("mUser", mUser==null ? "null" : mUser.getName());
//	        Utils.LogI("currentUser", MyBaby.currentUser==null ? "null" : MyBaby.currentUser.getName());
//	        Utils.LogI("MyBaby.screenWidth",String.valueOf(MyBaby.screenWidth));
//	        Utils.LogI("MyBaby.version",String.valueOf(MyBaby.version));
//	        Utils.LogI("myBabyDB",MyBaby.myBabyDB==null ? "null" : "mei wen ti");
	    }
	   
	   @Override
	   public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		   View v=inflater.inflate(R.layout.fragment_timeline_list, null);
		   mRecyclerView= (RecyclerView) v.findViewById(R.id.recycle_view);
		   mSwipeRefreshWidget= (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_widget);
		   init();
		   mFriendsCount=FriendRepository.friendCount();
		   mIsFriend=FriendRepository.existByUserId(mUser.getUserId());
		   BaseOnrefreshAndLoadMoreFragment.setRecyclerStyle(mSwipeRefreshWidget);
		   mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		   mSwipeRefreshWidget.setOnRefreshListener(this);
		   mRecyclerView.setHasFixedSize(true);
		   mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		   mListAdapter=new ListAdapter(getPosts());
		   mListAdapter.addHeaderView(createHeader());
		   mRecyclerView.setAdapter(mListAdapter);
			  return  v;
	   }
	   
	    @Override
		public void onResume() {
	        super.onResume();

	    }

	@Override
	public void onRefresh() {
		try {
			Refush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	    protected abstract void Refush();
	protected abstract void init();
	    protected abstract View createHeader();
	    
	    protected abstract void reCreateHeader(Intent intent);

	    protected abstract Baby getAgeForBaby();
	    
	    public static void setHeaderBackgroundImage(ImageView imageView,Person person){
	    	if (person!=null&&imageView!=null) {
				Media background=person.getBackground();
				if(background != null){
					ImageViewUtil.showImageByMediaFile(imageView, background);
				}else{
					imageView.setImageResource(R.drawable.home_backgroud);
					}
				}
	    }

	protected abstract List<Post> getPosts();
	    
	    /*protected  void loadPosts() {
	        try {
	        	if(mPosts==null){
	        		mPosts =new Post[0];
	        	}
	        } catch (Exception e1) {
				onLoad();
	            return;
	        }

            if (mListAdapter != null) {
            	mListAdapter.notifyDataSetChanged();
            	onLoad();
            }else{
				List<Post> postList=new ArrayList<>();
				if (mPosts!=null)
					for (Post post:mPosts)
						postList.add(post);
            	mListAdapter = new ListAdapter(postList);
            	mRecyclerView.setAdapter(mListAdapter);
            	onLoad();
            }
	    }*/
	   
	    /**
	     * 刷新完毕后
	     */
	    public void onLoad() {
	    	mSwipeRefreshWidget.setRefreshing(false);
	    	refushOk=true;
	    }

	    private String getTime() {
	        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
	    }
	   
	   
	    public void reLoadPost() {
	    	refushOk=false;
			PostRPC.getPosts(MyBabyApp.currentUser, new XMLRPCCallback() {
				
				@Override
				public void onSuccess(long id, Object result) {
					if(StringUtils.isEmpty(MyBabyApp.currentUser.getFrdTelephoneCountryCode())){
                		MyBabyApp.currentUser.setFrdTelephoneCountryCode(86+"");
                 		MyBabyApp.currentUser.setBzInfoModified(true);
                 		UserRepository.save(MyBabyApp.currentUser);
                	}
					new CustomAbsClass.doSomething(getActivity()) {
						@Override
						public void todo() {
							mListAdapter.addHeaderView(createHeader());
							mListAdapter.setNewData(getPosts());
							onLoad();
						}
					};
				}
				
				@Override
				public void onFailure(long id, XMLRPCException error) {
					new CustomAbsClass.doSomething(getActivity()) {
						@Override
						public void todo() {
							Toast.makeText(MyBabyApp.getContext(), "网络错误！", Toast.LENGTH_SHORT).show();
							onLoad();
						}
					};
				}
			});
		}




		private class ListAdapter extends BaseQuickAdapter<Post> {

	        public ListAdapter(List<Post> postList) {
				super(R.layout.post,postList);
	        }

			@Override
			protected void convert(BaseViewHolder baseViewHolder, Post post) {
				PostHolder postHolder=new PostHolder(baseViewHolder.convertView);

				PostRow.fillContent(getFragment(),mUser, post,getAgeForBaby(),mIsFriend,postHolder);
			}

	    }
	public static class PostHolder {
		LinearLayout post_lin;
		TextView txtDateMonth;
		TextView txtDateDay;
		TextView txtDateYear;
		TextView txtAge;
		TextView txtContent;
		TextView tv_spread;
		TextView btnEdit;
		TextView btnComment;
		TextView iconLock;
		TextView btnShare;
		TextView btnMore;
		RelativeLayout rela_place_pic;
		TextView txtPlace;
		TextView place_pic;
		FrameLayout mediasContainer;
		View first_line;

		public PostHolder(View pv) {
			 post_lin= (LinearLayout) pv.findViewById(R.id.post_lin);
			 txtDateMonth= (TextView) pv.findViewById(R.id.post_date_month);
			 txtDateDay= (TextView) pv.findViewById(R.id.post_date_day);
			 txtDateYear= (TextView) pv.findViewById(R.id.post_date_year);
			 txtAge= (TextView) pv.findViewById(R.id.post_age);
			 txtContent = (TextView) pv.findViewById(R.id.post_text_content);
			 tv_spread = (TextView) pv.findViewById(R.id.bt_spread);
			 btnEdit= (TextView) pv.findViewById(R.id.post_edit);
			 btnComment= (TextView) pv.findViewById(R.id.post_comment);
			 iconLock= (TextView) pv.findViewById(R.id.post_privacy);
			 btnShare= (TextView) pv.findViewById(R.id.post_share);
			 btnMore= (TextView) pv.findViewById(R.id.post_more);
			 rela_place_pic = (RelativeLayout) pv.findViewById(R.id.rela_place_pic);
			 txtPlace = (TextView) pv.findViewById(R.id.post_place);
			 place_pic = (TextView) pv.findViewById(R.id.place_pic);
			 mediasContainer = (FrameLayout) pv
					.findViewById(R.id.post_medias_container);
			 first_line = pv.findViewById(R.id.first_line);
		}
	}
	    private TimelineListFragment getFragment(){
	    	return this;
	    }
	    
	    
	    //������ָ����pid
	    protected void scrollTo(int pId){
			if (mPosts==null)
				return;
	    	for(int i=0;i<mPosts.length;i++){
	    		if(pId==mPosts[i].getId()){
	    			mRecyclerView.smoothScrollToPosition(i);
	    		}
	    	}
	    }
	    
	    
	    public User getUser(){
	    	return mUser;
	    }
	    

}
