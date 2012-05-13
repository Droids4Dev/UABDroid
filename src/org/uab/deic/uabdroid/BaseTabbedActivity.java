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

import org.uab.deic.uabdroid.utils.Tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.TabManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * As all of the tabbed activities in this application share some functionality,
 * this activity is used as base for them, and extends the BaseActivity in order to share also
 * the same menu options and about dialog
 * 
 * @author Rubén Serrano
 */
public class BaseTabbedActivity extends BaseActivity 
{	
	private TabHost mTabHost;
    private TabManager mTabManager;
	
    @Override
    protected void onCreate(Bundle _savedInstanceState)
    {
		super.onCreate(_savedInstanceState);
    	setContentView(R.layout.tabbed_activity);
        
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		//mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle _outState) 
    {
    	super.onSaveInstanceState(_outState);
    	_outState.putString("tab", mTabHost.getCurrentTabTag());
    }
    
    public void onCreateTabs(Bundle _savedInstanceState, final Tabs _tabs)
    {   
    	int size = _tabs.getSize();
    	int i = 0;
    	
    	for (i=0; i < size; i++)
    	{
    		TabSpec tabSpec = mTabHost.newTabSpec(_tabs.getTabName(i)).setIndicator(createTabView(mTabHost.getContext(), _tabs.getTabText(i)));
    		//TabSpec tabSpec = mTabHost.newTabSpec(_tabs.getTabName(i)).setIndicator(_tabs.getTabText(i));
    		mTabManager.addTab(tabSpec, _tabs.getFragmentClass(i), null);
    	}
    	
    	if (_savedInstanceState != null) 
		{
			mTabHost.setCurrentTabByTag(_savedInstanceState.getString("tab"));
		}
    }
    
    private static View createTabView(final Context _context, final String _text) 
    {
        View view = LayoutInflater.from(_context).inflate(R.layout.tabs_bg, null);
        TextView textView = (TextView) view.findViewById(R.id.tabsText);
        textView.setText(_text);
        return view;
    }
}