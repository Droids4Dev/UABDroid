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

import android.os.Bundle;

/**
 * 
 * @author Joan Fuentes
 * @author Rubén Serrano
 *
 */
public class SessionListActivity extends BaseActivity 
{
	@Override
	public void onCreate(Bundle _savedInstanceState) 
	{
		super.onCreate(_savedInstanceState);
		
		// We only need to set the layout ...
		setContentView(R.layout.sessions_and_details);
		// (the trick is that the layout has a SessionListFragment)

		// ... and the activity's action bar title
		setActionBarTitleOrDefault(R.string.app_text_sessions);
	}
}

