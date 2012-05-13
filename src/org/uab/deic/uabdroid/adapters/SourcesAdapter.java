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

import java.util.Locale;

import org.uab.deic.uabdroid.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Joan Fuentes
 *
 */

public class SourcesAdapter extends SimpleCursorAdapter
{
    private int mLayoutID;

    public SourcesAdapter(Context _context, int _layoutID, Cursor _cursor, String[] _columnsFrom, int[] _viewsTo) 
    {
		super(_context, _layoutID, _cursor, _columnsFrom, _viewsTo, 0);
		mLayoutID = _layoutID;
	}

	@Override
	public void bindView(View _view, Context _context, Cursor _cursor) 
	{
		fillView(_view, _cursor);
	}

	@Override
	public View newView(Context _context, Cursor _cursor, ViewGroup _parentView) 
	{
		LayoutInflater inflater = LayoutInflater.from(_context);
        View itemView = inflater.inflate(mLayoutID, _parentView, false);
		fillView(itemView, _cursor);
        
        return itemView;
    }
	
	private void fillView(View _view, Cursor _cursor)
	{
		int columnNameIndex = 0;
		int columnDescriptionIndex = 0;
		
        if(Locale.getDefault().toString().compareTo("ca_ES")==0)
        {
        	columnNameIndex = _cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_SOURCES_NAME_CA);
        	columnDescriptionIndex = _cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_SOURCES_DESCRIPTION_CA);
        }
        else
        {
        	columnNameIndex = _cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_SOURCES_NAME_ES);
        	columnDescriptionIndex = _cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_SOURCES_DESCRIPTION_ES);
        }
        
        int columnTypeIndex = _cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_SOURCES_TYPE);
        int columnUrlIndex = _cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_SOURCES_URL);

        TextView textViewName = (TextView) _view.findViewById(R.id.txtName);
        TextView textViewUrl = (TextView) _view.findViewById(R.id.txtUrl);
        ImageView imageViewIcon = (ImageView) _view.findViewById(R.id.imageIcon);
        TextView textViewDescription = (TextView) _view.findViewById(R.id.txtDescription);
        
        if (textViewName != null) 
        {
        	textViewName.setText(_cursor.getString(columnNameIndex));
        }
        if (textViewUrl != null) 
        {
        	textViewUrl.setText(_cursor.getString(columnUrlIndex));
        }
        if (textViewDescription != null)
        {
        	textViewDescription.setText(_cursor.getString(columnDescriptionIndex));
        }
        
        switch ((int) _cursor.getLong(columnTypeIndex)) 
        {
			case 0:
				imageViewIcon.setImageResource(R.drawable.googleblogger);			
				break;
			default:
				imageViewIcon.setImageResource(R.drawable.googlenotebook);
				break;
		}
	}
}