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

#include "MusicEncoder.hpp"

// If input string has no path to mp3 file
MusicEncoder::MusicEncoder(string _inFilename) {
    inFilename = _inFilename;
    outFilename = inFilename.substr(0, inFilename.size() - 8);
    outFilename += ".mp3";

    inFile.open(inFilename, ios::out | ios::binary);
    outFile.open(outFilename);
}

MusicEncoder::MusicEncoder(string _inFilename, string _outFilename) {
    inFilename = _inFilename;
    outFilename = _outFilename;

    inFile.open(inFilename, ios::out | ios::binary);
    outFile.open(outFilename);
}

void MusicEncoder::processBytes() {
    int strByte = 0;

    if (!inFile) {
        cout << "Can't open file. Abort." << endl;
        exit(1);
    }
    if (!outFile) {
#ifdef __linux
        struct stat st = { 0 };
        if (stat(outFilename.c_str(), &st) == -1) {
            string path = outFilename.substr(0, outFilename.find_last_of("\\/"));
            mkdir(path.c_str(), 0700);

            outFile.close();
            outFile.open(outFilename);
        }
#endif
    }

    cout << "ENCODED: " << inFilename << endl;
    cout << "MP3: " << outFilename << endl << endl;

    while (!inFile.eof()) {
        int byte;

        inFile.read(reinterpret_cast<char *>(&byte), 1);
        byte ^= data_str[strByte];
        outFile.write(reinterpret_cast<char *>(&byte), 1);

        data_str[strByte] += 0x10;

        if (strByte < 15)
            strByte++;
        else
            strByte = 0;
    }
}

// Close all files and delete object
void MusicEncoder::close() {
    inFile.close();
    outFile.close();

    delete this;
}
