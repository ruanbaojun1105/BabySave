//来自:https://github.com/jackyhwb/MultiPhotoPicker

package mybaby.ui.photopicker;

import me.hibb.mybaby.android.R;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.Toast;

public class GalleryPickerActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, MultiChoiceModeListener {
	final static String[] PROJECTION = { MediaStore.Images.Media.DATA,
			MediaStore.Images.Media._ID };
	final static String SORTORDER = MediaStore.Images.Media._ID + " DESC";
	final static Uri URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	public static final int DATA_INDEX = 0;
	public static final int ID_INDEX = 1;
	private static final int LOADER_ID = 0;
	private GridView mGridView;
	private GalleryAdapter mAdapter;
	private boolean mIsMultiple;
	private int mMaxSelectionsAllowed;

	public static final String EXTRA_DATA = "goodev.intent.extra.DATA";
	public static final String EXTRA_IS_MULTIPLE = "org.goodev.picker.is_multiple";
	public static final String EXTRA_SELECTION_LIMIT = "org.goodev.picker.selection_limit";
	public static final int RESULT_CAMERA = 6538;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_picker_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.photos);

		mIsMultiple = getIntent().getBooleanExtra(EXTRA_IS_MULTIPLE, true);
		mMaxSelectionsAllowed = getIntent().getIntExtra(EXTRA_SELECTION_LIMIT,
				Integer.MAX_VALUE);
		final GridView view = (GridView) findViewById(R.id.grid);
		mAdapter = new GalleryAdapter(this);
		view.setAdapter(mAdapter);
		view.setOnItemClickListener(this);
		view.setMultiChoiceModeListener(this);
		mGridView = view;
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, URI, PROJECTION, null, null, SORTORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.photo_picker_common, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_picker_camera) {
			setResult(RESULT_CAMERA);
			finish();
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mIsMultiple) {
			if (view instanceof Checkable) {
				Checkable c = (Checkable) view;
				boolean checked = !c.isChecked();
				mGridView.setItemChecked(position, checked);
			}
		} else {
			Uri uri = ContentUris.withAppendedId(URI, id);
			Intent data = new Intent();
			data.putExtra(EXTRA_DATA, uri);
			setResult(RESULT_OK, data);
			finish();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.photo_picker, menu);
		int selectCount = mGridView.getCheckedItemCount();
		String title = getTitleString(selectCount);
		mode.setTitle(title);
		return true;
	}

	
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}
	
	

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if (item.getItemId() == R.id.menu_done) {
			// System.out.println(item.getTitle() +" "+ item.getItemId());
			long[] ids = mGridView.getCheckedItemIds();
			Uri[] uris = new Uri[ids.length];
			for (int i = 0; i < ids.length; i++) {
				Uri uri = ContentUris.withAppendedId(URI, ids[i]);
				uris[i] = uri;
			}

			Intent data = new Intent();
			data.putExtra(EXTRA_DATA, uris);
			setResult(RESULT_OK, data);
			finish();
			return true;
		}
		return false;
	}
	

	@Override
	public void onDestroyActionMode(ActionMode mode) {
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		if (checked && mGridView.getCheckedItemCount() > mMaxSelectionsAllowed) {
			mGridView.setItemChecked(position, false);
			Toast.makeText(this,"当前最多可以选择"+mMaxSelectionsAllowed+"张照片",
					Toast.LENGTH_SHORT).show();
		} else {
			int selectCount = mGridView.getCheckedItemCount();
			String title = getTitleString(selectCount);
			mode.setTitle(title);
		}
	}

	private String getTitleString(int selectCount) {
		return String.format("%1$s: %2$s/%3$s", getString(R.string.photos),
				selectCount, mMaxSelectionsAllowed);
	}
}
