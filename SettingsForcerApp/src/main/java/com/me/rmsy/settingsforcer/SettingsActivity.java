/*
 * Copyright 2017 Isaac Moore
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.me.rmsy.settingsforcer;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import com.me.rmsy.settingsforcer.lib.AppCompatPreferenceActivity;

public class SettingsActivity extends AppCompatPreferenceActivity {
    public static final String GLOBAL_SETUP_WIZARD_PREFERENCE = "vr_setupwizard_completed";

    private PreferenceChangeListener prefListener = new PreferenceChangeListener(this);

    private void registerListeners(Preference preference) {
        preference.setOnPreferenceChangeListener(prefListener);
    }

    private String loadInitialValue(ListPreference preference) {
        String value = Settings.Global.getString(getContentResolver(), GLOBAL_SETUP_WIZARD_PREFERENCE);
        preference.getEditor().putString("setup_wizard_complete", value).apply();
        preference.setValue(value);
        return value;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference vrPref = findPreference("setup_wizard_complete");
        Preference svcPref = findPreference("autostart_service");
        registerListeners(vrPref);
        registerListeners(svcPref);

        SharedPreferences prefs = vrPref.getSharedPreferences();

        String setupWizard = prefs.getString("setup_wizard_complete", null);
        if (setupWizard == null) {
            setupWizard = loadInitialValue((ListPreference) vrPref);
        }

        prefListener.onPreferenceChange(vrPref, setupWizard);
        prefListener.onPreferenceChange(svcPref, prefs.getBoolean("autostart_service", false));
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * Update the {@link android.provider.Settings.Global} setup wizard preference
     * value to the supplied value.
     *
     * @param content The {@link ContentResolver} to use for lookup
     * @param newValue The new value to update
     */
    static void updateGlobal(ContentResolver content, int newValue) {
        if (newValue < 0 || newValue > 2) {
            throw new IllegalArgumentException("updateGlobal: setup_wizard_complete not within range 0..2! value=" + newValue);
        }

        try {
            int currValue = Settings.Global.getInt(content, SettingsActivity.GLOBAL_SETUP_WIZARD_PREFERENCE);

            if (currValue == newValue) return;
        } catch (Settings.SettingNotFoundException ignored) {}

        Settings.Global.putInt(content, SettingsActivity.GLOBAL_SETUP_WIZARD_PREFERENCE, newValue);
    }
}
