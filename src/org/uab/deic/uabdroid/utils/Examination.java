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
 * @author Joan Fuentes
 *
 */

public class Examination 
{	
	private int mIndex;
	private HashMap<String, String> mQuestionsMap;
	private HashMap<Integer, String> mAnswersMap;
	
	public Examination()
	{
		mQuestionsMap = new HashMap<String, String>();
		mAnswersMap = new HashMap<Integer, String>();
		mIndex = 0;
	}
	
	public void addQuestion(String _locale, String _name)
	{
		if (mQuestionsMap.containsKey(_locale))
		{
			mQuestionsMap.remove(_locale);
		}
		mQuestionsMap.put(_locale, _name);
	}
	
	public String getQuestion(String _locale)
	{
		return mQuestionsMap.get(_locale);
	}
	
	public void addAnswer(int _index, String _answer)
	{
		if (mAnswersMap.containsKey(_index))
		{
			mAnswersMap.remove(_index);
		}
		mAnswersMap.put(_index, _answer);
	}
	
	public String getAnswer(int _index)
	{
		return mAnswersMap.get(_index);
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
		mQuestionsMap.clear();
		mAnswersMap.clear();
		mIndex = 0;
	}
}
