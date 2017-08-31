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

public class Song {

    String path;
    String filename;
    String title;
    String artist;
    boolean isDecoded;

    public void setPath(String path) { this.path = path; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setDecoded(boolean isDecoded) { this.isDecoded = isDecoded; }

    public String getPath() { return path; }
    public String getFilename() { return filename; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public boolean getSongDecoded() {
        if (isDecoded == true)
            return true;
        return false;
    }
}