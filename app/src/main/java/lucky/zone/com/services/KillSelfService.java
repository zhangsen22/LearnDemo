package lucky.zone.com.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import lucky.zone.com.Logger;

/**
 * 作者: created by zhangsen on 2019/3/15
 * 邮箱: zhangsen839705693@163.com
 */
/**
 * 自杀式服务--重启app
 */
public class KillSelfService extends Service {

    private static final String TAG = KillSelfService.class.getSimpleName();
    private Handler handler;

    public KillSelfService() {
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long stopDelayed = intent.getLongExtra("Delayed", 2000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(LaunchIntent);
                KillSelfService.this.stopSelf();
            }
        }, stopDelayed);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG,"onDestroy .....");
        handler.removeCallbacksAndMessages(null);
        handler = null;
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        Logger.d(TAG,"onCreate .....");
        super.onCreate();
    }
}
