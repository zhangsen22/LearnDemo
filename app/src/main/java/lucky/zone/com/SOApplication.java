package lucky.zone.com;

import android.app.Application;

/**
 * 作者: created by zhangsen on 2019/3/14
 * 邮箱: zhangsen839705693@163.com
 */
public class SOApplication extends Application {

    private static String TAG = SOApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
    }
}
