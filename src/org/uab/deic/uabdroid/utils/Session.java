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
package org.uab.deic.uabdroid.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Ruben Serrano
 *
 */

public class Session 
{
	private int mIndex;
	private HashMap<String, String> mLocaleTitles;
	private String mRoom;
	private String mDate;
	private String mHour;
	private HashMap<String, String> mLocaleContents;
	private ArrayList<Integer> mMaterials;
	private ArrayList<Integer> mSources;
	
	public Session()
	{
		mIndex = 0;
		mLocaleTitles = new HashMap<String, String>();
		mRoom = "";
		mDate = "";
		mHour = "";
		mLocaleContents = new HashMap<String, String>();
		mMaterials = new ArrayList<Integer>();
		mSources = new ArrayList<Integer>();
	}
	
	public int getIndex() 
	{
		return mIndex;
	}

	public void setIndex(int _index) 
	{
		mIndex = _index;
	}

	public void addTitle(String _locale, String _title)
	{
		if (mLocaleTitles.containsKey(_locale))
		{
			mLocaleTitles.remove(_locale);
		}
		mLocaleTitles.put(_locale, _title);
	}
	
	public String getTitle(String _locale)
	{
		return mLocaleTitles.get(_locale);
	}

	public String getRoom() 
	{
		return mRoom;
	}

	public void setRoom(String _room) 
	{
		mRoom = _room;
	}
	
	public void setDay(String _date)
	{
		mDate = _date;	
	}
	
	public void setHour(String _hour)
	{
		mHour = _hour;
	}
	
	public String getDay()
	{
		return mDate;
	}

	public String getHour()
	{		
		return mHour;
	}
	
	public void addContent(String _locale, String _content)
	{
		if (mLocaleContents.containsKey(_locale))
		{
			mLocaleContents.remove(_locale);
		}
		mLocaleContents.put(_locale, _content);
	}
	
	public String getContent(String _locale)
	{
		return mLocaleContents.get(_locale);
	}
	
	public void addMaterial(Integer _material)
	{
		mMaterials.add(_material);
	}
	
	public Integer getMaterial(int _index)
	{
		return mMaterials.get(_index);
	}
	
	public int getMaterialsSize()
	{
		return mMaterials.size();
	}
	
	public void addSource(Integer _source)
	{
		mSources.add(_source);
	}
	
	public Integer getSource(int _index)
	{
		return mSources.get(_index);
	}
	
	public int getSourcesSize()
	{
		return mSources.size();
	}
	
	public void clear()
	{
		mIndex = 0;
		mLocaleTitles.clear();
		mRoom = "";
		mDate = "";
		mHour = "";
		mLocaleContents.clear();
		mMaterials.clear();
		mSources.clear();
	}
}