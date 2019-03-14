package lucky.zone.com;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lucky.zone.com.services.ANRService;

public class ActivityB extends AppCompatActivity {

    private static String TAG = ActivityB.class.getSimpleName();
    @BindView(R.id.tv_bindService)
    TextView tvBindService;
    @BindView(R.id.tv_unbindService)
    TextView tvUnbindService;
    @BindView(R.id.tv_finish)
    TextView tvFinish;

    private boolean isBinder = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBinder = true;
            Log.d(TAG, "ActivityB onServiceConnected");
            ANRService.MyBinder myBinder = (ANRService.MyBinder) service;
            ANRService service1 = myBinder.getService();
            int randomNumber = service1.getRandomNumber();
            Log.d(TAG, "ActivityB 中调用 ANRService的getRandomNumber方法, 结果: " + randomNumber);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBinder = false;
            Log.d(TAG, "ActivityB onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        ButterKnife.bind(this);
        Log.d(TAG, "ActivityB -> onCreate, Thread: " + Thread.currentThread().getName());
    }

    @OnClick({R.id.tv_bindService, R.id.tv_unbindService, R.id.tv_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_bindService:
                Intent intent = new Intent(this, ANRService.class);
                intent.putExtra("from","ActivityB");
                bindService(intent,serviceConnection,BIND_AUTO_CREATE);
                break;
            case R.id.tv_unbindService:
                if(isBinder) {
                    unbindService(serviceConnection);
                }
                break;
            case R.id.tv_finish:
                finish();
                break;
        }
    }
}
