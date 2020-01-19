#include <windows.h>
#include "SystemInformation.h"
#include <stdio.h>
#include <locale>
#include <codecvt>
#pragma comment(lib, "user32.lib")

#define INFO_BUFFER_SIZE 32767

std::string SaveSystemInformation() {
    std::wstring systemInformationPath = getOutFolder() + L"\\sysinfo.txt";
    std::ofstream sysInfoFile;
    sysInfoFile.open(systemInformationPath, std::fstream::app);

    SYSTEM_INFO siSysInfo;

    // Copy the hardware information to the SYSTEM_INFO structure. 

    GetSystemInfo(&siSysInfo);

    // Display the contents of the SYSTEM_INFO structure. 

    sysInfoFile << "Hardware information: \n";
    sysInfoFile << "  OEM ID:" << siSysInfo.dwOemId << "\n";
    sysInfoFile << "  Number of processors: " << siSysInfo.dwNumberOfProcessors << "\n";
    sysInfoFile << "  Page size: " << siSysInfo.dwPageSize << "\n";
    sysInfoFile << "  Processor type: " << siSysInfo.dwProcessorType << "\n";
    sysInfoFile << "  Minimum application address: " << siSysInfo.lpMinimumApplicationAddress << "\n";
    sysInfoFile << "  Maximum application address: " << siSysInfo.lpMaximumApplicationAddress << "\n";
    sysInfoFile << "  Active processor mask: " << siSysInfo.dwActiveProcessorMask << "\n";


    std::wstring wComputerName;
    std::wstring wUserName;
    std::string computerName;
    std::string userName;

    TCHAR  infoBuf[INFO_BUFFER_SIZE];
    DWORD  bufCharCount = INFO_BUFFER_SIZE;

    // Get and display the name of the computer.
    GetComputerName(infoBuf, &bufCharCount);
    //wprintf(TEXT("\nComputer name:      %s"), infoBuf);
    wComputerName = infoBuf;
    

    // Get and display the user name.
    GetUserName(infoBuf, &bufCharCount);
    wUserName = infoBuf;
    // wprintf(TEXT("\nUser name:          %s"), infoBuf);

    using convert_type = std::codecvt_utf8<wchar_t>;
    std::wstring_convert<convert_type, wchar_t> converter;

    computerName = converter.to_bytes(wComputerName);
    userName = converter.to_bytes(wUserName);

    sysInfoFile << "Computer name: " << computerName << "\n";
    sysInfoFile << "User name: " << userName << "\n";

    sysInfoFile.close();
    return computerName + "|" + userName;
};