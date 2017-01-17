#include <QtCore/QString>
#include <QtCore/QObject>
#include <QtCore/QFileSystemWatcher>
#include <iostream>
#include <fstream>
#include <sstream>
#include "ParamTuner.hpp"
#include "rapidxml-1.13/rapidxml.hpp"
#include <algorithm>

using namespace rapidxml;
using namespace std;


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



ParamTuner::ParamTuner(const char *path) :
		settingPath(path)
{
	settingWatcher.addPath(settingPath);
	// connect the signal from Qt Watcher to receiveSignal() slot
	connect(&settingWatcher, SIGNAL(fileChanged(QString)),
			this, SLOT(receiveSignal(QString)));
	connect(&settingWatcher, SIGNAL(directoryChanged(QString)),
			this, SLOT(receiveSignal(QString)));
}


void ParamTuner::lptBind(const string &setting, void *ptr)
{
	binding[setting] = ptr;
}

void ParamTuner::receiveSignal(const QString &path)
{
	if (path != settingPath)
		return;
	/* Parfois, il est nécessaire de remettre le fichier dans le Watcher
	 * pour être notifié de la modification suivante. Dans d'autres cas,
	 * ce n'est pas nécessaire et un message d'erreur peut apparaitre
	 * dans le terminal.
	 */
	settingWatcher.addPath(settingPath);
	
	
	
	// inspiré de https://gist.github.com/JSchaenzle/2726944
	
	
	xml_document<> doc;
	xml_node<> * root_node;
	// Read the xml file into a vector
	ifstream theFile (settingPath.toStdString());
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
