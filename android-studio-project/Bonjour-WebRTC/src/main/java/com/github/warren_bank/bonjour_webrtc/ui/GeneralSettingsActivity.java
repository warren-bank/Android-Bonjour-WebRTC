package com.github.warren_bank.bonjour_webrtc.ui;

import com.github.warren_bank.bonjour_webrtc.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GeneralSettingsActivity extends Activity {
    public static final class GeneralSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.general_preferences);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);

            // fix for Android 15+ edge-to-edge layout enforcement
            if (view != null)
                view.setFitsSystemWindows(true);

            return view;
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
