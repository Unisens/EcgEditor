/**
 * This class implementes some utility-function associated with time/date
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import de.fzi.ekgeditor.data.Constants;

public class TimeUtil
{

	/** how much seconds does one minute have */
	private static final int MINUTES_IN_S = 60;
	private static final int HOURS_IN_MIN = 60;

	/**
	 * get some string of long variable representing some time measured in
	 * milliSeconds
	 * 
	 * @param timeInMilliSeconds
	 *        time in milliseconds
	 *        
	 *         
	 * @return String representing this time.
	 */
	public static String getTimeString(long timeInMilliSeconds, boolean withMilliSecs)
	{
		return getTimeString(timeInMilliSeconds, 0, withMilliSecs);
	}

	public static String getTimeString(long timeInMilliSeconds, long offset, boolean withMilliSecs)
	{
		if (timeInMilliSeconds >= 0)
		{
			String format;
			
			if (withMilliSecs)
			{
				format = "HH:mm:ss.SSS";
			}
			else
			{
				format = "HH:mm:ss";
			}
			
			SimpleDateFormat sdfTime = new SimpleDateFormat(format);			
			String timeString = sdfTime.format(new Date(timeInMilliSeconds));
			
			return timeString;
		}
		else
			return "-";
	}
	
	public static String getDateString(long time)
	{
		String format = "EE, dd.MM.yyyy";
		SimpleDateFormat sdfTime = new SimpleDateFormat(format);
		
		String dateString = sdfTime.format(new Date(time));		
		
		return dateString;
	}

	public static long getTimeInMs(String time)
	{
		int ms = 0, min = 0, sec = 0, hours = 0;

		ms = Integer.parseInt(time.substring(time.indexOf(".") + 1, time.length()));
		time = time.substring(0, time.indexOf("."));

		sec = Integer.parseInt(time.substring(time.lastIndexOf(":") + 1, time.length()));
		time = time.substring(0, time.lastIndexOf(":"));

		min = Integer.parseInt(time.substring(time.lastIndexOf(":") + 1, time.length()));
		if (time.indexOf(":") != -1)
		{
			time = time.substring(0, time.lastIndexOf(":"));
			hours = Integer.parseInt(time);
		}

		return (ms + sec * 1000 + min * 60 * 1000 + hours * 60 * 60 * 1000);
	}

	public static boolean isValidTime(String time)
	{
		return Pattern.matches("[0-9][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]", time)
				|| Pattern.matches("[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]", time);
	}

	/**
	 * get some string of long variable representing some time measured in
	 * seconds
	 * 
	 * @param timeInSeconds
	 *            time in seconds
	 * @return String representing this time.
	 */
	public static String getTimeString(int timeInSeconds)
	{
		if (timeInSeconds >= 0)
		{
			int rest = 0;

			int minutes = (int) (timeInSeconds / MINUTES_IN_S);
			rest = (int) (timeInSeconds % MINUTES_IN_S);

			int seconds = rest;

			return minutes + ":" + getSeconds(seconds);
		}
		else
			return "-";
	}

	/**
	 * convert seconds to string
	 * 
	 * @param secs
	 *            seconds
	 * @return string representing this seconds.
	 */
	public static String getSeconds(int secs)
	{
		if (secs < 0)
		{
			return "-";
		}
		if (secs < 10)
		{
			return "0" + secs;
		}
		else
		{
			return "" + secs;
		}
	}

	private static String long2String(long t)
	{
		if (t >= 0)
		{
			return Long.toString(t);
		}
		else
		{
			return "-";
		}
	}

	public static String getMsString(long t)
	{
		return long2String(t);
	}

	public static String getSampleString(long sample)
	{
		return long2String(sample);
	}

	public static String getFullTimeString(long timeInMilliSeconds)
	{
		return TimeUtil.getTimeString(timeInMilliSeconds, Constants.withMilliSecs) + " ("
				+ TimeUtil.getMsString(timeInMilliSeconds) + " ms)";
	}

	public static String getFullDateString(long timeInMilliSeconds, long offsetInMilliSeconds)
	{
		return TimeUtil.getDateString(timeInMilliSeconds) + ", " + TimeUtil.getTimeString(timeInMilliSeconds, Constants.withMilliSecs) + " Uhr";
//		+ " (" + TimeUtil.getMsString(timeInMilliSeconds) + " ms)";
	}
}
