/*
 * Copyright (c) 2018 Alexander "CheeryLee" Pluzhnikov
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.cheerylee.vkencodemusic.MusicActivity;
import com.cheerylee.vkencodemusic.MusicEncoder;
import com.cheerylee.vkencodemusic.R;

public class MusicAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;

	public MusicAdapter(Context c) {
		this.context = c;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return MusicActivity.data.size();
	}

	@Override
	public Song getItem(int p1) {
		return MusicActivity.data.get(p1);
	}

	@Override
	public long getItemId(int p1) {
		return p1;
	}

	@Override
	public View getView(final int p1, View p2, ViewGroup p3) {
		View rowView = inflater.inflate(R.layout.item_song, null, false);

		final Song songItem = getItem(p1);

		String title = songItem.getTitle();
		String artist = songItem.getArtist();

		TextView titleView = (TextView) rowView.findViewById(R.id.item_song_name);
		TextView artistView = (TextView) rowView.findViewById(R.id.item_song_artist);
		ImageButton button = (ImageButton) rowView.findViewById(R.id.item_song_button);

		if (title == null) {
			title = songItem.getFilename();
		}
		titleView.setText(title);

		if (artist != null) {
			artistView.setText(artist);
		}

		if (songItem.getSongDecoded() == true) {
			button.setImageResource(R.drawable.done);
			button.setEnabled(false);
		}

		button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String encodedName = songItem.getPath();
					String mp3Name = "";
				
					if (MusicActivity.useHumanityFilename == false || MusicActivity.hasDatabase == false)
						mp3Name = MusicActivity.musicPath + songItem.getFilename() + ".mp3";
					if (MusicActivity.useHumanityFilename == true && MusicActivity.hasDatabase == true) {
						if (songItem.getArtist() == null || songItem.getTitle() == null)
							mp3Name = MusicActivity.musicPath + songItem.getFilename() + ".mp3";
						else
							mp3Name = MusicActivity.musicPath + songItem.getArtist() + " - " + songItem.getTitle() + ".mp3";
					}

					MusicEncoder m_Encoder = new MusicEncoder(encodedName, mp3Name);
					m_Encoder.processBytes();

					MusicActivity.data.get(p1).setDecoded(true);
					notifyDataSetChanged();
				}
			});

		return rowView;
	}
}
