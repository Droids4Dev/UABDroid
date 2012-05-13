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

import java.io.FileNotFoundException;

import org.uab.deic.uabdroid.fragments.FAQFragment;
import org.uab.deic.uabdroid.fragments.GeneralInfoFragment;
import org.uab.deic.uabdroid.utils.GeneralInfo;
import org.uab.deic.uabdroid.utils.Tabs;
import org.uab.deic.uabdroid.utils.XMLParser;

import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author Joan Fuentes
 * @author Rubén Serrano
 *
 */

public class GeneralInfoActivity extends BaseTabbedActivity 
{	
	private final GeneralInfo mGeneralInfo = new GeneralInfo();
	
    @Override
    public void onCreate(Bundle _savedInstanceState) 
    {
    	super.onCreate(_savedInstanceState);
    	setActionBarTitleOrDefault(R.string.app_text_information);
    	
    	String fileToOpen = XMLParser.INFO_FILE;
    	
    	try
    	{
    		XMLParser.parseInfoXML(openFileInput(fileToOpen),mGeneralInfo);
    		
    		Tabs tabs = new Tabs();

        	tabs.addTab(0, "info",getResources().getString(R.string.tab_info), GeneralInfoFragment.class);
        	tabs.addTab(1, "faqs",getResources().getString(R.string.tab_faq), FAQFragment.class);
    		
    		super.onCreateTabs(_savedInstanceState, tabs);
    	}
    	catch(FileNotFoundException fileException)
    	{
    		Log.v("Exception", "URL opening error: " + fileToOpen);
    	}
    }

	public GeneralInfo getGeneralinfo() 
	{
		return mGeneralInfo;
	}
}