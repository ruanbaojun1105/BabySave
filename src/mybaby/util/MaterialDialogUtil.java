package mybaby.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by LeJi_BJ on 2016/3/10.
 * 全部统一形式管理提示框
 * 提示列表
 */
public class MaterialDialogUtil {

    /**
     * 普通的提示框
     */
    public interface DialogCommListener {
        void todosomething();
    }

    /**
     * 列表的提示框
     */
    public interface DialogListListener {
        void todosomething(int position);
    }

    /**
     * 列表的提示框带参返回
     * 返回一个int类型
     */
    public interface DialogListBackIntListener {
        void todosomething(int position,int type);
    }

    /**
     * 默认可边界返回的
     * @param context
     * @param title
     * @param message
     * @param trueStr
     * @param falseStr
     * @param trueCommListener
     * @param falseCommListener
     * @param dismissCommListener
     */
    public static void showCommDialog(Context context,String title,String message,String trueStr,String falseStr,final DialogCommListener trueCommListener,
                                  final DialogCommListener falseCommListener,
                                  final DialogCommListener dismissCommListener){
        showCommDialog(context,title,message,trueStr,falseStr,true,true,0,trueCommListener,falseCommListener,dismissCommListener);
    }

    /**
     * 一般普通形式的
     * @param context
     * @param title
     * @param message
     * @param trueStr
     * @param falseStr
     * @param trueCommListener
     * @param falseCommListener
     */
    public static void showCommDialog(Context context,String title,String message,String trueStr,String falseStr,final DialogCommListener trueCommListener,
                                            final DialogCommListener falseCommListener){
        showCommDialog(context, title, message, trueStr, falseStr, true, true, 0, trueCommListener, falseCommListener, null);
    }

    public static void showCommDialog(Context context,String title,String message,String trueStr,String falseStr,final DialogCommListener trueCommListener){
        showCommDialog(context, title, message, trueStr, falseStr, true, true,0, trueCommListener, null, null);
    }

    public static void showCommDialog(Context context,String title,String message,String trueStr,String falseStr,boolean canCancelCallBack,
                                      final DialogCommListener trueCommListener,final DialogCommListener falseCommListener,final DialogCommListener dismissCommListener){
        showCommDialog(context, title, message, trueStr, falseStr, true, canCancelCallBack,0, trueCommListener, falseCommListener, dismissCommListener);
    }

    public static void showCommDialog(Context context,String message,String trueStr,String falseStr,final DialogCommListener trueCommListener){
        showCommDialog(context, null, message, trueStr, falseStr, true, true,0, trueCommListener, null, null);
    }

    public static void showCommDialog(Context context,String message,String trueStr,String falseStr,boolean canCancelCallBack,final DialogCommListener trueCommListener){
        showCommDialog(context, null, message, trueStr, falseStr, true,canCancelCallBack, 0, trueCommListener, null, null);
    }
    public static void showCommDialog(Context context,String message,String trueStr,final DialogCommListener trueCommListener){
        showCommDialog(context, null, message, trueStr, null, true,false, 0, trueCommListener, null, null);
    }
    public static void showCommDialog(Context context,String message,final DialogCommListener trueCommListener){
        showCommDialog(context, null, message, null, null, true, true, 0, trueCommListener, null, null);
    }

    public static void showCommDialog(Context context,String title,String message,String trueStr,String falseStr,
                                  boolean canOutside,boolean canCancelCallBack,int bgResId,final DialogCommListener trueCommListener,
                                  final DialogCommListener falseCommListener,
                                  final DialogCommListener dismissCommListener){
        if (context==null)
            return;
        final MaterialDialog mMaterialDialog=new MaterialDialog(context);
        if (bgResId!=0)
            mMaterialDialog.setBackgroundResource(bgResId);
        if (!TextUtils.isEmpty(title))
            mMaterialDialog.setTitle(title);
        if (canCancelCallBack)
            mMaterialDialog.setNegativeButton(TextUtils.isEmpty(falseStr)?"取消":falseStr,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        if (falseCommListener!=null)
                            falseCommListener.todosomething();
                    }
                });
        mMaterialDialog.setMessage(message)
                .setPositiveButton(TextUtils.isEmpty(trueStr)?"确定":trueStr, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        if (trueCommListener != null)
                            trueCommListener.todosomething();
                    }
                })
                .setCanceledOnTouchOutside(canOutside)
                        // You can change the message anytime.
                        // mMaterialDialog.setTitle("提示");
                .setOnDismissListener(
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (dismissCommListener != null)
                                    dismissCommListener.todosomething();
                            }
                        })
                .show();
    }

    /**
     * 普通列表提示框
     * @param context
     * @param title
     * @param items
     * @param listListener
     */
    public static void showListDialog(Context context,String title, String[] items,final DialogListListener listListener){
        if (context==null)
            return;
        if (items==null||items.length==0)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        builder.setCancelable(true);
        if (!TextUtils.isEmpty(title))
            builder.setTitle(title);
        builder.setItems(items,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (listListener!=null)
                            listListener.todosomething(which);
                        dialog.dismiss();
                    }
                });
        Dialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    public static void showListMaterDialog(Context context,String title, String[] items,final DialogListListener listListener){
        if (context==null)
            return;
        if (items==null||items.length==0)
            return;
        final MaterialDialog mMaterialDialog=new MaterialDialog(context);
        if (!TextUtils.isEmpty(title))
            mMaterialDialog.setTitle(title);
        mMaterialDialog.setCanceledOnTouchOutside(true);
        final ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1);
        for (String item:items){
            arrayAdapter.add(item);
        }

        ListView listView = new ListView(context);
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setPadding(0, 10, 0, 10);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listListener!=null)
                    listListener.todosomething(i);
                mMaterialDialog.dismiss();
            }
        });

        mMaterialDialog.setContentView(listView).show();
    }

}
