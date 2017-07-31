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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.io.File;

public class SQLReader {

	private SQLiteDatabase db;

	public SQLReader(String path) throws SQLiteException {
		File f = new File(path);
		if (f.isDirectory() || !f.exists())
			throw new SQLiteException("Database doesn't exist on path " + path);

		db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}

	public String[] getSong(String filename) {
		Cursor c = db.query("saved_track", new String[] {"title", "artist", "file"}, null, null, null, null, null);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			if (c.getString(2).contains(filename)) {
				return new String[]{c.getString(0), c.getString(1)};
			}
		}
		return null;
	}
}
