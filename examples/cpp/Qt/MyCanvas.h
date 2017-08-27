/* -*- mode: c++ -*-
 *
 * MyCanvas.h --
 *
 * Authors: Géry Casiez
 *
 *
 */

#ifndef MyCanvas_h
#define MyCanvas_h


#include <QWidget>
#include <QTimer>

using namespace std;

class MyCanvas : public QWidget {

  Q_OBJECT ;

  string s;
  int x, y, fontsize;
  double angle;
  bool toggleColor; 

protected:

  void paintEvent(QPaintEvent *event) ;

public:

  explicit MyCanvas(QWidget *parent=0) ;

  void update() ;

  ~MyCanvas(void) ;

} ;

#endif
