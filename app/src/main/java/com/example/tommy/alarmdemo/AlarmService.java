package com.example.tommy.alarmdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class AlarmService extends Service {

    private AlarmManager mAm;
    private PendingIntent mPi;
    private Handler mHandler;
    private final int MODE_SEND_BROADCAST = 1;
    private final int MODE_SEND_MESSAGE = 2;
    private static final String ACTION_ALARM_TEST = "alarm_test_in_service";

    private final BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("wanghuan", "alarm in service");
            updateUI(MODE_SEND_MESSAGE);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("wanghuan", "Service onCreate");
        mAm = (AlarmManager) getSystemService(ALARM_SERVICE);
        // For StartService
        Intent i = new Intent(this, AlarmReceiver.class);
        mPi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        // For BindService
        registerReceiver(mAlarmReceiver, new IntentFilter(ACTION_ALARM_TEST));
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("wanghuan", "Service onBind");
        return new AlarmBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("wanghuan", "Service onStartCommand");
        //要把框架里唤醒alarm转为非唤醒alarm的逻辑注释掉，这里才能保证准时触发
        //否则转为非唤醒alarm的话会出现延迟的现象
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60 * 1000, mPi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAm.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60 * 1000, mPi);
        } else {
            mAm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60 * 1000, mPi);
        }
        updateUI(MODE_SEND_BROADCAST);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("wanghuan", "Service onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("wanghuan", "Service onDestroy");
        mAm.cancel(mPi);
        unregisterReceiver(mAlarmReceiver);
    }

    class AlarmBinder extends Binder {
        AlarmService getService() {
            return AlarmService.this;
        }
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void setAlarmManager() {
        Intent intent = new Intent(ACTION_ALARM_TEST);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, pi);
    }

    private void updateUI(int mode) {
        if (mode == MODE_SEND_BROADCAST) {
            Intent intent = new Intent(MainActivity.ACTION_UPDATE_UI);
            sendBroadcast(intent);
        } else if (mode == MODE_SEND_MESSAGE) {
            Message msg = mHandler.obtainMessage();
            msg.what = 1;
            msg.sendToTarget();
        }
    }
}
