package mybaby.util;

import android.util.Log;

/**
 * Created by niubaobei on 2016/1/11.
 */
public class ExceptionUtil {
    public static void getStack(Exception e){
        try {
            e.printStackTrace();
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                int lineNumber = element.getLineNumber();
                String fileName = element.getFileName();
                String methodName = element.getMethodName();
                Log.e("TAG", "fileName:" + fileName + " lineNumber:" + lineNumber + " className:" + className + " methodName" + methodName);
            }
        } catch (Exception e1) {
        }
    }
}
