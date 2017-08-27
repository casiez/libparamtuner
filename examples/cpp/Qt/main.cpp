/* -*- mode: c++ -*-
 *
 *
 * Authors: Géry Casiez
 *
 *
 */

#include <QApplication>

#include "MyCanvas.h"

int main(int argc, char **argv)
{
    QApplication app (argc, argv);

    MyCanvas myCanvas;
    myCanvas.show();

    return app.exec();
}
