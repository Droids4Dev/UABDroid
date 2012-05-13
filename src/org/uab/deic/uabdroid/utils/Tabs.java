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

public class Tabs 
{
	private HashMap<Integer, TabContent> mHashMap;

	public Tabs()
	{
		mHashMap = new HashMap<Integer, TabContent>();
	}
	
	public void addTab(int _order, String _tabName, String _tabText, Class<?> _fragmentClass)
	{
		TabContent tabContent = new TabContent(_tabName, _tabText, _fragmentClass);
		mHashMap.put(_order, tabContent);
	}
	
	public int getSize()
	{
		return mHashMap.size();
	}
	
	public String getTabName(int _order)
	{
		return mHashMap.get(_order).getTabName();
	}
	
	public String getTabText(int _order)
	{
		return mHashMap.get(_order).getTabText();
	}
	
	public Class<?> getFragmentClass(int _order)
	{
		return mHashMap.get(_order).getFragmentClass();
	}
	
	private class TabContent
	{
		private String mTabName;
		private String mTabText;
		private Class<?> mFragmentClass;
		
		public TabContent(String _tabName, String _tabText, Class<?> _fragmentClass)
		{
			mTabName = _tabName;
			mTabText = _tabText;
			mFragmentClass = _fragmentClass;
		}
		
		public String getTabName()
		{
			return mTabName;
		}
		
		public String getTabText()
		{
			return mTabText;
		}
		
		public Class<?> getFragmentClass()
		{
			return mFragmentClass;
		}
	}
}
