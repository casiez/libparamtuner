#ifndef PARAM_TUNER_HPP
#define PARAM_TUNER_HPP

#include <QtCore/QString>
#include <QtCore/QObject>
#include <QtCore/QFileSystemWatcher>
#include <map>

class ParamTuner : public QObject
{
	Q_OBJECT
public:
	QFileSystemWatcher settingWatcher;
	QString settingPath;
	std::map<std::string, void*> binding;

	ParamTuner(const char *path);

	virtual ~ParamTuner(void) {}

	void lptBind(const std::string &setting, void *ptr);

public slots:
	void receiveSignal(const QString &path);
};

#endif
