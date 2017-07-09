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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cheerylee.vkencodemusic;

import java.io.*;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class MusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        
        // Разрешение на чтение памяти
        if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
			!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			ActivityCompat.requestPermissions(this, 
			new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);
		}

        if (checkSD() == 1)
            return;
		else {
			getEncodedFiles();	
		}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == 10001) {
            
            for (int i = 0; i < 2; i++) {
				if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
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
    }
    
	//
	// ЕСЛИ РАЗРЕШЕНИЕ НА ПАМЯТЬ УЖЕ БЫЛО ПОЛУЧЕНО
	//
	private boolean isPermissionGranted(String permission) {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }
    
    //
    // СОЗДАНИЕ ЭЛЕМЕНТА СПИСКА
    //
    private void createSongWidget(String fileNames[]) {
		ListView lvMain = (ListView) findViewById(R.id.lvMain);
		SongWidget w = new SongWidget(this, fileNames);
		lvMain.setAdapter(w);
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
		String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.vkontakte.android/files/Music/";
                
		TextView warn_not_found_text = (TextView)findViewById(R.id.warn_text);
                
		File f = new File(path);
        File[] files = f.listFiles();
        
        if (files != null) {
			int count = 0;
			
			for (int i = 0; i < files.length; i++)
				count++;
				
			String fileNames[] = new String[count];
			for (int i = 0; i < files.length; i++)
				fileNames[i] = files[i].getName().substring(0, files[i].getName().length() - 8);

            warn_not_found_text.setVisibility(View.GONE);
            createSongWidget(fileNames);
		} else {
            warn_not_found_text.setText(R.string.warn_not_found);
		}
	}
}
