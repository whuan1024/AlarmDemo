package com.example.tommy.alarmdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 模拟三种方式设置定时器，同时研究Service的生命周期
 * 1、在Activity中以setRepeating方式设置定时器，要持锁保持不熄屏Alarm才能正常触发
 * 2、通过startService启动一个服务，在服务中以set方式设置定时器，然后通过循环启动服务来保证任务在后台定时执行
 * 3、通过bindService绑定一个服务，在服务中以setRepeating方式设置定时器，服务在后台执行因此不需要持锁Alarm就能正常触发
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTextView1;
    private TextView mTextView2;
    private AlarmService mAlarmService;
    public static final String ACTION_UPDATE_UI = "update_ui";

    private final BroadcastReceiver mUiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showText(mTextView1);
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                showText(mTextView2);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("wanghuan", "MainActivity onCreate");
        setContentView(R.layout.activity_main);
        registerReceiver(mUiReceiver, new IntentFilter(ACTION_UPDATE_UI));
        mTextView1 = (TextView) findViewById(R.id.textView1);
        mTextView2 = (TextView) findViewById(R.id.textView2);
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("wanghuan", "click startActivity");
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("wanghuan", "click startService");
                Intent intent = new Intent(MainActivity.this, AlarmService.class);
                startService(intent);
            }
        });
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("wanghuan", "click stopService");
                Intent intent = new Intent(MainActivity.this, AlarmService.class);
                stopService(intent);
            }
        });
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("wanghuan", "click bindService");
                Intent intent = new Intent(MainActivity.this, AlarmService.class);
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
            }
        });
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("wanghuan", "click unbindService");
                //如果服务尚未绑定，调用unbindService会报错
                if (mAlarmService != null) {
                    unbindService(conn);
                }
            }
        });
    }

    //如果onDestroy中不调用stopService，则MainActivity销毁后，通过StartService设置的Alarm仍将定时触发
    //如果onDestroy中不调用unbindService，没关系，因为通过bindService绑定的服务，当MainActivity销毁后，服务也随之销毁，Service会依次调用onUnbind和onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("wanghuan", "MainActivity onDestroy");
        unregisterReceiver(mUiReceiver);
    }

    private void showText(TextView textView) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        textView.append("alarm at " + sdf.format(new Date()) + "\n");
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("wanghuan", "MainActivity onServiceConnected");
            mAlarmService = ((AlarmService.AlarmBinder) service).getService();
            mAlarmService.setHandler(mHandler);
            mAlarmService.setAlarmManager();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("wanghuan", "MainActivity onServiceDisconnected");
            mAlarmService = null;
        }
    };
}
