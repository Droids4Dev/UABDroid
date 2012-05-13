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

import android.app.Application;

/**
 * 
 * @author Rubén Serrano
 *
 */

public class UABDroidApplication extends Application 
{
	// Fields
	private static UABDroidApplication mSingleton;
	private UABDroidAlarmManager mAlarmManager;
	
	@Override
	public final void onCreate() 
	{
		super.onCreate();
		mAlarmManager = new UABDroidAlarmManager(this);
		mSingleton = this;
	}
	
	// Returns the application instance
	public static UABDroidApplication getInstance() 
	{
		return mSingleton;
	}
	
	public UABDroidAlarmManager getAlarmManager() 
	{
		return mAlarmManager;
	}
}
