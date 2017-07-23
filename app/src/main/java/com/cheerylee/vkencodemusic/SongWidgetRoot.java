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

public class SongWidgetRoot extends ArrayAdapter<String> {
	private Context context;
	private String values[];
	private String fileNames[];
	private String artistName[];
	private String songName[];
	
	SQLReader r;
	
	static int donePositions[];
	
	public SongWidgetRoot(Context _context, String _values[], String _fileNames[]) {
		super(_context, R.layout.song_widget_root, _values);
		context = _context;
		values = _values;
		fileNames = _fileNames;
		donePositions = new int[values.length];
		
		artistName = new String[values.length];
		songName = new String[values.length];
		
		r = new SQLReader();
		for (int i = 0; i < values.length; i++) {
			if (r.getSong(values[i]) == 0) {
				artistName[i] = r.getArtistName();
				songName[i] = r.getSongName();
			}
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		final String filename = fileNames[position];
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.song_widget_root, parent, false);
		
		final TextView artistName_text = (TextView)rowView.findViewById(R.id.artistName_root);
		final TextView songName_text = (TextView)rowView.findViewById(R.id.songName_root);
		final ImageButton button = (ImageButton)rowView.findViewById(R.id.downloadButton_root);

		artistName_text.setText(artistName[position]);
		songName_text.setText(songName[position]);
		
		if (donePositions[position] == 1) {
			button.setImageResource(R.drawable.done);
			button.setEnabled(false);
		}
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String encodedName = MusicActivity.encodedPath + filename + ".encoded";
				String mp3Name = MusicActivity.musicPath + filename + ".mp3";
				
				MusicEncoder m_Encoder = new MusicEncoder(encodedName, mp3Name);
				m_Encoder.processBytes();
				
				donePositions[pos] = 1;
				button.setImageResource(R.drawable.done);
				button.setEnabled(false);
			}
		});
		
		return rowView;
	}
}
