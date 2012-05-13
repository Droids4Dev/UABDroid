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

import org.uab.deic.uabdroid.fragments.SessionInformationFragment;
import org.uab.deic.uabdroid.fragments.SessionMaterialsFragment;
import org.uab.deic.uabdroid.fragments.SessionSourcesFragment;
import org.uab.deic.uabdroid.utils.Tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * @author Joan Fuentes
 * @author Rubén Serrano
 *
 */


public class SessionDetailsActivity extends BaseTabbedActivity 
{
	public static final String SESSION_DETAILS_INTENT = "org.uab.deic.uabdroid.SESSION_DETAILS_INTENT";
	
	public static final String SESSION_ID_KEY = "sessionIDKey";

    @Override
    public void onCreate(Bundle _savedInstanceState) 
    {
        super.onCreate(_savedInstanceState);
        
        setActionBarTitleOrDefault(R.string.session_text);
        
        Tabs tabs = new Tabs();

    	tabs.addTab(0, "info",getResources().getString(R.string.tab_information), SessionInformationFragment.class);
    	tabs.addTab(1, "material",getResources().getString(R.string.tab_materials), SessionMaterialsFragment.class);
    	tabs.addTab(2, "sources",getResources().getString(R.string.tab_sources), SessionSourcesFragment.class);
		
		super.onCreateTabs(_savedInstanceState, tabs);
		
		if (_savedInstanceState == null) 
        {		
        	Intent intent = getIntent();
        	Uri uri = intent.getData();
        	String action = intent.getAction();
        	
        	if ((action != null)&&(uri != null))
        	{
        		String id = uri.getLastPathSegment();
        		intent.putExtra(SESSION_ID_KEY, Integer.valueOf(id));
        		TextView txtTitle = (TextView)findViewById(R.id.actionBarTextView);
        		txtTitle.setText(getResources().getString(R.string.session_text) + " " + id);
        	}
        }
    }
}
