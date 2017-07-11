/*
 * Copyright (c) 2017 Alexander "CheeryLee" Pluzhnikov
 * 
 * This file is part of VKEncodeMusic.
 * 
 * VKEncodeMusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VKEncodeMusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VKEncodeMusic.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cheerylee.vkencodemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.os.Environment;

public class SongWidget extends ArrayAdapter<String> {
	private Context context;
	private String values[];
	
	public SongWidget(Context _context, String _values[]) {
		super(_context, R.layout.song_widget, _values);
		context = _context;
		values = _values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.song_widget, parent, false);
		
		final TextView filename_text = (TextView)rowView.findViewById(R.id.songName);
		final ImageButton button = (ImageButton)rowView.findViewById(R.id.downloadButton);
		
		filename_text.setText(values[position]);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String headerPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.vkontakte.android/files/Music/";
                String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Music/";
                
				String filename = headerPath + filename_text.getText().toString() + ".encoded";
				String mp3Name = musicPath + filename_text.getText().toString() + ".mp3";
				
				MusicEncoder m_Encoder = new MusicEncoder(filename, mp3Name);
				m_Encoder.processBytes();
				
				button.setImageResource(R.drawable.done);
			}
		});
		
		return rowView;
	}
}
