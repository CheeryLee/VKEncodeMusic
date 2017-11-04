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

#ifndef MUSICENCODER_H
#define MUSICENCODER_H

#include <string>
#include <fstream>
#include <iostream>

#ifdef __linux
#include <sys/stat.h>
#endif

using namespace std;

class MusicEncoder {

private:
    string inFilename;
    string outFilename;

    ifstream inFile;
    ofstream outFile;

    int data_str[16] = { 0x0D, 0x1E, 0x2F, 0x40, 0x51, 0x62, 0x73, 0x84, 0x95,
                        0xA6, 0xB7, 0xC8, 0xD9, 0xEA, 0xFB, 0x0C };
	
public:
    MusicEncoder(string _inFilename);
    MusicEncoder(string _inFilename, string _outFilename);
    void processBytes();
    void close();
};

#endif
