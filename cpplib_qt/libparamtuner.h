#ifndef LIB_PARAM_TUNER_H
#define LIB_PARAM_TUNER_H

int lptLoad(const std::string &path);
int lptBind(const std::string &setting, void *ptr);

#endif
