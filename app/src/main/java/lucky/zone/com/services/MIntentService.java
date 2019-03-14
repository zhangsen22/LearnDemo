package lucky.zone.com.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 作者: created by zhangsen on 2019/3/14
 * 邮箱: zhangsen839705693@163.com
 * IntentService是继承并处理异步请求的一个类，
 * 在IntentService内有一个工作线程来处理耗时操作，
 * 启动IntentService的方式和启动传统的Service一样，
 * 同时，当任务执行完后，IntentService会自动停止，而不需要我们手动去控制或stopSelf()。
 * 另外，可以启动IntentService多次，而每一个耗时操作会以工作队列的方式在IntentService的onHandleIntent回调方法中执行，并且，
 * 每次只会执行一个工作线程，执行完第一个再执行第二个，以此类推。
 */
public class MIntentService extends IntentService {
    private String TAG = MIntentService.class.getSimpleName();

    public MIntentService(){
        super("MIntentService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */


    public MIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"MIntentService   onCreate  ....");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"MIntentService   onStartCommand  ....");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG,"MIntentService   onStart  ....");
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"MIntentService   onBind  ....");
        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, Thread.currentThread().getName() + "--" + intent.getStringExtra("info") );
        for(int i = 0; i < 10; i++){ //耗时操作
            Log.d(TAG,  i + "--" + Thread.currentThread().getName());
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"MIntentService   onDestroy  ....");
        super.onDestroy();
    }

}
