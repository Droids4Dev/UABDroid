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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.uab.deic.uabdroid.adapters.DatabaseAdapter;
import org.uab.deic.uabdroid.utils.XMLParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * This class represents the application's home screen
 * 
 * Note: As this applications was thought to be used in an Android development initiation course,
 * the commentaries found in the code are intended to help the students who aren't supposed to
 * have any previous knowledge of the platform, so the advanced developer will find most of them
 * trivial
 * 
 * @author Joan Fuentes
 * @author Rubén Serrano
 * 
 */

public class MainActivity extends BaseActivity 
{	
	private static final String FIRST_TIME_PREFERENCE = "firstTime";
	
	private DatabaseAdapter mDatabaseAdapter = null;
	private ProgressDialog mProgressDialog;
	
	/**
	 * Method called when the activity needs to be created for the first time, or if 
	 * it's called again after being destroyed 
	 */
    @Override
    public void onCreate(Bundle _savedInstanceState) 
    {
        super.onCreate(_savedInstanceState);
        
        //This method inflates the screen's layout. The layout in this case is defined in the
        // $PROJECT/res/layout directory, in a file named main.xml. This file contains the 
        // hierarchy of classes that 
        setContentView(R.layout.main);

        Button buttonInformation = (Button)findViewById(R.id.buttonInformation);
        buttonInformation.setOnClickListener(new OnClickListener() 
		{
            public void onClick(View view) 
            {
                Intent intent = new Intent(MainActivity.this, GeneralInfoActivity.class);
                startActivity(intent);                
            }
        });
        
        Button buttonSessions = (Button)findViewById(R.id.buttonSessions);
        buttonSessions.setOnClickListener(new OnClickListener() 
		{
            public void onClick(View view) 
            {
                Intent intent = new Intent(MainActivity.this, SessionListActivity.class);
                startActivity(intent);                
            }
        });
        
        Button buttonExamination = (Button)findViewById(R.id.buttonExamination);
        buttonExamination.setOnClickListener(new OnClickListener() 
		{
            public void onClick(View view) 
            {
                Intent intent = new Intent(MainActivity.this, ExaminationActivity.class);
                startActivity(intent);                
            }
        }); 
		
        mDatabaseAdapter = new DatabaseAdapter(this);
        mDatabaseAdapter.open();
        
        SharedPreferences sharedPreferences = getSharedPreferences(AppPreferencesActivity.INIT_PREFS, Activity.MODE_PRIVATE);
               
        if (sharedPreferences.getBoolean(FIRST_TIME_PREFERENCE, true))
        {
        	//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        	mProgressDialog = new ProgressDialog(this);
        	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	mProgressDialog.setProgress(0);
        	mProgressDialog.setCancelable(false);
        	mProgressDialog.setTitle(R.string.init_dialog_title);
        	mProgressDialog.show();
        	new InitialLoadAsyncTask().execute();
        }
    }

    @Override
    public void onDestroy()
    {
    	mDatabaseAdapter.close();
    	super.onDestroy();
    }
    
    
    // Tasca as√≠ncrona per crear i omplir la BBDD la primera vegada que obrim la app
    private class InitialLoadAsyncTask extends AsyncTask<Void, Integer, Boolean>
    {
    	private Context mContext;

		@Override
		protected Boolean doInBackground(Void... params) 
		{
			Boolean result;
			// Inicialitzacio d'atributs de la classe
			mContext = getBaseContext();
			
			// Cridem als parsers dels fitxers que tenen el contingut de la 
			// BBDD a crear
			result = parse();
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			super.onProgressUpdate(values);
			mProgressDialog.setProgress(values[0]);
		}


		@Override
		protected void onPostExecute(Boolean result) 
		{
			mProgressDialog.dismiss();
			
			Toast t = Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
			
			if (result)
			{
				t.setText(getResources().getString(R.string.main_initial_load_ok));
				SharedPreferences sharedPreferences = getSharedPreferences(AppPreferencesActivity.INIT_PREFS, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
		        editor.putBoolean(FIRST_TIME_PREFERENCE, false);
		        editor.commit();
			}
			else
			{
				t.setText(getResources().getString(R.string.main_initial_load_nok));
			}
			t.show();
			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			super.onPostExecute(result);
		}
		
		private boolean parse()
		{
			AssetManager assetManager = mContext.getAssets();;
			InputStream inputStream;
			Boolean result = true;
			String fileName = "";
			
			try 
			{
				// update.xml
				fileName = XMLParser.UPDATE_FILE;
				inputStream = assetManager.open(fileName);
				result = result && XMLParser.parseUpdateXML(inputStream, mContext, true);
				inputStream.close();
				publishProgress(15);
				
				// info.xml
				fileName = XMLParser.INFO_FILE;
				inputStream = assetManager.open(fileName);
				FileOutputStream fos = openFileOutput(XMLParser.INFO_FILE, Context.MODE_PRIVATE);
				
				byte[] b = new byte[512];
				while (inputStream.read(b)!=-1)
				{
					fos.write(b);
				}
				fos.close();
				inputStream.close();
				publishProgress(30);
				
				// materials.xml
				fileName = XMLParser.MATERIALS_FILE;
				inputStream = assetManager.open(fileName);
				result = result && XMLParser.parseMaterialsXML(inputStream, mDatabaseAdapter, true);
				inputStream.close();
				publishProgress(45);

				// fonts.xml
				fileName = XMLParser.FONTS_FILE;
				inputStream = assetManager.open(fileName);
				result = result && XMLParser.parseFontsXML(inputStream, mDatabaseAdapter, true);
				inputStream.close();
				publishProgress(60);
				
				// sessions.xml
				fileName = XMLParser.SESSIONS_FILE;
				inputStream = assetManager.open(fileName);
				result = result && XMLParser.parseSessionsXML(inputStream, mDatabaseAdapter, true);
				inputStream.close();
				publishProgress(85);
				
				// test.xml
				fileName = XMLParser.TEST_FILE;
				inputStream = assetManager.open(fileName);
				result = result && XMLParser.parseTestXML(inputStream, mDatabaseAdapter, true);
				inputStream.close();
				publishProgress(100);
			}
			catch (IOException e)
			{
				Log.v("Exception", "Error opening asset file: " + fileName);
				return false;
			}
			return result;
		}	
    }

}