package com.pancoit.mod_main.Utils;

import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

// 捕获异常
public class CatchExceptionUtils implements UncaughtExceptionHandler {

    public final String TAG = "CatchExceptionUtils";
    private CatchExceptionUtils(){}
    // 单例 --------------------------------------
    private static CatchExceptionUtils INSTANCE = new CatchExceptionUtils();
    public static CatchExceptionUtils getInstance() {
        return INSTANCE;
    }

    private Context my_context;
    private UncaughtExceptionHandler handler;


    public void init(Context context) {
        my_context = context;
        handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);  // 注册
    }


    public void uncaughtException(Thread thread, Throwable ex) {
        //保存错误日志
        handleException(ex);
        // 不重启
//        Intent reboot = my_context.getPackageManager().getLaunchIntentForPackage(my_context.getPackageName());
//        reboot.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        my_context.startActivity(reboot);
        System.exit(0);
    }

    // 处理异常
    private void handleException(Throwable ex) {
        if (ex == null) {return;}
        GlobalControlUtils.INSTANCE.showToast("程序出现异常",0);  // 报错
        String stackTraceInfo = getStackTraceInfo(ex);
        Log.e(TAG, "程序崩溃，错误信息："+stackTraceInfo);
        saveThrowableMessage(stackTraceInfo);
//        try {
//            Thread.sleep(2000);  // 两秒后重启程序
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private String getStackTraceInfo(final Throwable throwable) {
        PrintWriter pw = null;
        Writer writer = new StringWriter();
        try {
            pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
        } catch (Exception e) {
            return "";
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return writer.toString();
    }


    // 保存错误日志
    private void saveThrowableMessage(String errorMessage) {
        if(errorMessage==null||errorMessage.isEmpty()) return;
//        FileUtils3.recordError(FileUtils.getLogFile(),errorMessage);
    }

}


