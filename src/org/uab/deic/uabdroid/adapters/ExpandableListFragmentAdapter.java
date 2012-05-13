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
import java.util.HashMap;

import org.uab.deic.uabdroid.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Joan Fuentes
 *
 */
public class ExpandableListFragmentAdapter extends BaseExpandableListAdapter
{
    protected final HashMap<String, ArrayList<String>> mData = new HashMap<String, ArrayList<String>>();
    protected final HashMap<Integer, String> mLookUp = new HashMap<Integer, String>();
    protected final Context mContext;
 
    public ExpandableListFragmentAdapter(final Context _context)
    {
    	mContext = _context;
    }
 
    public boolean AddGroup(final String _groupName, final ArrayList<String> _list)
    {
        final ArrayList<String> previousList = mData.put(_groupName, _list);
  
        if (previousList != null)
            return false;
  
        mLookUp.put(mData.size() - 1, _groupName);
  
        notifyDataSetChanged();
        return true;
    }
 
    @Override
    public Object getChild(int _groupPosition, int _childPosition) 
    {
        if (mLookUp.containsKey(_groupPosition))
        {
            final String key = mLookUp.get(_groupPosition);
            final ArrayList<String> data = mData.get(key);
   
            return data.get(_childPosition);
        }
  
        return null;
    }

    @Override
    public long getChildId(int _groupPosition, int _childPosition) 
    {  
        return 0;
    }

    @Override
    public View getChildView(int _groupPosition, int _childPosition, boolean _isLastChild, View _convertView, ViewGroup _parentView) 
    {
        LinearLayout linearLayout = new LinearLayout(mContext);
  
        final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
  
        TextView textView = new TextView(mContext);
  
        // Indent
        //final String str = "\t\t\t" + (String)getChild(groupPos, childPos);
        final String text = (String)getChild(_groupPosition, _childPosition);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        textView.setTextColor(mContext.getResources().getColor(R.color.FAQFragment_Answer_text));
        textView.setPadding(20, 0, 20, 0);
        textView.setLayoutParams(params);
        textView.setText(text);
        linearLayout.addView(textView);
  
        return linearLayout;
    }

    @Override
    public int getChildrenCount(int _groupPosition) 
    {
        if (mLookUp.containsKey(_groupPosition))
            return mData.get(mLookUp.get(_groupPosition)).size();

        return 0;
    }

    @Override
    public Object getGroup(int _groupPosition) 
    {
        if (mLookUp.containsKey(_groupPosition))
        {
            return mData.get(mLookUp.get(_groupPosition));
        }
        return null;
    }

    @Override
    public int getGroupCount() 
    {
        return mData.size();
    }

    @Override
    public long getGroupId(int _groupPosition) 
    {
        return 0;
    }

    @Override
    public View getGroupView(int _groupPosition, boolean _isExpanded, View _convertView, ViewGroup _parentView) 
    {
        LinearLayout linearLayout = new LinearLayout(mContext);
  
        final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
  
        TextView textView = new TextView(mContext);
  
        // Push the group name slightly to the right for drop down icon
        //final String str = "\t\t" + mLookUp.get(groupPos);
        final String text = mLookUp.get(_groupPosition);
  
        linearLayout.setOrientation(LinearLayout.VERTICAL);
  
        linearLayout.setPadding(0, 10, 0, 10);
        textView.setPadding(60, 0, 0, 0);
        
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextSize(18.0f);
        linearLayout.addView(textView);
  
        return linearLayout;
    }

    @Override
    public boolean hasStableIds() 
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int _groupPosition, int _childPosition) 
    {
        return false;
    }
}