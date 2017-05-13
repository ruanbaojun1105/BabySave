package mybaby.ui.posts.person;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.diary.Post;
import mybaby.ui.MyBabyApp;
import mybaby.models.User;
import mybaby.models.UserRepository;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.Person;
import mybaby.ui.posts.TimelineListFragment;


public class PersonTimelineFragment extends TimelineListFragment {

	private Person mPerson;
	private PersonHeader mPersonHeader;

	private  MyBroadcastReceiver mBroadcastReceiver;

	private FrameLayout headView;
	   
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
 
        //注册广播接收
        mBroadcastReceiver=new MyBroadcastReceiver();
        mBroadcastReceiver.registerMyReceiver();
        
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            int personId = extras.getInt("personId");
            try {
				if(personId>0){
					mPerson=PostRepository.load(personId).createPerson();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        if (mPerson==null)
			return;
		try {
			if(getUser().isSelf()){
                if(mPerson.getData().getIsSelf()){
                    getActivity().getActionBar().setTitle(R.string.me);
                }else{
                    getActivity().getActionBar().setTitle(mPerson.getName());
                }
            }else{
                if(mPerson.getData().getIsSelf()){
                    getActivity().getActionBar().setTitle(getUser().getName());
                }else{
                    getActivity().getActionBar().setTitle(getUser().getName() + " - " + mPerson.getName());
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(getUser().isSelf()){
        	//TODO  原本是发送谷歌统计的Tracker  其名称为Person Timeline
        }else{
        	//TODO  原本是发送谷歌统计的Tracker  其名称为Friend Person Timeline
        }
    }

    @Override
    protected void Refush() {
    	// TODO Auto-generated method stub
		onLoad();
    }

	@Override
	protected void init() {

	}

	@Override
	protected View createHeader(){
		User user=UserRepository.load(mPerson.getData().getUserid());
		mPersonHeader=new PersonHeader(getActivity());
		if (headView==null) {
			headView=new FrameLayout(getContext());
		}else
			headView.removeAllViews();
		headView.addView(mPersonHeader.setHeadData(user, mPerson));
		return headView;
	}
	
	@Override
	protected void reCreateHeader(Intent intent){
		mPerson=PostRepository.load(mPerson.getId()).createPerson();
    }
	
	@Override
	protected Baby getAgeForBaby(){
		if(mPerson.getClass()==Baby.class){
			return (Baby)mPerson;
		}else{
			return null;
		}
    }

	@Override
	protected List<Post> getPosts() {
		Post[] mPosts=null;
		List<Post> postList=new ArrayList<>();
		try {
			mPosts =PostRepository.loadPostsByPerson(mPerson.getId());
		} catch (Exception e1) {
			return postList;
		}
		if (mPosts!=null)
			for (Post post:mPosts)
				postList.add(post);
		return postList;
	}

	/*@Override
	protected void loadPosts() {
        try{
        	mPosts =PostRepository.loadPostsByPerson(mPerson.getId());
        	
        } catch (Exception e1) {
            return;
        }
        super.loadPosts();
	}*/
	
    
    public Person getPerson(){
    	return mPerson;
    }
    
    public PersonHeader getPersonHeader(){
    	return mPersonHeader;
    }


    @Override
	public void onDestroy()
    {
    	super.onDestroy();
    	
	    if(mBroadcastReceiver!=null)
	    	LocalBroadcastManager.getInstance(MyBabyApp.getContext()).unregisterReceiver(mBroadcastReceiver);
    }
    
    
	//广播接收并响应处理
    private  class MyBroadcastReceiver extends BroadcastReceiver
    {
    	public void registerMyReceiver(){
            IntentFilter filter = new IntentFilter();
    		filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Add);
	        filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Edit);
	        filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Delete);
	        filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
	        LocalBroadcastManager.getInstance(MyBabyApp.getContext()).registerReceiver(this, filter);
    	}	    	
    	
	    @Override
	    public void onReceive(Context context, Intent intent)
	    {
	    	if(mUser.isSelf()){
		    	if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Post_Add)
		    		|| intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Post_Edit)
		    		|| intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Post_Delete))
			    {
					mListAdapter.setNewData(getPosts());
		    		if(intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Post_Add)){
		    			int pid=intent.getIntExtra("id", 0);
		    			scrollTo(pid);
		    		}
			    }else if(intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Person_Edit)){
			    	reCreateHeader(intent);
					mListAdapter.addHeaderView(createHeader());
			    }
	    	}
	    }
    }
}
