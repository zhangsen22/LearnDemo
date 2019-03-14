package lucky.zone.com;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lucky.zone.com.services.KillSelfService;

/**
 * Android 中如何捕获未捕获的异常
 * （一）UncaughtExceptionHandler
 * 1、自 定 义 一 个 Application ， 比 如 叫 MyApplication 继 承 Application 实 现
 * UncaughtExceptionHandler。
 * 2、覆写UncaughtExceptionHandler的 onCreate和 uncaughtException方法。
 * ---------------------
 * 作者：麦晓宇
 * 来源：CSDN
 * 原文：https://blog.csdn.net/fishmai/article/details/52170495
 * 版权声明：本文为博主原创文章，转载请附上博文链接！
 * uncaughtException方法里你要处理异常，不处理就会交给系统处理
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{

    private static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private Context mContext;
    /**
     * 获取CrashHandler实例
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d(TAG, "an error occured when collect package info", ex);
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            //退出程序
            AllActivityManager.getInstance().close();
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
                        mContext.startService(new Intent(mContext,KillSelfService.class));
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        //收集设备参数信息
        collectDeviceInfo(mContext);

        //保存日志文件
        return null != saveCrashInfo2File(ex);

    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        FileOutputStream fos = null;
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            String sdDir = "";
            try {
                boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
                if (sdCardExist) {
                    sdDir = Environment.getExternalStorageDirectory() + "/zhangsen/crash/";// SD卡根目录
                } else {
                    sdDir = "/data/data/" + mContext.getPackageName() + "/crash/"; //"/data/data/com.zone.lucky/"
                }
                File file = new File(sdDir);
                if (!file.exists()) {
                    file.mkdirs();
                    if (!file.exists()) {
                        sdDir = mContext.getFilesDir().getPath();
                        File tryfile = new File(sdDir);
                        if (!tryfile.exists()) {
                            tryfile.mkdirs();
                        }
                    }
                }
                sdDir = file.getAbsolutePath();
                fos = new FileOutputStream(sdDir + "/" + fileName);
                fos.write(sb.toString().getBytes("utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logger.d(TAG, "sdDir " + sdDir);
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
