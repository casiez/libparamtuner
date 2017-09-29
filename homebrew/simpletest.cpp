
#include <paramtuner.h>
#include <iostream>

using namespace std;

int
main(int argc, char **argv) {
	double varDouble = 2.0;
	int varInt = 1;
	bool varBool = false;
	string varString;

	ParamTuner::load("test/settings.xml");
	ParamTuner::bind("setting1", &varDouble);
	ParamTuner::bind("setting2", &varInt);
	ParamTuner::bind("mybool", &varBool);
	ParamTuner::bind("mystring", &varString);

	cout << "setting1 (double) = " << varDouble
		<< " ; setting2 (int) = " << varInt
		<< " ; mybool (bool) = " << varBool
		<< " ; mystring (string) = " << varString
		<< endl;

  return 0 ;
}

