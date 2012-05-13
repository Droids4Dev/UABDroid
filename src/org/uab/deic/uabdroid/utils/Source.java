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

import java.util.HashMap;

/**
 * 
 * @author Ruben Serrano
 *
 */

public class Source 
{
	public static final int BOOK_TYPE = 0;
	public static final int GUIDE_TYPE = 1; 
	
	private HashMap<String, String> mLocaleName;
	private HashMap<String, String> mLocaleDescription;
	private String mURL;
	private int mType;
	private int mIndex;
	
	public Source()
	{
		mLocaleName = new HashMap<String, String>();
		mLocaleDescription = new HashMap<String, String>();
		mURL = "";
		mType = -1;
		mIndex = 0;
	}

	public String getUrl() 
	{
		return mURL;
	}

	public void setUrl(String _url) 
	{
		mURL = _url;
	}
	
	public void addName(String _locale, String _name)
	{
		if (mLocaleName.containsKey(_locale))
		{
			mLocaleName.remove(_locale);
		}
		mLocaleName.put(_locale, _name);
	}
	
	public String getName(String _locale)
	{
		return mLocaleName.get(_locale);
	}
	
	public void addDescription(String _locale, String _description)
	{
		if (mLocaleDescription.containsKey(_locale))
		{
			mLocaleDescription.remove(_locale);
		}
		mLocaleDescription.put(_locale, _description);
	}
	
	public String getDescription(String _locale)
	{
		return mLocaleDescription.get(_locale);
	}

	public int getType() 
	{
		return mType;
	}

	public void setType(int _type) 
	{
		mType = _type;
	}

	public int getIndex() 
	{
		return mIndex;
	}

	public void setIndex(int _index) 
	{
		mIndex = _index;
	}
	
	public void clear()
	{
		mLocaleName.clear();
		mLocaleDescription.clear();
		mURL = "";
		mType = -1;
		mIndex = 0;
	}
}
