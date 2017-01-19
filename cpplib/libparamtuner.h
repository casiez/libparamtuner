#ifndef LIB_PARAM_TUNER_H
#define LIB_PARAM_TUNER_H

int lptLoad(const std::string &path);
void lptBind(const std::string &setting, void *ptr);

#endif
