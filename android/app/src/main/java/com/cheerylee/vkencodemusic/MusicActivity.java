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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class MusicActivity extends Activity {

	private static final String TAG = "vk-encoder";

	private Handler handler = new Handler();
	static boolean hasDatabase = false;

	static String filesDir;
	static String vkMusicPath = "/Android/data/com.vkontakte.android/files/Music/";
	static String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/";
	static String[] storagePath;

	public static ArrayList<Song> data = new ArrayList<Song>();
	private MusicAdapter adapter;

	public static boolean useHumanityFilename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);

		filesDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.cheerylee.vkencodemusic/";

		hasDatabase = copyDatabase();
		storagePath = findStorages(this);

		ListView list = (ListView) findViewById(R.id.activity_music_list);
		adapter = new MusicAdapter(this);
		list.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		useHumanityFilename = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("PREF_NAME_HUMANITY", true);

		if (android.os.Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
				|| checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
				requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
		}

		if (isSDAccessible()) {
			reloadMusicList();
		} else {
			new AlertDialog.Builder(this)
				.setMessage("К сожалению, память недоступна. Приложение работать не может.")
				.setCancelable(false)
				.setPositiveButton("Ясно", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2) {
						finish();
					}
				})
				.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_process_all:
				new ProcessTask(data).execute();
				return true;
			case R.id.action_settings:
				Intent settings_intent = new Intent(MusicActivity.this, PreferencesActivity.class);
				startActivity(settings_intent);
				return true;
			case R.id.action_about:
				Intent about_intent = new Intent(MusicActivity.this, AboutActivity.class);
				startActivity(about_intent);
				return true;
		}
		return false;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		reloadMusicList();
	}

	/**
	 * Ищет все доступные хранилища
	 * @returns массив путей, включая путь к кэшу ВК
	 */
	public static String[] findStorages(Context context) {
		File[] list = {};
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			list = context.getExternalFilesDirs(null);
		} else {
			list = new File[]{context.getExternalFilesDir(null)};
		}

		String[] storages = new String[list.length];
		String appPath = "/Android/data/com.cheerylee.vkencodemusic/files";

		for (int i = 0; i < list.length; i++) {
			storages[i] = list[i].getAbsolutePath();
			storages[i] = storages[i].substring(0, storages[i].length() - appPath.length());
			storages[i] += vkMusicPath;
		}

		return storages;
	}

	/**
	 * Копирует базу данных из VK в приложение
	 * @returns успешно ли
	 */
	private boolean copyDatabase() {
		// TODO: move into external thread

		try {
			String copy[] = {"su", "-c", "cp /data/data/com.vkontakte.android/databases/databaseVerThree.db " + filesDir + "databaseVerThree.db"};
			File myDir = new File(filesDir);
			if (!myDir.exists())
				myDir.mkdirs();

			Process p = Runtime.getRuntime().exec(copy);
			p.waitFor();
			return new File(filesDir, "databaseVerThree.db").exists();
		} catch (Exception ex) {
			Log.e(TAG, "Error while copying db", ex);
			return false;
		}
	}

	private boolean isSDAccessible() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			TextView errorView = (TextView)findViewById(R.id.warn_text);
			errorView.setVisibility(View.VISIBLE);
			errorView.setText(R.string.warn_not_mounted);
			return false;
		} else {
			return true;
		}
	}

	private void reloadMusicList() {
		new Thread(){

			public boolean findSongs(File dir, ArrayList<Song> songData) {
				File[] list = dir.listFiles();

				if (list != null) {
					SQLReader reader = null;

					if (hasDatabase) {
						try {
							reader = new SQLReader(filesDir + "databaseVerThree.db");
						} catch (Exception e) {
							Log.e(TAG, "Couldn't instantiate db reader", e);
							reader = null;
						}
					}

					for (File file : list) {
						Song songItem = new Song();
						String ext = ".encoded";
						String name = file.getName();
						songItem.setFilename(name.substring(0, name.length() - ext.length()));
						songItem.setPath(file.getAbsolutePath());

						if (reader != null) {
							String[] meta = reader.getSong(songItem.getFilename());
							if (meta != null) {
								songItem.setTitle(meta[0]);
								songItem.setArtist(meta[1]);
							} else {
								Log.d(TAG, "Data for " + songItem.getFilename() + " doesnt exist.");
							}
						}

						// if (new File(internalMusicPath + "/" + songItem.getFilename() + ".mp3").exists() || 
						// 	new File(sdMusicPath + "/" + songItem.getFilename() + ".mp3").exists()) {
						// 	songItem.setDecoded(true);
						// }

						songData.add(songItem);
					}
					hideWarning();

					return true;
				} else {
					return false;
				}
			}

			@Override
			public void run() {
				boolean[] success = new boolean[storagePath.length];

				ArrayList<Song> songData = new ArrayList<Song>();

				for (int i = 0; i < storagePath.length; i++) {
					success[i] = findSongs(new File(storagePath[i]), songData);
					Log.d(TAG, storagePath[i]);
				}

				if (songData.size() < 1)
					doSetWarning(getString(R.string.warn_not_found));

				synchronized (data) {
					data = songData;
				}

				doUpdateAdapter();
			}
		}.start();
	}

	private void doSetWarning(final String warn) {
		handler.post(new Runnable(){

				@Override
				public void run() {
					TextView warnNotFoundText = (TextView)findViewById(R.id.warn_text);
					warnNotFoundText.setVisibility(View.VISIBLE);
					warnNotFoundText.setText(warn);
				}
			});
	}

	private void hideWarning() {
		handler.post(new Runnable(){

				@Override
				public void run() {
					TextView warnNotFoundText = (TextView)findViewById(R.id.warn_text);
					warnNotFoundText.setVisibility(View.GONE);
				}
			});
	}

	private void doUpdateAdapter() {
		handler.post(new Runnable(){

				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
	}

	class ProcessTask extends AsyncTask<Void, Void, Void> {
		Song[] data;
		ProgressDialog dialog;

		public ProcessTask(ArrayList<Song> data) {
			this.data = data.toArray(new Song[data.size()]);
			dialog = new ProgressDialog(MusicActivity.this);
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Перекодирование ...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (int j = 0; j < data.length; j++) {
				Song songItem = data[j];

				String filename = songItem.getPath();
				String mp3Name = "";

				if (useHumanityFilename == false || hasDatabase == false)
					mp3Name = MusicActivity.musicPath + songItem.getFilename() + ".mp3";
				if (useHumanityFilename == true && hasDatabase == true)
					mp3Name = MusicActivity.musicPath + songItem.getArtist() + " - " + songItem.getTitle() + ".mp3";

				MusicEncoder m_Encoder = new MusicEncoder(filename, mp3Name);
				m_Encoder.processBytes();

				data[j].setDecoded(true);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			doUpdateAdapter();
		}
	}
}
