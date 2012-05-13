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

import org.uab.deic.uabdroid.R;
import org.uab.deic.uabdroid.SessionDetailsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.LiveFolders;

/**
 * 
 * @author Ruben Serrano
 *
 */

public class UABDroidLiveFolders extends Activity 
{
	public static class SessionsLiveFolder extends Activity
	{
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			
			String action = getIntent().getAction();
			
			if (LiveFolders.ACTION_CREATE_LIVE_FOLDER.equals(action))
			{
				setResult(RESULT_OK, createLiveFolderIntent(this));
			}
			else
			{
				setResult(RESULT_CANCELED);
			}
			finish();
		}

		private static Intent createLiveFolderIntent(Context context) 
		{
			Intent intent = new Intent();
			
			intent.setData(SessionsProvider.LIVE_FOLDER_URI);
			
			intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_BASE_INTENT, 
							new Intent(SessionDetailsActivity.SESSION_DETAILS_INTENT, SessionsProvider.CONTENT_URI));
			
			intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE,
							LiveFolders.DISPLAY_MODE_LIST);
			
			intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON,
							Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));
			
			intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME,
							context.getResources().getString(R.string.live_folder_name));
			return intent;
		}
	}
}
