#pragma once
#include <stdio.h>
#include <stdlib.h>
#include "portaudio.h"

#define SAMPLE_RATE			44100
#define NUM_CHANNELS		2	
#define NUM_SECONDS			15
#define FRAMES_PER_BUFFER	512
#define PA_SAMPLE_TYPE		paInt16
#define SAMPLE_SILENCE		0
#define PRINTF_S_FORMAT		"%d"

typedef short SAMPLE;

void startMicrophoneCapture();
