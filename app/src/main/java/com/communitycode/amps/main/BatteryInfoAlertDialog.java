package com.communitycode.amps.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.communitycode.amps.main.settings.SettingsActivity;

public class BatteryInfoAlertDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.battery_info_alert_message)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(R.string.go_to_battery_info_settings, (dialog, which) -> {
                    Intent intent = new Intent(getContext(), SettingsActivity.class);
                    startActivity(intent);
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
