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
import org.uab.deic.uabdroid.adapters.ExpandableMaterialsAdapter;
import org.uab.deic.uabdroid.loaders.MaterialsCursorLoader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ExpandableListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ExpandableListView;

/**
 * 
 * @author Joan Fuentes
 * @author Ruben Serrano
 *
 */
public class SessionMaterialsFragment extends ExpandableListFragment
{
    private static final String NAME_COLUMN_LANGUAGE;
    static
    {
    	if(Locale.getDefault().toString().compareTo("ca_ES")==0)
        {
    		NAME_COLUMN_LANGUAGE = DatabaseAdapter.KEY_MATERIALS_NAME_CA;
        }
    	else
    	{
    		NAME_COLUMN_LANGUAGE = DatabaseAdapter.KEY_MATERIALS_NAME_ES;
    	}
    }
    
    private static final String[] GROUP_COLUMNS_FROM = {DatabaseAdapter.KEY_MATERIALS_TYPE};
    private static final String[] CHILD_COLUMNS_FROM = {NAME_COLUMN_LANGUAGE};
    private static final int[] GROUP_VIEWS_TO = {R.id.expandable_list_group_item_text};
    private static final int[] CHILD_VIEWS_TO = {R.id.materials_expandable_list_child_item_text};
	
    private Activity mParentActivity;
    private Cursor mCursor;
    private ExpandableMaterialsAdapter mExpandableListAdapter;
    private int mSessionID;

    
    
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
        
        mExpandableListAdapter = 
        		new ExpandableMaterialsAdapter(mParentActivity,
        									R.layout.expandable_list_group_item,  
        									GROUP_COLUMNS_FROM, 
        									GROUP_VIEWS_TO, 
        									R.layout.materials_expandable_list_child_item, 
        									CHILD_COLUMNS_FROM, 
        									CHILD_VIEWS_TO,
        									getLoaderManager(),
        									mSessionID);
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
    	super.onActivityCreated(savedInstanceState);
				
		setListAdapter(mExpandableListAdapter);
                
        ExpandableListView expandableListView = getExpandableListView();
        expandableListView.setTextFilterEnabled(true);
        expandableListView.setOnChildClickListener(this);
        // Trick to avoid the "flicker" effect
        expandableListView.setCacheColorHint(0);
        
        getLoaderManager().initLoader(0, null, new MaterialsCursorLoaderCallback());
    }

	@Override
	public boolean onChildClick(ExpandableListView _parentView, View _itemView, int _groupPosition, int _childPosition, long _id) 
	{
		if ((mCursor != null) && (!mCursor.isClosed()))
		{
			mCursor.move(_groupPosition);
			String url =  mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_MATERIALS_URL));
			Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			mParentActivity.startActivity(intent);
		}
		return super.onChildClick(_parentView, _itemView, _groupPosition, _childPosition, _id);
	}
	
	@Override
	public void onDestroy()
	{
		getLoaderManager().destroyLoader(0);
		
		int loadersCount = mExpandableListAdapter.getLoadersCount() + 1;
		
		for (int i=1; i<loadersCount; i++)
		{
			getLoaderManager().destroyLoader(i);
		}
		
		super.onDestroy();
	}
	
	private class MaterialsCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
	{
		// In the constructor, obtain a new Loader
		@Override
		public Loader<Cursor> onCreateLoader(int _id, Bundle _args) 
		{
			return new MaterialsCursorLoader(mParentActivity, mSessionID);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> _loader, Cursor _cursor) 
		{
			if ((_cursor != null) && (!_cursor.isClosed()))
			{
				if (!_cursor.equals(mExpandableListAdapter.getCursor()))
				{
					mCursor = _cursor;
					mExpandableListAdapter.changeCursor(_cursor);
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> _loader) 
		{
			
		}
	}
}


