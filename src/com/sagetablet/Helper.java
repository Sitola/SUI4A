package com.sagetablet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

import android.graphics.Point;
import android.graphics.PointF;

public class Helper {
	public static boolean badStart = false;
	
	private static final int LEFT_TOP = 1;
	private static final int RIGHT_TOP = 2;
	private static final int RIGHT_BOTTOM = 3;
	private static final int LEFT_BOTTOM = 4;

	public static boolean checkIPAndPort(String ip, String port) {
		boolean ok = true;
		Pattern ipPattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

		if (!ipPattern.matcher(ip).matches()) {
			ok = false;
		}

		int porti = 0;
		try {
			porti = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			ok = false;
			e.printStackTrace();
		}
		if (porti < 1 && porti > 65535) {
			ok = false;
		}

		return ok;
	}

	public static void logToFile(String filename, String log) {
		File myFile = new File("/sdcard/SAGETablet/" + filename + ".txt");
		try {
			if (!myFile.exists()) {
				myFile.createNewFile();
			}
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(log);
			myOutWriter.close();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double distanceBetweenPoints(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	public static PointF midPoint(PointF p1, PointF p2) {
		return new PointF((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
	}

	public static boolean isMinimal(double min, double x1, double x2) {
		return ((min <= x1) && (min <= x2));
	}

	public static boolean isMinimal(double min, double x1, double x2, double x3) {
		return ((min <= x1) && (min <= x2) && (min <= x3));
	}

	public static int getSign(int pc, double x1, double y1, double x2, double y2) {
		if ((pc == LEFT_TOP) || (pc == LEFT_BOTTOM)) {
			if ((x1 >= x2) && (y1 >= y2)) {
				return -1;
			} else if ((x1 < x2) && (y1 < y2)) {
				return 1;
			} else if (x1 > x2) {
				return -1;
			} else {
				return 1;
			}						
		} else {
			if ((x1 >= x2) && (y1 >= y2)) {
				return 1;
			} else if ((x1 < x2) && (y1 < y2)) {
				return -1;
			} else if (x1 > x2) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
