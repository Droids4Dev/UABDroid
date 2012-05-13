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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * As most of the activities in this application share the same options menu and other features,
 * this activity is used as base for them, defining the options menu and About dialog fragment
 * 
 * @author Rubén Serrano
 *
 */

public class BaseActivity extends FragmentActivity 
{
	/**
	 * Method for setting the title of the activity. It searches for one in the intent's extras, using
	 * the key defined at ActionBarCompat.ACTION_BAR_TITLE_EXTRA_KEY. In case none is found, it uses a 
	 * default string ID provided by the integer parameter
	 * 
	 * @param _defaultTitleID The string ID for the default title in case there was none defined in the intent
	 */
	protected void setActionBarTitleOrDefault(int _defaultTitleID)
	{
		TextView textViewTitle = (TextView)findViewById(R.id.actionBarTextView);
		
    	String title = getIntent().getStringExtra(ActionBarCompat.ACTION_BAR_TITLE_EXTRA_KEY);
    	
		if (title == null)
		{
			title = getResources().getString(_defaultTitleID);
		}
		
		textViewTitle.setText(title);
	}
	
	// Uncomment this line for using the deprecated dialog system
	//private static final int DIALOG_ABOUT_ID = 0;
	   
	// Event for inflating the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu _menu) 
    {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, _menu);
        return true; 	
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem _item) 
    {
    	switch (_item.getItemId()) 
    	{
        	case R.id.PREFERENCES_ID:
        		Intent intent = new Intent(this, AppPreferencesActivity.class);
                startActivity(intent);
        		return true;
        	case R.id.ABOUT_ID:
        		// Uncomment for the old good times
        		//showDialog(DIALOG_ABOUT_ID);
        		showDialog();
        		return true;   
    	}
        return super.onOptionsItemSelected(_item);
    }    
   
    // Uncomment for using the old dialog system
    // === Begin old dialog system ===
    /*
    @Override
	protected Dialog onCreateDialog(int id) 
    {
        Dialog dialog = new Dialog(this);
        
        switch(id) 
        {
        	case DIALOG_ABOUT_ID:
	            // do the work to define the pause Dialog. The configuration is the same as in the
	            // fragment version, so you can refer to that code for commentaries
	            dialog.setContentView(R.layout.about_dialog);
	            dialog.setTitle(getResources().getString(R.string.about_dialog_about) + " " + getResources().getString(R.string.app_name));
	            dialog.setCancelable(true);
	            ImageView image = (ImageView) dialog.findViewById(R.id.image_layout);
	            image.setImageResource(R.drawable.logo);    
	            image.setAdjustViewBounds(true);
	            break;
        	default:
        		dialog = null;
        }
        return dialog;
    }
    */
    // === End old dialog system ===
    
    /**
     *  Method for creating, adding and showing the dialog fragment
     */
    // Comment for using the old dialog system
    // === New dialog system ===
    private void showDialog()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment previousDialog = getSupportFragmentManager().findFragmentByTag("dialog");
        
        if (previousDialog != null) 
        {
        	fragmentTransaction.remove(previousDialog);
        }
        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment dialogFragment = AboutDialogFragment.getInstance();
        dialogFragment.show(fragmentTransaction, "dialog");
    }
    // === End new dialog system
    
    /**
     * Fragment used to show the About dialog
     * 
     * @author Rubén Serrano
     *
     */
    private static class AboutDialogFragment extends DialogFragment 
    {
    	private AboutDialogFragment(){}
    	
    	/**
    	 * Factory method for creating the AboutDialogFragment (not really needed in this case)
    	 * 
    	 * @return new instance of AboutDialogFragment
    	 */
        public static AboutDialogFragment getInstance() 
        {
            return new AboutDialogFragment();
        }
        
		@Override
		public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) 
		{
			// First we need to inflate the view hierarchy from a XML file, as in every other fragment
			View fragmentViewHierarchy = _inflater.inflate(R.layout.about_dialog, _container, false);
			// Add the Droids4Dev logo, adjusting its size to the horizontal bounds
			ImageView droids4DevLogo = (ImageView) fragmentViewHierarchy.findViewById(R.id.image_layout);
			droids4DevLogo.setImageResource(R.drawable.logo);
			droids4DevLogo.setAdjustViewBounds(true);
			// The dialog's title should be set in the Dialog object obtained from calling getDialog,
			// as the dialog layout does not include the title
            getDialog().setTitle(getResources().getString(R.string.about_dialog_about) + " "+ getResources().getString(R.string.app_name));
            // The user should be able to go back from the dialog to the previous activity state, so
            // we set the cancelable property to true
			setCancelable(true);
			return fragmentViewHierarchy;
		}
    }
}
