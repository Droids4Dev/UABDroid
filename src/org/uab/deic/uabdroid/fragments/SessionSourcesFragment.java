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

import org.uab.deic.uabdroid.R;
import org.uab.deic.uabdroid.SessionDetailsActivity;
import org.uab.deic.uabdroid.adapters.DatabaseAdapter;
import org.uab.deic.uabdroid.adapters.SourcesAdapter;
import org.uab.deic.uabdroid.loaders.SourcesCursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author Joan Fuentes
 * @author Ruben Serrano
 *
 */
public class SessionSourcesFragment extends ListFragment 
{
    private FragmentActivity mParentActivity;
    private int mSessionID;
    private SourcesAdapter mSourcesAdapter;
	
    @Override
    public void onCreate(Bundle _savedInstanceState)
    {
    	super.onCreate(_savedInstanceState);
    	
    	mParentActivity = getActivity();

    	mSessionID = mParentActivity.getIntent().getExtras().getInt(SessionDetailsActivity.SESSION_ID_KEY);
    	
        if(mSessionID == 0)
        {
        	mSessionID = getArguments().getInt(SessionDetailsActivity.SESSION_ID_KEY,0);	
        }       
        
        if (mSessionID != 0)
	    {        	
            String[] columns = new String[] { DatabaseAdapter.KEY_ROWID };
            int[] to = new int[] { R.id.txtName };
            
            mSourcesAdapter = new SourcesAdapter(mParentActivity, R.layout.element_sources_list, null, columns, to);
            
            getLoaderManager().initLoader(0, null, new SourcesCursorLoaderCallback());
	    }
    }
    
	@Override
	public void onActivityCreated(Bundle _savedInstanceState) 
	{
		super.onActivityCreated(_savedInstanceState);
        
        // Trick to avoid the "flicker" effect.
        getListView().setCacheColorHint(0);
	}

    @Override
	public void onListItemClick(ListView _parentListView, View _itemView, int _position, long _id) 
    {
		super.onListItemClick(_parentListView, _itemView, _position, _id);

		TextView textViewtUrl = (TextView) _itemView.findViewById(R.id.txtUrl);
		String url =  (String) textViewtUrl.getText();

		Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
		mParentActivity.startActivity(intent);
	}
    
    @Override
    public void onResume()
    {
    	mSourcesAdapter.swapCursor(null);
    	setListAdapter(mSourcesAdapter);
    	super.onResume();
    }
    
    @Override
	public void onDestroy()
	{
		getLoaderManager().destroyLoader(0);
		super.onDestroy();
	}
    
	private class SourcesCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
	{
		// In the constructor, obtain a new Loader
		@Override
		public Loader<Cursor> onCreateLoader(int _id, Bundle _args) 
		{
			return new SourcesCursorLoader(mParentActivity, mSessionID);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> _loader, Cursor _cursor) 
		{
			mSourcesAdapter.swapCursor(_cursor);		
		}

		// If the Loader is reseted, we empty the adapter
		@Override
		public void onLoaderReset(Loader<Cursor> _loader) 
		{
			mSourcesAdapter.swapCursor(null);
		}
	}
}
