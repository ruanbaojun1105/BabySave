package mybaby.push;

import android.content.Context;
import android.os.Looper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import mybaby.util.LogUtils;

/**
 * 异常处理类
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-04
 * Time: 14:48
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();
    private Context mContext;
    private static volatile CrashHandler instance;
    private Thread.UncaughtExceptionHandler defalutHandler;
    private DateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss.SSS", Locale.CHINA);
    private CrashHandler(){

    }
    /**
     * 获得单例
     * @return 单例
     */
    public static CrashHandler getInstance() {
        if (instance==null){
            synchronized (CrashHandler.class){
                if (instance==null){
                    instance=new CrashHandler();
                }
            }
        }
        return instance;
    }

    public void init(Context context){
        mContext=context.getApplicationContext();
        defalutHandler=Thread.getDefaultUncaughtExceptionHandler();
        // 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 设置该CrashHandler为程序的默认处理器
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        boolean hasHandle=handleException(ex);
        //是否处理
        if (!hasHandle && defalutHandler!=null){
            defalutHandler.uncaughtException(thread,ex);
            //如果用户没有处理则让系统默认的异常处理器来处理
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LogUtils.e(TAG+ "error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            /*Intent intent=new Intent(mContext, MyBayMainActivity.class);
            mContext.startActivity(intent);*/
        }


    }


    private boolean handleException(final Throwable ex){
        if (ex==null){
            return false;
        }
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();

                ex.printStackTrace();

                String err="["+ex.getMessage()+"]";
                //Toast.makeText(mContext, "程序出现异常,5秒后自动退出", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        String str = collectDeviceInfo(ex);
        // 收集设备参数信息,日志信息
        saveCrashInfoToFile(str);
        // 保存日志文件
        return true;
    }

    /**
     * 收集设备信息，日志信息
     * @param ex Throwable
     * @return 收集的信息
     */
    private String collectDeviceInfo(Throwable ex){
        LogUtils.e(TAG,"collectDeviceInfo:"+ex.getMessage());
        StringBuilder builder=new StringBuilder();

        return builder.toString();
    }

    /**
     * 保存出错信息
     * @param error 待保存的出错信息
     */
    private void saveCrashInfoToFile(String error){
        LogUtils.e(TAG,"saveCrashInfoToFile:"+error);
    }
}