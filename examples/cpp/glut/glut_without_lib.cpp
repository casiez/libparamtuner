
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

long time = 0;
long targetDeltaTime = 33; // 30 FPS = 33 ms/frame


double x1 = 0,
       y1 = 0,
	   x2 = 200,
	   y2 = 200,
	   x3 = 20,
	   y3 = 200;

void display(void)
{
  glClear(GL_COLOR_BUFFER_BIT);
  glBegin(GL_TRIANGLES);
    glColor3f(0.0, 0.0, 1.0);  /* blue */
    glVertex2i(x1, y1);
    glColor3f(0.0, 1.0, 0.0);  /* green */
    glVertex2i(x2, y2);
    glColor3f(1.0, 0.0, 0.0);  /* red */
    glVertex2i(x3, y3);
  glEnd();
  glFlush();  /* Single buffered, so needs a flush. */
}


void update() {
	long currTime = glutGet(GLUT_ELAPSED_TIME);
	if (currTime < time + targetDeltaTime) {
		SLEEP(time + targetDeltaTime - currTime);
	}
	time += targetDeltaTime;
	display();
}

int main(int argc, char **argv)
{
  glutInit(&argc, argv);
  glutCreateWindow("Single Triangle - libParamTuner examples");
  glutDisplayFunc(display);
  glutReshapeFunc(reshape);
  glutIdleFunc(update);
  glutMainLoop();
  return 0;
}