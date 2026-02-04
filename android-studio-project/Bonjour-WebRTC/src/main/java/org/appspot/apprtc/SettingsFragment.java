/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc;

import com.github.warren_bank.bonjour_webrtc.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Settings fragment for AppRTC.
 */
public class SettingsFragment extends PreferenceFragment {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.org_appspot_apprtc_preferences);
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
