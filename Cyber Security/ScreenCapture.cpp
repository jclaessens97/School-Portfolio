#include "ScreenCapture.h"
#include <iostream>

void takeScreenshot(std::wstring filename)
{
    uint16_t BitsPerPixel = 24;
    uint32_t Width = GetSystemMetrics(SM_CXSCREEN);
    uint32_t Height = GetSystemMetrics(SM_CYSCREEN);

    // Create Header
    BITMAPFILEHEADER Header;
    memset(&Header, 0, sizeof(Header));
    Header.bfType = 0x4D42;
    Header.bfOffBits = sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER);

    // Create Info
    BITMAPINFO Info;
    memset(&Info, 0, sizeof(Info));
    Info.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
    Info.bmiHeader.biWidth = Width;
    Info.bmiHeader.biHeight = Height;
    Info.bmiHeader.biPlanes = 1;
    Info.bmiHeader.biBitCount = BitsPerPixel;
    Info.bmiHeader.biCompression = BI_RGB;
    Info.bmiHeader.biSizeImage = Width * Height * (BitsPerPixel > 24 ? 4 : 3);

    // Capture screen and save to Pixels
    char* Pixels = NULL;
    HDC MemDC = CreateCompatibleDC(0);//Context);
    HBITMAP Section = CreateDIBSection(MemDC, &Info, DIB_RGB_COLORS, (void**)&Pixels, 0, 0);
    DeleteObject(SelectObject(MemDC, Section));
    BitBlt(MemDC, 0, 0, Width, Height, GetDC(0), 0, 0, SRCCOPY);
    DeleteDC(MemDC);

    // Concatenate everything
    char* buffer = (char*)malloc(sizeof(Header) + sizeof(Info.bmiHeader) + (((BitsPerPixel * Width + 31) & ~31) / 8) * Height);

    memcpy(buffer, (char*)&Header, sizeof(Header));
    memcpy(buffer + sizeof(Header), (char*)&Info.bmiHeader, sizeof(Info.bmiHeader));
    memcpy(buffer + sizeof(Header) + sizeof(Info.bmiHeader), Pixels, (((BitsPerPixel * Width + 31) & ~31) / 8) * Height);

    // Save to file
    std::fstream hFile(filename, std::ios::out | std::ios::binary);

    hFile.write(buffer, sizeof(Header) + sizeof(Info.bmiHeader) + (((BitsPerPixel * Width + 31) & ~31) / 8) * Height);

    // Clean up
    hFile.close();
    DeleteObject(Section);
    free(buffer);
}

void startScreenCapture()
{
    std::wstring outFolder = getOutFolder() + L"\\ScreenCaptures";    
    std::wcout << outFolder;
    createFolderIfNotExist(outFolder);
    int index = 0;

    while (1) {
        Sleep(3000);
        
        char filenameBuffer[45];
        std::wstring outPath = outFolder + L"\\ScreenCapture_" + std::to_wstring(index++) + L".bmp";
        // sprintf_s(filenameBuffer, "ScreenCaptures/ScreenCapture_%d.bmp", index++);
        takeScreenshot(outPath);
    }
}
