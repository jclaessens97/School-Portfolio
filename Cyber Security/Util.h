#pragma once
#include <Windows.h>
#include <stdio.h>
#include <string>

void createFolderIfNotExist(std::wstring path);
std::wstring getOutFolder();
