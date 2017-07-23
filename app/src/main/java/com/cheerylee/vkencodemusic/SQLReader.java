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

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Bundle;

public class SQLReader {
	private String artistName;
	private String songName;
	
	private int iArtist;
	private int iTitle;
	private int iFilename;
	
	SQLiteDatabase db = SQLiteDatabase.openDatabase("/sdcard/Android/data/com.cheerylee.vkencodemusic/databaseVerThree.db", null, 0);
	
	public int getSong(String filename) {
		Cursor c = db.query("saved_track", new String[] {"artist", "title", "file"}, null, null, null, null, null);
		iArtist = c.getColumnIndex("artist");
		iTitle = c.getColumnIndex("title");
		iFilename = c.getColumnIndex("file");
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			if (c.getString(iFilename).compareTo(filename) == 0) {
				artistName = c.getString(iArtist);
				songName = c.getString(iTitle);
				return 0;
			}
		}
		return 1;
	}
	
	public String getArtistName() {
		return artistName;
	}
	
	public String getSongName() {
		return songName;
	}
}
