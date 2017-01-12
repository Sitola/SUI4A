package com.sagetablet;

public class PrepareMessage {
	private String message;
	
	public PrepareMessage (String distance, String message, String data, String applicationCode) {
        String fst = padLeft(distance, 8) + "\0";
        String snd = padLeft(message, 8) + "\0";
        String trd = padLeft(applicationCode, 8) + "\0";
        String fth = padLeft(data, 8) + "\0";

        String assembledMessage = fst + snd + trd + fth;

        String size = (assembledMessage.length() + 9) + "";
        String paddedSize = padLeft(size, 8) + "\0";
        
        String finalMessage = paddedSize + assembledMessage;
        System.out.println (finalMessage);
        this.message = finalMessage;
	}
	
	private String padLeft (String s, int n) {
	    int forCap = n - s.length();
	    String returnString = "";

	    for (int i = 0; i < forCap; i++) {
	        returnString = returnString + " ";
	    }
	    returnString += s;

	    return returnString;
	}
	
	public String getMessage () {
		return message;
	}
	
	public void printMessage () {
		System.out.println (message);
	}
}