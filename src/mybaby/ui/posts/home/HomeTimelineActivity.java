package mybaby.ui.posts.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.person.Baby;
import mybaby.models.person.Person;
import mybaby.ui.MediaHelper;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.customclass.CustomAbsClass;

public class HomeTimelineActivity extends FragmentActivity implements MediaHelper.MediaHelperCallback {
	private HomeTimelineFragment mFragment;
    MediaHelper mMediaHelper;
    boolean needOpenPhotos=false;
    boolean needSelectPhoto=false;
    Person person;

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_timeline_list);
        setProgressBarIndeterminateVisibility(false);

        initActionBar();

        mMediaHelper=new MediaHelper(this,this);
        mFragment = new  HomeTimelineFragment();
        //mFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.timeline_list_fragment_container, mFragment).commit();
        if (getIntent().getExtras()!=null) {
            needOpenPhotos = getIntent().getExtras().getBoolean("needOpenPhotos", false);
            needSelectPhoto = getIntent().getExtras().getBoolean("needSelectPhoto", false);
            person= (Baby) getIntent().getExtras().getSerializable("person");
        }
		if (needOpenPhotos) {
            new CustomAbsClass.StarEdit() {
                @Override
                public void todo() {
                    // TODO Auto-generated method stub
                    mMediaHelper.launchMulPicker(Constants.MAX_PHOTO_PER_POST);
                }
            }.StarPostEdit(this);
        }

        if (needSelectPhoto){
            mMediaHelper.launchPictureLibrary(true);
        }
    }

    private void initActionBar(){
        View view = LayoutInflater.from(this).inflate(
                R.layout.actionbar_friend, null);
        TextView tv_title = (TextView) view.findViewById(R.id.actionbar_title);
        TextView tv_back = (TextView) view.findViewById(R.id.actionbar_back);
        tv_back.setTypeface(MyBabyApp.fontAwesome);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_title.setText("宝宝空间");
        new UpdateRedTextReceiver((TextView) view.findViewById(R.id.actionbar_back_badge)).regiest();
        tv_back.setTextColor(Color.WHITE);
        tv_title.setTextColor(Color.WHITE);
        view.setBackgroundColor(getResources().getColor(R.color.mainThemeColor));
        ImageView addPicture = (ImageView) view.findViewById(R.id.addPicture);
        addPicture.setImageResource(R.drawable.edit_tag);
        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAbsClass.starPostEdit(HomeTimelineActivity.this);
            }
        });
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //requestCode = requestCode & 0xffff;//android bug:  https://groups.google.com/forum/#!msg/android-developers/NiM_dAOtXQU/ufi_FYqMGxIJ

        if(MyBabyApp.currentMediaHelper != null){//此处必须是MyBabyApp.currentMediaHelper，否则影响其他地方调用图片库
            MyBabyApp.currentMediaHelper.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMediaFileDone(String[] mediaFilePaths) {
            if(mediaFilePaths.length>0){
                if (needOpenPhotos)
                    TodayWrite.openEditPostActivity(this, mediaFilePaths,false);
                else {
                    if (person==null)
                        return;
                    person.setAvatar(mediaFilePaths[0]);
                    //广播
                    Intent bi = new Intent();
                    bi.setAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
                    bi.putExtra("id", person.getId());
                    LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(bi);
                }
        }
    }

    // Menu actions
    /*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}*/
}
