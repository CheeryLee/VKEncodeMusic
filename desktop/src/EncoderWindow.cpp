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

#include "EncoderWindow.h"
#include "ui_EncoderWindow.h"

EncoderWindow::EncoderWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::EncoderWindow)
{
    ui->setupUi(this);

    QSizePolicy sizePolicy = ui->iconLabel->sizePolicy();
    sizePolicy.setRetainSizeWhenHidden(true);
    ui->iconLabel->setSizePolicy(sizePolicy);

    aw = new AboutWindow(this);

    setAcceptDrops(true);

    connect(ui->aboutAction, SIGNAL(triggered()), this, SLOT(showAboutWindow()));
}

EncoderWindow::~EncoderWindow() {
    delete ui;
}

void EncoderWindow::dragEnterEvent(QDragEnterEvent *event) {
    if (event->mimeData()->hasUrls())
        event->acceptProposedAction();
}

void EncoderWindow::dropEvent(QDropEvent *event) {
    ui->dragWarn->setText("Перекодировка ...");
    ui->iconLabel->setVisible(false);

    foreach (const QUrl &url, event->mimeData()->urls()) {
        MusicEncoder *me = new MusicEncoder(url.toLocalFile().toStdString());
        me->processBytes();
        me->close();
        qApp->processEvents(); // workaround, need the fix
    }

    ui->dragWarn->setText("Готово!");
    QTimer::singleShot(3500, [&](){ui->dragWarn->setText("Перетащите ENCODED-файл сюда!");});
    QTimer::singleShot(3500, [&](){ui->iconLabel->show();});
}

void EncoderWindow::paintEvent(QPaintEvent *event) {
    QPainter painter(this);
    painter.setPen(QPen(QColor(128, 128, 128), 3, Qt::DashLine));
    painter.drawRoundedRect(10, 35, width()-20, height()-45, 20, 20);

    QWidget::paintEvent(event);
}

void EncoderWindow::showAboutWindow() {
    if (!aw->isVisible())
        aw->show();
    else
        aw->activateWindow();
}
