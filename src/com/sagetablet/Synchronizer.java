package com.sagetablet;

import android.os.AsyncTask;

public class Synchronizer extends AsyncTask<String, Integer, String> {
	@Override
	protected String doInBackground(String... params) {

		int appid = Integer.parseInt(params[0]);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		MainActivity.communicator.synchronizeApps();
		/*
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		MainActivity.communicator.startPerformance(appid);*/
		
		return "";
	}

	protected void onPostExecute(String message) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
