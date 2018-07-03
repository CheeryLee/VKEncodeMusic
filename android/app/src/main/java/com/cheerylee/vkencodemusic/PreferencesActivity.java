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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle p1) {
		super.onCreate(p1);
		addPreferencesFromResource(R.xml.preferences);

		Preference humanity = findPreference("PREF_NAME_HUMANITY");
		humanity.setEnabled(MusicActivity.hasDatabase);
		if (!MusicActivity.hasDatabase) humanity.setSummary("База данных недоступна");

		Preference storages = findPreference("PREF_VIEW_STORAGES");
		String[] storAvail = MusicActivity.findStorages(this);
		String storMes = "";
		for (String s : storAvail) {
			storMes += "• " + s + "\n";
		}
		storages.setSummary(storMes.substring(0,storMes.length()-1));
	}


}
