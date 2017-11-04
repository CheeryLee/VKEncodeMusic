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
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView ver_text = (TextView)findViewById(R.id.ver_text);
        TextView about_glm_text = (TextView)findViewById(R.id.about_glm_text);
        TextView about_github_text = (TextView)findViewById(R.id.about_github_text);
        TextView about_4pda_text = (TextView)findViewById(R.id.about_4pda_post_text);

		ver_text.setText("ver. " + BuildConfig.VERSION_NAME);
        about_glm_text.setMovementMethod(LinkMovementMethod.getInstance());
        about_github_text.setMovementMethod(LinkMovementMethod.getInstance());
        about_4pda_text.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
