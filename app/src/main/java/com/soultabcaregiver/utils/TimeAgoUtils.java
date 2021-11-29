package com.soultabcaregiver.utils;



import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class TimeAgoUtils {
	
	private static final int SECOND_MILLIS = 1000;
	
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
	
	public static String covertTimeToText(String dataDate) {
		String convTime = null;
		
		String prefix = "";
		String suffix = "Ago";
		
		try {
			
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date pasTime = dateFormat.parse(dataDate);
			
			Date nowTime = new Date();
			
			long dateDiff = nowTime.getTime() - pasTime.getTime();
			
			long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
			long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
			long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
			long day = TimeUnit.MILLISECONDS.toDays(dateDiff);
			
			if (second < 60) {
				convTime = "just now";
			} else if (minute < 60) {
				if (minute == 1) {
					convTime = "a minute " + suffix;
				} else {
					convTime = minute + " minutes " + suffix;
				}
			} else if (hour < 24) {
				convTime = hour + " hour ago";
			} else if (day >= 7) {
				if (day > 360) {
					if ((day / 360) == 1) {
						convTime = "a year " + suffix;
					} else {
						convTime = (day / 360) + " years " + suffix;
					}
				} else if (day > 30) {
					if ((day / 30) == 1) {
						convTime = "a month " + suffix;
					} else {
						convTime = (day / 30) + " months " + suffix;
					}
				} else {
					if ((day / 7) == 1) {
						convTime = "a week " + suffix;
					} else {
						convTime = (day / 7) + " week " + suffix;
					}
				}
			} else if (day < 7) {
				if (day == 0) {
					convTime = "today";
				} else if (day == 1) {
					convTime = "yesterday";
				} else {
					convTime = day + " days " + suffix;
				}
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e("ConvTimeE", e.getMessage());
		}
		return convTime;
	}
}
