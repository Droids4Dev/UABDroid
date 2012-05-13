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

/**
 * 
 * @author Ruben Serrano
 *
 */
public class GeneralInfo 
{
	private String mDescription;
	private ArrayList<String> mQuestions;
	private ArrayList<String> mAnswers;
	private boolean mSelected;
	private boolean mFound;

	public GeneralInfo()
	{
		mDescription = "";
		mQuestions = new ArrayList<String>();
		mAnswers = new ArrayList<String>();
	}

	public void addQuestion(String _question)
	{
		mQuestions.add(_question);
	}
	
	public void addAnswer(String _answer)
	{
		mAnswers.add(_answer);
	}
	
	public String getQuestion(int _index)
	{
		return mQuestions.get(_index);
	}
	
	public String getAnswer(int _index)
	{
		return mAnswers.get(_index);
	}
	
	public int getFAQsSize()
	{
		return mQuestions.size();
	}
	
	public String getDescription() 
	{
		return mDescription;
	}

	public void setDescription(String _description) 
	{
		mDescription = _description;
	}
	
	public boolean isSelected() 
	{
		return mSelected;
	}

	public void setSelected(boolean _selected) 
	{
		mSelected = _selected;
	}

	public boolean isFound() 
	{
		return mFound;
	}

	public void setFound(boolean _found) 
	{
		mFound = _found;
	}
	
	public void clear()
	{
		mDescription = "";
		mQuestions.clear();
		mAnswers.clear();
	}
}
