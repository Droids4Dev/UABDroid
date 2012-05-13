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
package org.uab.deic.uabdroid.adapters;

import org.uab.deic.uabdroid.R;
import org.uab.deic.uabdroid.loaders.MaterialsCursorLoader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

/**
 * 
 * @author Ruben Serrano
 *
 */

public class ExpandableMaterialsAdapter extends SimpleCursorTreeAdapter
{
	private static final String CHILD_POSITION_KEY = "CHILD_POSITION_KEY";
	private final String[] mMaterialsTypeArray;
	private Context mContext;
	private LoaderManager mLoaderManager;
	private int mSessionID;
	private static int mLoadersCounter = 0;
	
	public ExpandableMaterialsAdapter(Context _context, int _groupLayout, String[] _groupFrom, int[] _groupTo, 
									  int _childLayout, String[] _childFrom, int[] _childTo, LoaderManager _loaderManager, int _sessionID)
	{
		super(_context, null, _groupLayout, _groupFrom, _groupTo, _childLayout, _childFrom, _childTo);
		mMaterialsTypeArray = _context.getResources().getStringArray(R.array.materials);
		mContext = _context;
		mLoaderManager = _loaderManager;
		mSessionID = _sessionID;
		mLoadersCounter = 0;
	}

	@Override
	public View getGroupView(int _groupPosition, boolean _isExpanded, View _convertView, ViewGroup _parentView) 
	{
		ViewGroup groupItemView = (ViewGroup) super.getGroupView(_groupPosition, _isExpanded, _convertView, _parentView);
		TextView itemTextView = (TextView) groupItemView.getChildAt(_groupPosition);
		itemTextView.setText(mMaterialsTypeArray[Integer.parseInt(itemTextView.getText().toString())]);
		
		return groupItemView;
	}

	@Override
	public View getChildView(int _groupPosition, int _childPosition, boolean _isLastChild, View _convertView, ViewGroup _parentView) 
	{
		ViewGroup viewGroup = (ViewGroup) super.getChildView(_groupPosition, _childPosition, _isLastChild, _convertView, _parentView);
		return viewGroup;
	}

	@Override
	protected Cursor getChildrenCursor(Cursor _groupCursor) 
	{	
		Loader<Cursor> loader = mLoaderManager.getLoader(1);
		
		if (loader == null)
		{
			Bundle arguments = new Bundle();
			arguments.putInt(CHILD_POSITION_KEY, _groupCursor.getPosition());
		
			mLoadersCounter++;
			mLoaderManager.initLoader(mLoadersCounter, arguments, new MaterialsAdapterCallback());
		}
		else
		{
			loader.forceLoad();
		}
		
		return null;
	}
	
	public int getLoadersCount()
	{
		return mLoadersCounter;
	}
	
	private class MaterialsAdapterCallback implements LoaderManager.LoaderCallbacks<Cursor>
	{
		private int mPosition;
		
		@Override
		public Loader<Cursor> onCreateLoader(int _id, Bundle _args) 
		{
			mPosition = _args.getInt(CHILD_POSITION_KEY);
			return new MaterialsCursorLoader(mContext, mSessionID);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> _loader, Cursor _cursor) 
		{
			if ((_cursor != null) && (!_cursor.isClosed()))
			{
				setChildrenCursor(mPosition, _cursor);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> _loader) 
		{

		}
		
	}
}