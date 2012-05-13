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
package org.uab.deic.uabdroid.services;

import org.uab.deic.uabdroid.AppPreferencesActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

/**
 * 
 * @author Ruben Serrano
 *
 */

public class UABDroidAlarmManager 
{
	public static final int SESSIONS_ALARM_TYPE = 0;
	public static final int UPDATES_ALARM_TYPE = 1;
	
	private AlarmManager mAlarmManager;
	private PendingIntent mSessionsAlarmIntent;
	private PendingIntent mUpdatesAlarmIntent;
	private Context mContext;
	
	public UABDroidAlarmManager(Context _context) 
	{
		mContext = _context;
		mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent intentToFire = new Intent(UABDroidReceiver.ALARM_INTENT);
		mSessionsAlarmIntent = PendingIntent.getBroadcast(mContext, 0, intentToFire, 0);
		intentToFire = new Intent(UABDroidReceiver.UPDATE_INTENT);
		mUpdatesAlarmIntent = PendingIntent.getBroadcast(mContext, 0, intentToFire, 0);		
	}
	
	public void cancelAlarm(int _type)
	{
		switch(_type)
		{
			case SESSIONS_ALARM_TYPE:
				mAlarmManager.cancel(mSessionsAlarmIntent);
				break;
			case UPDATES_ALARM_TYPE:
				mAlarmManager.cancel(mUpdatesAlarmIntent);
				break;
		}
	}
	
	public void setAlarm(int _type) 
	{
		String prefsFreq = null;
		String prefsCheck = null;
		PendingIntent alarmIntent = null;
		String defaultTime = null;

		switch(_type)
		{
			case SESSIONS_ALARM_TYPE:
				mAlarmManager.cancel(mSessionsAlarmIntent);
				prefsCheck = AppPreferencesActivity.PREF_NEXT_SESSION_ALERTS_CHECK;
				prefsFreq = AppPreferencesActivity.PREF_NEXT_SESSION_ALERT_FREQ;
				alarmIntent = mSessionsAlarmIntent;
				defaultTime = "24";
				break;
			case UPDATES_ALARM_TYPE:
				mAlarmManager.cancel(mUpdatesAlarmIntent);
				prefsCheck = AppPreferencesActivity.PREF_AUTOMATIC_UPDATES_CHECK;
				prefsFreq = AppPreferencesActivity.PREF_AUTOMATIC_UPDATES_FREQ;
				alarmIntent = mUpdatesAlarmIntent;
				defaultTime = "48";
				break;
		}
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		if (sharedPreferences.getBoolean(prefsCheck, false)) 
		{
			int alertFreq = Integer.valueOf(sharedPreferences.getString(prefsFreq, defaultTime))*60; //hores
			int alarmType = AlarmManager.ELAPSED_REALTIME; // No despierta al dispositivo
			long timeToRefresh = SystemClock.elapsedRealtime() + alertFreq*60*1000;;
			
			mAlarmManager.setInexactRepeating(alarmType, timeToRefresh, AlarmManager.INTERVAL_DAY, alarmIntent);
		}
	}
}
