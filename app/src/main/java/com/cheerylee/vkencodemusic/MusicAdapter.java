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
		return MusicActivity.data.length;
	}

	@Override
	public String[] getItem(int p1) {
		return MusicActivity.data[p1];
	}

	@Override
	public long getItemId(int p1) {
		return p1;
	}

	@Override
	public View getView(final int p1, View p2, ViewGroup p3) {
		View rowView = inflater.inflate(R.layout.item_song, null, false);

		final String[] item = getItem(p1);

		String title = item[1];
		String subtitle = item[2];

		TextView titleView = (TextView) rowView.findViewById(R.id.item_song_name);
		TextView subtitleView = (TextView) rowView.findViewById(R.id.item_song_subtitle);
		ImageButton button = (ImageButton) rowView.findViewById(R.id.item_song_button);

		if (title == null) {
			title = item[0];
		}
		titleView.setText(title);

		if (subtitle != null) {
			subtitleView.setText(subtitle);
		}

		if (item[3] != null) {
			button.setImageResource(R.drawable.done);
			button.setEnabled(false);
		}

		button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String encodedName = MusicActivity.encodedPath + item[0] + ".encoded";
					String mp3Name = MusicActivity.musicPath + item[0] + ".mp3";

					MusicEncoder m_Encoder = new MusicEncoder(encodedName, mp3Name);
					m_Encoder.processBytes();

					MusicActivity.data[p1][3] = "";
					notifyDataSetChanged();
				}
			});

		return rowView;
	}
}
