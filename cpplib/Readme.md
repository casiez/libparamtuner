# libParamTuner C++

## Static library compilation

### For Qt 4.8+ applications

* Go into directory `cpplib` and open terminal
* Open the file `cpplib.pro`, uncomment the line `CONFIG += lpt_use_qt` and save.
* Run `qmake && make`
* Library files : `libParamTuner-Qt.a` (may be in `release` subdirectory)
* Header file : `libparamtuner.h`

### For Native Windows, linux (and Mac OS ? )

* Go into directory `cpplib` and open terminal
* Open the file `cpplib.pro`, comment the line `CONFIG += lpt_use_qt` and save.
* Run `qmake && make`
* Library files : `libParamTuner.a` (may be in `release` subdirectory)
* Header file : `libparamtuner.h`

## Test compilation and execution

* Go into one of the 'example_...' directories
* Run `qmake && make`
* Run `./example_...` executable

You can modify and save `settings.xml` file, the programm will
automatically update the variables in the memory.

`qt` examples work with the Qt version of the library.

`nativ` examples work with the native OS dependencies.
