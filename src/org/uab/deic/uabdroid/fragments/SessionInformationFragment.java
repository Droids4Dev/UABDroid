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
package org.uab.deic.uabdroid.fragments;

import java.util.Locale;

import org.uab.deic.uabdroid.R;
import org.uab.deic.uabdroid.SessionDetailsActivity;
import org.uab.deic.uabdroid.adapters.DatabaseAdapter;
import org.uab.deic.uabdroid.loaders.SessionCursorLoader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * 
 * @author Joan Fuentes
 * @author Ruben Serrano
 *
 */
public class SessionInformationFragment extends Fragment 
{   
    private static final String CONTENT_COLUMN_LANGUAGE;
    static
    {
    	if(Locale.getDefault().toString().compareTo("ca_ES")==0)
        {
    		CONTENT_COLUMN_LANGUAGE = DatabaseAdapter.KEY_SESSIONS_CONTENT_CA;
        }
    	else
    	{
    		CONTENT_COLUMN_LANGUAGE = DatabaseAdapter.KEY_SESSIONS_CONTENT_ES;
    	}
    }
    
    private FragmentActivity mParentActivity;
    private TextView mTextViewContent;
    private int mSessionID;
    
	@Override 
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) 
	{
		View fragmentViewHierarchy = _inflater.inflate(R.layout.fragment_info, _container, false);
		mTextViewContent = (TextView) fragmentViewHierarchy.findViewById(R.id.generalinfo_text);
		return fragmentViewHierarchy;
	}
	
	@Override
	public void onCreate(Bundle _savedInstanceState)
	{
		super.onCreate(_savedInstanceState);
		
		mParentActivity = getActivity();
	       
        mSessionID = mParentActivity.getIntent().getExtras().getInt(SessionDetailsActivity.SESSION_ID_KEY);
        
        if(mSessionID==0)
        {
        	mSessionID= getArguments().getInt(SessionDetailsActivity.SESSION_ID_KEY,0);	
        } 
        
        if (mSessionID!=0)
	    {       	
        	getLoaderManager().initLoader(0, null, new SessionCursorLoaderCallback());
	    }
	}
	
	@Override
	public void onDestroy()
	{
		getLoaderManager().destroyLoader(0);
		super.onDestroy();
	}
	
	private class SessionCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
	{
		// In the constructor, obtain a new Loader
		@Override
		public Loader<Cursor> onCreateLoader(int _id, Bundle _args) 
		{
			return new SessionCursorLoader(mParentActivity, mSessionID);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> _loader, Cursor _cursor) 
		{
			if ((_cursor != null) && (!_cursor.isClosed()))
			{
				_cursor.moveToFirst();
				int contentColumnIndex = _cursor.getColumnIndex(CONTENT_COLUMN_LANGUAGE);
				try
				{
					String content = _cursor.getString(contentColumnIndex);
					mTextViewContent.setText(Html.fromHtml(content), BufferType.SPANNABLE);
				}
				catch(Exception e)
				{
					Log.wtf("Sometimes...", "shit happens");
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> _loader) 
		{
			
		}
	}
}

