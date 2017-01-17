#include "QtFileSystemWatcher.hpp"
#include "libparamtuner.h"
#include <map>
#include "rapidxml-1.13/rapidxml.hpp"
#include <algorithm>
#include <iostream>
#include <fstream>
#include <sstream>

using namespace rapidxml;
using namespace std;



// Data structure

std::map<std::string, void*> binding;

FileSystemWatcher* watcher;


// Private function

double string_to_double(const string & str)
{
	double dest;
	istringstream iss(str);
	iss >> dest;
	return dest;
}
int string_to_int(const string & str)
{
	int dest;
	istringstream iss(str);
	iss >> dest;
	return dest;
}

void fileModificationCallback()
{
	
	// inspiré de https://gist.github.com/JSchaenzle/2726944
	
	
	xml_document<> doc;
	xml_node<> * root_node;
	// Read the xml file into a vector
	ifstream theFile (watcher->getPath());
	vector<char> buffer((istreambuf_iterator<char>(theFile)), istreambuf_iterator<char>());
	buffer.push_back('\0');
	// Parse the buffer using the xml file parsing library into doc 
	doc.parse<0>(&buffer[0]);
	
	// Find our root node
	root_node = doc.first_node("ParamList");
	
	if (!root_node) {
		cerr << "Settings file does not contains ParamList root node" << endl;
		return;
	}
	// Iterate over the brewerys
	for (xml_node<> * param_node = root_node->first_node(); param_node; param_node = param_node->next_sibling())
	{
		string name(param_node->name());
		if (binding.find(name) == binding.end()) {
			cerr << "Setting '" << name << "' is not binded with lptBind()" << endl;
			continue;
		}
		xml_attribute<> *value_attr = param_node->first_attribute("value");
		xml_attribute<> *type_attr = param_node->first_attribute("type");
		if (!value_attr) {
			cerr << "Setting '" << name << "' does not have 'value' attribute" << endl;
			continue;
		}
		if (!type_attr) {
			cerr << "Setting '" << name << "' does not have 'type' attribute" << endl;
			continue;
		}
	    string type(type_attr->value());
		std::transform(type.begin(), type.end(), type.begin(), ::tolower);
		string value(value_attr->value());
	    
	    if (type == "bool") {
			std::transform(value.begin(), value.end(), value.begin(), ::tolower);
			*((bool*)binding[name]) = value == "true";
		}
		else if (type == "string") {
			*((string*)binding[name]) = value;
		}
		else if (type == "int") {
			*((int*)binding[name]) = string_to_int(value);
		}
		else if (type == "double") {
			*((double*)binding[name]) = string_to_double(value);
		}
		else {
			cerr << "Setting '" << name << "' has an unsupported 'type' attribute" << endl;
		}
	    
	}
	
	
	
	
}






// Public function
int lptLoad(const string &path)
{
	binding.clear();
	if (watcher) { delete watcher; }

	// Construit l'objet permettant de surveiller le fichier de config
	watcher = new QtFileSystemWatcher(path, fileModificationCallback);
	// Verifie que la construction s'est bien passé
	if (!watcher) { return -1; }

	return 0;
}

void lptBind(const string &setting, void *ptr)
{
	binding[setting] = ptr;
}
