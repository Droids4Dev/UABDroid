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

import org.uab.deic.uabdroid.utils.XMLParser;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

/**
 * 
 * @author Ruben Serrano
 *
 */

public class UpdateService extends Service 
{
	@Override
	public int onStartCommand(Intent _intent, int _flags, int _startId) 
	{
		new UpdateAsyncTask(_startId).execute();
		
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	private class UpdateAsyncTask extends AsyncTask<Void,Void,Void>
	{
		private int mStartId;
		
		public UpdateAsyncTask(int _startId)
		{
			super();
			mStartId = _startId;
			
		}
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			XMLParser.update(getBaseContext());
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
