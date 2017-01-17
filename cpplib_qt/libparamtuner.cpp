#include "ParamTuner.hpp"
#include "libparamtuner.h"

using namespace std;

static ParamTuner *paramTuner = NULL;

// Public function
int lptLoad(const string &path)
{
	if (paramTuner) { delete paramTuner; }

	// Construit l'objet permettant de surveiller le fichier de config
	paramTuner = new ParamTuner(path.c_str());
	// Verifie que la construction c'est bien passer
	if (!paramTuner) { return -1; }

	return 0;
}

int lptBind(const string &setting, void *ptr)
{
	if (!paramTuner) { return -1; }

	paramTuner->lptBind(setting, ptr);
	return 0;
}
