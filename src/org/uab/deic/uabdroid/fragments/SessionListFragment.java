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

import org.uab.deic.uabdroid.ActionBarCompat;
import org.uab.deic.uabdroid.R;
import org.uab.deic.uabdroid.SessionDetailsActivity;
import org.uab.deic.uabdroid.adapters.DatabaseAdapter;
import org.uab.deic.uabdroid.loaders.SessionsCursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;

/**
 * 
 * @author Joan Fuentes
 * @author Ruben Serrano
 *
 */
public class SessionListFragment extends ListFragment 
{
	private SimpleCursorAdapter mCursorAdapter;
	private FragmentActivity mParentActivity;

    @Override
    public void onActivityCreated(Bundle _savedInstanceState) 
    {	
        super.onActivityCreated(_savedInstanceState);
        
        mParentActivity = getActivity();
        
        String locale = Locale.getDefault().toString();
        
        String keySessionsName = null;
        if(locale.compareTo("ca_ES")==0)
        {
        	keySessionsName = DatabaseAdapter.KEY_SESSIONS_TITLE_CA;
        }
        else
        {
        	keySessionsName = DatabaseAdapter.KEY_SESSIONS_TITLE_ES;
        }
        
        String[] columns = new String[] { DatabaseAdapter.KEY_ROWID, keySessionsName, DatabaseAdapter.KEY_SESSIONS_DATE };
        int[] to = new int[] { R.id.txtNumOfSession, R.id.txtName, R.id.txtDate};

        // Now create an array adapter and set it to display using our row
        mCursorAdapter = new SimpleCursorAdapter(mParentActivity, R.layout.element_sessions_list, null, columns, to, 0);
        setListAdapter(mCursorAdapter);
        
        mParentActivity.getSupportLoaderManager().initLoader(0, null, new SessionsCursorLoaderCallback());
        
        // Patch to avoid "flicker" effect.
        getListView().setCacheColorHint(0);
    }

	@Override
    public void onListItemClick(ListView _parentListView, View _itemView, int _position, long _id) 
    {
        Intent intent = new Intent(SessionDetailsActivity.SESSION_DETAILS_INTENT);
        intent.putExtra(SessionDetailsActivity.SESSION_ID_KEY, (int)_id);
        String sessionTitle = mParentActivity.getResources().getString(R.string.session_text) + " " + _id;
		intent.putExtra(ActionBarCompat.ACTION_BAR_TITLE_EXTRA_KEY, sessionTitle);
        startActivity(intent);
    }
	
	private class SessionsCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
	{
		// In the constructor, obtain a new Loader
		@Override
		public Loader<Cursor> onCreateLoader(int _id, Bundle _args) 
		{
			return new SessionsCursorLoader(mParentActivity);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> _loader, Cursor _cursor) 
		{
			mCursorAdapter.swapCursor(_cursor);		
		}

		// If the Loader is reseted, we empty the adapter
		@Override
		public void onLoaderReset(Loader<Cursor> _loader) 
		{
			mCursorAdapter.swapCursor(null);
		}
	}
}
