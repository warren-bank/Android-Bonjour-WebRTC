package com.github.warren_bank.bonjour_webrtc.ui;

import com.github.warren_bank.bonjour_webrtc.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class GeneralSettingsActivity extends Activity {
    public static final class GeneralSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.general_preferences);
        }
    }

    private GeneralSettingsFragment generalSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generalSettingsFragment = new GeneralSettingsFragment();
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, generalSettingsFragment)
            .commit();
    }
}
