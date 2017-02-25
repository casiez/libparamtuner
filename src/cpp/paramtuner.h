/*
 *  libParamTuner
 *  Copyright (C) 2017 Marc Baloup, Veïs Oudjail
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef LIB_PARAM_TUNER_H
#define LIB_PARAM_TUNER_H

#include <string>

namespace ParamTuner {

	/**
		\brief Start listening modifications of the specified file.
		
		After this function call, when the specified file is modified
		by another program, the variables binded with lptBind() are updated
		to their new values from file.
		
		The specified file's content must be an XML file with a root node
		"ParamList". Direct child node of the ParamList node represent a
		parameter in your program, for example :
			<paramName value="foo" type="string"/>
		See the documentation of lptBind() to see all possible types.
		
		If a node is not binded to a variable, a message will be sent to standard
		error stream. If a node has not a valid type or value, an error will be
		displayed too, and the variable will not be updated.
		
		When this function is called multiple times, the current call disable
		listener of the previous call.
		
		\param path the relative or absolute path to the file to listen to
		
		\return -1 if a problem occurs when starting the listener, 0 otherwise.
	*/
	int load(const std::string &path);


	/**
		\brief Bind a variable with a parameter in the XML file.
		
		Theses type of variable are actually supported, with the corresponding
		type in XML file :
		* Integer type (C/C++ type `int`) : type="int".
		* Floating point type (C/C++ type `double`) : type="double".
		* Boolean type (C++ type `bool`) : type="bool". 
		  The variable in program will have the value true if and only if the value in
		  XML file is equal, case ignored, to "true".
		* String type (c++ type 'std::string') : type="string".
		
		If a variable is binded to a parameter that doesn't have compatible
		type, bad values may be written to the variables.
		
		This function may be called before or after calling lptLoad().
		The internal storage of binded values is always preserved.
		
		If a variable is already binded with the specified name, the old
		binding will be erased.
		
		You must ensure that the memory at the address specified by ptr
		is always accessible during the program execution, to avoid
		segmentation fault.
		
		\param name the parameter name, that is equal to the node name
		containing the parameter value.
		
		\param ptr a pointer to the variable that will be updated when the
		file is modified.
	*/
	void bind(const std::string &name, void *ptr);

}
#endif
