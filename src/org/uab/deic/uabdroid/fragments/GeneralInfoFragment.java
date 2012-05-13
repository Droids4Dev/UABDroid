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

import org.uab.deic.uabdroid.GeneralInfoActivity;
import org.uab.deic.uabdroid.R;
import org.uab.deic.uabdroid.utils.GeneralInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * 
 * @author Joan Fuentes
 * @author Rubén Serrano
 *
 */


public class GeneralInfoFragment extends Fragment 
{	
	@Override 
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) 
	{
		View v = _inflater.inflate(R.layout.fragment_info, _container, false);
		return v;
	}
	
    @Override
    public void onActivityCreated(Bundle _savedInstanceState) 
    {
    	super.onActivityCreated(_savedInstanceState);
        
    	GeneralInfoActivity parentActivity = (GeneralInfoActivity) getActivity();
    	GeneralInfo generalInfo = parentActivity.getGeneralinfo();
    	
    	TextView textviewGeneralInfo = (TextView) parentActivity.findViewById(R.id.generalinfo_text);
		textviewGeneralInfo.setText(Html.fromHtml(generalInfo.getDescription()), BufferType.SPANNABLE);
		Linkify.addLinks(textviewGeneralInfo, Linkify.ALL);	
    }
}
