package cn.call110.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import cn.call110.R;
import cn.call110.utils.DataUtils;

public class WelcomeActivity extends Activity {
    private Timer timer;
    final private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1314){
                timer.cancel();
                Intent intent = new Intent(WelcomeActivity.this,
                        HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        DataUtils.initPhoneList();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1314);
            }
        },2000,5000);
    }
    @Override
    protected void onDestroy(){
        timer.cancel();
        super.onDestroy();
    }
}
