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

import java.util.ArrayList;
import java.util.Random;

import org.uab.deic.uabdroid.utils.Examination;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple session database access helper class. Defines the basic CRUD operations
 * for the application, and gives the ability to list all sessions as well as
 * retrieve or modify a specific session.
 * 
 * @author Joan Fuentes
 * @author RubŽn Serrano
 * 
 */
public class DatabaseAdapter 
{
	// Intents for broadcasting the update database events
	public static final String UPDATE_SESSIONS = "org.uab.deic.uabdroid.UPDATE_SESSIONS";
	private final Intent mUpdateSessionsIntent = new Intent(UPDATE_SESSIONS);
	public static final String UPDATE_MATERIALS = "org.uab.deic.uabdroid.UPDATE_MATERIALS";
	private final Intent mUpdateMaterialsIntent = new Intent(UPDATE_MATERIALS);
	public static final String UPDATE_SOURCES = "org.uab.deic.uabdroid.UPDATE_SOURCES";
	private final Intent mUpdateSourcesIntent = new Intent(UPDATE_SOURCES);
	
    private static final String DB_NAME = "appdb";
    public static final String DB_TABLE_SESSIONS = "tblSessions";
    private static final String DB_TABLE_MATERIALS = "tblMaterials";
    private static final String DB_TABLE_SOURCES = "tblSources";
    private static final String DB_TABLE_MATERIALSBYSESSION = "tblMaterialsBySession";
    private static final String DB_TABLE_SOURCESBYSESSION = "tblSourcesBySession";
    private static final String DB_TABLE_SOURCETYPES = "tblSourceTypes";
    private static final String DB_TABLE_MATERIALTYPES = "tblMaterialTypes";
    private static final String DB_TABLE_TEST = "tblTest";

    public static final String KEY_SESSIONS_TITLE_CA = "title_ca";
    public static final String KEY_SESSIONS_TITLE_ES = "title_es";
    public static final String KEY_SESSIONS_CLASSROOM = "classroom";
    public static final String KEY_SESSIONS_DATE = "date";
    public static final String KEY_SESSIONS_HOUR = "hour";
    public static final String KEY_SESSIONS_CONTENT_CA = "content_ca";
    public static final String KEY_SESSIONS_CONTENT_ES = "content_es";

    public static final String KEY_MATERIALS_TYPE = "type";
    public static final String KEY_MATERIALS_NAME_CA = "name_ca";
    public static final String KEY_MATERIALS_NAME_ES = "name_es";
    public static final String KEY_MATERIALS_URL = "url";

    public static final String KEY_SOURCES_NAME_CA = "name_ca";
    public static final String KEY_SOURCES_NAME_ES = "name_es";
    public static final String KEY_SOURCES_DESCRIPTION_CA = "description_ca";
    public static final String KEY_SOURCES_DESCRIPTION_ES = "description_es";
    public static final String KEY_SOURCES_TYPE = "type";
    public static final String KEY_SOURCES_URL = "url";

    public static final String KEY_MATERIALSBYSESSION_ID_MATERIAL = "id_material";
    public static final String KEY_MATERIALSBYSESSION_ID_SESSION = "id_session";

    public static final String KEY_SOURCESBYSESSION_ID_SOURCE = "id_source";
    public static final String KEY_SOURCESBYSESSION_ID_SESSION = "id_session";

    public static final String KEY_SOURCETYPES_NAME = "name";

    public static final String KEY_MATERIALTYPES_NAME = "name";

    public static final String KEY_TEST_QUESTION_CA = "question_ca";
    public static final String KEY_TEST_QUESTION_ES = "question_es";
    public static final String KEY_TEST_CORRECTANSWER = "correct_answer";
    public static final String KEY_TEST_ANSWER_1 = "answer_1";
    public static final String KEY_TEST_ANSWER_2 = "answer_2";
    
    public static final String KEY_ROWID = "_id";

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_SESSIONS =
        "create table " + DB_TABLE_SESSIONS + "(" + KEY_ROWID +" integer primary key, "
        + KEY_SESSIONS_TITLE_CA + " text not null, " + KEY_SESSIONS_TITLE_ES + " text not null, " + KEY_SESSIONS_CLASSROOM + " text not null, " + KEY_SESSIONS_DATE + " text not null, " + KEY_SESSIONS_HOUR + " text not null, " + KEY_SESSIONS_CONTENT_CA + " text not null, " + KEY_SESSIONS_CONTENT_ES + " text not null);";

    private static final String DATABASE_CREATE_MATERIALS =
        "create table " + DB_TABLE_MATERIALS + "(" + KEY_ROWID +" integer primary key, "
        + KEY_MATERIALS_TYPE + " integer not null, " + KEY_MATERIALS_NAME_CA + " text not null, " + KEY_MATERIALS_NAME_ES + " text not null, " + KEY_MATERIALS_URL + " text not null);";

    private static final String DATABASE_CREATE_SOURCES =
        "create table " + DB_TABLE_SOURCES + "(" + KEY_ROWID +" integer primary key, "
        + KEY_SOURCES_NAME_CA + " text not null, " + KEY_SOURCES_NAME_ES + " text not null, " + KEY_SOURCES_DESCRIPTION_CA + " text not null, " + KEY_SOURCES_DESCRIPTION_ES + " text not null, " + KEY_SOURCES_TYPE + " integer not null, " + KEY_SOURCES_URL + " text not null);";

    private static final String DATABASE_CREATE_MATERIALSBYSESSION =
        "create table " + DB_TABLE_MATERIALSBYSESSION + "("
        + KEY_MATERIALSBYSESSION_ID_MATERIAL + " integer, " + KEY_MATERIALSBYSESSION_ID_SESSION + " integer , primary key(" + KEY_MATERIALSBYSESSION_ID_MATERIAL + "," + KEY_MATERIALSBYSESSION_ID_SESSION + "));";

    private static final String DATABASE_CREATE_SOURCESBYSESSION =
        "create table " + DB_TABLE_SOURCESBYSESSION + "("
        + KEY_SOURCESBYSESSION_ID_SOURCE + " integer, " + KEY_SOURCESBYSESSION_ID_SESSION + " integer,primary key(" + KEY_SOURCESBYSESSION_ID_SOURCE + "," + KEY_SOURCESBYSESSION_ID_SESSION + "));";

    private static final String DATABASE_CREATE_SOURCETYPE =
        "create table " + DB_TABLE_SOURCETYPES + "(" + KEY_ROWID +" integer primary key, "
        + KEY_SOURCETYPES_NAME + " text not null);";
    
    private static final String DATABASE_CREATE_MATERIALTYPE =
        "create table " + DB_TABLE_MATERIALTYPES + "(" + KEY_ROWID +" integer primary key, "
        + KEY_MATERIALTYPES_NAME + " text not null);";
 
    private static final String DATABASE_CREATE_TEST =
        "create table " + DB_TABLE_TEST + "(" + KEY_ROWID +" integer primary key, "
        + KEY_TEST_QUESTION_CA + " text not null, " + KEY_TEST_QUESTION_ES + " text not null, " + KEY_TEST_CORRECTANSWER + " text not null, " + KEY_TEST_ANSWER_1 + " text not null, " + KEY_TEST_ANSWER_2 + " text not null);";
 
    
    private static final int DB_VERSION = 2;
    private static final String TAG = "DatabaseAdapter";
    
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;
    private final Context mContext;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DatabaseAdapter(Context _context) 
    {
        mContext = _context;
    }

    /**
     * Opens the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @throws SQLException if the database could be neither opened or created
     */
    public void open() throws SQLException 
    {
    	if (mDatabaseHelper == null)
    	{
    		mDatabaseHelper = new DatabaseHelper(mContext);
    		mDatabase = mDatabaseHelper.getWritableDatabase();
    	}
    }
    
    public void openReadOnly() throws SQLException 
    {
    	if (mDatabaseHelper == null)
    	{
    		mDatabaseHelper = new DatabaseHelper(mContext);
    		mDatabase = mDatabaseHelper.getReadableDatabase();
    	}
    }

    public void close() 
    {
    	if (mDatabaseHelper != null)
    	{
    		mDatabaseHelper.close();
    		mDatabase = null;
    		mDatabaseHelper = null;
    	}
    }
    
    public boolean isClosed()
    {
    	if (mDatabase == null)
    	{
    		return true;
    	}
    	return (!mDatabase.isOpen());
    }
    
    /**
     * Whenever that we update the database, we should inform of this fact
     * for the loader sake
     */
 	private void sendUpdatedSessionsEvent()
 	{
 		mContext.sendBroadcast(mUpdateSessionsIntent);
 	}
 	
 	/**
     * Whenever that we update the database, we should inform of this fact
     * for the loader sake
     */
 	private void sendUpdatedMaterialsEvent()
 	{
 		mContext.sendBroadcast(mUpdateMaterialsIntent);
 	}
 	
 	/**
     * Whenever that we update the database, we should inform of this fact
     * for the loader sake
     */
 	private void sendUpdatedSourcesEvent()
 	{
 		mContext.sendBroadcast(mUpdateSourcesIntent);
 	}

    /**
     * Create a new Session using the params provided. If the session is
     * successfully created return the new rowId for that session, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title_ca the catalan title of the session
     * @param title_es the spanish title of the session
     * @param classroom the classroom where the session will to take place
     * @param date the date for the session
     * @param hour the hour when the session will take place at
     * @param content_ca
     * @param content_es  
     * @return rowId or -1 if failed
     */
    public long addSession(long rowId, String title_ca, String title_es, String classroom, String date, String hour, 
    					   String content_ca, String content_es) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, rowId);
        initialValues.put(KEY_SESSIONS_TITLE_CA, title_ca);
        initialValues.put(KEY_SESSIONS_TITLE_ES, title_es);
        initialValues.put(KEY_SESSIONS_CLASSROOM, classroom);
        initialValues.put(KEY_SESSIONS_DATE, date);
        initialValues.put(KEY_SESSIONS_HOUR, hour);
        initialValues.put(KEY_SESSIONS_CONTENT_CA, content_ca);
        initialValues.put(KEY_SESSIONS_CONTENT_ES, content_es);
        
        sendUpdatedSessionsEvent();
 
        return mDatabase.insert(DB_TABLE_SESSIONS, null, initialValues);
    }

    /**
     * Delete the pil with the given rowId
     * 
     * @param rowId id of pil to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSession(long rowId) 
    {
    	sendUpdatedSessionsEvent();
    	
        return mDatabase.delete(DB_TABLE_SESSIONS, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all sessions in the database
     * 
     * @return Cursor over all sessions
     */
    public Cursor fetchAllSessions() 
    {
        Cursor mCursor = mDatabase.query(DB_TABLE_SESSIONS, 
        							new String[] 
        							{
        								KEY_ROWID, KEY_SESSIONS_TITLE_CA,KEY_SESSIONS_TITLE_ES,KEY_SESSIONS_CLASSROOM,
        								KEY_SESSIONS_DATE,KEY_SESSIONS_HOUR,KEY_SESSIONS_CONTENT_CA,
        								KEY_SESSIONS_CONTENT_ES
        							}, 
        							null, null, null, null, null);
        if (mCursor != null) 
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Return a Cursor positioned at the pil that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching pil, if found
     * @throws SQLException if pil could not be found/retrieved
     */
    public Cursor fetchSession(long rowId) throws SQLException 
    {
        Cursor mCursor = mDatabase.query(true, DB_TABLE_SESSIONS, 
        							new String[] 
        							{
        								KEY_ROWID,KEY_SESSIONS_TITLE_CA,KEY_SESSIONS_TITLE_ES,KEY_SESSIONS_CLASSROOM,
        								KEY_SESSIONS_DATE,KEY_SESSIONS_HOUR,KEY_SESSIONS_CONTENT_CA,
        								KEY_SESSIONS_CONTENT_ES
        							}, 
        							KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) 
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the pil using the details provided. The pil to be updated is
     * specified using the rowId, and it is altered to use the values passed in
     * 
     * @param rowId id of pil to update
     * @param title value to set pil title to
     * @param amount value to set pil amount to
     * @param tin value to set pil tin to
     * @param installment value to set pil installment to
     * @param firstquote value to set pil firstquote date to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateSession(long rowId, String title_ca, String title_es, String classroom, 
    							 String date, String hour, String content_ca, String content_es) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_SESSIONS_TITLE_CA, title_ca);
        args.put(KEY_SESSIONS_TITLE_ES, title_es);
        args.put(KEY_SESSIONS_CLASSROOM, classroom);
        args.put(KEY_SESSIONS_DATE, date);
        args.put(KEY_SESSIONS_HOUR, hour);
        args.put(KEY_SESSIONS_CONTENT_CA, content_ca);
        args.put(KEY_SESSIONS_CONTENT_ES, content_es);
        
        sendUpdatedSessionsEvent();
        
        return mDatabase.update(DB_TABLE_SESSIONS, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
        
    /**
     * TODO
     */
    public boolean isEmpty(String tblName) 
    {
    	boolean result = false;
    	long numRows = 0;
        Cursor mCursor =
        	mDatabase.rawQuery("SELECT COUNT(*) FROM [" + tblName + "]", null);

        if (mCursor != null) mCursor.moveToFirst();
        numRows = mCursor.getLong(0);
		if (numRows == 0) result = true;
		
		mCursor.close();
		return result;
    }       

    /**
     * TODO
     */
    private long getNumberOfRows(String tblName) 
    {
    	long numRows = 0;
        Cursor mCursor =
        	mDatabase.rawQuery("SELECT COUNT(*) FROM [" + tblName + "]", null);

        if (mCursor != null) mCursor.moveToFirst();
        numRows = mCursor.getLong(0);
        mCursor.close();
		return numRows;
    }    

    /**
     * TODO
     */
    public long getNumberOfSessions() 
    {
		return getNumberOfRows(DB_TABLE_SESSIONS);
    }    
    
    /**
     * TODO
     */    
    public void addMaterial(long rowId, long type, String name_ca, String name_es, String url) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, rowId);
        initialValues.put(KEY_MATERIALS_TYPE, type);
        initialValues.put(KEY_MATERIALS_NAME_CA, name_ca);
        initialValues.put(KEY_MATERIALS_NAME_ES, name_es);
        initialValues.put(KEY_MATERIALS_URL, url);
 
        mDatabase.insert(DB_TABLE_MATERIALS, null, initialValues);
    }

    /**
     * TODO
     */
    public boolean updateMaterial(long rowId, long type, String name_ca, String name_es, String url) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_MATERIALS_TYPE, type);
        args.put(KEY_MATERIALS_NAME_CA, name_ca);
        args.put(KEY_MATERIALS_NAME_ES, name_es);
        args.put(KEY_MATERIALS_URL, url);
        
        return mDatabase.update(DB_TABLE_MATERIALS, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * TODO
     */
    public boolean deleteMaterial(long rowId) 
    {	
        return mDatabase.delete(DB_TABLE_MATERIALS, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * TODO
     */    
    public void addSource(long rowId, String name_ca, String name_es, String description_ca, String description_es, long type, String url)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, rowId);
        initialValues.put(KEY_SOURCES_NAME_CA, name_ca);
        initialValues.put(KEY_SOURCES_NAME_ES, name_es);
        initialValues.put(KEY_SOURCES_DESCRIPTION_CA, description_ca);
        initialValues.put(KEY_SOURCES_DESCRIPTION_ES, description_es);
        initialValues.put(KEY_SOURCES_TYPE, type);
        initialValues.put(KEY_SOURCES_URL, url);
 
        mDatabase.insert(DB_TABLE_SOURCES, null, initialValues);
    }

    /**
     * TODO
     */
    public boolean updateSource(long rowId, String name_ca, String name_es, String description_ca, String description_es, long type, String url)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_SOURCES_NAME_CA, name_ca);
        args.put(KEY_SOURCES_NAME_ES, name_es);
        args.put(KEY_SOURCES_DESCRIPTION_CA, description_ca);
        args.put(KEY_SOURCES_DESCRIPTION_ES, description_es);
        args.put(KEY_SOURCES_TYPE, type);
        args.put(KEY_SOURCES_URL, url);
        
        return mDatabase.update(DB_TABLE_SOURCES, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * TODO
     */
    public boolean deleteSource(long rowId)
    {
        return mDatabase.delete(DB_TABLE_SOURCES, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * TODO
     */    
    public void addMaterialBySession(long id_material, long id_session) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MATERIALSBYSESSION_ID_MATERIAL, id_material);
        initialValues.put(KEY_MATERIALSBYSESSION_ID_SESSION, id_session);
        
        sendUpdatedMaterialsEvent();
        
        mDatabase.insert(DB_TABLE_MATERIALSBYSESSION, null, initialValues);
    }

    /**
     * TODO
     */
    public boolean updateMaterialBySession(long id_material, long id_session) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_MATERIALSBYSESSION_ID_MATERIAL, id_material);
        args.put(KEY_MATERIALSBYSESSION_ID_SESSION, id_session);
        
        sendUpdatedMaterialsEvent();
         
        return mDatabase.update(DB_TABLE_MATERIALSBYSESSION, args, KEY_MATERIALSBYSESSION_ID_MATERIAL + "=" + id_material + 
        					"AND " + KEY_MATERIALSBYSESSION_ID_SESSION + "=" + id_session, null) > 0;
    }
    
    /**
     * TODO
     */
    public boolean deleteMaterialBySession(long id_material, long id_session) 
    {
    	sendUpdatedMaterialsEvent();
    	
        return mDatabase.delete(DB_TABLE_MATERIALSBYSESSION, KEY_MATERIALSBYSESSION_ID_MATERIAL + "=" + id_material + 
        					"AND " + KEY_MATERIALSBYSESSION_ID_SESSION + "=" + id_session, null) > 0;
    }   

    /**
     * TODO
     */
    public boolean deleteMaterialsBySession(long id_session) 
    {
    	sendUpdatedMaterialsEvent();
    	
        return mDatabase.delete(DB_TABLE_MATERIALSBYSESSION, KEY_MATERIALSBYSESSION_ID_SESSION + "=" + id_session, null) > 0;
    }
    
    /**
     * TODO
     */    
    public Cursor fetchMaterialsBySession(long id_session) throws SQLException 
    {
        Cursor mCursor = 
        		mDatabase.rawQuery("SELECT M.* " +
        							"FROM (" +
        								"SELECT * " +
        								"FROM [" + DB_TABLE_MATERIALSBYSESSION + "] " +
										"WHERE [" + DB_TABLE_MATERIALSBYSESSION + "].[" + KEY_MATERIALSBYSESSION_ID_SESSION + "]=" + id_session + " " +
									") as MS " +
									"INNER JOIN [" + DB_TABLE_MATERIALS + "] as M " +
											"ON MS.[" + KEY_MATERIALSBYSESSION_ID_MATERIAL + "] = M.[" + KEY_ROWID + "] " +
									"ORDER by M.[" + KEY_MATERIALS_TYPE + "] ASC", 
									null);
        return mCursor;
    }
    
    /**
     * TODO
     */    
    public void addSourceBySession(long id_source, long id_session) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SOURCESBYSESSION_ID_SOURCE, id_source);
        initialValues.put(KEY_SOURCESBYSESSION_ID_SESSION, id_session);
        
        sendUpdatedSourcesEvent();
        
        mDatabase.insert(DB_TABLE_SOURCESBYSESSION, null, initialValues);
    }
   
    /**
     * TODO
     */
    public boolean updateSourceBySession(long id_source, long id_session) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_SOURCESBYSESSION_ID_SOURCE, id_source);
        args.put(KEY_SOURCESBYSESSION_ID_SESSION, id_session);
        
        sendUpdatedSourcesEvent();
        
        return mDatabase.update(DB_TABLE_SOURCESBYSESSION, args, KEY_SOURCESBYSESSION_ID_SOURCE + "=" + id_source + 
        					"AND " + KEY_SOURCESBYSESSION_ID_SESSION + "=" + id_session, null) > 0;
    }
    
    /**
     * TODO
     */
    public boolean deleteSourceBySession(long id_source, long id_session) 
    {
    	sendUpdatedSourcesEvent();
    	
        return mDatabase.delete(DB_TABLE_SOURCESBYSESSION, KEY_SOURCESBYSESSION_ID_SOURCE + "=" + id_source + 
        					"AND " + KEY_SOURCESBYSESSION_ID_SESSION + "=" + id_session, null) > 0;
    }   

    /**
     * TODO
     */
    public boolean deleteSourcesBySession(long id_session)
    {
    	sendUpdatedSourcesEvent();
    	
        return mDatabase.delete(DB_TABLE_SOURCESBYSESSION, KEY_SOURCESBYSESSION_ID_SESSION + "=" + id_session, null) > 0;
    }
    
    
    /**
     * TODO
     */    
    public Cursor fetchSourcesBySession(long _sessionID) throws SQLException 
    {
        Cursor mCursor = mDatabase.rawQuery(
        		"SELECT S.* from " +
        				" (SELECT * FROM [" + DB_TABLE_SOURCESBYSESSION + "] " +
        				" WHERE [" + DB_TABLE_SOURCESBYSESSION + "].[" + KEY_SOURCESBYSESSION_ID_SESSION + "]=" + _sessionID + ") as MS " +
        				" INNER JOIN [" + DB_TABLE_SOURCES + "] as S ON MS.[" + KEY_SOURCESBYSESSION_ID_SOURCE + "] = S.[" + KEY_ROWID + "] " +
        				" ORDER by S.[" + KEY_SOURCES_TYPE + "] ASC", null);

        if (mCursor != null) 
        {
        	mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * TODO
     */    
    public void addSourceType(long rowId, String name) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, rowId);
        initialValues.put(KEY_SOURCETYPES_NAME, name);
 
        mDatabase.insert(DB_TABLE_SOURCETYPES, null, initialValues);
    }
    
    /**
     * TODO
     */
    public boolean updateSourceType(long id_source, String name) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_SOURCETYPES_NAME, name);
         
        return mDatabase.update(DB_TABLE_SOURCETYPES, args, KEY_ROWID + "=" + id_source, null) > 0;
    }
    
    /**
     * TODO
     */
    public boolean deleteSourceType(long id_source) 
    {	
        return mDatabase.delete(DB_TABLE_SOURCETYPES, KEY_ROWID + "=" + id_source, null) > 0;
    }       

    
    /**
     * TODO
     */    
    public void addMaterialType(long rowId, String name) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, rowId);
        initialValues.put(KEY_MATERIALTYPES_NAME, name);
        
        mDatabase.insert(DB_TABLE_MATERIALTYPES, null, initialValues);
    }
    
    /**
     * TODO
     */
    public boolean updateMaterialType(long id_material, String name) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_MATERIALTYPES_NAME, name);
        
        return mDatabase.update(DB_TABLE_MATERIALTYPES, args, KEY_ROWID + "=" + id_material, null) > 0;
    }
    
    /**
     * TODO
     */
    public boolean deleteMaterialType(long id_source) 
    {	
        return mDatabase.delete(DB_TABLE_MATERIALTYPES, KEY_ROWID + "=" + id_source, null) > 0;
    }   
    
    /**
     * TODO
     */
    public long addTestQuestion(long rowId, String question_ca, String question_es, String correct_answer, 
    						String answer_1, String answer_2) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, rowId);
        initialValues.put(KEY_TEST_QUESTION_CA, question_ca);
        initialValues.put(KEY_TEST_QUESTION_ES, question_es);
        initialValues.put(KEY_TEST_CORRECTANSWER, correct_answer);
        initialValues.put(KEY_TEST_ANSWER_1, answer_1);
        initialValues.put(KEY_TEST_ANSWER_2, answer_2);
        
        return mDatabase.insert(DB_TABLE_TEST, null, initialValues);
    }

    /**
     * TODO
     */
    public boolean deleteTestQuestion(long rowId) 
    {	
        return mDatabase.delete(DB_TABLE_TEST, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * TODO
     */
    public boolean deleteTestQuestions() 
    {	
        return mDatabase.delete(DB_TABLE_TEST, null, null) > 0;
    }
    
    /**
     * TODO
     */
    public Cursor fetchAllTestQuestions() {

        Cursor mCursor = mDatabase.query(DB_TABLE_TEST, 
        							new String[] 
        							{
        								KEY_ROWID, KEY_TEST_QUESTION_CA,KEY_TEST_QUESTION_ES,
        								KEY_TEST_CORRECTANSWER,KEY_TEST_ANSWER_1,KEY_TEST_ANSWER_2
        							}, 
        							null, null, null, null, null);
        if (mCursor != null) 
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * TODO
     */
    public Cursor fetchSomeTestQuestions(int numberOfQuestions) {

        Cursor mCursor = mDatabase.rawQuery("SELECT * FROM [" + DB_TABLE_TEST + "] ORDER BY RANDOM() LIMIT " + numberOfQuestions, null);

        if (mCursor != null) 
        {
        	mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * TODO
     */
    public Cursor fetchTestQuestion(long rowId) throws SQLException 
    {
        Cursor mCursor = mDatabase.query(true, DB_TABLE_TEST, 
        							new String[]
        							{
        								KEY_ROWID,KEY_TEST_QUESTION_CA,KEY_TEST_QUESTION_ES,
        								KEY_TEST_CORRECTANSWER,KEY_TEST_ANSWER_1,KEY_TEST_ANSWER_2
        							}, 
        							KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) 
        {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * TODO
     */
    public boolean updateTestQuestion(long rowId, String question_ca, String question_es, String correct_answer, String answer_1, String answer_2) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_TEST_QUESTION_CA, question_ca);
        args.put(KEY_TEST_QUESTION_ES, question_es);
        args.put(KEY_TEST_CORRECTANSWER, correct_answer);
        args.put(KEY_TEST_ANSWER_1, answer_1);
        args.put(KEY_TEST_ANSWER_2, answer_2);
        
        return mDatabase.update(DB_TABLE_TEST, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * TODO
     */
    public boolean getQuestionsAndAnswers(ArrayList<Examination> questionsAndAnswers, int[] correctAnswers, int numberOfQuestions) 
    {
    	try 
    	{
    		Examination questionAndAnswers;
    		int position;
			Cursor mCursor;
			mCursor = fetchSomeTestQuestions(numberOfQuestions);
			
			if(mCursor.getCount()>0)
			{
		        int columnQUESTIONCAIndex = mCursor.getColumnIndexOrThrow(KEY_TEST_QUESTION_CA);
		        int columnQUESTIONESIndex = mCursor.getColumnIndexOrThrow(KEY_TEST_QUESTION_ES);
		        int columnCORRECTANSWERIndex = mCursor.getColumnIndexOrThrow(KEY_TEST_CORRECTANSWER);
		        int columnANSWER1Index = mCursor.getColumnIndexOrThrow(KEY_TEST_ANSWER_1);
		        int columnANSWER2Index = mCursor.getColumnIndexOrThrow(KEY_TEST_ANSWER_2);

				for (int i = 0; i < mCursor.getCount(); i++) 
				{
					questionAndAnswers = new Examination();
					questionAndAnswers.addQuestion("ca_ES", mCursor.getString(columnQUESTIONCAIndex));
					questionAndAnswers.addQuestion("es_ES", mCursor.getString(columnQUESTIONESIndex));
					position = (new Random()).nextInt(2);
					questionAndAnswers.addAnswer(position, mCursor.getString(columnCORRECTANSWERIndex));
					questionAndAnswers.addAnswer((position+1)%3, mCursor.getString(columnANSWER1Index));
					questionAndAnswers.addAnswer((position+2)%3, mCursor.getString(columnANSWER2Index));
					
					correctAnswers[i] = position+1;
					questionsAndAnswers.add(questionAndAnswers);
					mCursor.moveToNext();
				}
			}	
		} 
    	catch (Exception e) 
    	{
			return false;
		}
  
        return true;
    }
    
    /**
     * 
     * Helper class for the database operations creare, update, open and close
     * 
     * @author Joan Fuentes
     *
     */
    public static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context _context) 
        {
            super(_context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _database) 
        {
        	_database.execSQL(DATABASE_CREATE_SESSIONS);
        	_database.execSQL(DATABASE_CREATE_MATERIALS);
        	_database.execSQL(DATABASE_CREATE_SOURCES);
        	_database.execSQL(DATABASE_CREATE_MATERIALSBYSESSION);
        	_database.execSQL(DATABASE_CREATE_SOURCESBYSESSION);
        	_database.execSQL(DATABASE_CREATE_MATERIALTYPE);
            _database.execSQL(DATABASE_CREATE_SOURCETYPE);
            _database.execSQL(DATABASE_CREATE_TEST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _database, int _oldVersion, int _newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + _oldVersion + " to "
                    + _newVersion + ", which will destroy all old data");
            _database.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SESSIONS);
            onCreate(_database);
        }
        
        public static final DatabaseHelper getInstance(Context _context)
        {
        	return new DatabaseHelper(_context);
        }
    }
}
