
/* Modified by Marc Baloup for libParamTuner examples */

/* Copyright (c) Mark J. Kilgard, 1996. */

/* This program is freely distributable without licensing fees
   and is provided without guarantee or warrantee expressed or
   implied. This program is -not- in the public domain. */

/* X compile line: cc -o simple simple.c -lglut -lGLU -lGL -lXmu -lXext -lX11 -lm */

#include <GL/glut.h>
#ifdef _WIN32
#   include <windows.h>
#   define SLEEP(ms) (Sleep(ms))
#else
#   include <unistd.h>
#   define SLEEP(ms) (usleep(ms * 1000))
#endif

#include <iostream>
#include "paramtuner.h"

using namespace std;

void reshape(int w, int h)
{
    glViewport(0, 0, w, h);       /* Establish viewing area to cover entire window. */
    glMatrixMode(GL_PROJECTION);  /* Start modifying the projection matrix. */
    glLoadIdentity();             /* Reset project matrix. */
    glOrtho(0, w, 0, h, -1, 1);   /* Map abstract coords directly to window coords. */
    glScalef(1, -1, 1);           /* Invert Y axis so increasing Y goes down. */
    glTranslatef(0, -h, 0);       /* Shift origin up to upper-left corner. */
}

long targetDeltaTime = 33; // 30 FPS = 33 ms/frame


double x1 = 0, y1 = 0, x2 = 200, y2 = 200, x3 = 20, y3 = 200;

void display(void)
{
    static float angle=0.0;
    angle+=1.0;
    glClear(GL_COLOR_BUFFER_BIT);
    glPushMatrix();
    glTranslatef(150.0,150.0,0.0);
    glRotatef(angle, 0.0, 0.0, 1.0);
    glBegin(GL_TRIANGLES);
    glColor3f(0.0, 0.0, 1.0);  /* blue */
    glVertex2i(x1, y1);
    glColor3f(0.0, 1.0, 0.0);  /* green */
    glVertex2i(x2, y2);
    glColor3f(1.0, 0.0, 0.0);  /* red */
    glVertex2i(x3, y3);
    glEnd();
    glPopMatrix();
    glFlush();  /* Single buffered, so needs a flush. */
}


void update() {
    static long long prevTime = 0 ;
    long long currTime = glutGet(GLUT_ELAPSED_TIME);
    if ( (currTime - prevTime) < targetDeltaTime) {
        SLEEP((targetDeltaTime-(currTime-prevTime)));
    }
    prevTime = currTime;
    glutPostRedisplay();
}

int main(int argc, char **argv)
{

    ParamTuner::load("settings.xml");
    ParamTuner::bind("x1", &x1);
    ParamTuner::bind("y1", &y1);
    ParamTuner::bind("x2", &x2);
    ParamTuner::bind("y2", &y2);
    ParamTuner::bind("x3", &x3);
    ParamTuner::bind("y3", &y3);
    glutInit(&argc, argv);
    glutCreateWindow("Single Triangle - libParamTuner examples");
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    glutIdleFunc(update);
    glutMainLoop();
    return 0;
}
