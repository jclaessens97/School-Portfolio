#include "Util.h"

void createFolderIfNotExist(std::wstring path)
{
	//// Converts const char* to LPCWSTR without outputting weird chinese characters
	//wchar_t wtext[45];
	//mbstowcs(wtext, path, strlen(path) + 1);
	//LPCWSTR ptr = wtext;

	//if (!CreateDirectoryW(ptr, NULL)) {
	//	return; // If directory already exists, just exit the function
	//}
	CreateDirectory(path.c_str(), NULL);
}

std::wstring getOutFolder(){
	char* appdataTmp = getenv("APPDATA");
	const size_t cSize = strlen(appdataTmp) + 1;
	std::wstring outFolder(cSize, L'#');
	mbstowcs(&outFolder[0], appdataTmp, cSize);
	outFolder.pop_back();
	outFolder.append(L"\\totallysafe");
	CreateDirectory(outFolder.c_str(), NULL);
	return outFolder;
}


