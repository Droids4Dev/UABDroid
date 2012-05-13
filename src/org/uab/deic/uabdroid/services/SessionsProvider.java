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
package org.uab.deic.uabdroid.services;

import java.util.HashMap;
import java.util.Locale;

import org.uab.deic.uabdroid.adapters.DatabaseAdapter;
import org.uab.deic.uabdroid.adapters.DatabaseAdapter.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.LiveFolders;
import android.text.TextUtils;

/**
 * 
 * @author Ruben Serrano
 *
 */

public class SessionsProvider extends ContentProvider 
{
	private static final String SESSIONS_URI = "content://org.uab.deic.provider.uabdroid/sessions";
	private static final String LIVE_FOLDER_STR = "content://org.uab.deic.provider.uabdroid/live_folder";
	
	public static final Uri CONTENT_URI = Uri.parse(SESSIONS_URI);
	public static final Uri LIVE_FOLDER_URI = Uri.parse(LIVE_FOLDER_STR);

	
	private static final int SESSIONS = 1;
	private static final int SINGLE_SESSION = 2;
	private static final int LIVE_FOLDER = 3;
	
	private static final UriMatcher mUriMatcher;
	
	private SQLiteDatabase mSessionsDatabase;
	
	static
	{
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI("org.uab.deic.provider.uabdroid", "sessions", SESSIONS);
		mUriMatcher.addURI("org.uab.deic.provider.uabdroid", "sessions/#", SINGLE_SESSION);
		mUriMatcher.addURI("org.uab.deic.provider.uabdroid", "live_folder", LIVE_FOLDER);
	}
	
	static final HashMap<String, String> LIVE_FOLDER_PROJECTION;
	static 
	{
		LIVE_FOLDER_PROJECTION = new HashMap<String, String>();
		LIVE_FOLDER_PROJECTION.put(LiveFolders._ID, DatabaseAdapter.KEY_ROWID + " AS " + LiveFolders._ID);
		if (Locale.getDefault().toString().equalsIgnoreCase("ca_ES"))
		{
			LIVE_FOLDER_PROJECTION.put(LiveFolders.NAME, DatabaseAdapter.KEY_SESSIONS_TITLE_CA + " AS " + LiveFolders.NAME);
		}
		else
		{
			LIVE_FOLDER_PROJECTION.put(LiveFolders.NAME, DatabaseAdapter.KEY_SESSIONS_TITLE_ES + " AS " + LiveFolders.NAME);
		}
		LIVE_FOLDER_PROJECTION.put(LiveFolders.DESCRIPTION, DatabaseAdapter.KEY_SESSIONS_DATE + " AS " + LiveFolders.DESCRIPTION);
	}

	
	@Override
	public String getType(Uri uri) 
	{
		switch (mUriMatcher.match(uri)) 
		{
			case SESSIONS|LIVE_FOLDER: 
				return "vnd.android.cursor.dir/vnd.uab.deic.session";
			case SINGLE_SESSION: 
				return "vnd.android.cursor.item/vnd.uab.deic.session";
			default: 
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public boolean onCreate() 
	{
		Context context = getContext();
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
		mSessionsDatabase = dbHelper.getReadableDatabase();
		return (mSessionsDatabase == null) ? false : true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DatabaseAdapter.DB_TABLE_SESSIONS);

		// If this is a row query, limit the result set to the passed in row.
		switch (mUriMatcher.match(uri)) 
		{
			case SINGLE_SESSION: 
				qb.appendWhere(DatabaseAdapter.KEY_ROWID + "=" + uri.getPathSegments().get(1));
				break;
			case LIVE_FOLDER:
				qb.setProjectionMap(LIVE_FOLDER_PROJECTION);
				sortOrder = LiveFolders._ID + " ASC";
				break;
			default: 
				break;
		}
		// If no sort order is specified sort by date / time
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) 
		{
			orderBy = DatabaseAdapter.KEY_ROWID;
		} 
		else 
		{
			orderBy = sortOrder;
		}
		// Apply the query to the underlying database.
		Cursor c = qb.query(mSessionsDatabase, projection, selection, selectionArgs, null, null, orderBy);
		// Register the contexts ContentResolver to be notified if
		// the cursor result set changes.
		c.setNotificationUri(getContext().getContentResolver(), uri);
		// Return a cursor to the query result.
		return c;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) 
	{
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) 
	{
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) 
	{
		return 0;
	}
}
