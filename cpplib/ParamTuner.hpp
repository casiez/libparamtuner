#ifndef PARAM_TUNER_HPP
#define PARAM_TUNER_HPP

#include <QtCore/QString>
#include <QtCore/QObject>
#include <QtCore/QFileSystemWatcher>

class ParamTuner : public QObject
{
	Q_OBJECT
public:
	QFileSystemWatcher *settingWatcher;
	QString settingPath;

	ParamTuner(const char *path);

	virtual ~ParamTuner(void) {}

	int lptBind(const std::string &setting, void *ptr);

public slots:
	void receiveSignal(const QString &path);
};

#endif
