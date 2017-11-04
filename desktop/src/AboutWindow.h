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

#ifndef ABOUTWINDOW_H
#define ABOUTWINDOW_H

#include <QMainWindow>
#include <QLabel>

namespace Ui {
class AboutWindow;
}

class AboutWindow : public QMainWindow {

    Q_OBJECT

public:
    explicit AboutWindow(QWidget *parent = 0);
    ~AboutWindow();

private:
    Ui::AboutWindow *ui;

};

#endif // ABOUTWINDOW_H

 
