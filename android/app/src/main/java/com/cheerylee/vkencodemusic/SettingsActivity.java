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

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.CheckBox;
import android.view.View;

import com.cheerylee.vkencodemusic.MusicActivity;

public class SettingsActivity extends Activity {

    CheckBox use_humanity_filename_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        reorderLayout();

        use_humanity_filename_checkbox = (CheckBox) findViewById(R.id.settings_use_humanity_filename_checkbox);

        if (MusicActivity.hasDatabase == true) { 
            use_humanity_filename_checkbox.setEnabled(true);
            if(MusicActivity.useHumanityFilename == true)
                use_humanity_filename_checkbox.setChecked(true);
            else
                use_humanity_filename_checkbox.setChecked(false);
        } else {
            use_humanity_filename_checkbox.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicActivity.useHumanityFilename = use_humanity_filename_checkbox.isChecked();
        MusicActivity.m_Settings.putBoolean("useHumanityFilename", MusicActivity.useHumanityFilename);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MusicActivity.useHumanityFilename = use_humanity_filename_checkbox.isChecked();
        MusicActivity.m_Settings.putBoolean("useHumanityFilename", MusicActivity.useHumanityFilename);
    }

    /**
     * Пересортирует activity после поиска всех доступных хранилищ
     */
    private void reorderLayout() {
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.settings_root_layout);
        RelativeLayout use_humanity_filename_layout = (RelativeLayout) findViewById(R.id.settings_use_humanity_filename_layout);
        View separator = (View) findViewById(R.id.separator);

        rootLayout.removeView(separator);
        rootLayout.removeView(use_humanity_filename_layout);
        
        for (int i = 0; i < MusicActivity.storagePath.length; i++) {
            TextView storage = new TextView(this);
            String path = MusicActivity.storagePath[i].substring(0, MusicActivity.storagePath[i].length() - MusicActivity.vkMusicPath.length());
            storage.setText(path);
            storage.setTextSize(14f);
            storage.setPaddingRelative(20, 5, 10, 10);
            rootLayout.addView(storage);
        }

        rootLayout.addView(separator);
        rootLayout.addView(use_humanity_filename_layout);
    }
}
