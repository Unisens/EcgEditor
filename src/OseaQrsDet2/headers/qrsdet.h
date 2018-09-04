/*****************************************************************************
FILE:  qrsdet.h
AUTHOR:	Patrick S. Hamilton
REVISED:	4/16/2002
  ___________________________________________________________________________

qrsdet.h QRS detector parameter definitions
Copywrite (C) 2000 Patrick S. Hamilton

This file is free software; you can redistribute it and/or modify it under
the terms of the GNU Library General Public License as published by the Free
Software Foundation; either version 2 of the License, or (at your option) any
later version.

This software is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU Library General Public License for more
details.

You should have received a copy of the GNU Library General Public License along
with this library; if not, write to the Free Software Foundation, Inc., 59
Temple Place - Suite 330, Boston, MA 02111-1307, USA.

You may contact the author by e-mail (pat@eplimited.com) or postal mail
(Patrick Hamilton, E.P. Limited, 35 Medford St., Suite 204 Somerville,
MA 02143 USA).  For updates to this software, please visit our website
(http://www.eplimited.com).
  __________________________________________________________________________
  Revisions:
	4/16: Modified to allow simplified modification of digital filters in
   	qrsfilt().
*****************************************************************************/

	/* Sample rate in Hz. */

int SAMPLE_RATE;
double MS_PER_SAMPLE;
int MS10;
int MS25;
int MS30;
int MS80;
int MS95;
int MS100;
int MS125;
int MS150;
int MS160;
int MS175;
int MS195;
int MS200;
int MS220;
int MS250;
int MS300;
int MS360;
int MS450;
int MS1000;
int MS1500;
int DERIV_LENGTH;
int LPBUFFER_LGTH;
int HPBUFFER_LGTH;

int WINDOW_WIDTH;			// Moving window integration width.
int PRE_BLANK;
int FILTER_DELAY;  // filter delays plus 200 ms blanking delay
int  DER_DELAY;

int QRSDet( int datum, int init );
