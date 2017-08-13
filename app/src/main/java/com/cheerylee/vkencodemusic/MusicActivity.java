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

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MusicActivity extends Activity {

	private static final String TAG = "vk-encoder";

	private Handler handler = new Handler();
	static boolean hasDatabase = false;

	static String filesDir;
	static String encodedPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.vkontakte.android/files/Music/";
	static String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/";

	/* Собственно, данные для отображения
	 * По-хорошему, надо бы заменить на класс
	 * Каждый массив содержит:
	 * [0] - путь
	 * [1] - заголовок или null
	 * [2] - подзаголовок или null
	 * [3] - флаг: null - песня не декодирована, иначе декодирована
	 */
	public static String[][] data = {};
	private MusicAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);

		filesDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/";//getFilesDir().getAbsolutePath();

		hasDatabase = copyDatabase();

		ListView list = (ListView) findViewById(R.id.activity_music_list);
		adapter = new MusicAdapter(this);
		list.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

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
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_process_all:
				new ProcessTask(data).execute();
				return true;
			case R.id.action_about:
				Intent about_intent = new Intent(MusicActivity.this, AboutActivity.class);
				startActivity(about_intent);
				return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		reloadMusicList();
	}

	/**
	 * Копирует базу данных из VK в приложение
	 * @returns успешно ли
	 */
	private boolean copyDatabase() {
		// TODO: move into external thread

		try {
			String copy[] = {"su", "-c", "cp /data/data/com.vkontakte.android/databases/databaseVerThree.db " + filesDir + "/databaseVerThree.db"};

			Process p = Runtime.getRuntime().exec(copy);
			p.waitFor();
			return true;
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

			@Override
			public void run() {
				boolean success = false;

				File dir = new File(encodedPath);

				if (dir.exists()) {
					File[] list = dir.listFiles();

					if (list != null) {

						SQLReader reader = null;

						if (hasDatabase) {
							try {
								reader = new SQLReader(filesDir + "/databaseVerThree.db");
							} catch (Exception e) {
								Log.e(TAG, "Couldn't instantiate db reader", e);
								reader = null;
							}
						}

						ArrayList<String[]> newData = new ArrayList<String[]>();

						for (File file : list) {
							String[] item = new String[4];
							String name = file.getName();
							String ext = ".encoded";
							item[0] = name.substring(0, name.length() - ext.length());

							if (reader != null) {
								String[] meta = reader.getSong(item[0]);
								if (meta != null) {
									item[1] = meta[0];
									item[2] = meta[1];
								} else {
									Log.d(TAG, "Data for " + item[0] + " doesnt exist.");
								}
							}

							if (new File(musicPath + "/" + item[0] + ".mp3").exists()) {
								item[3] = "";
							}

							newData.add(item);
						}

						String[][] newDataArray = newData.toArray(new String[newData.size()][]);

						synchronized (data) {
							data = newDataArray;
						}
						doUpdateAdapter();
						hideWarning();
						
						success = true;
					}
					
					if (list == null || list.length < 1)
						doSetWarning(getString(R.string.warn_not_found));
				}

				if (!success) {
					doSetWarning("Error while reading list");
				}
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
		String[][] data;
		ProgressDialog dialog;

		public ProcessTask(String[][] data) {
			this.data = data;
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
			for (int i = 0; i < data.length; i++) {
				String[] item = data[i];

				String filename = MusicActivity.encodedPath + item[0] + ".encoded";
				String mp3Name;
				
				if (hasDatabase == false)
					mp3Name = MusicActivity.musicPath + item[0] + ".mp3";
				else
					mp3Name = MusicActivity.musicPath + item[2] + " - " + item[1] + ".mp3";
				
				MusicEncoder m_Encoder = new MusicEncoder(filename, mp3Name);
				m_Encoder.processBytes();

				data[i][3] = "";
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
