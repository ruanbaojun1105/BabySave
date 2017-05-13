package mybaby.ui;

import me.hibb.mybaby.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class DialogActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_dialog);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		finish();
		return true;
	}

}