package mybaby.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import photopickerlib.PhotoPickerActivity;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.community.customclass.CustomAbsClass;

public class MediaHelper {
    PopupWindow mAddMediaPopup;
    Activity mActivity;
    MediaHelperCallback mMediaHelperCallback;

    public interface MediaHelperCallback {
        void onMediaFileDone(String[] mediaFilePaths);
    }


    public MediaHelper(Activity activity,MediaHelperCallback mediaHelperCallback){
        mActivity=activity;
        mMediaHelperCallback=mediaHelperCallback;
    }

    public void launchPictureLibraryAndCameraMenu(View v){
        launchPictureLibraryAndCameraMenu(v,false);
    }
    public void launchPictureLibraryAndCameraMenu(View v,boolean isCrop){
        if(mAddMediaPopup==null){
            setupAddMenuPopup(isCrop);
        }
        showAddMenuPopup(v);
    }


    private void setupAddMenuPopup(final boolean mIsCrop) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                R.layout.actionbar_add_media_cell,
                new String[] {
                        mActivity.getResources().getString(R.string.choose_from_photos),
                        mActivity.getResources().getString(R.string.take_photo)
                });

        View layoutView = mActivity.getLayoutInflater().inflate(R.layout.actionbar_add_media, null, false);
        ListView listView = (ListView) layoutView.findViewById(R.id.actionbar_add_media_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    launchPictureLibrary(mIsCrop);
                } else if (position == 1) {
                    launchCamera(mIsCrop);
                }
                mAddMediaPopup.dismiss();
            }

        });

        int width = mActivity.getResources().getDimensionPixelSize(R.dimen.action_bar_spinner_width);

        mAddMediaPopup = new PopupWindow(layoutView, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mAddMediaPopup.setBackgroundDrawable(new ColorDrawable());
    }


    private void showAddMenuPopup(View view){
        int y_offset = mActivity.getResources().getDimensionPixelSize(R.dimen.action_bar_spinner_y_offset);
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        mAddMediaPopup.showAtLocation(view, Gravity.TOP | Gravity.CENTER, loc[0],
                loc[1] + view.getHeight() + y_offset);
    }


    public void launchPictureLibrary(){
        launchPictureLibrary(false);
    }

    /**
     * 单选图片
     * @param isCrop 是否需要裁剪
     */
    public void launchPictureLibrary(boolean isCrop) {
        MyBabyApp.currentMediaHelper=this;
        CustomAbsClass.starPhotoPickerActivity(mActivity,Constants.RequestCode.ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY,true,isCrop);
    }

    /**
     * 多选照片，默认限制9
     * @param limitCount
     */
    public void launchMulPicker(int limitCount){
        MyBabyApp.currentMediaHelper=this;
        CustomAbsClass.starPhotoPickerActivity(mActivity,Constants.RequestCode.ACTIVITY_REQUEST_CODE_MUL_PICKER
                ,true, PhotoPickerActivity.MODE_MULTI,limitCount);
    }

    /**
     * 直接开启相机
     * @param isCrop 裁剪
     */
    public void launchCamera(boolean isCrop) {
        MyBabyApp.currentMediaHelper=this;
        CustomAbsClass.starPhotoPickerActivity(mActivity,Constants.RequestCode.ACTIVITY_REQUEST_CODE_TAKE_PHOTO,isCrop);
    }

    public void launchCamera() {
        launchCamera(false);
    }

    /**
     * 所有回调接收返回给activity
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        /*单选或者直接开启相机*/
        if(requestCode ==  Constants.RequestCode.ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY ||requestCode ==  Constants.RequestCode.ACTIVITY_REQUEST_CODE_TAKE_PHOTO){
            if (resultCode == Activity.RESULT_OK) {
                if (intent==null)
                    return;
                String path=null;
                ArrayList<String> result = intent.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                if (result == null)
                    return;
                if (result.size()>0){
                    path =result.get(0);
                }else return;
                result.clear();
                if (TextUtils.isEmpty(path))
                    return;
                mMediaHelperCallback.onMediaFileDone(new String[]{path});
            }
        }else if(requestCode == Constants.RequestCode.ACTIVITY_REQUEST_CODE_MUL_PICKER){
            if (resultCode == Activity.RESULT_OK) {
                if (intent==null)
                    return;
                String path=null;
                ArrayList<String> result = intent.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                if (result == null)
                    return;
                String[] strs=new String[result.size()];
                for (int i = 0; i <result.size() ; i++) {
                    strs[i]=result.get(i);
                }
                mMediaHelperCallback.onMediaFileDone(strs);
            }
        }
    }

    public static long getDatetimeFromURI(String uri) {
        Cursor imageCursor = null;
        long lngLastDatetime=0;

        try {
            final String[] columns = {MediaStore.Images.Media.DATE_ADDED};
            final String where=MediaStore.Images.Media.DATA + "='" + uri + "'";
            imageCursor = MyBabyApp.getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, where, null, null);
            while (imageCursor.moveToNext()) {
                lngLastDatetime = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }
        }
        return lngLastDatetime*1000;//系统数据用的是秒，转换为毫秒
    }

}
