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

#ifndef ENCODERWINDOW_H
#define ENCODERWINDOW_H

#include <QApplication>
#include <QMainWindow>
#include <QWidget>
#include <QPainter>
#include <QDragEnterEvent>
#include <QMimeData>
#include <QTimer>

#include "AboutWindow.h"
#include "MusicEncoder.hpp"

namespace Ui {
class EncoderWindow;
}

class EncoderWindow : public QMainWindow {

    Q_OBJECT

public:
    explicit EncoderWindow(QWidget *parent = 0);
    ~EncoderWindow();

private slots:
    void showAboutWindow();

protected:
    void dragEnterEvent(QDragEnterEvent *event);
    void dropEvent(QDropEvent *event);
    void paintEvent(QPaintEvent *event);

private:
    Ui::EncoderWindow *ui;
    AboutWindow *aw;
};

#endif // ENCODERWINDOW_H

