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
package org.uab.deic.uabdroid.loaders;

import org.uab.deic.uabdroid.adapters.DatabaseAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;


/**
 * 
 * @author Ruben Serrano
 *
 */
//As there is no Loader that enables us to load data asyncrhonously
//from a database, we must create a new one
public abstract class DatabaseCursorLoader extends AsyncTaskLoader<Cursor> 
{
	private DatabaseAdapter mDatabaseAdapter;
	private DatabaseReceiver mDatabaseReceiver;
	private String mEventToFilter;
	protected Cursor mCursor;
	
	// When constructed, we open the database
	public DatabaseCursorLoader(Context _context, String _eventToFilter) 
	{
		super(_context);
		mDatabaseAdapter = new DatabaseAdapter(getContext());
		mDatabaseAdapter.openReadOnly();
		mEventToFilter = _eventToFilter;
	}
	
	protected DatabaseAdapter getDatabaseAdapter()
	{
		return mDatabaseAdapter;
	}

	// When the LoaderManager inits a load, we must call the
	// forceLoad() method from AsyncTaskLoader. Doing that, we 
	// are launching a new thread, that will execute the 
	// loadInBackground method
	@Override
	protected void onStartLoading() 
	{
		// The DatabaseReceiver is used to capture the update database event.
		// BroadcastReceivers are a part of CursorLoader, but we will see them at
		// unit 5
		if (mDatabaseReceiver == null)
		{
			mDatabaseReceiver = new DatabaseReceiver();
		}
		
		forceLoad();
		super.onStartLoading();
	}

	// This method will be executed in a new thread
	// Here we perform the data loading operations
	@Override
	public Cursor loadInBackground() 
	{	
		// We only close the cursor in this method, and expect that the
		// class who inherits that one implements the query
		if ((mCursor != null)&&(!mCursor.isClosed()))
		{
			mCursor.close();
			mCursor = null;
		}
		
		return placeQuery();
	}
	
	// To be implemented by each subclass with a call to the 
	// query that generates the cursor
	protected abstract Cursor placeQuery();
	
	// If the loader if reseted, we must do some cleaning :)
	@Override 
	protected void onReset() 
	{
		super.onReset();
         
		// Stop the loading, if it's being performed
		onStopLoading();
		
		// Close the cursor if it's opened
		if ((mCursor != null)&&(!mCursor.isClosed()))
		{
			mCursor.close();
			mCursor = null;
		}

		// Close the database
		if (mDatabaseAdapter != null)
		{
			mDatabaseAdapter.close();
			mDatabaseAdapter = null;
		}

		// Unregister the receiver
		if (mDatabaseReceiver != null) 
		{
			getContext().unregisterReceiver(mDatabaseReceiver);
			mDatabaseReceiver = null;
		}
     }
	
	@Override
	public void onCanceled(Cursor _data) 
	{
		super.onCanceled(_data);
		
		// Close the cursor if it's opened
		if ((mCursor != null)&&(!mCursor.isClosed()))
		{
			mCursor.close();
			mCursor = null;
		}
	}

	private class DatabaseReceiver extends BroadcastReceiver
	{
		public DatabaseReceiver()
		{
			// We need to register the receiver to filter one or more intents
			IntentFilter filter = new IntentFilter(mEventToFilter);
            getContext().registerReceiver(this, filter);
		}
		
		@Override
		public void onReceive(Context _context, Intent _intent) 
		{
			if (_intent.getAction().compareTo(mEventToFilter) == 0)
			{
				// We call onContentChanged(), a method from the Loader, to indicate
				// that a change in the database has taken place, so the Loader has
				// to restart the loading process
				onContentChanged();
			}
		}		
	}
}
