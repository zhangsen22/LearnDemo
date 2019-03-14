package lucky.zone.com.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

/**
 * 作者: created by zhangsen on 2019/3/14
 * 邮箱: zhangsen839705693@163.com
 */
public class ANRService extends Service {

    private static String TAG = ANRService.class.getSimpleName();

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean flag = true;
    private int lasttick, mTick;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        exception();
        super.onCreate();
    }

    private void exception() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    lasttick = mTick;
                    handler.post(tickerRunnable);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(mTick == lasttick){
                        flag = false;
                        Log.d(TAG,"anr happend in hear    mTick   "+mTick);
                        handlerANRError();
                    }
                }
            }
        }).start();
    }

    private void handlerANRError() {
        Log.d(TAG,"handlerANRError  ");
    }

    private final Runnable tickerRunnable = new Runnable() {
        @Override
        public void run() {
            mTick = (mTick + 1) % 10;
            Log.d(TAG,"mTick is   "+mTick);
        }
    };
}
