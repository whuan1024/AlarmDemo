package com.example.tommy.alarmdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("wanghuan", "start service again");
        Intent i = new Intent(context, AlarmService.class);
        context.startService(i);
    }
}
