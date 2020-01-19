#include "WebcamCapture.h"

using namespace cv;

void startWebcamCapture()
{
	std::wstring outFolder = getOutFolder() + L"\\WebcamCaptures";
	createFolderIfNotExist(outFolder);
	
	VideoCapture cap;
	int index = 0;

	if (!cap.open(0)) return;

	while (1) {
		Mat frame;
		cap >> frame;

		if (frame.empty()) break;

		char filenameBuffer[45];
		std::wstring outPath = outFolder + L"\\ScreenCapture_" + std::to_wstring(index++) + L".bmp";
		//sprintf_s(filenameBuffer, "WebcamCaptures/WebcamCapture_%d.bmp", index++);
		imwrite(filenameBuffer, frame);

		Sleep(3000);
	}
}
