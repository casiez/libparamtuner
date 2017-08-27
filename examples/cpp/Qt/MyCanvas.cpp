/*
 *
 * MyCanvas.cpp --
 *
 * Authors: Géry Casiez
 *
 *
 */

#include "MyCanvas.h"

#include <QApplication>
#include <QPainter>

#include <paramtuner.h>

MyCanvas::MyCanvas(QWidget *parent) : QWidget(parent) {
    setGeometry(0,0,800,600);

    ParamTuner::load("settings.xml");
    ParamTuner::bind("s", &s);
    ParamTuner::bind("x", &x);
    ParamTuner::bind("y", &y);
    ParamTuner::bind("fontsize", &fontsize);
    ParamTuner::bind("angle", &angle);
    ParamTuner::bind("toggle", &toggleColor);

    QTimer *timer = new QTimer(this);
    connect(timer, SIGNAL(timeout()), this, SLOT(update()));
    timer->start(16);
}

void MyCanvas::update() {
    this->repaint();
}

void MyCanvas::paintEvent(QPaintEvent * /*event*/) {
  QPainter painter(this) ;

  QFont font = painter.font() ;
  font.setPointSize(fontsize);
  painter.setFont(font);
  if (toggleColor)
    painter.setPen(Qt::red);
  else
    painter.setPen(Qt::black);
  painter.translate(QPoint(x, y));
  painter.rotate(angle);
  painter.drawText(QPoint(0, 0), QString::fromStdString(s));
}

MyCanvas::~MyCanvas() {

}
