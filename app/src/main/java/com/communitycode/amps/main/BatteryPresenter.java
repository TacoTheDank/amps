package com.communitycode.amps.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryPresenter {
    private final BatteryInfoInterface mBatteryInfoInterface;
    private final Context mCtx;
    private final CurrentTracker mCurrentTracker;

    private final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // Avoid crashing the app
                updateBatteryData(intent);
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        }
    };

    public BatteryPresenter(Context ctx, BatteryInfoInterface batteryInfoInterface) {
        mBatteryInfoInterface = batteryInfoInterface;
        mCtx = ctx;
        mCurrentTracker = new CurrentTracker(ctx, batteryInfoInterface);
    }

    private void updateBatteryData(Intent intent) {
        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        mBatteryInfoInterface.setBatteryHealth(health);

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        boolean isError = level == -1 || scale == -1 || scale == 0;
        mBatteryInfoInterface.setBatteryPercent(isError ? null : level / (double) scale);

        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        mBatteryInfoInterface.setPluggedInStatus(plugged);

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        mBatteryInfoInterface.setChargingStatus(status);

        String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        isError = technology == null || technology.isEmpty();
        mBatteryInfoInterface.setBatteryTechnology(isError ? null : technology);

        double temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f;
        mBatteryInfoInterface.setTemperature(temperature > 0 ? temperature : null);

        double voltage = (double) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000;
        mBatteryInfoInterface.setVoltage(voltage > 0 ? voltage : null);
    }

    public void resetCurrentHistory() {
        mCurrentTracker.resetHistory();
    }

    public void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mCtx.registerReceiver(batteryInfoReceiver, intentFilter);

        mCurrentTracker.start();
    }

    public void stop() {
        mCtx.unregisterReceiver(batteryInfoReceiver);

        mCurrentTracker.stop();
    }
}
