package lucky.zone.com.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.Random;

/**
 * 作者: created by zhangsen on 2019/3/14
 * 邮箱: zhangsen839705693@163.com
 * 1）启动Service服务
 * 单次：startService() —> onCreate() —> onStartCommand()
 * 多次：startService() —> onCreate() —> onStartCommand() —> onStartCommand()
 * 2）停止Service服务
 * stopService() —> onDestroy()
 * 3）绑定Service服务
 * bindService() —> onCreate() —> onBind()
 * 4）解绑Service服务
 * unbindService() —> onUnbind() —> onDestroy()
 * 5）启动绑定Service服务
 * startService() —> onCreate() —> onStartCommand() —> bindService() —> onBind()
 * 6）解绑停止Service服务
 * unbindService() —> onUnbind() —> stopService() —> onDestroy()
 * 7）解绑绑定Service服务
 * unbindService() —> onUnbind(ture) —> bindService() —> onRebind()
 *
 * 作者：翻译不了的声响
 * 链接：https://www.jianshu.com/p/cc25fbb5c0b3
 * 来源：简书
 * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
 */
public class ANRService extends Service {

    private static String TAG = ANRService.class.getSimpleName();

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean flag = true;
    private int lasttick, mTick;
    //通过binder实现调用者client与service之间的通信
    private MyBinder myBinder = new MyBinder();

    private final Random generator = new Random();

    @Override
    public void onCreate() {
        Log.d(TAG, "ANRService -> onCreate, Thread: " + Thread.currentThread().getName());
        exception();
        super.onCreate();
    }

    /**
     * START_NOT_STICKY
     *
     * 如果系统在onStartCommand()方法返回之后杀死这个服务，那么直到接受到新的Intent对象，这个服务才会被重新创建。这是最安全的选项，用来避免在不需要的时候运行你的服务。
     *
     *  
     *
     * START_STICKY
     *
     * 如果系统在onStartCommand()返回后杀死了这个服务，系统就会重新创建这个服务并且调用onStartCommand()方法，但是它不会重新传递最后的Intent对象，系统会用一个null的Intent对象来调用onStartCommand()方法，在这个情况下，除非有一些被发送的Intent对象在等待启动服务。这适用于不执行命令的媒体播放器（或类似的服务），它只是无限期的运行着并等待工作的到来。
     *
     *  
     *
     * START_REDELIVER_INTENT
     *
     * 如果系统在onStartCommand()方法返回后，系统就会重新创建了这个服务，并且用发送给这个服务的最后的Intent对象调用了onStartCommand()方法。任意等待中的Intent对象会依次被发送。这适用于那些应该立即恢复正在执行的工作的服务，如下载文件。
     * ---------------------
     * 作者：taki_dsm
     * 来源：CSDN
     * 原文：https://blog.csdn.net/taki_dsm/article/details/8865913
     * 版权声明：本文为博主原创文章，转载请附上博文链接！
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ANRService -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().getName() + ", flags: "+flags);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "ANRService -> onBind, Thread: " + Thread.currentThread().getName()+"      from:" + intent.getStringExtra("from"));
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "ANRService -> onUnbind, from:" + intent.getStringExtra("from"));
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind......");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "ANRService -> onDestroy, Thread: " + Thread.currentThread().getName());
        flag = false;
        super.onDestroy();
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
                    if (mTick == lasttick) {
                        flag = false;
                        Log.d(TAG, "anr happend in hear    mTick   " + mTick);
                        handlerANRError();
                    }
                }
            }
        }).start();
    }

    private void handlerANRError() {
        Log.d(TAG, "handlerANRError  ");
    }

    private final Runnable tickerRunnable = new Runnable() {
        @Override
        public void run() {
            mTick = (mTick + 1) % 10;
            Log.d(TAG, "mTick is   " + mTick);
        }
    };

    public class MyBinder extends Binder{
        public ANRService getService(){
            return ANRService.this;
        }
    }

    //getRandomNumber是Service暴露出去供client调用的公共方法
    public int getRandomNumber(){
        return generator.nextInt();
    }
}
