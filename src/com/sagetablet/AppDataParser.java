package com.sagetablet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AppDataParser {

	public static boolean isAppInfo(String word) {
		return (word.length() > 40);
	}

	public static Application parseAppInfoAndReturnApplication(String appInfo) {
		String[] parsedBySpace = appInfo.split(" ");
		String appName = parsedBySpace[0];
		int ID = Integer.parseInt(parsedBySpace[1]);
		double left = Integer.parseInt(parsedBySpace[2]);
		double right = Integer.parseInt(parsedBySpace[3]);
		double top = Integer.parseInt(parsedBySpace[4]);
		double bottom = Integer.parseInt(parsedBySpace[5]);
		int sailID = Integer.parseInt(parsedBySpace[6]);
		int Z = Integer.parseInt(parsedBySpace[7]);
		String stuff = "";
		Application returnedApplication = new Application(appName, ID, left, right, top, bottom, sailID, Z, stuff);

		
		return returnedApplication;
	}

	public static boolean isAppInfoString(String word) {
		// System.out.println("Splitting the word in isAppInfo");
		String[] parsedByNull = word.split("\0");
		for (String parsedWord : parsedByNull) {
			if ((parsedWord.equals("40001"))) {
				// System.out.println("Getting out of isAppInfo(true)");
				return true;
			}
		}
		return false;

	}

	public static boolean isTiledDisplayResolution(String word) {
		if (word.contains("\0")) {
			return false;
		}
		String[] parsedBySpace = word.split(" ");
		if (parsedBySpace.length == 2) {
			return true;
		} else
			return false;
	}

	public static boolean isSingleDisplayResolution(String word) {
		if (word.contains("\0")) {
			return false;
		}
		String[] parsedBySpace = word.split(" ");
		if (parsedBySpace.length == 3) {
			return true;
		} else
			return false;
	}

	public static boolean isDisplayInfo(String word) {
		String[] parsedByNull = word.split("\0");
		for (String parsedWord : parsedByNull) {
			if ((parsedWord.equals("40004"))) {
				// System.out.println("Getting out of isAppInfo(true)");
				return true;
			}
		}
		return false;
	}
//Deprecated
	/*
	public static boolean isPerformanceInfo(String word) {
		// System.out.println("Splitting the word in perfInfo");
		String[] parsedBySpace = word.split(" ");
		for (String parsedWord : parsedBySpace) {
			if ((parsedWord.equals("Display")) || (parsedWord.equals("Rendering"))) {
				// System.out.println("Getting out of perfInfo(true)");
				return true;
			}
		}
		// System.out.println("Getting out of perfInfo(false)");
		return false;

	}
*/	
	public static boolean isPerformanceInfo(String word) {
		// System.out.println("Splitting the word in perfInfo");
		String[] parsedByNull = word.split("\0");
		for (String parsedWord : parsedByNull) {
			if (parsedWord.equals("40002")) {
				// System.out.println("Getting out of perfInfo(true)");
				return true;
			}
		}
		// System.out.println("Getting out of perfInfo(false)");
		return false;

		
		
	}
	
	public static int getAppID(String word){
		String[] parsedByNull = word.split("\n")[0].split("\0");
		/*
		for(int i=0; i<parsedByNull.length; i++){
		
		System.out.println("parsedByNull ID : " + parsedByNull[i]);
		
		}
		*/
		return Integer.parseInt(parsedByNull[parsedByNull.length-1]);
		
	}
	
	


	public static void parseAndSetDisplayInfo(String word) {
		String[] parsedByNull = word.split("\0");

		// take the last word and split by spaces
		String[] lastWordParsedBySpace = (parsedByNull[parsedByNull.length - 1].split("\n")[0]).split(" ");
		System.out.println("last word is : " + lastWordParsedBySpace);
		SAGE.DISPLAYS_X = Integer.parseInt(lastWordParsedBySpace[1]);
		System.out.println(SAGE.DISPLAYS_X);
		SAGE.DISPLAYS_Y = Integer.parseInt(lastWordParsedBySpace[2]);
		System.out.println(SAGE.DISPLAYS_Y);
	}

	public static void parseAndSetDisplayResolution(String word) {
		String[] parsedBySpace = (word.split("\n")[0]).split(" ");
		System.out.println("displayRes is: " + parsedBySpace[0] + " and " + parsedBySpace[1]);
		System.out.println(parsedBySpace[0]);
		System.out.println(parsedBySpace[1]);
		SAGE.X = Integer.parseInt(parsedBySpace[0]);
		SAGE.Y = Integer.parseInt(parsedBySpace[1]);
	}

	public static List<Application> parseAppStringAndReturnAppList(String appStringToParse) {
		String[] parsedByNull = appStringToParse.split("\0");

		List<Application> appList = MainActivity.communicator.getAppList();

		for (String parsedWord : parsedByNull) {

			// System.out.println("parsedWord is : "+parsedWord);
			if (isAppInfo(parsedWord)) {

				Application returnedApplication = parseAppInfoAndReturnApplication(parsedWord);
				boolean found = false;

				for (Application application : appList) {
					// pokud tam je prepiseme
					if (returnedApplication.getID() == application.getID()) {
						application = returnedApplication;
						found = true;
						System.out.println("Found je alespon nekdy true");
					}
				}
				// pokud tam neni pridame aplikaci do seznamu
				if (found == false) {
					appList.add(returnedApplication);
					if (returnedApplication.getZ() > MainActivity.communicator.maxZ) {
						MainActivity.communicator.maxZ = returnedApplication.getZ();
					}
				}
				// appList.add(returnedApplication);
			}
		}
		return appList;
	}

}
