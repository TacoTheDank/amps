package com.communitycode.amps.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.communitycode.amps.main.battery.BatteryMethodInterface;
import com.communitycode.amps.main.battery.OfficialBatteryMethod;
import com.communitycode.amps.main.battery.UnofficialBatteryApi;
import com.communitycode.amps.main.settings.BatteryMethodPickler;

import java.util.ArrayList;

public class CurrentTracker {
    protected static final int MAX_HISTORY = 1000;
    private static final int UPDATE_DELAY = 500;
    final private Handler handler = new Handler();
    final private Context mCtx;
    final private BatteryInfoInterface mBatteryInfoInterface;
    // current in milliamps
    protected ArrayList<Integer> currentHistory = new ArrayList<>();
    private Runnable sendData;

    public CurrentTracker(Context ctx, BatteryInfoInterface batteryInfoInterface) {
        mCtx = ctx;
        mBatteryInfoInterface = batteryInfoInterface;
        sendData = new Runnable() {
            public void run() {
                try {
                    updateAmps();

                    handler.postDelayed(this, UPDATE_DELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void start() {
        handler.post(sendData);
    }

    public void stop() {
        handler.removeCallbacks(sendData);
    }

    private void updateAmps() {
        Integer current = null;
        boolean showAmpInfo = true;

        // Get current by preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mCtx);
        String json = sharedPref.getString("unofficial_measurement", null);
        BatteryMethodInterface method = BatteryMethodPickler.fromJson(json, mCtx);
        if (method != null) {
            current = method.read();
        } else {
            // Get current by best guess
            current = new OfficialBatteryMethod(mCtx).read();

            if (current == null || current == 0) {
                current = UnofficialBatteryApi.getCurrent();
            } else {
                // Official method worked and preference is not set. No need to clutter the UI.
                showAmpInfo = false;
            }
        }

        mBatteryInfoInterface.showAmpInfoButton(showAmpInfo);

        if (current != null) {
            addHistory(current);
            updateAmpStatistics();
        }
    }

    protected void addHistory(int value) {
        currentHistory.add(value);

        while (currentHistory.size() > MAX_HISTORY) {
            currentHistory.remove(0);
        }
    }

    public void resetHistory() {
        currentHistory.clear();
        updateAmpStatistics();
    }

    protected void updateAmpStatistics() {
        if (currentHistory.size() > 0) {
            int max = currentHistory.get(0);
            int min = currentHistory.get(0);
            for (int i = 0; i < currentHistory.size(); i++) {
                int val = currentHistory.get(i);
                max = val > max ? val : max;
                min = val < min ? val : min;
            }
            int last = currentHistory.get(currentHistory.size() - 1);
            mBatteryInfoInterface.setMaxAmps(max);
            mBatteryInfoInterface.setMinAmps(min);
            mBatteryInfoInterface.setCurrentAmps(last);
        } else {
            mBatteryInfoInterface.setMaxAmps(null);
            mBatteryInfoInterface.setMinAmps(null);
            mBatteryInfoInterface.setCurrentAmps(null);
        }
    }
}
