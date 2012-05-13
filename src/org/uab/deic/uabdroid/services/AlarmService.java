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

import java.util.Calendar;

import org.uab.deic.uabdroid.ActionBarCompat;
import org.uab.deic.uabdroid.AppPreferencesActivity;
import org.uab.deic.uabdroid.R;
import org.uab.deic.uabdroid.SessionDetailsActivity;
import org.uab.deic.uabdroid.UABDroidApplication;
import org.uab.deic.uabdroid.adapters.DatabaseAdapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;

import android.support.v4.app.NotificationCompat;

/**
 * 
 * @author Rubén Serrano
 *
 */
public class AlarmService extends Service 
{
	public static final int NOTIFICATION_ID = 1;
	
	private int mStartId;

	@Override
	public int onStartCommand(Intent _intent, int _flags, int _startId) 
	{
		mStartId = _startId;
		new AlarmAsyncTask().execute();
		
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	private class AlarmAsyncTask extends AsyncTask<Void,Void,Void>
	{
		@Override
		protected Void doInBackground(Void... arg0) 
		{
			Context context = getBaseContext();
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			
			if (!sharedPreferences.getBoolean(AppPreferencesActivity.PREF_NEXT_SESSION_ALERTS_CHECK,false))
			{
				UABDroidApplication.getInstance().getAlarmManager().cancelAlarm(UABDroidAlarmManager.SESSIONS_ALARM_TYPE);
				return null;
			}
			
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			databaseAdapter.open();
			
		    Cursor cursor = databaseAdapter.fetchAllSessions();
		    
		    if (cursor.getCount() > 0)
		    {
		    	int nextSessionInterval = Integer.valueOf(sharedPreferences.getString(AppPreferencesActivity.PREF_NEXT_SESSION_ALERT_INTERVAL, "4"));
		    	String date = null;
	
		    	Calendar currentDate = Calendar.getInstance();
		    	Calendar intervalDate = Calendar.getInstance();
		    	Calendar nextSessionDate = Calendar.getInstance();
	
	    		intervalDate.add(Calendar.DAY_OF_YEAR, nextSessionInterval);

		    	do 
		    	{
		    		date = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_SESSIONS_DATE));
		    		
		    		String[] splitDate = date.split("/");
	
		    		nextSessionDate.set(Calendar.YEAR, Integer.valueOf(splitDate[2]));
		    		nextSessionDate.set(Calendar.MONTH, Integer.valueOf(splitDate[1])-1);
		    		nextSessionDate.set(Calendar.DAY_OF_MONTH, Integer.valueOf(splitDate[0]));
		    		
		    		if ((nextSessionDate.compareTo(currentDate) >= 0) && (nextSessionDate.compareTo(intervalDate) <= 0)) 
		    		{
		    			int sessionId = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ROWID));
		    			
		    			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		    			
		    			String expandedText = getResources().getString(R.string.next_session_notification_text);
		    			String expandedTitle = getResources().getString(R.string.next_session_notification_title);
		    			
		    			Intent intent = new Intent(context, SessionDetailsActivity.class);
		    			intent.putExtra(SessionDetailsActivity.SESSION_ID_KEY, sessionId);
		    			intent.putExtra(ActionBarCompat.ACTION_BAR_TITLE_EXTRA_KEY, 
		    							context.getResources().getString(R.string.session_text) + " " + sessionId);
		    			
		    			PendingIntent launchIntent = PendingIntent.getActivity(context, 0, intent, 0);
		    			
		    			String tickerText = getResources().getString(R.string.next_session_notification_warning);
		    			
		    			// Deprecated since Android 3.0. Instead use Notification.Builder for 3.0+,
		    			// or NotificationCompat.Builder for 2.3.7-
		    			/*
		    			Notification newSessionNotification = new Notification(R.drawable.ic_launcher, 
		    																   tickerText, 
		    																   System.currentTimeMillis());
		    			newSessionNotification.setLatestEventInfo(context, expandedTitle, expandedText, launchIntent);
		    			*/
		    			
		    			NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		    			
		    			builder.setAutoCancel(true);
		    			builder.setTicker(tickerText);
		    			builder.setSmallIcon(R.drawable.ic_launcher);
		    			builder.setWhen(System.currentTimeMillis());
		    			builder.setContentTitle(expandedTitle);
		    			builder.setContentText(expandedText);
		    			builder.setContentIntent(launchIntent);
		    			
		    			Notification newSessionNotification = builder.getNotification();
		    			
		    			notificationManager.notify(NOTIFICATION_ID, newSessionNotification);

		    			break;
		    		}
		    	} while (cursor.moveToNext());
		    }
		    cursor.close();
		    databaseAdapter.close();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) 
		{
			stopSelf(mStartId);
			super.onPostExecute(result);
		}
	}
}
