package com.vknews;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	/**
	 * Seconds in month
	 */
	public static final long MONTH = 2592000;
	/**
	 * Seconds in day
	 */
	public static final long DAY = 86400;
	/**
	 * Seconds in hour
	 */
	public static final long HOUR = 3600;
	/**
	 * Seconds in minute
	 */
	public static final long MINUTE = 60;
	
	/**
	 * Format time in format n минут / n часов / n дней назад
	 * 
	 * @param time
	 * @return String - "n минут / n часов / n дней назад"
	 */
	public static String formatTimeAgo(long time) {
		long days = time / DAY;
		if (days != 0) {
			time = time % (days * DAY);
		}

		long hours = time / HOUR;
		if (hours != 0) {
			time = time % (hours * HOUR);
		}

		long minutes = time / MINUTE;
		return minutes + " минут /" + hours + " часов /" + days + " дней назад";
	}
	
	/**
	 * Get Md5 hash.
	 * 
	 * @param input
	 *            Input string.
	 * @return hash string.
	 */
	public static String hashMd5(String input) {
		if (input == null) {
			return null; 
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);
			while (md5.length() < 32) {
				// md5 = "0" + md5;
			}
			return md5;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}


}
