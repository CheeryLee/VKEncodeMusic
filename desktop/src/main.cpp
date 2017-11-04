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

#ifdef GUI_BUILD_VAR
#include <QApplication>
#include "EncoderWindow.h"
#endif

#include "MusicEncoder.hpp"

void showHelp() {
    cout << "This small program translates encoded music files to MP3\n\
                \ras in old versions of VK.\n\n\
                \rUSAGE:\n\
                \rvk_encode <encoded-file> <path-to-mp3>\n\n\
                \rIf path to mp3 not specified, mp3 will be created in\n\
                \rrunning directory.\n\n\
                \rOriginally created by Alexander \"CheeryLee\" Pluzhnikov\n\n";
    exit(0);
}

void checkArgs(char *argv[]) {
    if ((string)argv[1] == "--help" || (string)argv[1] == "-h")
        showHelp();
}

int main(int argc, char* argv[]) {
    cout << "VKEncodeMusic" << endl << endl;

    if (argc == 1) {
#ifdef GUI_BUILD_VAR
        QApplication a(argc, argv);
        EncoderWindow w;
        w.show();

        return a.exec();
#else
        printf("ENCODED-file not specified.\n\
                \rEnter --help or -h to get information.\n\n");
        return 1;
#endif
    } else {
        checkArgs(argv);

        MusicEncoder *me;

        if (argc == 3)
            me = new MusicEncoder(argv[1], argv[2]);
        else
            me = new MusicEncoder(argv[1]);

        me->processBytes();
        me->close();
    }

    return 0;
}
