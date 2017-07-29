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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;

public class MusicActivity extends Activity {

	private Handler handler = new Handler();

	static String encodedPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.vkontakte.android/files/Music/";
	static String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/";
	private int suCode;
	private String fileNames[];
	SongWidgetRoot w_root;
	SongWidgetNonRoot w_nonroot;
	File f_path;
	File[] files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Разрешение на чтение памяти
		if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
			!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//			requestPermissions(this, 
//			new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);
		}

		suCode = requestRootAccess();

		if (checkSD() == 1) {
			return;
		} else {
			getEncodedFiles();
		}
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_process_all:
				processAllFiles(files);
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

		if (files == null || files.length == 0)
			menu.getItem(0).setVisible(false);

		return true;
	}

	/*	@Override
	 public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
	 if (requestCode == 10001) {
	 for (int i = 0; i < 2; i++) {
	 if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
	 Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
	 files = f_path.listFiles(); // обновить права
	 getEncodedFiles();
	 } else {
	 Toast.makeText(this, "Разрешения не получены", Toast.LENGTH_LONG).show();
	 TextView warn_not_mounted_text = (TextView)findViewById(R.id.warn_text);
	 warn_not_mounted_text.setText(R.string.warn_not_mounted);
	 }
	 }
	 } else {
	 super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	 }
	 }	*/

	//
	// ЕСЛИ РАЗРЕШЕНИЕ НА ПАМЯТЬ УЖЕ БЫЛО ПОЛУЧЕНО
	//
	private boolean isPermissionGranted(String permission) {
//		int permissionCheck = ActivityCompat.checkSelfPermission(this, permission);
		return true;//permissionCheck == PackageManager.PERMISSION_GRANTED;
	}

	//
	// ЗАПРОС РУТ-ПРАВ ДЛЯ КОПИРОВАНИЯ БАЗЫ ДАННЫХ
	//
	private int requestRootAccess() {
		Process p;
		try {
			String copy[] = {"su", "-c", "cp /data/data/com.vkontakte.android/databases/databaseVerThree.db /sdcard/Android/data/com.cheerylee.vkencodemusic/databaseVerThree.db"};
			File myDir = new File("/sdcard/Android/data/com.cheerylee.vkencodemusic/");
			if (!myDir.exists())
				myDir.mkdirs();
			p = Runtime.getRuntime().exec(copy);

			try {
				p.waitFor();
				return 1;
			} catch (InterruptedException e) {
				return 255;
			}

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			return 255;
		}
	}

	//
	// СОЗДАНИЕ ЭЛЕМЕНТА СПИСКА
	//
	private void createSongWidget(String fileNames[]) {
		ListView lvMain = (ListView) findViewById(R.id.lvMain);
		w_nonroot = new SongWidgetNonRoot(this, fileNames);
		lvMain.setAdapter(w_nonroot);
	}

	private void createSongWidget(File files[], String fileNames[]) {
		String filePaths[] = new String[files.length];
		for (int i = 0; i < files.length; i++)
			filePaths[i] = files[i].getAbsolutePath();

		ListView lvMain = (ListView) findViewById(R.id.lvMain);
		w_root = new SongWidgetRoot(this, filePaths, fileNames);
		lvMain.setAdapter(w_root);
	}

	//
	// ПРОВЕРКА НАЛИЧИЯ КАРТЫ ПАМЯТИ ИЛИ ДОСТУПА К ПАМЯТИ
	//
	private int checkSD() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			TextView warn_not_mounted_text = (TextView)findViewById(R.id.warn_text);
			warn_not_mounted_text.setText(R.string.warn_not_mounted);
			return 1;
		}
		return 0;
	}

	//
	// ПОИСК КЭША
	//
	private void getEncodedFiles() {

		new Thread(){

			@Override
			public void run() {
				f_path = new File(encodedPath);

				if (f_path.exists()) {
					files = f_path.listFiles();

					if (files != null)
						fileNames = new String[files.length];

					for (int i = 0; i < files.length; i++) {
						fileNames[i] = files[i].getName().substring(0, files[i].getName().length() - 8);
					}

					if (files.length > 0) {
						doCreateSongWidget();
					} else {
						doSetWarning();
					}
				} else {
					doSetWarning();
				}
			}
		}.start();
	}

	private void doSetWarning() {
		handler.post(new Runnable(){

				@Override
				public void run() {
					TextView warnNotFoundText = (TextView)findViewById(R.id.warn_text);
					warnNotFoundText.setVisibility(View.GONE);
					warnNotFoundText.setText(R.string.warn_not_found);
				}
			});
	}

	private void doCreateSongWidget() {
		handler.post(new Runnable(){

				@Override
				public void run() {
					if (suCode == 1)
						createSongWidget(files, fileNames);
					else
						createSongWidget(fileNames);
				}
			});
	}

	// 
	// ПЕРЕКОДИРОВАТЬ ВСЕ ФАЙЛЫ СРАЗУ
	//
	private void processAllFiles(File[] files) {
		if (files != null) {
			ProcessTask task;
			if (suCode == 1)
				task = new ProcessTask(this, w_root, files, fileNames);
			else
				task = new ProcessTask(this, w_nonroot, files, fileNames);
			task.execute();
		}
	}

	class ProcessTask extends AsyncTask<Void, Void, Void> {

		File files[];
		String encodedPath, musicPath;
		String fileNames[];
		ProgressDialog dialog;
		SongWidgetNonRoot w_nonroot;
		SongWidgetRoot w_root;
		Context context;
		int suCode;

		// Конструктор с виджетом без рута 
		ProcessTask(Context _context, SongWidgetNonRoot _w_nonroot, File _files[], String _fileNames[]) {
			context = _context;
			w_nonroot = _w_nonroot;
			files = _files;
			fileNames = _fileNames;
			suCode = 255;
			dialog = new ProgressDialog(context);
		}
		// Конструктор с виджетом для рута
		ProcessTask(Context _context, SongWidgetRoot _w_root, File _files[], String _fileNames[]) {
			context = _context;
			w_root = _w_root;
			files = _files;
			fileNames = _fileNames;
			suCode = 1;
			dialog = new ProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Перекодирование ...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < files.length; i++) {
				if (suCode == 1) {
					if (SongWidgetRoot.donePositions[i] != 1) {
						String filename = MusicActivity.encodedPath + fileNames[i] + ".encoded";
						String mp3Name = MusicActivity.musicPath + fileNames[i] + ".mp3";
						MusicEncoder m_Encoder = new MusicEncoder(filename, mp3Name);
						m_Encoder.processBytes();
						SongWidgetRoot.donePositions[i] = 1;
					}
				} else {
					if (SongWidgetNonRoot.donePositions[i] != 1) {
						String filename = MusicActivity.encodedPath + fileNames[i] + ".encoded";
						String mp3Name = MusicActivity.musicPath + fileNames[i] + ".mp3";
						MusicEncoder m_Encoder = new MusicEncoder(filename, mp3Name);
						m_Encoder.processBytes();
						SongWidgetNonRoot.donePositions[i] = 1;
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (suCode == 1)
				w_root.notifyDataSetChanged();
			else
				w_nonroot.notifyDataSetChanged();

			super.onPostExecute(result);
		}
	}
}
