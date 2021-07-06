package com.communitycode.amps.main.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.communitycode.amps.main.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}
