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

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import static com.me.rmsy.settingsforcer.SettingsActivity.GLOBAL_SETUP_WIZARD_PREFERENCE;

public class DetectGlobalChangeService extends Service {
    private SharedPreferences setupWizardPref;
    private SettingsContentObserver contentObserver;

    public DetectGlobalChangeService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        this.setupWizardPref = PreferenceManager.getDefaultSharedPreferences(this);

        processChange(); // process initial value when service starts
        this.contentObserver = new SettingsContentObserver(new Handler());
        this.getContentResolver().registerContentObserver(Settings.Global.getUriFor("vr_setupwizard_completed"), true, this.contentObserver);
    }

    @Override
    public void onDestroy() {
        this.getContentResolver().unregisterContentObserver(this.contentObserver);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; } // not supported

    private void processChange() {
        String newValue = Settings.Global.getString(getContentResolver(), GLOBAL_SETUP_WIZARD_PREFERENCE);
        String preferredValue = setupWizardPref.getString("setup_wizard_complete", null);

        if (preferredValue != null && !newValue.equals(preferredValue)) {
            SettingsActivity.updateGlobal(getContentResolver(), Integer.valueOf(preferredValue));
            Log.i(
                    "DetectGlobalChangeSvc", "VR setup wizard value changed! newValue=" + newValue
                            + " preferredValue=" + preferredValue
                            + " (resetting to " + preferredValue + ")");
        }
    }

    private class SettingsContentObserver extends ContentObserver {
        private SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            DetectGlobalChangeService.this.processChange();
        }
    }
}
