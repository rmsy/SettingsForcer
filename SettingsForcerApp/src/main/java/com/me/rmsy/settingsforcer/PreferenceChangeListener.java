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

import android.app.Activity;
import android.content.Intent;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.TwoStatePreference;

/**
 * Currently serves three roles:
 *   * Update summary values when preferences change
 *   * Update Global setting when the setup wizard preference changes
 *   * Start and stop the background service when the background setting changes
 *
 * Could eventually be structured better.
 */
class PreferenceChangeListener implements Preference.OnPreferenceChangeListener {
    private Activity owningActivity;

    private PreferenceChangeListener() {}

    PreferenceChangeListener(Activity owningActivity) {
        super();
        assert owningActivity != null;
        this.owningActivity = owningActivity;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

            if (preference.getKey().equals("setup_wizard_complete") && index >= 0) {
                SettingsActivity.updateGlobal(owningActivity.getContentResolver(), index);
            }
        } else if (preference instanceof TwoStatePreference) {
            if (preference.getKey().equals("autostart_service")) {
                if (Boolean.valueOf(stringValue)) {
                    this.owningActivity.startService(new Intent(this.owningActivity.getApplicationContext(), DetectGlobalChangeService.class));
                } else {
                    this.owningActivity.stopService(new Intent(this.owningActivity.getApplicationContext(), DetectGlobalChangeService.class));
                }
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }
}
