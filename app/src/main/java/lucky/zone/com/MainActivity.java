package lucky.zone.com;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import lucky.zone.com.services.ANRService;
import lucky.zone.com.services.MIntentService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public volatile String string;

    @BindView(R.id.tv_intentservice)
    TextView tvIntentservice;
    @BindView(R.id.tv_volatile)
    TextView tvVolatile;
    @BindView(R.id.tv_ANR)
    TextView tvANR;
    @BindView(R.id.tv_startService)
    TextView tvStartService;
    @BindView(R.id.tv_stopService)
    TextView tvStopService;
    @BindView(R.id.tv_start_activityA)
    TextView tvStartActivityA;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        intent = new Intent(this, ANRService.class);
        //动态换取权限
        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Logger.d(TAG,"aBoolean  is  "+aBoolean);
                        if (aBoolean) {

                        } else {
                            finish();
                        }
                    }
                });
    }

    @OnClick({R.id.tv_intentservice, R.id.tv_volatile, R.id.tv_ANR, R.id.tv_startService, R.id.tv_stopService, R.id.tv_start_activityA})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.tv_intentservice:
                startIntentService();
                break;
            case R.id.tv_volatile:
                openVolatile();
                break;
            case R.id.tv_ANR:
                Intent anrIntent = new Intent(this, ANRService.class);
                startService(anrIntent);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "点我你就发了", Toast.LENGTH_SHORT).show();
                    }
                }).start();
                break;
            case R.id.tv_startService:
                startService(intent);
                break;
            case R.id.tv_stopService:
                stopService(intent);
                break;
            case R.id.tv_start_activityA:
                startActivity(new Intent(this, ActivityA.class));
                break;
        }
    }

    private void openVolatile() {
//        volatile关键字的总结
//        1.volatile关键字只用修饰变量，不能修饰方法和类。
//        2.volatile变量的值都是从主存中获取的，而不是从线程的本地内存。
//        3.long和double变量被volatile关键字修饰之后，读写(赋值操作，读取操作)都是原子操作.
//        4.使用volatile关键字可以避免内存不一致的错误；写入volatile变量一定会比接下来的读操作先发生。5.从jdk5开始对volatile变量的修改对其他的线程都是可见的；当线程读取volatile变量的时候，会先把其他线程中缓存着的volatile变量(如果还没有更新到主存中的时候)强制写入到主存。
//        6.除了long和double其他的基本类型读写操作都是原子性的；引用类型的读写操作也是原子性的。
//        7.volatile变量只能做简单的读写，没有锁，没有阻塞。
//        8.volatile变量可以是空的.
//        9.volatile不能保证原子性，比如volatile修饰的int变量++操作还是非原子的。
//        10.变量没有在多个线程之间共享，没有必要做任何同步的操作，比如使用volatile关键字修饰。
//        synchronized和volatile关键字的比较
//        volatile关键字代替不了synchronized关键字，不过在某些场合可以作为替代方案。
//        1.volatile关键字只能修饰字段，而synchronized只能修饰代码块和方法。
//        2.synchronized关键字需要获得锁释放锁，volatile关键字不需要。
//        3.synchronized代码块或方法在等待锁的时候会被阻塞；volatile不是这样的。
//        4.synchronized代码块或方法会比volatile关键字更影响性能。
//        5.volatile关键只同步被修饰的变量，而synchronized关键字却同步代码块或方法中所有的变量，并且还会获得锁释放锁，所以synchronized的负载更大。
//        6.不能同步(synchronized)null对象，而volatile变量可以是null的。
//        7.读取volatile变量效果等同获取锁，写入volatile变量效果等同释放锁。
    }

    public void startIntentService() {
        Intent intent = new Intent(this, MIntentService.class);
        intent.putExtra("info", "good good study");
        startService(intent);
    }
}
