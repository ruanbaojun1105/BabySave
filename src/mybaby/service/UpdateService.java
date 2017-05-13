package mybaby.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.util.MaterialDialogUtil;
import mybaby.util.Utils;

/**
 * 20160612 bj
 */
public class UpdateService extends Service {
    private final static int MAX_DOWNLOAD_THREAD = 2; // 有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
    private final Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD, true);
    // 文件存储
    private File updateDir = null;
    private File updateFile = null;
    // 下载状态
    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;
    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    // 通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent;
    int downloadCount = 0;
    int currentSize = 0;
    long totalSize = 0;
    int updateTotalSize = 0;

    // 在onStartCommand()方法中准备相关的下载工作：
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取传值
        // 创建文件TextUtils.isEmpty(Constants.Channel)?Constants.APP_NAME:Constants.Channel
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(), Constants.APP_NAME);
            updateFile = new File(updateDir.getPath(), Constants.APP_NAME+ String.valueOf(MyBabyApp.version).hashCode()+System.currentTimeMillis()+".apk");//此处需要添加版本号，否则已经下载了不会重复下载，更新一次就无法更新了
        }

        this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        updateIntent = new Intent(this, MyBayMainActivity.class);
        this.updateNotification = new Notification();

        // 设置下载过程中，点击通知栏，回到主界面
        updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
        // 设置通知栏显示内容
        updateNotification.icon = R.drawable.ic_launcher;
        updateNotification.tickerText = "开始下载";
        RemoteViews myNotificationView = new RemoteViews(
                this.getPackageName(),
                R.layout.notification_progress);
        myNotificationView.setTextViewText(R.id.notification_title, "正在下载");
        myNotificationView.setProgressBar(R.id.notification_progress, 100, 0, false);
        myNotificationView.setOnClickPendingIntent(R.id.notification_progress_rela, updatePendingIntent);
        updateNotification.contentView = myNotificationView;
        //updateNotification.flags |= Notification.DEFAULT_ALL;
        updateNotificationManager.notify(0, updateNotification);

        //new Thread(new updateRunnable()).start();

        RequestParams params = new RequestParams("http://s.hibb.me/dl/lms_"+ (TextUtils.isEmpty(Constants.Channel)?"bdzs":(BuildConfig.DEBUG?"bdzs":Constants.Channel))+".apk");
        params.setSaveFilePath(updateFile.getPath());
        params.setExecutor(executor);
        params.setCancelFast(true);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (isDownloading && total > 0) {
                    if (((int) (current * 100 / total))+5 - 10 > downloadCount) {
                        downloadCount += 10;
                        updateNotification.contentView.setTextViewText(
                                R.id.notification_title, "正在下载");
                        updateNotification.contentView.setProgressBar(
                                R.id.notification_progress, 100,(int) (current * 100 / total), false);
                        updateNotificationManager.notify(0, updateNotification);
                    }
                }
            }


            @Override
            public void onSuccess(File result) {
                Toast.makeText(x.app(), "下载完成,点击安装", Toast.LENGTH_LONG).show();
                Utils.sendMessage(DOWNLOAD_COMPLETE, updateHandler);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //if (ex instanceof HttpException||ex instanceof ConnectException) { // 网络错误
                    Utils.sendMessage(DOWNLOAD_FAIL, updateHandler);
                //}
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * 调用系统自带程序打开文件
     * @param context
     * @param f
     */
    public static void openFile(Context context,File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        context.startActivity(intent);
    }
    public static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Uri uri = Uri.fromFile(updateFile);
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            switch (msg.what) {

            case DOWNLOAD_COMPLETE:
                // 点击安装PendingIntent
                updatePendingIntent = PendingIntent.getActivity(
                        UpdateService.this, 0, installIntent, 0);
                updateNotification.contentView.setTextViewText(R.id.notification_title, "下载完成，点击安装！");
                updateNotification.contentView.setProgressBar(R.id.notification_progress, 100, 100, false);
                updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
                updateNotification.tickerText = "下载完成,点击安装。";
                updateNotification.contentView.setOnClickPendingIntent(R.id.notification_progress_rela, updatePendingIntent);
                updateNotificationManager.notify(0, updateNotification);

                // 停止服务
                stopService(updateIntent);
                break;
            case DOWNLOAD_FAIL:
                //Toast.makeText(x.app(), "下载失败", Toast.LENGTH_LONG).show();
                updateNotification.tickerText = "下载失败！";
                updateNotification.contentView.setTextViewText(R.id.notification_title, "下载失败！");
                updateNotification.contentView.setProgressBar(R.id.notification_progress, 100, 0, false);
                updateNotification.contentView.setOnClickPendingIntent(R.id.notification_progress_rela, updatePendingIntent);
                updateNotification.flags=Notification.FLAG_AUTO_CANCEL;
                updateNotificationManager.notify(0, updateNotification);
                // 停止服务
                stopService(updateIntent);

                MaterialDialogUtil.showCommDialog(MyBayMainActivity.activity, "更新失败了？", "是否需要重新下载新版本？", "马上更新", null, true, new MaterialDialogUtil.DialogCommListener() {
                    @Override
                    public void todosomething() {
                        try {
                            updateNotificationManager.cancel(0);
                            updateNotificationManager.cancelAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent updateIntent = new Intent(x.app(),
                                UpdateService.class);
                        x.app().startService(updateIntent);
                    }
                }, new MaterialDialogUtil.DialogCommListener() {
                    @Override
                    public void todosomething() {
                        try {
                            updateNotificationManager.cancel(0);
                            updateNotificationManager.cancelAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new MaterialDialogUtil.DialogCommListener() {
                    @Override
                    public void todosomething() {
                        try {
                            updateNotificationManager.cancel(0);
                            updateNotificationManager.cancelAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            default:
                stopService(updateIntent);
            }
        }
    };

    @Deprecated
    public long downloadUpdateFile(String downloadUrl, File saveFile)
            throws Exception {

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            /*if (currentSize > 0) {
                httpConnection.setRequestProperty(RANGE, bytes= + currentSize + -);
            }*/
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[4096];
            int readsize = 0;
            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                // 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                if ((downloadCount == 0)
                        || (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
                    downloadCount += 10;

                    updateNotification.contentView.setTextViewText(
                            R.id.notification_title, "正在下载");
                    updateNotification.contentView.setProgressBar(
                            R.id.notification_progress, 100, (int) totalSize * 100 / updateTotalSize, false);
                    updateNotificationManager.notify(0, updateNotification);
                }
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return totalSize;
    }

    class updateRunnable implements Runnable {
        public void run() {
            try {
                // 增加权限<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE">;
                if (!updateDir.exists()) {
                    updateDir.mkdirs();
                }
                if (!updateFile.exists()) {
                    updateFile.createNewFile();
                }
                String channel=Constants.Channel;
                long downloadSize = downloadUpdateFile("http://s.hibb.me/dl/lms_"+ (TextUtils.isEmpty(channel)?"bdzs":(BuildConfig.DEBUG?"bdzs":channel))+".apk", updateFile);
                if (downloadSize > 0) {
                    // 下载成功
                    Utils.sendMessage(DOWNLOAD_COMPLETE,updateHandler);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.sendMessage(DOWNLOAD_FAIL,updateHandler);
            }
        }
    }
}