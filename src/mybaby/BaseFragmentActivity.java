package mybaby;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.xutils.x;

/**
 * Created by wyouflf on 15/11/4.
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
    
}
