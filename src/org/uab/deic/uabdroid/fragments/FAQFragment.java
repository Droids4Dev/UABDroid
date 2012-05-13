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

import java.util.ArrayList;

import org.uab.deic.uabdroid.GeneralInfoActivity;
import org.uab.deic.uabdroid.adapters.ExpandableListFragmentAdapter;
import org.uab.deic.uabdroid.utils.GeneralInfo;

import android.os.Bundle;
import android.support.v4.app.ExpandableListFragment;

/**
 * 
 * @author Joan Fuentes
 *
 */

public class FAQFragment extends ExpandableListFragment 
{
    @Override
    public void onActivityCreated(Bundle _savedInstanceState) 
    {
    	super.onActivityCreated(_savedInstanceState);
        
    	GeneralInfoActivity parentActivity = (GeneralInfoActivity) getActivity();
        ExpandableListFragmentAdapter expandableListFragmentAdapter = new ExpandableListFragmentAdapter(parentActivity);
    	GeneralInfo generalInfo = parentActivity.getGeneralinfo();
    	
    	int numOfQuestions = generalInfo.getFAQsSize();
    	
    	for(int i=0;i<numOfQuestions;i++)
    	{
    		ArrayList<String> models = new ArrayList<String>();
    		models.add(generalInfo.getAnswer(i));
    		expandableListFragmentAdapter.AddGroup(generalInfo.getQuestion(i), models);
    	}
   
        setListAdapter(expandableListFragmentAdapter); 	
        
        // Patch to avoid "flicker" effect.
        getExpandableListView().setCacheColorHint(0);
    }
}

