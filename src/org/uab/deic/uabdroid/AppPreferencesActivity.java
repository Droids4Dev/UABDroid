/*
   Copyright 2012 Ruben Serrano, Joan Fuentes

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.uab.deic.uabdroid;

import org.uab.deic.uabdroid.services.UABDroidAlarmManager;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

/**
 * Activity used to choose the application settings
 * 
 * @author Rubén Serrano
 *
 */
public class AppPreferencesActivity extends PreferenceActivity 
{
	//  Keys for other shared preferences files 
	public static final String INIT_PREFS = "INIT_PREFS";
	
	// Keys for the session alerts preferences
	public static final String PREF_NEXT_SESSION_ALERTS_CHECK = "PREF_NEXT_SESSION_ALERTS_CHECK";
	public static final String PREF_NEXT_SESSION_ALERT_FREQ = "PREF_NEXT_SESSION_ALERT_FREQ";
	public static final String PREF_NEXT_SESSION_ALERT_INTERVAL = "PREF_NEXT_SESSION_ALERT_INTERVAL";
	
	// Keys for the automatic updates preferences
	public static final String PREF_AUTOMATIC_UPDATES_CHECK = "PREF_AUTOMATIC_UPDATES_CHECK";
	public static final String PREF_AUTOMATIC_UPDATES_FREQ = "PREF_AUTOMATIC_UPDATES_FREQ";
	
	// CheckBoxPreferences stored as fields because they are used both in the onCreate and 
	// onStop methods
	private CheckBoxPreference mCheckAlertsAlarms;
	private CheckBoxPreference mCheckUpdatesAlarms;
	
	@Override
	public void onCreate(Bundle _savedInstanceState) 
	{
		super.onCreate(_savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
		
		// Get the PreferenceManager reference for this PreferenceActivity
		PreferenceManager preferenceManager = getPreferenceManager();
			
		// Obtain the reference for the alerts frequency and interval for using later
	    final Preference alertsFrequency = preferenceManager.findPreference(PREF_NEXT_SESSION_ALERT_FREQ);
		final Preference alertsInterval = preferenceManager.findPreference(PREF_NEXT_SESSION_ALERT_INTERVAL);

		// Set a preference change listener in the Alerts checkbox so depending on the value that
		// the user has selected, enable or disable the frequency and interval preferences, as they 
		// are only helpful when the alerts are enabled
		mCheckAlertsAlarms = (CheckBoxPreference) preferenceManager.findPreference(PREF_NEXT_SESSION_ALERTS_CHECK);
		mCheckAlertsAlarms.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference _preference, Object _object) 
			{
				alertsFrequency.setEnabled(!((CheckBoxPreference) _preference).isChecked());
				alertsInterval.setEnabled(!((CheckBoxPreference) _preference).isChecked());
				return true;
			}
		});
		
		// For the first time, we recover the current status (enabled or disabled) for the 
		// frequency and interval preferences
		alertsFrequency.setEnabled(mCheckAlertsAlarms.isChecked());
		alertsInterval.setEnabled(mCheckAlertsAlarms.isChecked());
		
		// Repeat the same as in the case of the Alerts preferences for the Updates preferences
		final Preference updatesFrequency = preferenceManager.findPreference(PREF_AUTOMATIC_UPDATES_FREQ);		
		
		mCheckUpdatesAlarms = (CheckBoxPreference) preferenceManager.findPreference(PREF_AUTOMATIC_UPDATES_CHECK);
		mCheckUpdatesAlarms.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference _preference, Object _object) 
			{
				updatesFrequency.setEnabled(!((CheckBoxPreference) _preference).isChecked());
				return true;
			}
		});
		
		updatesFrequency.setEnabled(mCheckUpdatesAlarms.isChecked());
		
		// Needed to avoid the "flicker" effect
		getListView().setCacheColorHint(0);
	}

	@Override
	public void onStop() 
	{
		// When the activity is stopped, check what services are enabled or disabled and then set or cancel the
		// associated service, using the Application singleton
		if (mCheckAlertsAlarms.isChecked())
		{
			UABDroidApplication.getInstance().getAlarmManager().setAlarm(UABDroidAlarmManager.SESSIONS_ALARM_TYPE);
		}
		else
		{
			UABDroidApplication.getInstance().getAlarmManager().cancelAlarm(UABDroidAlarmManager.SESSIONS_ALARM_TYPE);
		}
		
		if (mCheckUpdatesAlarms.isChecked())
		{
			UABDroidApplication.getInstance().getAlarmManager().setAlarm(UABDroidAlarmManager.UPDATES_ALARM_TYPE);
		}
		else
		{
			UABDroidApplication.getInstance().getAlarmManager().cancelAlarm(UABDroidAlarmManager.UPDATES_ALARM_TYPE);
		}
		
		super.onStop();
	}
}
