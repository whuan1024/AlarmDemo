package com.example.tommy.alarmdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity {

    private TextView mTextView;
    private AlarmManager mAlarmManager;
    private PowerManager.WakeLock mWakeLock;
    private static final String ACTION_ALARM_TEST = "alarm_test_in_activity";

    private final BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("wanghuan", "alarm in activity");
            showText();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mTextView = (TextView) findViewById(R.id.textView);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //要持锁保持不熄屏，activity中的alarm才能正常工作
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Alarm_WakeLock");
        mWakeLock.acquire();
        registerReceiver(mAlarmReceiver, new IntentFilter(ACTION_ALARM_TEST));
        setAlarmManager();
    }

    private void setAlarmManager() {
        Intent intent = new Intent(ACTION_ALARM_TEST);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, pi);
    }

    private void showText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mTextView.append("triggered alarm at " + sdf.format(new Date()) + "\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAlarmReceiver);
        mWakeLock.release();
    }
}
