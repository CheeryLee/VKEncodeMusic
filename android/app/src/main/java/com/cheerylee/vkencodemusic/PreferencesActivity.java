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
