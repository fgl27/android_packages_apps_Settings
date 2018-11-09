/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.settings.display;

import android.content.Context;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class TapToWakePreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private static final String KEY_TAP_TO_WAKE = "tap_to_wake";

    public TapToWakePreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return KEY_TAP_TO_WAKE;
    }

    @Override
    public boolean isAvailable() {
        return mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_supportDoubleTapWake);
    }

    @Override
    public void updateState(Preference preference) {
        ((SwitchPreference) preference).setChecked(readState());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Settings.Secure.putInt(
                mContext.getContentResolver(), Settings.Secure.DOUBLE_TAP_TO_WAKE, (Boolean) newValue ? 1 : 0);
        return true;
    }

    private static boolean readState() {
        BufferedReader br;
        String line = "0";
        try {
            br = new BufferedReader(new FileReader("/sys/android_touch/doubletap2wake"), 512);
            try {
                line = br.readLine();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            return false;
        }
        return line.contains("1");
    }
}
