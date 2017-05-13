package mybaby.ui.Notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;

/**
 * Created by bj on 2016/1/7.
 */
public class ContactsListFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    private RelativeLayout toseefance;
    private RelativeLayout toseefollow;

    public ContactsListFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.contacts_fragment, null);
        OverScrollDecoratorHelper.setUpStaticOverScroll(rootView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        return rootView;
    }

    public void init(){
        toseefollow= (RelativeLayout) rootView.findViewById(R.id.to_see_follow);
        toseefollow.setOnClickListener(this);
        toseefance= (RelativeLayout) rootView.findViewById(R.id.to_see_fance);
        toseefance.setOnClickListener(this);

        ((TextView)rootView.findViewById(R.id.to_see_follow_tag)).setTypeface(MyBabyApp.fontAwesome);
        ((TextView)rootView.findViewById(R.id.to_see_fance_tag)).setTypeface(MyBabyApp.fontAwesome);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.to_see_fance:
                CustomAbsClass.starPersionFance(getActivity(), MyBabyApp.currentUser.getUserId());
                break;
            case R.id.to_see_follow:
                CustomAbsClass.starPersionFollow(getActivity(), MyBabyApp.currentUser.getUserId());
                break;
        }
    }
}
