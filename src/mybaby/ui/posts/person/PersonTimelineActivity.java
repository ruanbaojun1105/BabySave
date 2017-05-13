package mybaby.ui.posts.person;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;


import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.posts.edit.EditPostActivity;
import mybaby.ui.user.PersonEditActivity;

public class PersonTimelineActivity extends FragmentActivity {
	PersonTimelineFragment mFragment;
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(getString(R.string.rijigerenliebiao)); //统计页面
	    //MobclickAgent.onResume(this);          //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(getString(R.string.rijigerenliebiao));
	    //MobclickAgent.onPause(this);          //统计时长
	}
	
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mFragment = new  PersonTimelineFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.timeline_list_fragment_container, mFragment).commit();
    }
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//requestCode = requestCode & 0xffff;//android bug:  https://groups.google.com/forum/#!msg/android-developers/NiM_dAOtXQU/ufi_FYqMGxIJ

    	if(MyBabyApp.currentMediaHelper != null){
    		MyBabyApp.currentMediaHelper.onActivityResult(requestCode, resultCode, data);
    	}
        super.onActivityResult(requestCode, resultCode, data);
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(mFragment.getUser().isSelf()){
        	android.view.MenuInflater inflater = getMenuInflater();
        	inflater.inflate(R.menu.person_timeline, menu);
        	
        	MenuItem menuDelete=menu.findItem(R.id.menu_delete);
        	if(mFragment.getPerson().getData().getIsSelf() 
        			|| (mFragment.getPerson().getData().getTypeNumber() == Post.type.Baby.ordinal() 
        				&& PostRepository.loadBabies(mFragment.getUser().getUserId()).length<=1)){
        		menuDelete.setVisible(false);
        	}
        }
        return true;
    }

    // Menu actions
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.menu_write:
	        	new CustomAbsClass.StarEdit() {
	    			@Override
	    			public void todo() {
	    				// TODO Auto-generated method stub
	    				Intent i = new Intent(PersonTimelineActivity.this, EditPostActivity.class);
	    	    		i.putExtra("personId", mFragment.getPerson().getId());
	    	    		startActivityForResult(i, Constants.RequestCode.ACTIVITY_REQUEST_CODE_EDIT_POST_REQUEST);
	    			}
	    		}.StarPostEdit(this);
	            return true;
	        case R.id.menu_cover:
	        	mFragment.getPersonHeader().setCoverUsePicuureLibrary();
	            return true;
	        case R.id.menu_setting:
	            Intent intent = new Intent(this, PersonEditActivity.class);
	            intent.putExtra("personId", mFragment.getPerson().getId());
	            startActivity(intent);
	            return true;
	        case R.id.menu_delete:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setTitle(getResources().getText(R.string.delete));
                dialogBuilder.setMessage(String.format("%1$s \"%2$s\"", getResources().getText(R.string.delete),mFragment.getPerson().getName()));
                dialogBuilder.setPositiveButton(getResources().getText(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int whichButton) {
                            	mFragment.getPerson().delete();
                            	
                            	//�㲥
                                Intent bi = new Intent();
                                bi.setAction(Constants.BroadcastAction.BroadcastAction_Person_Delete);
                                bi.putExtra("id", mFragment.getPerson().getData().getId());
                                LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(bi);
                                
                                finish();
                            }
                        });
                dialogBuilder.setNegativeButton(getResources().getText(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                // Just close the window.
                            }
                        });
                dialogBuilder.setCancelable(true);
                Dialog dialog=dialogBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
	            return true;
	        case android.R.id.home:
	        	finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
