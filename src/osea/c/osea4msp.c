/*
 * @file   osea4msp.c
 * @author Patrick S. Hamilton
 * @author Malte Kirst
 * 
 * @brief A MSP based QRS detector for Java.
 * 
 * Copywrite (C) 2002 Patrick S. Hamilton
 *               2008 Malte Kirst
 *                    FZI Research Center for Information Technology
 * 
 * This file is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 */

#include "osea4msp.h"


#define TRUE 1
#define HPBUFFER_LGTH	32
#define SAMPLE_RATE_250 1




#ifdef SAMPLE_RATE_200
	// Time interval constants for 200 Hz ECG data.
	/** 16 samples for 80ms */
	#define MS80	16
	#define MS95	19
	#define MS150	30
	#define MS200	40
	#define MS360	72
	#define MS450	90
	#define MS1000	200
	#define MS1500	300
#endif

#ifdef SAMPLE_RATE_250
	// Time interval constants for 250 Hz ECG data.
	/** 16 samples for 80ms */
	#define MS80	20
	#define MS95	23.75
	#define MS150	37.5
	#define MS200	50
	#define MS360	90
	#define MS450	112.5
	#define MS1000	250
	#define MS1500	375
#endif

#define WINDOW_WIDTH	MS80
#define FILTER_DELAY	21 + MS200




// Global values for QRS detector.

int16 Q0 = 0, Q1 = 0, Q2 = 0, Q3 = 0, Q4 = 0, Q5 = 0, Q6 = 0, Q7 = 0;
int16 N0 = 0, N1 = 0, N2 = 0, N3 = 0, N4 = 0, N5 = 0, N6 = 0, N7 = 0;
int16 RR0 = 0, RR1 = 0, RR2 = 0, RR3 = 0, RR4 = 0, RR5 = 0, RR6 = 0, RR7 = 0;
int16 QSum = 0, NSum = 0, RRSum = 0;
short detectionThreshold;
int16 sbcount;

int16 tempQSum, tempNSum, tempRRSum;

int16 QN0 = 0, QN1 = 0;
int Reg0 = 0;
byte isPause = 0;


/**
 * Main Function
 */
 int main()
 {
 	/** delay of last detected QRS complex in samples */
 	int qrsDelay;
 	
 	/** current ECG sample */
 	int16 currentSample = 0;
 	
 	// init QRS detection
 	qrsDetection(0, 1);
 	
 	// run this loop with 250 Hz
 	while (1)
 	{
 		// Caution: In noisy ECG periods QRS detection needs
 		// more time because of its integrated search back
 		// algorithm!
 		qrsDelay = qrsDetection(currentSample, 0);
 	}
 	
 	return 1;
 }


/** 
 * qrsDetection takes 16-bit ECG samples (5 uV/LSB) as input and returns the
 * detection delay when a QRS is detected.  Passing a nonzero value for init
 * resets the QRS detector.
 * @param x current ECG sample
 * @param init > 0 for initialisation
 * @return QRS delay
 */
int16 qrsDetection(int16 x, int init)
{
	static int16 tempPeak, initMax;
	static int16  iBlankPeriod = 0, iQrsPeak = 0; // int8 ?
	static int16 initBlank = 0;
	static int16 count, sbpeak, sbloc;
	int16 qrsDelay = 0;
	
	if (init)
	{
		hpFilt(0, 1);
		lpFilt(0, 1);
		derivate(0, 1);
		movingWindowIntegration(0, 1);
		Peak(0, 1);
		iQrsPeak = 0;
		count = sbpeak = 0;
		QSum = NSum = 0;
		
		RRSum = MS1000<<3;
		RR0 = RR1 = RR2 = RR3 = RR4 = RR5 = RR6 = RR7 = MS1000;
		
		Q0 = Q1 = Q2 = Q3 = Q4 = Q5 = Q6 = Q7 = 0;
		N0 = N1 = N2 = N3 = N4 = N5 = N6 = N7 = 0;
		NSum = 0;
		
		return 0;
	}
	
	x = lpFilt(x, 0);
	x = hpFilt(x, 0);
	x = derivate(x, 0);
	if (x < 0) 
	{
		x = -x;
	}
	x = movingWindowIntegration(x, 0);
	x = Peak(x, 0);

	
	// Hold any peak that is detected for 200 ms
	// in case a bigger one comes along.  There
	// can only be one QRS complex in any 200 ms window.
	
	if (!x && !iBlankPeriod)
	{
		x = 0;
	}
	
	// If we have held onto a peak for
	// 200 ms pass it on for evaluation.
	else if (!x && iBlankPeriod)		
	{				
		if (--iBlankPeriod ==  0)
		{
			x = tempPeak;
		}
		else 
		{
			x = 0;
		}
	}
	
	// If there has been no peak for 200 ms
	// save this one and start counting.
	else if (x && !iBlankPeriod)		
	{			
		tempPeak = x;
		iBlankPeriod = MS200;
		x = 0;
	}
	
	// If we were holding a peak, but
	// this ones bigger, save it and
	// start counting to 200 ms again.
	else if (x)				
	{				
		if (x > tempPeak)		
		{
			tempPeak = x;
			iBlankPeriod = MS200;
			x = 0;
		}
		else if (--iBlankPeriod ==  0)
		{
			x = tempPeak;
		}
		else 
		{
			x = 0;
		}
	}

//	mexPrintf("x = %d ", x);
	if (count == 16)
		x = x;

	
	// Initialize the qrs peak buffer with the first eight
	// local maximum peaks detected.
	
	if ( iQrsPeak < 8 )
	{
		++count;
		if (x > 0) 
		{
			count = WINDOW_WIDTH;
		}
		
		if (++initBlank ==  MS1000)
		{
			initBlank = 0;
			UpdateQ(initMax);
			initMax = 0;
			++iQrsPeak;
			if (iQrsPeak ==  8)
			{
				RRSum = MS1000 << 3;
				RR0 = RR1 = RR2 = RR3 = RR4 = RR5 = RR6 = RR7 = MS1000;
				
				sbcount = MS1500 + MS150;
			}
		}
		if (x > initMax)
		{
			initMax = x;
		}
	}
	
	else
	{
		++count;
		
		// Check if peak is above detection threshold.
		
		if(x > detectionThreshold  &&  x > 0)
		{
			UpdateQ(x);
			
			// Update RR Interval estimate and search back limit

			UpdateRR(count - WINDOW_WIDTH);
			count = WINDOW_WIDTH;
			sbpeak = 0;
			qrsDelay = WINDOW_WIDTH + FILTER_DELAY;
		}
		
		// If a peak is below the detection threshold.
		
		else if (x !=  0)
		{
			UpdateN(x);
			
			QN1 = QN0;
			QN0 = count;
			
			if((x > sbpeak) && ((count - WINDOW_WIDTH) >=  MS360))
			{
				sbpeak = x;
				sbloc = count - WINDOW_WIDTH;
			}
			
		}
		
		// Test for search back condition.  If a QRS is found in
		// search back update the QRS buffer and detectionThreshold.
		
		if ((count > sbcount) && (sbpeak > (detectionThreshold >> 1)))
		{
			UpdateQ(sbpeak);
			
			// Update RR Interval estimate and search back limit
			
			UpdateRR(sbloc);
			
			qrsDelay = count = count - sbloc;
			qrsDelay +=  FILTER_DELAY;
			sbpeak = 0;
		}
	}
	
	//mexPrintf("debug = %d, %d \n", x, detectionThreshold);

	return qrsDelay;
}


/**
 * UpdateQ takes a new QRS peak value and updates the QRS mean estimate
 * and detection threshold.
 * 
 * @param newQ new QRS sample
 */
void UpdateQ(int16 newQ)
{
	if (isPause)
	{
		return;
	}

	QSum -=  Q7;
	Q7 = Q6; 
	Q6 = Q5; 
	Q5 = Q4; 
	Q4 = Q3; 
	Q3 = Q2; 
	Q2 = Q1; 
	Q1 = Q0;
	Q0 = newQ;
	QSum +=  Q0;
	
	detectionThreshold = QSum - NSum;

	// detectionThreshold = NSum + detectionThreshold * 0.375
	detectionThreshold = NSum + (detectionThreshold >> 1) - (detectionThreshold >> 3);
	
	// detectionThreshold = detectionThreshold * 0.125
	detectionThreshold >>=  3;
}


/**
 * UpdateN takes a new noise peak value and updates the noise mean estimate
 * and detection threshold.
 * 
 * @param newN new noise sample
 */
void UpdateN(int16 newN)
{
	if (isPause)
	{
		return;
	}

	NSum -=  N7;
	N7 = N6; 
	N6 = N5; 
	N5 = N4; 
	N4 = N3; 
	N3 = N2; 
	N2 = N1; 
	N1 = N0; 
	N0 = newN;
	NSum += N0;

	
	detectionThreshold = QSum - NSum;
	detectionThreshold = NSum + (detectionThreshold >> 1) - (detectionThreshold >> 3);
	
	detectionThreshold >>=  3;
}

/**
 * UpdateRR takes a new RR value and updates the RR mean estimate
 * @param newRR new RR interval
 */
void UpdateRR(int16 newRR)
{
	RRSum -=  RR7;
	RR7 = RR6; 
	RR6 = RR5; 
	RR5 = RR4; 
	RR4 = RR3; 
	RR3 = RR2; 
	RR2 = RR1; 
	RR1 = RR0;
	RR0 = newRR;
	RRSum +=  RR0;
	
	sbcount = RRSum + (RRSum >> 1);
	sbcount >>=  3;
	sbcount +=  WINDOW_WIDTH;
}


/**
 * lpFilt() implements the digital filter represented by the difference
 * equation:
 * y[n] = 2*y[n-1] - y[n-2] + x[n] - 2*x[n-5] + x[n-10]
 * Note that the filter delay is five samples.
 * 
 * @param datum current sample
 * @param init >0 for initialization
 */

int16 lpFilt(int16 datum , int init)
{
	static int16 y1 = 0, y2 = 0;
	static int16 d0, d1, d2, d3, d4, d5, d6, d7, d8, d9;
	int16 y0;
	int16 output;
	
	if(init)
	{
		d0 = d1 = d2 = d3 = d4 = d5 = d6 = d7 = d8 = d9 = 0;
		y1 = y2 = 0;
	}
	
	y0 = (y1 << 1) - y2 + datum - (d4<<1) + d9;
	y2 = y1;
	y1 = y0;
	if (y0 >=  0)
	{ 
		output = y0 >> 5;
	}
	else 
	{
		output = (y0 >> 5) | 0xF800;
	}
	
	d9 = d8;
	d8 = d7;
	d7 = d6;
	d6 = d5;
	d5 = d4;
	d4 = d3;
	d3 = d2;
	d2 = d1;
	d1 = d0;
	d0 = datum;
	
	return output;
}

/**
 * hpFilt() implements the high pass filter represented by the following
 * difference equation:
 * 
 * y[n] = y[n-1] + x[n] - x[n-32]
 * z[n] = x[n-16] - y[n];
 * 
 * Note that the filter delay is 15.5 samples
 * 
 * @param datum current sample
 * @param init >0 for initialization
 */
int16 hpFilt(int16 datum, int init)
{
	static int16 y = 0;
	static int16 data[HPBUFFER_LGTH];
	static int ptr = 0;
	int16 z;
	int halfPtr;
	
	if (init)
	{
		for (ptr = 0; ptr < HPBUFFER_LGTH; ++ptr)
		{
			data[ptr] = 0;
		}
		ptr = 0;
		y = 0;
		return 0;
	}
	
	y += datum - data[ptr];
	
	halfPtr = ptr - (HPBUFFER_LGTH / 2);
	halfPtr &=  0x1F;
	
	
	z = data[halfPtr];		// Compensate for CCS shift bug.
	if (y >=  0) 
	{
		z -=  (y >> 5);
	}
	else 
	{
		z -=  (y >> 5) | 0xF800;
	}
	
	
	data[ptr] = datum;
	ptr = (ptr + 1) & 0x1F;
	
	return z;
}

/**
 * derivate and deriv2 implement derivative approximations represented by
 * the difference equation:
 * 
 * y[n] = 2*x[n] + x[n-1] - x[n-3] - 2*x[n-4]
 * 
 * The filter has a delay of 2.
 * 
 * @param x0 current sample
 * @param init >0 for initialization
 */
int16 derivate(int16 x0, int init)
{
	static int16 x1, x2, x3, x4;
	int16 output;
	if (init)
	{
		x1 = x2 = x3 = x4 = 0;
	}
	
	output = x1 - x3;
	if (output < 0)
	{
		 output = (output >> 1) | 0x8000;	// Compensate for shift bug.
	}
	else 
	{
		output >>= 1;
	}
	
	output +=  (x0 - x4);
	if (output < 0)  
	{
		output = (output>>1) | 0x8000;
	}
	else 
	{
		output >>=  1;
	}
	
	
	x4 = x3;
	x3 = x2;
	x2 = x1;
	x1 = x0;
	
	return output;
}


/**
 * movingWindowIntegration() implements a moving window integrator, averaging
 * the signal values over the last 16
 * 
 * @param datum current sample
 * @param init >0 for initialization
 */
int16 movingWindowIntegration(int16 datum, int init)
{
	static uint16 sum = 0;
	static uint d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13, d14, d15;
	
	if (init)
	{
		d0 = d1 = d2 = d3 = d4 = d5 = d6 = d7 = d8 = d9 = d10 = d11 = d12 = d13 = d14 = d15 = 0;
		sum = 0;
	}
	sum -=  d15;
	
	d15 = d14;
	d14 = d13;
	d13 = d12;
	d12 = d11;
	d11 = d10;
	d10 = d9;
	d9 = d8;
	d8 = d7;
	d7 = d6;
	d6 = d5;
	d5 = d4;
	d4 = d3;
	d3 = d2;
	d2 = d1;
	d1 = d0;
	if (datum >=  0x0400)
	{ 
		d0 = 0x03ff;
	}
	else 
	{
		d0 = (datum >> 2);
	}
	sum +=  d0;
	
	return (sum >> 2);
}


/**
 * peak() takes a datum as input and returns a peak height
 * when the signal returns to half its peak height, or it has been
 * 95 ms since the peak height was detected.
 * 
 * @param datum current sample
 * @param init >0 for initialization
 */
int16 Peak(int16 datum, int init)
{
	static int16 max = 0, lastDatum;
	static int timeSinceMax = 0;
	int16 peak = 0;
	
	if (init)
	{
		max = 0;
		timeSinceMax = 0;
		return 0;
	}
	
	if (timeSinceMax > 0)
	{
		++timeSinceMax;
	}
	
	if ((datum > lastDatum) && (datum > max))
	{
		max = datum;
		if(max > 2)
		{
			timeSinceMax = 1;
		}
	}
	
	else if (datum < (max >> 1))
	{
		peak = max;
		max = 0;
		timeSinceMax = 0;
	}
	
	else if (timeSinceMax > MS95)
	{
		peak = max;
		max = 0;
		timeSinceMax = 0;
	}
	lastDatum = datum;
	
	return peak;
}

int16 getDetectionThreshold()
{
	return detectionThreshold;
}

int16 getNoiseSum()
{
	return NSum;
}

int16 getQrsSum()
{
	return QSum;
}

void setPause(byte b)
{
	isPause = b;
}


	
	
			


		