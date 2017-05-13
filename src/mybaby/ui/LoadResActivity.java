package mybaby.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.view.Window;
import android.widget.ImageView;

import me.hibb.mybaby.android.R;
import mybaby.util.LogUtils;

public class LoadResActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //System.out.print("t1------");
        super.onCreate(savedInstanceState);
        //System.out.print("t2------");
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
        //System.out.print("t3------");
        setContentView(R.layout.image_item);
        //System.out.print("t4------");
        ((ImageView)findViewById(R.id.image)).setImageResource(R.drawable.welcome_logo);
        //System.out.print("t5------");
        new LoadDexTask().execute();
    }

    @Override
    public void finish() {
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
    }

    class LoadDexTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                MultiDex.install(getApplication());
                LogUtils.d("loadDex>>>install finish");
                ((MyBabyApp) getApplication()).installFinish(getApplication());
            } catch (Exception e) {
                LogUtils.e("loadDex" + e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            LogUtils.d("loadDex>>>>get install finish");
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        //cannot backpress
    }
}