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

import org.uab.deic.uabdroid.services.UpdateService;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class represents an Action Bar.
 * 
 * As this version of UABDroid targets Android 2.2, if we want to use the Action Bar Pattern we have to 
 * create our own widget, or use one from another library. In this example, our Action Bar layout includes 
 * a Home Button and a Refresh Button.
 * 
 * @author Joan Fuentes
 * 
 */
public class ActionBarCompat extends LinearLayout 
{	
	/**
	 * Used for adding a title for the action bar in an intent for the activity
	 * that is called
	 */
	public final static String ACTION_BAR_TITLE_EXTRA_KEY = "actionBarTitle";
	
	/**
	 * Action bar constructor
	 * 
	 * @param _context the context where the Action Bar is included
	 * @param _attributeSet the attributes set of the widget
	 */
	public ActionBarCompat(final Context _context, AttributeSet _attributeSet)
	{
		super(_context, _attributeSet);
		
		// Inflate the view from the layout resource.
		LayoutInflater layoutInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.action_bar, this, true);
		
		// Obtain the reference of the buttons for using them later
		ImageButton homeButton = (ImageButton) findViewById(R.id.homeButton);
		ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton);
		
		// Obtain the Action Bar's attributes in order to know if the Home Button is enabled
		// or disabled
		TypedArray typedArray = _context.obtainStyledAttributes(_attributeSet,R.styleable.ActionBarCompat);
		boolean showHomeButton = typedArray.getBoolean(R.styleable.ActionBarCompat_showHomeButton, false);
		
		if (!showHomeButton)
		{	
			// If the Home button is disabled, we have to hide its icon, the separator, and add in
			// its place the application name
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutActionBar);
			TextView actionBarTextView = (TextView) findViewById(R.id.actionBarTextView);
			ImageView separatorRefreshImageView1 = (ImageView) findViewById(R.id.separatorRefreshButton1);
		
			linearLayout.removeView(homeButton);
			linearLayout.removeView(separatorRefreshImageView1);
			actionBarTextView.setText(R.string.app_title);	
		}
		else
		{
			// If the Home button is enabled (default pattern) we add a click listener to it
			// for returning to the main activity in case it is pressed. Also, we must set the
			// intent flag FLAG_ACTIVITY_CLEAR_TOP to destroy any other activity instance that is
			// in the pile so the back button does not return to the activity where the Home button 
			// was pressed
			homeButton.setOnClickListener(new OnClickListener() 
			{
				@Override
	            public void onClick(View view) 
	            {
	                final Intent intent = new Intent(getContext(), MainActivity.class);
	                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                getContext().startActivity(intent);                
	            }
	        });
		}
		
		// Free the attributes for a posterior use
		typedArray.recycle();

		// As the Refresh button is always in the action bar, we add the click listener to 
		// start the update service
		refreshButton.setOnClickListener(new OnClickListener() 
		{
			@Override
            public void onClick(View view) 
			{
				_context.startService(new Intent(_context, UpdateService.class));                
            }
        });   	
	}
	
}